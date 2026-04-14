export interface MemberTypeApi {
  memberTypeId: number;
  memberTypeCode: string;
  memberTypeName: string;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface MemberType {
  memberTypeId: number;
  memberTypeCode: string;
  memberTypeName: string;
  active: boolean;
}

export interface MemberTypeRequest {
  memberTypeCode: string;
  memberTypeName: string;
  active: boolean;
}
