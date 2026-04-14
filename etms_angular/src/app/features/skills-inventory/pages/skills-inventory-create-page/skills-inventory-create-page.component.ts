import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SkillsService } from '../../../skills/services/skills.service';
import { SkillsResponse } from '../../../skills/models/skill.model';
import { SkillLvlResponse, SkillsInventoryRequest } from '../../models/skills-inventory.model';
import { SkillsInventoryService } from '../../services/skills-inventory.service';

@Component({
  selector: 'app-skills-inventory-create-page',
  templateUrl: './skills-inventory-create-page.component.html'
})
export class SkillsInventoryCreatePageComponent implements OnInit {
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
    private router: Router,
    private service: SkillsInventoryService,
    private skillsService: SkillsService
  ) {}

  ngOnInit(): void {
    this.skillsService.getSkills().subscribe({ next: (data) => (this.skills = data) });
  }

  onSkillChange(): void {
    const skillId = this.form.value.skillId;
    this.levels = [];
    this.form.patchValue({ skillLvlId: null });
    if (!skillId) return;

    this.service.getSkillLevelsBySkill(skillId).subscribe({
      next: (data) => (this.levels = data),
      error: () => (this.levels = [])
    });
  }

  submit(): void {
    if (this.form.invalid) return this.form.markAllAsTouched();

    const raw = this.form.getRawValue();
    const payload: SkillsInventoryRequest = {
      employeeNumber: raw.employeeNumber!,
      skillId: raw.skillId!,
      skillLvlId: raw.skillLvlId!
    };

    this.loading = true;
    this.service.createSkillsInventory(payload).subscribe({
      next: () => this.router.navigate(['/skills-inventory']),
      error: (err) => {
        this.error = err?.message || 'Failed to assign skill.';
        this.loading = false;
      }
    });
  }
}