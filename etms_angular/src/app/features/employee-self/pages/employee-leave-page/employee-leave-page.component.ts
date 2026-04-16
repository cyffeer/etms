import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { Leave, LeaveRequest } from '../../../leaves/models/leave.models';
import { LeaveService } from '../../../leaves/services/leave.service';
import { LeaveType } from '../../../leave-types/models/leave-type.models';
import { LeaveTypeService } from '../../../leave-types/services/leave-type.service';

@Component({
  selector: 'app-employee-leave-page',
  templateUrl: './employee-leave-page.component.html',
  styleUrls: ['./employee-leave-page.component.css']
})
export class EmployeeLeavePageComponent implements OnInit {
  rows: Leave[] = [];
  leaveTypes: LeaveType[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';
  editingId: number | null = null;
  page = 0;
  readonly size = 10;
  totalPages = 0;
  totalElements = 0;

  form = {
    leaveType: '',
    startDate: '',
    endDate: '',
    remarks: ''
  };

  constructor(
    private readonly authService: AuthService,
    private readonly leaveService: LeaveService,
    private readonly leaveTypeService: LeaveTypeService
  ) {}

  ngOnInit(): void {
    this.loadLeaveTypes();
    this.loadRows();
  }

  edit(item: Leave): void {
    this.editingId = item.leaveRecordId;
    this.form.leaveType = item.leaveType ?? '';
    this.form.startDate = item.startDate ?? '';
    this.form.endDate = item.endDate ?? '';
    this.form.remarks = item.remarks ?? '';
  }

  resetForm(): void {
    this.editingId = null;
    this.form = { leaveType: '', startDate: '', endDate: '', remarks: '' };
  }

  submit(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.username) {
      this.error = 'Missing employee identity.';
      return;
    }
    if (!this.form.leaveType.trim()) {
      this.error = 'Leave type is required.';
      return;
    }
    if (!this.form.startDate.trim()) {
      this.error = 'Start date is required.';
      return;
    }
    if (!this.form.endDate.trim()) {
      this.error = 'End date is required.';
      return;
    }
    if (this.form.endDate < this.form.startDate) {
      this.error = 'End date cannot be before start date.';
      return;
    }

    const payload: LeaveRequest = {
      employeeNumber: user.username,
      leaveType: this.form.leaveType.trim(),
      startDate: this.form.startDate,
      endDate: this.form.endDate,
      status: 'PENDING',
      remarks: this.form.remarks.trim() || null
    };

    this.error = '';
    this.success = '';
    this.saving = true;

    const request$ = this.editingId == null
      ? this.leaveService.create(payload)
      : this.leaveService.update(this.editingId, payload);

    request$.subscribe({
      next: () => {
        this.success = this.editingId == null ? 'Leave request submitted.' : 'Leave request updated.';
        this.resetForm();
        this.page = 0;
        this.saving = false;
        this.loadRows();
      },
      error: (err) => {
        this.error = this.extractApiError(err) || 'Unable to save leave request.';
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
    this.leaveService.search({ employeeNumber: user.username }, this.page, this.size).subscribe({
      next: (result) => {
        this.rows = result.items;
        this.totalPages = result.totalPages ?? 0;
        this.totalElements = result.totalElements ?? result.items.length;
        this.loading = false;
      },
      error: (err) => {
        this.error = this.extractApiError(err) || 'Unable to load leave records.';
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

  private loadLeaveTypes(): void {
    this.leaveTypeService.getAll().subscribe({
      next: (result) => {
        this.leaveTypes = result.items.filter((item) => item.active);
      },
      error: () => {
        this.leaveTypes = [];
      }
    });
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
