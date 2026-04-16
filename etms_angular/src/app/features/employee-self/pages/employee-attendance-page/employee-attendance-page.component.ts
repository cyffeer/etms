import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { AttendanceResponse } from '../../../attendance/attendance.model';
import { AttendanceService } from '../../../attendance/services/attendance.service';

@Component({
  selector: 'app-employee-attendance-page',
  templateUrl: './employee-attendance-page.component.html',
  styleUrls: ['./employee-attendance-page.component.css']
})
export class EmployeeAttendancePageComponent implements OnInit {
  rows: AttendanceResponse[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';
  page = 0;
  readonly size = 10;
  totalPages = 0;
  totalElements = 0;

  form = {
    attendanceDate: '',
    timeIn: '',
    timeOut: '',
    status: 'PRESENT'
  };

  constructor(
    private readonly authService: AuthService,
    private readonly attendanceService: AttendanceService
  ) {}

  ngOnInit(): void {
    this.loadRows();
  }

  submit(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.username) {
      this.error = 'Missing employee identity.';
      return;
    }
    if (!this.form.attendanceDate.trim()) {
      this.error = 'Attendance date is required.';
      return;
    }
    if (this.form.timeIn && this.form.timeOut && this.form.timeOut < this.form.timeIn) {
      this.error = 'Time out cannot be before time in.';
      return;
    }

    this.error = '';
    this.success = '';
    this.saving = true;

    this.attendanceService.create({
      employeeNumber: user.username,
      attendanceDate: this.form.attendanceDate,
      timeIn: this.form.timeIn.trim() || null,
      timeOut: this.form.timeOut.trim() || null,
      status: this.form.status.trim() || null
    }).subscribe({
      next: () => {
        this.success = 'Attendance submitted successfully.';
        this.form = { attendanceDate: '', timeIn: '', timeOut: '', status: 'PRESENT' };
        this.saving = false;
        this.loadRows();
      },
      error: (err) => {
        this.error = this.extractApiError(err) || 'Unable to submit attendance.';
        this.saving = false;
      }
    });
  }

  private loadRows(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.username) {
      this.rows = [];
      return;
    }

    this.loading = true;
    this.error = '';
    this.attendanceService.search({ employeeNumber: user.username }, this.page, this.size).subscribe({
      next: (result) => {
        this.rows = result.items;
        this.totalPages = result.totalPages ?? 0;
        this.totalElements = result.totalElements ?? result.items.length;
        this.loading = false;
      },
      error: (err) => {
        this.error = this.extractApiError(err) || 'Unable to load attendance records.';
        this.loading = false;
      }
    });
  }

  prevPage(): void {
    if (this.page <= 0) return;
    this.page -= 1;
    this.loadRows();
  }

  nextPage(): void {
    if (!this.hasNextPage()) return;
    this.page += 1;
    this.loadRows();
  }

  hasPrevPage(): boolean {
    return this.page > 0;
  }

  hasNextPage(): boolean {
    return this.totalPages > 0 && this.page + 1 < this.totalPages;
  }

  private extractApiError(err: any): string | null {
    const details = err?.details ?? err?.error;
    const errors: unknown = details?.errors;
    if (Array.isArray(errors) && errors.length > 0) {
      return errors.join(', ');
    }
    if (typeof details?.message === 'string' && details.message.trim()) {
      return details.message;
    }
    if (typeof err?.message === 'string' && err.message.trim()) {
      return err.message;
    }
    return null;
  }
}
