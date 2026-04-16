import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Leave } from '../../models/leave.models';
import { LeaveService } from '../../services/leave.service';
import { LeaveType } from '../../../leave-types/models/leave-type.models';
import { LeaveTypeService } from '../../../leave-types/services/leave-type.service';

@Component({
  selector: 'app-leave-list-page',
  templateUrl: './leave-list-page.component.html',
})
export class LeaveListPageComponent implements OnInit {
  loading = false;
  error = '';
  items: Leave[] = [];
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizes = [5, 10, 20, 50];
  leaveTypes: LeaveType[] = [];
  exporting = false;
  filters = {
    employeeNumber: '',
    leaveType: '',
    status: '',
    startDate: '',
    endDate: '',
  };
  readonly statusOptions = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];

  constructor(
    private service: LeaveService,
    private router: Router,
    private leaveTypeService: LeaveTypeService
  ) {}

  ngOnInit(): void {
    this.loadLeaveTypes();
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = '';
    const hasFilters =
      !!this.filters.employeeNumber.trim() ||
      !!this.filters.leaveType.trim() ||
      !!this.filters.status.trim() ||
      !!this.filters.startDate.trim() ||
      !!this.filters.endDate.trim();
    const request$ = hasFilters ? this.service.search(this.filters, this.page, this.size) : this.service.getAll(this.page, this.size);

    request$.subscribe({
      next: (res) => {
        this.items = res.items;
        this.totalElements = res.totalElements ?? res.items.length;
        this.totalPages = res.totalPages ?? 1;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load leaves.';
        this.loading = false;
      }
    });
  }

  create(): void {
    this.router.navigate(['leaves/new']);
  }

  view(id: number): void {
    this.router.navigate(['leaves', id]);
  }

  edit(id: number): void {
    this.router.navigate(['leaves', id, 'edit']);
  }

  remove(id: number): void {
    if (!confirm('Delete this leave record?')) return;
    this.service.delete(id).subscribe({
      next: () => this.loadData(),
      error: (err) => this.error = err?.message || 'Delete failed.'
    });
  }

  exportReport(format: 'xlsx' | 'pdf'): void {
    this.exporting = true;
    this.error = '';
    this.service.exportReport(format, {
      employeeNumber: this.filters.employeeNumber,
      leaveType: this.filters.leaveType,
      status: this.filters.status,
      startDate: this.filters.startDate,
      endDate: this.filters.endDate,
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = `leave-report.${format}`;
        anchor.click();
        window.URL.revokeObjectURL(url);
        this.exporting = false;
      },
      error: (err) => {
        this.error = err?.error?.message || err?.message || 'Failed to export leave report.';
        this.exporting = false;
      }
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      leaveType: '',
      status: '',
      startDate: '',
      endDate: '',
    };
    this.page = 0;
    this.loadData();
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.loadData();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.page) {
      return;
    }
    this.page = page;
    this.loadData();
  }

  get visiblePages(): number[] {
    return Array.from({ length: this.totalPages }, (_, index) => index);
  }

  leaveTypeLabel(code: string): string {
    const match = this.leaveTypes.find((leaveType) => leaveType.leaveTypeCode === code);
    return match ? `${match.leaveTypeCode} - ${match.leaveTypeName}` : code;
  }

  private loadLeaveTypes(): void {
    this.leaveTypeService.getAll().subscribe({
      next: (result) => {
        this.leaveTypes = result.items.filter((row: LeaveType) => row.active);
      },
      error: () => {
        this.leaveTypes = [];
      }
    });
  }
}
