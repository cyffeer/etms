import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { EmployeeEventResponse } from '../../models/employee-event.model';
import { EmployeeEventService } from '../../services/employee-event.service';

@Component({
  selector: 'app-employee-event-list-page',
  templateUrl: './employee-event-list-page.component.html'
})
export class EmployeeEventListPageComponent implements OnInit {
  rows: EmployeeEventResponse[] = [];
  loading = false;
  error = '';

  readonly eventTypes = [
    'PROMOTION',
    'VIOLATION',
    'CITATION',
    'PROJECT_ASSIGNMENT',
    'RESIGNATION',
    'SUSPENSION',
    'TERMINATION'
  ];
  readonly statuses = ['ACTIVE', 'CLOSED', 'PENDING', 'APPROVED', 'REJECTED'];
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);
  readonly canDelete = this.authService.hasAnyRole(['ADMIN']);

  filters = {
    employeeNumber: '',
    eventType: '',
    status: '',
    keyword: '',
    startDate: '',
    endDate: ''
  };

  constructor(private employeeEventService: EmployeeEventService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters = Object.values(this.filters).some((value) => !!value.trim());
    const request$ = hasFilters ? this.employeeEventService.search(this.filters) : this.employeeEventService.getAll();

    request$.subscribe({
      next: (rows) => {
        this.rows = rows;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load monitoring events.';
        this.loading = false;
      }
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      eventType: '',
      status: '',
      keyword: '',
      startDate: '',
      endDate: ''
    };
    this.load();
  }

  onDelete(employeeEventId: number): void {
    if (!this.canDelete) {
      return;
    }
    if (!confirm('Delete this monitoring event?')) {
      return;
    }
    this.employeeEventService.delete(employeeEventId).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to delete monitoring event.';
      }
    });
  }
}
