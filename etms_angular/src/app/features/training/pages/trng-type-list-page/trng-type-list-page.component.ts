import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { TrngTypeService } from '../../services/trng-type.service';
import { TrngType } from '../../models/training.models';

@Component({
  selector: 'app-trng-type-list-page',
  templateUrl: './trng-type-list-page.component.html',
})
export class TrngTypeListPageComponent implements OnInit {
  items: TrngType[] = [];
  loading = false;
  error = '';
  readonly canManage = this.authService.hasAnyRole(['ADMIN']);

  constructor(
    private service: TrngTypeService,
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
        this.error = err.error?.message || 'Failed to load training types';
        this.loading = false;
      },
    });
  }

  create() {
    this.router.navigate(['training/types/new']);
  }

  edit(id: number) {
    this.router.navigate([`training/types/${id}/edit`]);
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
