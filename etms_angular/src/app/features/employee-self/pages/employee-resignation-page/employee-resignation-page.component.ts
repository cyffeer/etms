import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { EmployeeEventRequest, EmployeeEventResponse } from '../../../monitoring/models/employee-event.model';
import { EmployeeEventService } from '../../../monitoring/services/employee-event.service';

@Component({
  selector: 'app-employee-resignation-page',
  templateUrl: './employee-resignation-page.component.html',
  styleUrls: ['./employee-resignation-page.component.css']
})
export class EmployeeResignationPageComponent implements OnInit {
  requests: EmployeeEventResponse[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';
  page = 0;
  readonly size = 10;
  totalPages = 0;
  totalElements = 0;

  form = {
    title: '',
    description: '',
    effectiveDate: '',
    endDate: ''
  };

  constructor(
    private readonly authService: AuthService,
    private readonly employeeEventService: EmployeeEventService
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  submit(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.username) {
      this.error = 'Missing employee identity.';
      return;
    }
    if (!this.form.title.trim()) {
      this.error = 'Title is required.';
      return;
    }
    if (!this.form.effectiveDate.trim()) {
      this.error = 'Effective date is required.';
      return;
    }
    if (this.form.endDate && this.form.endDate < this.form.effectiveDate) {
      this.error = 'Last working date cannot be before effective date.';
      return;
    }

    const payload: EmployeeEventRequest = {
      employeeNumber: user.username,
      eventType: 'RESIGNATION',
      title: this.form.title.trim(),
      description: this.form.description.trim() || null,
      effectiveDate: this.form.effectiveDate,
      endDate: this.form.endDate || null,
      status: 'PENDING'
    };

    this.saving = true;
    this.error = '';
    this.success = '';
    this.employeeEventService.create(payload).subscribe({
      next: () => {
        this.success = 'Resignation request submitted.';
        this.form = { title: '', description: '', effectiveDate: '', endDate: '' };
        this.page = 0;
        this.saving = false;
        this.loadRequests();
      },
      error: (err) => {
        this.error = this.extractApiError(err) || 'Unable to submit resignation request.';
        this.saving = false;
      }
    });
  }

  private loadRequests(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.username) {
      this.requests = [];
      return;
    }

    this.loading = true;
    this.error = '';
    this.employeeEventService.searchCategory('RESIGNATION', { employeeNumber: user.username }, this.page, this.size).subscribe({
      next: (result) => {
        this.requests = result.items;
        this.totalPages = result.totalPages ?? 0;
        this.totalElements = result.totalElements ?? result.items.length;
        this.loading = false;
      },
      error: (err) => {
        this.error = this.extractApiError(err) || 'Unable to load resignation requests.';
        this.loading = false;
      }
    });
  }

  prevPage(): void {
    if (this.page <= 0) return;
    this.page -= 1;
    this.loadRequests();
  }

  nextPage(): void {
    if (!this.hasNextPage()) return;
    this.page += 1;
    this.loadRequests();
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
