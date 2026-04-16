import { Component, OnInit } from '@angular/core';
import { EmployeeResponse } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-employee-list-page',
  templateUrl: './employee-list-page.component.html'
})
export class EmployeeListPageComponent implements OnInit {
  employees: EmployeeResponse[] = [];
  private filteredEmployees: EmployeeResponse[] = [];
  loading = false;
  exporting = false;
  error = '';
  page = 0;
  readonly size = 10;
  totalPages = 0;
  totalElements = 0;
  hasFilters = false;
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
    this.hasFilters =
      !!this.filters.employeeNumber.trim() ||
      !!this.filters.nameKeyword.trim() ||
      !!this.filters.startDate.trim() ||
      !!this.filters.endDate.trim();
    if (this.hasFilters) {
      this.employeesService.searchEmployees(this.filters).subscribe({
        next: (employees: EmployeeResponse[]) => {
          this.filteredEmployees = employees;
          this.totalElements = this.filteredEmployees.length;
          this.totalPages = this.totalElements === 0 ? 0 : Math.ceil(this.totalElements / this.size);
          this.applyFilteredPageSlice();
          this.loading = false;
        },
        error: (err: { message?: string }) => {
          this.error = err?.message || 'Failed to load employees.';
          this.loading = false;
        }
      });
      return;
    }

    this.employeesService.getEmployees(this.page, this.size).subscribe({
      next: (result) => {
        this.employees = result.items;
        this.totalPages = result.totalPages ?? 0;
        this.totalElements = result.totalElements ?? result.items.length;
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
    this.page = 0;
    this.load();
  }

  search(): void {
    this.page = 0;
    this.load();
  }

  prevPage(): void {
    if (!this.hasPrevPage()) return;
    this.page -= 1;
    this.hasFilters ? this.applyFilteredPageSlice() : this.load();
  }

  nextPage(): void {
    if (!this.hasNextPage()) return;
    this.page += 1;
    this.hasFilters ? this.applyFilteredPageSlice() : this.load();
  }

  hasPrevPage(): boolean {
    return this.page > 0;
  }

  hasNextPage(): boolean {
    return this.totalPages > 0 && this.page + 1 < this.totalPages;
  }

  private applyFilteredPageSlice(): void {
    if (this.totalPages > 0 && this.page >= this.totalPages) {
      this.page = this.totalPages - 1;
    }
    const from = this.page * this.size;
    const to = from + this.size;
    this.employees = this.filteredEmployees.slice(from, to);
  }

  exportReport(format: 'xlsx' | 'pdf'): void {
    this.exporting = true;
    this.error = '';
    this.employeesService.exportEmployees(format).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = `employees-report.${format}`;
        anchor.click();
        window.URL.revokeObjectURL(url);
        this.exporting = false;
      },
      error: (err) => {
        this.error = err?.error?.message || err?.message || 'Failed to export employee report.';
        this.exporting = false;
      }
    });
  }
}
