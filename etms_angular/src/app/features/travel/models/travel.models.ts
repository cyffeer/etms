export interface PassportInfo {
  passportInfoId: number;
  passportNumber: string;
  employeeNumber: string;
  issuedDate?: string | null;
  expiryDate?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface VisaInfo {
  visaInfoId: number;
  employeeNumber: string;
  visaTypeId: number;
  issuedDate?: string | null;
  expiryDate?: string | null;
  cancelFlag?: boolean | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface VisaType {
  visaTypeId: number;
  visaTypeCode: string;
  visaTypeName: string;
  description?: string | null;
  active: boolean;
}

export interface VisaTypeRequest {
  visaTypeCode: string;
  visaTypeName: string;
  description?: string | null;
  active?: boolean;
}
