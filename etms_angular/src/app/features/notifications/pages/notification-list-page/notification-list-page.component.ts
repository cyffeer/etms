import { Component, OnInit } from '@angular/core';
import { NotificationItem } from '../../models/notification.model';
import { NotificationsService } from '../../services/notifications.service';

@Component({
  selector: 'app-notification-list-page',
  templateUrl: './notification-list-page.component.html',
  styleUrls: ['./notification-list-page.component.css']
})
export class NotificationListPageComponent implements OnInit {
  loading = false;
  error = '';
  items: NotificationItem[] = [];
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizes = [5, 10, 20, 50];
  filters = {
    severity: '',
    type: '',
    limit: 100,
  };

  readonly severityOptions = ['', 'info', 'warning', 'danger'];

  constructor(private service: NotificationsService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.service.getAll({
      severity: this.filters.severity,
      type: this.filters.type,
      limit: this.filters.limit,
    }, this.page, this.size).subscribe({
      next: (result) => {
        this.items = result.items;
        this.totalElements = result.totalElements ?? result.items.length;
        this.totalPages = result.totalPages ?? 1;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load notifications.';
        this.loading = false;
      }
    });
  }

  reset(): void {
    this.filters = {
      severity: '',
      type: '',
      limit: 100,
    };
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

  severityBadge(severity: string): string {
    switch ((severity || '').toLowerCase()) {
      case 'danger':
        return 'bg-danger';
      case 'warning':
        return 'bg-warning text-dark';
      default:
        return 'bg-info text-dark';
    }
  }
}
