import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { VisaType } from '../../models/travel.models';
import { VisaTypeService } from '../../services/visa-type.service';

@Component({
  selector: 'app-travel-visa-type-list-page',
  templateUrl: './travel-visa-type-list-page.component.html',
})
export class TravelVisaTypeListPageComponent implements OnInit {
  loading = false;
  error = '';
  keyword = '';
  rows: VisaType[] = [];
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'HR']);

  constructor(
    private readonly visaTypeService: VisaTypeService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.visaTypeService.getAll().subscribe({
      next: (rows) => {
        const keyword = this.keyword.trim().toLowerCase();
        this.rows = keyword
          ? rows.filter((row) =>
              row.visaTypeCode.toLowerCase().includes(keyword)
              || row.visaTypeName.toLowerCase().includes(keyword))
          : rows;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load visa types.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.keyword = '';
    this.load();
  }

  onDelete(id: number): void {
    if (!confirm('Delete this visa type?')) {
      return;
    }

    this.visaTypeService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to delete visa type.';
      },
    });
  }
}
