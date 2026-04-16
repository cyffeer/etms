import { Component, OnInit } from '@angular/core';
import { AttendanceResponse } from '../../attendance.model';
import { AttendanceService } from '../../services/attendance.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-attendance-list-page',
  templateUrl: './attendance-list-page.component.html',
  styleUrls: ['./attendance-list-page.component.css']
})
export class AttendanceListPageComponent implements OnInit {
  rows: AttendanceResponse[] = [];
  loading = false;
  error = '';
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizes = [5, 10, 20, 50];
  filters = {
    employeeNumber: '',
    employeeName: '',
    designation: '',
    officeName: '',
    year: '',
    month: '',
  };
  readonly canCreate = this.authService.hasAnyRole(['ADMIN', 'HR', 'EMPLOYEE']);
  readonly canEdit = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);
  readonly canDelete = this.authService.hasAnyRole(['ADMIN']);
  exporting = false;

  constructor(private service: AttendanceService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters =
      !!this.filters.employeeNumber.trim() ||
      !!this.filters.employeeName.trim() ||
      !!this.filters.designation.trim() ||
      !!this.filters.officeName.trim() ||
      !!this.filters.year ||
      !!this.filters.month;
    const request$ = hasFilters
      ? this.service.search({
          employeeNumber: this.filters.employeeNumber,
          employeeName: this.filters.employeeName,
          designation: this.filters.designation,
          officeName: this.filters.officeName,
          year: this.filters.year ? Number(this.filters.year) : null,
          month: this.filters.month ? Number(this.filters.month) : null,
        }, this.page, this.size)
      : this.service.getAll(this.page, this.size);

    request$.subscribe({
      next: (result) => {
        this.rows = result.items;
        this.totalElements = result.totalElements ?? result.items.length;
        this.totalPages = result.totalPages ?? 1;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load attendance.';
        this.loading = false;
      }
    });
  }

  onDelete(id: number): void {
    if (!this.canDelete) {
      return;
    }
    if (!confirm('Delete this attendance record?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete attendance record.')
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      employeeName: '',
      designation: '',
      officeName: '',
      year: '',
      month: '',
    };
    this.page = 0;
    this.load();
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.load();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.page) {
      return;
    }
    this.page = page;
    this.load();
  }

  get visiblePages(): number[] {
    return Array.from({ length: this.totalPages }, (_, index) => index);
  }

  exportReport(format: 'xlsx' | 'pdf'): void {
    this.exporting = true;
    this.error = '';
    this.service.exportReport(format, {
      employeeNumber: this.filters.employeeNumber,
      employeeName: this.filters.employeeName,
      designation: this.filters.designation,
      officeName: this.filters.officeName,
      year: this.filters.year ? Number(this.filters.year) : null,
      month: this.filters.month ? Number(this.filters.month) : null,
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = `attendance-report.${format}`;
        anchor.click();
        window.URL.revokeObjectURL(url);
        this.exporting = false;
      },
      error: (err) => {
        this.error = err?.error?.message || err?.message || 'Failed to export attendance report.';
        this.exporting = false;
      }
    });
  }
}
