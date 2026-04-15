export interface DepartmentMemberRequest {
  departmentCode: string;
  employeeNumber: string;
  memberTypeId?: number | null;
  memberStart: string;
  memberEnd?: string | null;
}

export interface DepartmentMemberResponse {
  deptMemberId: number;
  departmentCode: string;
  departmentName?: string | null;
  employeeNumber: string;
  employeeName?: string | null;
  memberTypeId?: number | null;
  memberTypeCode?: string | null;
  memberTypeName?: string | null;
  memberStart: string;
  memberEnd?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}
