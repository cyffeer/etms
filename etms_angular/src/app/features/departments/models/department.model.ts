export interface DepartmentRequest {
  departmentCode: string;
  departmentName: string;
  active: boolean;
}

export interface DepartmentResponse {
  departmentId: number;
  departmentCode: string;
  departmentName: string;
  active: boolean;
  createdAt: string | null; // LocalDateTime
  updatedAt: string | null; // LocalDateTime
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: string[];
}