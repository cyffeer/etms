import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs';
import { EmployeeResponse } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';

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
  };

  constructor(private employeesService: EmployeesService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters = !!this.filters.employeeNumber.trim() || !!this.filters.nameKeyword.trim();
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
    };
    this.load();
  }
}
