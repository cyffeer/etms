import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { SkillsResponse } from '../../../skills/models/skill.model';
import { SkillsService } from '../../../skills/services/skills.service';
import { SkillLevelResponse } from '../../models/skill-level.model';
import { SkillLevelsService } from '../../services/skill-levels.service';

@Component({
  selector: 'app-skill-level-list-page',
  templateUrl: './skill-level-list-page.component.html'
})
export class SkillLevelListPageComponent implements OnInit {
  rows: SkillLevelResponse[] = [];
  skills: SkillsResponse[] = [];
  loading = false;
  error = '';
  filters = {
    skillLvlId: null as number | null,
    skillId: null as number | null,
    keyword: ''
  };
  readonly canManage = this.authService.hasAnyRole(['ADMIN']);

  constructor(
    private skillLevelsService: SkillLevelsService,
    private skillsService: SkillsService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.skillsService.getSkills().subscribe({
      next: (rows) => {
        this.skills = rows;
      }
    });
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.skillLevelsService.getSkillLevels(this.filters).subscribe({
      next: (rows) => {
        this.rows = rows;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load skill levels.';
        this.loading = false;
      }
    });
  }

  resetFilters(): void {
    this.filters = {
      skillLvlId: null,
      skillId: null,
      keyword: ''
    };
    this.load();
  }

  onDelete(skillLvlId: number): void {
    if (!this.canManage) {
      return;
    }
    if (!confirm('Delete this skill level?')) {
      return;
    }
    this.skillLevelsService.deleteSkillLevel(skillLvlId).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.error = err?.message || 'Failed to delete skill level.';
      }
    });
  }
}
