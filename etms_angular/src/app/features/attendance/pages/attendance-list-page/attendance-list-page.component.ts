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
  filters = {
    employeeNumber: '',
    year: '',
    month: '',
  };
  readonly canCreate = this.authService.hasAnyRole(['ADMIN', 'HR', 'EMPLOYEE']);
  readonly canEdit = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);
  readonly canDelete = this.authService.hasAnyRole(['ADMIN']);

  constructor(private service: AttendanceService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters = !!this.filters.employeeNumber.trim() || !!this.filters.year || !!this.filters.month;
    const request$ = hasFilters
      ? this.service.search({
          employeeNumber: this.filters.employeeNumber,
          year: this.filters.year ? Number(this.filters.year) : null,
          month: this.filters.month ? Number(this.filters.month) : null,
        })
      : this.service.getAll();

    request$.subscribe({
      next: (data) => {
        this.rows = data;
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
      year: '',
      month: '',
    };
    this.load();
  }
}
