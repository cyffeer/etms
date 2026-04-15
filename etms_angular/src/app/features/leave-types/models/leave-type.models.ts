export interface LeaveType {
  leaveTypeId: number;
  leaveTypeCode: string;
  leaveTypeName: string;
  description?: string | null;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface LeaveTypeRequest {
  leaveTypeCode: string;
  leaveTypeName: string;
  description?: string | null;
  active: boolean;
}
