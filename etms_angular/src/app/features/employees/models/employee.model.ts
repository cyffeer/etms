export interface Employee {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  departmentId?: number | null;
  departmentName?: string | null;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateEmployeeRequest {
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  departmentId?: number | null;
  active: boolean;
}

export interface UpdateEmployeeRequest {
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  departmentId?: number | null;
  active: boolean;
}

export interface EmployeeRequest {
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  hireDate: string | null; // LocalDate -> 'yyyy-MM-dd'
  active: boolean;
}

export interface EmployeeResponse {
  employeeId: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  hireDate: string | null;   // LocalDate
  active: boolean;
  createdAt: string | null;  // LocalDateTime
  updatedAt: string | null;  // LocalDateTime
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: string[];
}

export interface EmployeePagedData {
  data: EmployeeResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface EmployeeListResult {
  items: EmployeeResponse[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}