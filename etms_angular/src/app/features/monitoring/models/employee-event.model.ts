export interface EmployeeEventRequest {
  employeeNumber: string;
  eventType: string;
  title: string;
  description?: string | null;
  departmentCode?: string | null;
  referenceCode?: string | null;
  effectiveDate: string;
  endDate?: string | null;
  status: string;
}

export interface EmployeeEventResponse {
  employeeEventId: number;
  employeeNumber: string;
  employeeName?: string | null;
  eventType: string;
  title: string;
  description?: string | null;
  departmentCode?: string | null;
  departmentName?: string | null;
  referenceCode?: string | null;
  effectiveDate: string;
  endDate?: string | null;
  status: string;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface EmployeeEventPagedData {
  data: EmployeeEventResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface EmployeeEventListResult {
  items: EmployeeEventResponse[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}

export interface EmployeeEventSummaryItem {
  eventType: string;
  label: string;
  count: number;
}

export interface EmployeeEventSummaryResponse {
  totalCount: number;
  activeCount: number;
  pendingCount: number;
  closedCount: number;
  categories: EmployeeEventSummaryItem[];
}
