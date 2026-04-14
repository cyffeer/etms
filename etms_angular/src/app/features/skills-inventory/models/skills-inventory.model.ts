export interface SkillsInventoryRequest {
  employeeNumber: string;
  skillId: number;
  skillLvlId: number;
}

export interface SkillsInventoryResponse {
  skillsInventoryId: number;
  employeeNumber: string;
  skillId: number;
  skillLvlId: number;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface SkillLvlResponse {
  skillLvlId: number;
  skillId: number;
  lvlCode: string;
  lvlName: string;
  lvlRank: number | null;
  active: boolean;
  createdAt: string | null;
  updatedAt: string | null;
}