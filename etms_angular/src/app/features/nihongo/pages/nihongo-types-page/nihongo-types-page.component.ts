import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { NpType } from '../../models/nihongo.models';
import { NpTypeService } from '../../services/np-type.service';

@Component({
  selector: 'app-nihongo-types-page',
  templateUrl: './nihongo-types-page.component.html',
})
export class NihongoTypesPageComponent implements OnInit {
  loading = false;
  error = '';
  keyword = '';
  rows: NpType[] = [];
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'HR']);

  constructor(
    private readonly npTypeService: NpTypeService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.npTypeService.getAll().subscribe({
      next: (rows) => {
        const keyword = this.keyword.trim().toLowerCase();
        const sortedRows = rows.sort((left, right) => left.npTypeCode.localeCompare(right.npTypeCode));
        this.rows = keyword
          ? sortedRows.filter((row) =>
              row.npTypeCode.toLowerCase().includes(keyword)
              || row.npTypeName.toLowerCase().includes(keyword))
          : sortedRows;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load Nihongo type master data.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.keyword = '';
    this.load();
  }

  onDelete(id: number): void {
    if (!confirm('Delete this Nihongo type?')) {
      return;
    }

    this.npTypeService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to delete Nihongo type.';
      },
    });
  }
}
