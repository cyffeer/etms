export interface LeaveType {
  leaveTypeId: number;
  leaveTypeCode: string;
  leaveTypeName: string;
  description?: string | null;
  annualEntitlementDays: number;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface LeaveTypeRequest {
  leaveTypeCode: string;
  leaveTypeName: string;
  description?: string | null;
  annualEntitlementDays: number;
  active: boolean;
}

export interface LeaveTypePagedData {
  data: LeaveType[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface LeaveTypeListResult {
  items: LeaveType[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
