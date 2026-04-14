import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SkillsService } from '../../../skills/services/skills.service';
import { SkillsResponse } from '../../../skills/models/skill.model';
import { SkillLvlResponse, SkillsInventoryRequest } from '../../models/skills-inventory.model';
import { SkillsInventoryService } from '../../services/skills-inventory.service';

@Component({
  selector: 'app-skills-inventory-edit-page',
  templateUrl: './skills-inventory-edit-page.component.html'
})
export class SkillsInventoryEditPageComponent implements OnInit {
  private id = 0;
  skills: SkillsResponse[] = [];
  levels: SkillLvlResponse[] = [];
  loading = false;
  error = '';

  form = this.fb.group({
    employeeNumber: ['', [Validators.required, Validators.maxLength(30)]],
    skillId: [null as number | null, [Validators.required]],
    skillLvlId: [null as number | null, [Validators.required]]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private service: SkillsInventoryService,
    private skillsService: SkillsService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('skillsInventoryId'));
    if (!this.id) return;

    this.skillsService.getSkills().subscribe({ next: (s) => (this.skills = s) });

    this.loading = true;
    this.service.getSkillsInventoryById(this.id).subscribe({
      next: (row) => {
        this.form.patchValue({
          employeeNumber: row.employeeNumber,
          skillId: row.skillId,
          skillLvlId: null
        });
        this.service.getSkillLevelsBySkill(row.skillId).subscribe({
          next: (levels) => {
            this.levels = levels;
            this.form.patchValue({ skillLvlId: row.skillLvlId });
            this.loading = false;
          },
          error: () => (this.loading = false)
        });
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load record.';
        this.loading = false;
      }
    });
  }

  onSkillChange(): void {
    const skillId = this.form.value.skillId;
    this.levels = [];
    this.form.patchValue({ skillLvlId: null });
    if (!skillId) return;
    this.service.getSkillLevelsBySkill(skillId).subscribe({ next: (data) => (this.levels = data) });
  }

  submit(): void {
    if (this.form.invalid || !this.id) return this.form.markAllAsTouched();

    const raw = this.form.getRawValue();
    const payload: SkillsInventoryRequest = {
      employeeNumber: raw.employeeNumber!,
      skillId: raw.skillId!,
      skillLvlId: raw.skillLvlId!
    };

    this.service.updateSkillsInventory(this.id, payload).subscribe({
      next: () => this.router.navigate(['/skills-inventory']),
      error: (err) => (this.error = err?.message || 'Failed to update record.')
    });
  }
}