import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { TrngInfoService } from '../../services/trng-info.service';
import { TrngInfo } from '../../models/training.models';

@Component({
  selector: 'app-trng-info-list-page',
  templateUrl: './trng-info-list-page.component.html',
})
export class TrngInfoListPageComponent implements OnInit {
  items: TrngInfo[] = [];
  loading = false;
  error = '';
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);

  constructor(
    private service: TrngInfoService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';
    this.service.getAll().subscribe({
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

  create() {
    this.router.navigate(['training/info/new']);
  }

  edit(id: number) {
    this.router.navigate([`training/info/${id}/edit`]);
  }

  delete(id: number) {
    if (confirm('Are you sure?')) {
      this.service.delete(id).subscribe({
        next: () => this.loadData(),
        error: (err) => (this.error = err.error?.message || 'Delete failed'),
      });
    }
  }
}
