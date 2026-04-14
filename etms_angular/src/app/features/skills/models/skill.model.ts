export interface SkillsRequest {
  skillCode: string;
  skillName: string;
  description: string | null;
  active: boolean;
}

export interface SkillsResponse {
  skillId: number;
  skillCode: string;
  skillName: string;
  description: string | null;
  active: boolean;
  createdAt: string | null;
  updatedAt: string | null;
}