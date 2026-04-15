import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs';
import { EmployeeResponse } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-employee-list-page',
  templateUrl: './employee-list-page.component.html'
})
export class EmployeeListPageComponent implements OnInit {
  employees: EmployeeResponse[] = [];
  loading = false;
  error = '';
  filters = {
    employeeNumber: '',
    nameKeyword: '',
    startDate: '',
    endDate: ''
  };
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'MANAGER']);
  readonly canDelete = this.authService.hasAnyRole(['ADMIN']);

  constructor(private employeesService: EmployeesService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters =
      !!this.filters.employeeNumber.trim() ||
      !!this.filters.nameKeyword.trim() ||
      !!this.filters.startDate.trim() ||
      !!this.filters.endDate.trim();
    const request$ = hasFilters
      ? this.employeesService.searchEmployees(this.filters)
      : this.employeesService.getEmployees().pipe(map((result) => result.items));

    request$.subscribe({
      next: (employees: EmployeeResponse[]) => {
        this.employees = employees;
        this.loading = false;
      },
      error: (err: { message?: string }) => {
        this.error = err?.message || 'Failed to load employees.';
        this.loading = false;
      }
    });
  }

  onDelete(employeeId: number): void {
    if (!this.canDelete) {
      return;
    }
    if (!confirm('Delete this employee?')) return;
    this.employeesService.deleteEmployee(employeeId).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete employee.')
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      nameKeyword: '',
      startDate: '',
      endDate: ''
    };
    this.load();
  }
}
