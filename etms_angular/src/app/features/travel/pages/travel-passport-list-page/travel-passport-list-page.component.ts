import { Component, OnInit } from '@angular/core';
import { PassportInfoService } from '../../services/passport-info.service';
import { PassportInfo } from '../../models/travel.models';

type PassportStatus = 'all' | 'current' | 'expired';

@Component({
  selector: 'app-travel-passport-list-page',
  templateUrl: './travel-passport-list-page.component.html',
})
export class TravelPassportListPageComponent implements OnInit {
  loading = false;
  error = '';
  rows: PassportInfo[] = [];
  summary = {
    total: 0,
    current: 0,
    expired: 0,
  };

  filters: { employeeNumber: string; status: PassportStatus } = {
    employeeNumber: '',
    status: 'all',
  };

  constructor(private readonly passportInfoService: PassportInfoService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    const employeeNumber = this.filters.employeeNumber.trim();
    const request$ = employeeNumber
      ? this.passportInfoService.getByEmployee(employeeNumber)
      : this.filters.status === 'expired'
        ? this.passportInfoService.getExpired()
        : this.passportInfoService.getAll();

    request$.subscribe({
      next: (rows) => {
        const filtered = this.applyStatusFilter(rows);
        this.rows = filtered;
        this.summary = {
          total: filtered.length,
          current: filtered.filter((row) => this.resolveStatus(row.expiryDate) === 'current').length,
          expired: filtered.filter((row) => this.resolveStatus(row.expiryDate) === 'expired').length,
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load passport records.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      status: 'all',
    };
    this.load();
  }

  resolveStatus(expiryDate?: string | null): 'current' | 'expired' | 'unknown' {
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
    return expiry < today ? 'expired' : 'current';
  }

  statusLabel(expiryDate?: string | null): string {
    const status = this.resolveStatus(expiryDate);
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
    if (status === 'current') {
      return 'status-pill--active';
    }
    return 'status-pill--neutral';
  }

  private applyStatusFilter(rows: PassportInfo[]): PassportInfo[] {
    if (this.filters.status === 'all') {
      return rows;
    }

    return rows.filter((row) => this.resolveStatus(row.expiryDate) === this.filters.status);
  }
}
