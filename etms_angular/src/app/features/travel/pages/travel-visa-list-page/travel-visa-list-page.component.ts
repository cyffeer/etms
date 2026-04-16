import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { VisaInfo, VisaType } from '../../models/travel.models';
import { VisaInfoService } from '../../services/visa-info.service';
import { VisaTypeService } from '../../services/visa-type.service';

type VisaMode = 'all' | 'expiring' | 'expired';

@Component({
  selector: 'app-travel-visa-list-page',
  templateUrl: './travel-visa-list-page.component.html',
})
export class TravelVisaListPageComponent implements OnInit {
  loading = false;
  error = '';
  rows: VisaInfo[] = [];
  visaTypesById: Record<number, VisaType> = {};
  summary = {
    total: 0,
    current: 0,
    expiring: 0,
    expired: 0,
  };

  filters = {
    employeeNumber: '',
    mode: 'all' as VisaMode,
    days: 30,
  };

  constructor(
    private readonly visaInfoService: VisaInfoService,
    private readonly visaTypeService: VisaTypeService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    const employeeNumber = this.filters.employeeNumber.trim();
    const request$ = employeeNumber
      ? this.visaInfoService.getByEmployee(employeeNumber)
      : this.filters.mode === 'expired'
        ? this.visaInfoService.getExpired()
        : this.filters.mode === 'expiring'
          ? this.visaInfoService.getExpiring(this.filters.days || 30)
          : this.visaInfoService.getAll();

    forkJoin({
      rows: request$,
      visaTypes: this.visaTypeService.getAll(),
    }).subscribe({
      next: ({ rows, visaTypes }) => {
        this.visaTypesById = visaTypes.reduce<Record<number, VisaType>>((acc, visaType) => {
          acc[visaType.visaTypeId] = visaType;
          return acc;
        }, {});

        const filtered = this.applyModeFilter(rows);
        this.rows = filtered;
        this.summary = {
          total: filtered.length,
          current: filtered.filter((row) => this.resolveStatus(row.expiryDate) === 'current').length,
          expiring: filtered.filter((row) => this.resolveStatus(row.expiryDate) === 'expiring').length,
          expired: filtered.filter((row) => this.resolveStatus(row.expiryDate) === 'expired').length,
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load visa records.';
        this.loading = false;
      },
    });
  }

  setCancelFlag(row: VisaInfo, cancelFlag: boolean): void {
    if (!confirm(`Mark visa cancel flag as ${cancelFlag ? 'YES' : 'NO'} for this record?`)) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.visaInfoService.updateCancelFlag(row.employeeNumber, row.visaTypeId, cancelFlag).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to update visa cancel flag.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      mode: 'all',
      days: 30,
    };
    this.load();
  }

  visaTypeName(visaTypeId: number): string {
    return this.visaTypesById[visaTypeId]?.visaTypeName || `Visa Type #${visaTypeId}`;
  }

  cancelFlagLabel(cancelFlag?: boolean | null): string {
    if (cancelFlag == null) {
      return 'Not Tracked';
    }
    return cancelFlag ? 'YES' : 'NO';
  }

  resolveStatus(expiryDate?: string | null): 'current' | 'expiring' | 'expired' | 'unknown' {
    if (!expiryDate) {
      return 'unknown';
    }

    const expiry = new Date(expiryDate);
    if (Number.isNaN(expiry.getTime())) {
      return 'unknown';
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    expiry.setHours(0, 0, 0, 0);

    if (expiry < today) {
      return 'expired';
    }

    const expiringThreshold = new Date(today);
    expiringThreshold.setDate(expiringThreshold.getDate() + (this.filters.days || 30));
    if (expiry <= expiringThreshold) {
      return 'expiring';
    }

    return 'current';
  }

  statusLabel(expiryDate?: string | null): string {
    const status = this.resolveStatus(expiryDate);
    if (status === 'expiring') {
      return `Expiring in ${this.filters.days}d`;
    }
    if (status === 'expired') {
      return 'Expired';
    }
    if (status === 'current') {
      return 'Current';
    }
    return 'Open';
  }

  statusClass(expiryDate?: string | null): string {
    const status = this.resolveStatus(expiryDate);
    if (status === 'expired') {
      return 'status-pill--inactive';
    }
    if (status === 'expiring') {
      return 'status-pill--warning';
    }
    if (status === 'current') {
      return 'status-pill--active';
    }
    return 'status-pill--neutral';
  }

  canEditCancelFlag(row: VisaInfo): boolean {
    return this.resolveStatus(row.expiryDate) !== 'expired';
  }

  private applyModeFilter(rows: VisaInfo[]): VisaInfo[] {
    if (this.filters.mode === 'all') {
      return rows;
    }

    return rows.filter((row) => this.resolveStatus(row.expiryDate) === this.filters.mode);
  }
}
