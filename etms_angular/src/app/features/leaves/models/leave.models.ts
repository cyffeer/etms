export interface LeaveApi {
  leaveRecordId: number;
  employeeNumber: string;
  leaveType: string;
  startDate: string;   // yyyy-MM-dd from backend
  endDate: string;     // yyyy-MM-dd from backend
  status?: string | null;
  remarks?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface LeavePagedData {
  data: LeaveApi[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface LeaveListResult {
  items: Leave[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}

export interface LeaveBalanceApi {
  employeeNumber: string;
  employeeName: string;
  year: number;
  leaveTypeCode: string;
  leaveTypeName: string;
  annualEntitlementDays: number;
  approvedDaysUsed: number;
  pendingDays: number;
  remainingDays: number;
}

export interface LeaveBalance {
  employeeNumber: string;
  employeeName: string;
  year: number;
  leaveTypeCode: string;
  leaveTypeName: string;
  annualEntitlementDays: number;
  approvedDaysUsed: number;
  pendingDays: number;
  remainingDays: number;
}

export interface Leave {
  leaveRecordId: number;
  employeeNumber: string;
  leaveType: string;
  startDate: string;   // kept as ISO date string for UI + date input
  endDate: string;
  status?: string | null;
  remarks?: string | null;
}

export interface LeaveRequest {
  employeeNumber: string;
  leaveType: string;
  startDate: string;   // yyyy-MM-dd
  endDate?: string | null;     // yyyy-MM-dd
  status?: string | null;
  remarks?: string | null;
}
