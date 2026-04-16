export interface SkillLevelRequest {
  skillId: number;
  lvlCode: string;
  lvlName: string;
  lvlRank?: number | null;
  active: boolean;
}

export interface SkillLevelResponse {
  skillLvlId: number;
  skillId: number;
  skillCode?: string | null;
  skillName?: string | null;
  lvlCode: string;
  lvlName: string;
  lvlRank?: number | null;
  active: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface SkillLevelPagedData {
  data: SkillLevelResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface SkillLevelListResult {
  items: SkillLevelResponse[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
