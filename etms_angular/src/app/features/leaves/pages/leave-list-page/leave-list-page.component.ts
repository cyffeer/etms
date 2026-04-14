import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Leave } from '../../models/leave.models';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-leave-list-page',
  templateUrl: './leave-list-page.component.html',
})
export class LeaveListPageComponent implements OnInit {
  loading = false;
  error = '';
  items: Leave[] = [];
  filters = {
    employeeNumber: '',
    leaveType: '',
    status: '',
  };
  readonly statusOptions = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];

  constructor(private service: LeaveService, private router: Router) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = '';
    const hasFilters = !!this.filters.employeeNumber.trim() || !!this.filters.leaveType.trim() || !!this.filters.status.trim();
    const request$ = hasFilters ? this.service.search(this.filters) : this.service.getAll();

    request$.subscribe({
      next: (res) => {
        this.items = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load leaves.';
        this.loading = false;
      }
    });
  }

  create(): void {
    this.router.navigate(['leaves/new']);
  }

  view(id: number): void {
    this.router.navigate(['leaves', id]);
  }

  edit(id: number): void {
    this.router.navigate(['leaves', id, 'edit']);
  }

  remove(id: number): void {
    if (!confirm('Delete this leave record?')) return;
    this.service.delete(id).subscribe({
      next: () => this.loadData(),
      error: (err) => this.error = err?.message || 'Delete failed.'
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      leaveType: '',
      status: '',
    };
    this.loadData();
  }
}
