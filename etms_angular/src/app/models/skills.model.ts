export interface SkillsResponse {
  skillId: number;
  skillCode?: string;
  skillName?: string;
  [key: string]: unknown;
}

export interface SkillsRequest {
  skillCode: string;
  skillName: string;
}