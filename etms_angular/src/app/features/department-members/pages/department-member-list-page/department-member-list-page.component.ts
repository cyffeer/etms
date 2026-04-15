import { Component, OnInit } from '@angular/core';
import { DepartmentMemberResponse } from '../../department-member.model';
import { DepartmentMemberService } from '../../services/department-member.service';
import { AuthService } from '../../../../core/services/auth.service';
import { MemberType } from '../../../member-types/models/member-type.models';
import { MemberTypeService } from '../../../member-types/services/member-type.service';

@Component({
  selector: 'app-department-member-list-page',
  templateUrl: './department-member-list-page.component.html',
  styleUrls: ['./department-member-list-page.component.css']
})
export class DepartmentMemberListPageComponent implements OnInit {
  rows: DepartmentMemberResponse[] = [];
  memberTypes: MemberType[] = [];
  loading = false;
  error = '';
  filters = {
    departmentKeyword: '',
    employeeNumber: '',
    memberTypeId: null as number | null,
    startDate: '',
    endDate: ''
  };
  readonly canCreateOrEdit = this.authService.hasAnyRole(['ADMIN', 'MANAGER']);
  readonly canDelete = this.authService.hasAnyRole(['ADMIN']);

  constructor(
    private service: DepartmentMemberService,
    private authService: AuthService,
    private memberTypeService: MemberTypeService
  ) {}

  ngOnInit(): void {
    this.memberTypeService.getAll().subscribe({
      next: (rows) => {
        this.memberTypes = rows;
      }
    });
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const hasFilters =
      !!this.filters.departmentKeyword.trim() ||
      !!this.filters.employeeNumber.trim() ||
      this.filters.memberTypeId != null ||
      !!this.filters.startDate.trim() ||
      !!this.filters.endDate.trim();
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
    if (!this.canDelete) {
      return;
    }
    if (!confirm('Delete this department member record?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete department member record.')
    });
  }

  resetFilters(): void {
    this.filters = {
      departmentKeyword: '',
      employeeNumber: '',
      memberTypeId: null,
      startDate: '',
      endDate: ''
    };
    this.load();
  }
}
