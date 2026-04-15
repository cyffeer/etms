import { Component, OnInit } from '@angular/core';
import { DashboardCard, DashboardNotification, DashboardTrendPoint } from '../models/dashboard.model';
import { DashboardService } from '../services/dashboard.service';

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.css']
})
export class DashboardPageComponent implements OnInit {
  loading = false;
  error = '';
  cards: DashboardCard[] = [];
  trainingsPerMonth: DashboardTrendPoint[] = [];
  attendanceTrends: DashboardTrendPoint[] = [];
  notifications: DashboardNotification[] = [];

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.loading = true;
    this.error = '';

    this.dashboardService.getSummary().subscribe({
      next: (summary) => {
        this.cards = summary.cards ?? [];
        this.trainingsPerMonth = summary.trainingsPerMonth ?? [];
        this.attendanceTrends = summary.attendanceTrends ?? [];
        this.notifications = summary.notifications ?? [];
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load dashboard summary.';
        this.loading = false;
      }
    });
  }

  maxValue(points: DashboardTrendPoint[]): number {
    return Math.max(...points.map((point) => point.value), 1);
  }
}
