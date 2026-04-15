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
  keyword = '';
  rows: LeaveType[] = [];

  constructor(private readonly leaveTypeService: LeaveTypeService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.leaveTypeService.getAll(this.keyword).subscribe({
      next: (rows) => {
        this.rows = rows;
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
    this.load();
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
