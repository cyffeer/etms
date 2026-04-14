import { Component, OnInit } from '@angular/core';
import { DepartmentResponse } from '../../models/department.model';
import { DepartmentsService } from '../../services/departments.service';

@Component({
  selector: 'app-department-list-page',
  templateUrl: './department-list-page.component.html'
})
export class DepartmentListPageComponent implements OnInit {
  departments: DepartmentResponse[] = [];
  loading = false;
  error = '';
  keyword = '';

  constructor(private departmentsService: DepartmentsService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const request$ = this.keyword.trim()
      ? this.departmentsService.searchDepartments(this.keyword)
      : this.departmentsService.getDepartments();

    request$.subscribe({
      next: (data) => {
        this.departments = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load departments.';
        this.loading = false;
      }
    });
  }

  onDelete(departmentId: number): void {
    if (!confirm('Delete this department?')) return;
    this.departmentsService.deleteDepartment(departmentId).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete department.')
    });
  }

  resetFilters(): void {
    this.keyword = '';
    this.load();
  }
}
