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
