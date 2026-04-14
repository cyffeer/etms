import { Component, OnInit } from '@angular/core';
import { NpLvlInfoResponse } from '../../np-lvl-info.model';
import { NpLvlInfoService } from '../../services/np-lvl-info.service';

@Component({
  selector: 'app-np-lvl-info-list-page',
  templateUrl: './np-lvl-info-list-page.component.html',
  styleUrls: ['./np-lvl-info-list-page.component.css']
})
export class NpLvlInfoListPageComponent implements OnInit {
  rows: NpLvlInfoResponse[] = [];
  loading = false;
  error = '';

  constructor(private service: NpLvlInfoService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.service.getAll().subscribe({
      next: (data) => {
        this.rows = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load NP level info.';
        this.loading = false;
      }
    });
  }

  onDelete(id: number): void {
    if (!confirm('Delete this NP level info record?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete NP level info.')
    });
  }
}
