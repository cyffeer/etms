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
