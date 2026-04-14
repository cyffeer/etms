export interface NpLvlInfoRequest {
  npLvlInfoCode: string;
  npLvlInfoName: string;
  npTypeCode: string;
  validFrom?: string | null;
  validTo?: string | null;
  allowanceAmount?: number | null;
  allowanceCurrency?: string | null;
  active: boolean;
}

export interface NpLvlInfoResponse {
  npLvlInfoId: number;
  npLvlInfoCode: string;
  npLvlInfoName: string;
  npTypeCode: string;
  validFrom?: string | null;
  validTo?: string | null;
  allowanceAmount?: number | null;
  allowanceCurrency?: string | null;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}
