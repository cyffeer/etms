import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { TrngHistoryService } from '../../services/trng-history.service';
import { TrngHist } from '../../models/training.models';

@Component({
  selector: 'app-trng-hist-list-page',
  templateUrl: './trng-hist-list-page.component.html',
})
export class TrngHistListPageComponent implements OnInit {
  items: TrngHist[] = [];
  loading = false;
  error = '';
  employeeNumberFilter = '';
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);

  constructor(
    private service: TrngHistoryService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';
    const request$ = this.employeeNumberFilter.trim()
      ? this.service.getByEmployee(this.employeeNumberFilter.trim())
      : this.service.getAll();

    request$.subscribe({
      next: (data) => {
        this.items = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load';
        this.loading = false;
      },
    });
  }

  assign() {
    this.router.navigate(['training/history/new']);
  }

  delete(item: TrngHist) {
    if (confirm('Are you sure?')) {
      this.service.delete(item.trngId, item.employeeNumber).subscribe({
        next: () => this.loadData(),
        error: (err) => (this.error = err.error?.message || 'Delete failed'),
      });
    }
  }

  resetFilters(): void {
    this.employeeNumberFilter = '';
    this.loadData();
  }
}
