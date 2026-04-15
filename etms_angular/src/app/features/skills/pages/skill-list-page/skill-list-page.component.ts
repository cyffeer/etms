import { Component, OnInit } from '@angular/core';
import { SkillsResponse } from '../../models/skill.model';
import { SkillsService } from '../../services/skills.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-skill-list-page',
  templateUrl: './skill-list-page.component.html'
})
export class SkillListPageComponent implements OnInit {
  skills: SkillsResponse[] = [];
  loading = false;
  error = '';
  readonly canManage = this.authService.hasAnyRole(['ADMIN']);

  constructor(private skillsService: SkillsService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.skillsService.getSkills().subscribe({
      next: (data) => {
        this.skills = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load skills.';
        this.loading = false;
      }
    });
  }

  onDelete(skillId: number): void {
    if (!this.canManage) {
      return;
    }
    if (!confirm('Delete this skill?')) return;
    this.skillsService.deleteSkill(skillId).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete skill.')
    });
  }
}
