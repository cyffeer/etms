export interface NpType {
  npTypeId: number;
  npTypeCode: string;
  npTypeName: string;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface NpTestHist {
  npTestHistId: number;
  npLvlInfoCode: string;
  testDate?: string | null;
  testCenter?: string | null;
  testLevel?: string | null;
  score?: number | null;
  passed?: boolean | null;
  remarks?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface NpTestEmpHist {
  npTestEmpHistId: number;
  employeeNumber: string;
  npTestHistId: number;
  npTypeCode?: string | null;
  npTypeName?: string | null;
  npLvlInfoCode?: string | null;
  npLvlInfoName?: string | null;
  policyRank?: number | null;
  allowanceStartDate?: string | null;
  allowanceEndDate?: string | null;
  effectiveAllowanceEndDate?: string | null;
  expired?: boolean | null;
  firstTimePass?: boolean | null;
  passFlag?: boolean | null;
  takeFlag?: boolean | null;
  points?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}
