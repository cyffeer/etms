import { Component, OnInit } from '@angular/core';
import { SkillsService } from '../../../skills/services/skills.service';
import { SkillLvlResponse, SkillsInventoryResponse } from '../../models/skills-inventory.model';
import { SkillsInventoryService } from '../../services/skills-inventory.service';
import { SkillsResponse } from '../../../skills/models/skill.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-skills-inventory-list-page',
  templateUrl: './skills-inventory-list-page.component.html'
})
export class SkillsInventoryListPageComponent implements OnInit {
  rows: SkillsInventoryResponse[] = [];
  skills: SkillsResponse[] = [];
  levels: SkillLvlResponse[] = [];
  loading = false;
  error = '';
  employeeNumberFilter = '';

  constructor(private service: SkillsInventoryService, private skillsService: SkillsService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    forkJoin({
      rows: this.employeeNumberFilter.trim()
        ? this.service.getByEmployee(this.employeeNumberFilter.trim())
        : this.service.getSkillsInventory(),
      skills: this.skillsService.getSkills()
    }).subscribe({
      next: ({ rows, skills }) => {
        this.rows = rows;
        this.skills = skills;
        const skillIds = [...new Set(rows.map((row) => row.skillId))];
        if (skillIds.length === 0) {
          this.levels = [];
          this.loading = false;
          return;
        }

        forkJoin(skillIds.map((skillId) => this.service.getSkillLevelsBySkill(skillId))).subscribe({
          next: (levelGroups) => {
            this.levels = levelGroups.flat();
            this.loading = false;
          },
          error: () => {
            this.levels = [];
            this.loading = false;
          }
        });
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load skills inventory.';
        this.loading = false;
      }
    });
  }

  getSkillName(skillId: number): string {
    return this.skills.find(s => s.skillId === skillId)?.skillName || String(skillId);
  }

  getLevelName(skillLvlId: number): string {
    return this.levels.find(l => l.skillLvlId === skillLvlId)?.lvlName || String(skillLvlId);
  }

  onDelete(id: number): void {
    if (!confirm('Delete this record?')) return;
    this.service.deleteSkillsInventory(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete record.')
    });
  }

  resetFilters(): void {
    this.employeeNumberFilter = '';
    this.load();
  }
}
