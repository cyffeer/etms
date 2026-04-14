export interface AttendanceRequest {
  employeeNumber: string;
  attendanceDate: string;
  timeIn?: string | null;
  timeOut?: string | null;
  status?: string | null;
}

export interface AttendanceResponse {
  attendanceRecordId: number;
  employeeNumber: string;
  attendanceDate: string;
  timeIn?: string | null;
  timeOut?: string | null;
  status?: string | null;
}
