import { Component, OnInit } from '@angular/core';
import { LeaveType } from '../../models/leave-type.models';
import { LeaveTypeService } from '../../services/leave-type.service';

@Component({
  selector: 'app-leave-type-list-page',
  templateUrl: './leave-type-list-page.component.html',
})
export class LeaveTypeListPageComponent implements OnInit {
  loading = false;
  error = '';
  leaveTypeId: number | null = null;
  keyword = '';
  rows: LeaveType[] = [];
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizes = [5, 10, 20, 50];

  constructor(private readonly leaveTypeService: LeaveTypeService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.leaveTypeService.getAll({ leaveTypeId: this.leaveTypeId, keyword: this.keyword }, this.page, this.size).subscribe({
      next: (result) => {
        this.rows = result.items;
        this.totalElements = result.totalElements ?? result.items.length;
        this.totalPages = result.totalPages ?? 1;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load leave types.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.keyword = '';
    this.leaveTypeId = null;
    this.page = 0;
    this.load();
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.load();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.page) {
      return;
    }
    this.page = page;
    this.load();
  }

  get visiblePages(): number[] {
    return Array.from({ length: this.totalPages }, (_, index) => index);
  }

  onDelete(id: number): void {
    if (!confirm('Delete this leave type?')) {
      return;
    }
    this.leaveTypeService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to delete leave type.';
      },
    });
  }
}
