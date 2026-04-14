export interface EmployeeResponse {
  employeeId: number;
  employeeNumber?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  [key: string]: unknown;
}

export interface EmployeeRequest {
  employeeNumber?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  [key: string]: unknown;
}