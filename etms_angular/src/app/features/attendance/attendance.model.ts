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
  employeeName?: string | null;
  designation?: string | null;
  officeName?: string | null;
  attendanceDate: string;
  timeIn?: string | null;
  timeOut?: string | null;
  status?: string | null;
}

export interface AttendancePagedData {
  data: AttendanceResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AttendanceListResult {
  items: AttendanceResponse[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
