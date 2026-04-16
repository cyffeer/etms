// Training Type
export interface TrngType {
  trngTypeId: number;
  trngTypeNm: string;
  description?: string | null;
}

export interface TrngTypeRequest {
  trngTypeNm: string;
  description?: string | null;
}

// Training Info
export interface TrngInfo {
  trngInfoId: number;
  trngCode: string;
  trngName: string;
  trngTypeCode: string;
  vendorCode?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  location?: string | null;
  active: boolean;
  certificatePath?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface TrngInfoRequest {
  trngCode: string;
  trngName: string;
  trngTypeCode: string;
  vendorCode?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  location?: string | null;
  active?: boolean;
}

// Training History
export interface TrngHist {
  trngId: number;
  employeeNumber: string;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export type TrngHistResponse = TrngHist;

export interface TrngHistRequest {
  employeeNumber: string;
  trngId: number;
}

// Vendor
export interface Vendor {
  vendorId: number;
  vendorCode: string;
  vendorName: string;
  vendorTypeCode: string;
  contactEmail?: string | null;
  contactPhone?: string | null;
  addressLine?: string | null;
  description?: string | null;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface VendorRequest {
  vendorCode: string;
  vendorName: string;
  vendorTypeCode: string;
  contactEmail?: string | null;
  contactPhone?: string | null;
  addressLine?: string | null;
  description?: string | null;
  active?: boolean;
}
