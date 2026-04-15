export interface DashboardCard {
  label: string;
  count: number;
  route: string;
}

export interface DashboardTrendPoint {
  label: string;
  value: number;
}

export interface DashboardNotification {
  type: string;
  title: string;
  message: string;
  route: string;
  severity: string;
}

export interface DashboardSummaryResponse {
  cards: DashboardCard[];
  trainingsPerMonth: DashboardTrendPoint[];
  attendanceTrends: DashboardTrendPoint[];
  notifications: DashboardNotification[];
}
