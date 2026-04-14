export interface DepartmentMemberRequest {
  departmentCode: string;
  employeeNumber: string;
  memberStart: string;
  memberEnd?: string | null;
}

export interface DepartmentMemberResponse {
  deptMemberId: number;
  departmentCode: string;
  employeeNumber: string;
  memberStart: string;
  memberEnd?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}
