import { Component, OnInit } from '@angular/core';
import { DepartmentMemberResponse } from '../../department-member.model';
import { DepartmentMemberService } from '../../services/department-member.service';

@Component({
  selector: 'app-department-member-list-page',
  templateUrl: './department-member-list-page.component.html',
  styleUrls: ['./department-member-list-page.component.css']
})
export class DepartmentMemberListPageComponent implements OnInit {
  rows: DepartmentMemberResponse[] = [];
  loading = false;
  error = '';
  filters = {
    departmentCode: '',
    employeeNumber: '',
  };

  constructor(private service: DepartmentMemberService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters = !!this.filters.departmentCode.trim() || !!this.filters.employeeNumber.trim();
    const request$ = hasFilters ? this.service.search(this.filters) : this.service.getAll();

    request$.subscribe({
      next: (data) => {
        this.rows = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load department members.';
        this.loading = false;
      }
    });
  }

  onDelete(id: number): void {
    if (!confirm('Delete this department member record?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete department member record.')
    });
  }

  resetFilters(): void {
    this.filters = {
      departmentCode: '',
      employeeNumber: '',
    };
    this.load();
  }
}
