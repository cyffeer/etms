export interface NotificationItem {
  type: string;
  title: string;
  message: string;
  route: string;
  severity: string;
}

export interface NotificationPagedData {
  data: NotificationItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface NotificationListResult {
  items: NotificationItem[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
