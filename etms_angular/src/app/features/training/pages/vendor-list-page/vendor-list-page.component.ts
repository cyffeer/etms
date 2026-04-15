import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { Vendor } from '../../models/training.models';
import { VendorService } from '../../services/vendor.service';

@Component({
  selector: 'app-vendor-list-page',
  templateUrl: './vendor-list-page.component.html',
})
export class VendorListPageComponent implements OnInit {
  loading = false;
  error = '';
  keyword = '';
  items: Vendor[] = [];
  readonly canManage = this.authService.hasAnyRole(['ADMIN']);

  constructor(
    private readonly vendorService: VendorService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.vendorService.getAll().subscribe({
      next: (items) => {
        const keyword = this.keyword.trim().toLowerCase();
        this.items = keyword
          ? items.filter((item) =>
              item.vendorCode.toLowerCase().includes(keyword)
              || item.vendorName.toLowerCase().includes(keyword))
          : items;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load vendors.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.keyword = '';
    this.load();
  }

  delete(item: Vendor): void {
    if (!confirm(`Delete vendor ${item.vendorName}?`)) {
      return;
    }

    this.vendorService.delete(item.vendorId).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to delete vendor.';
      },
    });
  }
}
