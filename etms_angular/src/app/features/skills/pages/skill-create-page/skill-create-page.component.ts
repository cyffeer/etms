import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SkillsRequest } from '../../models/skill.model';
import { SkillsService } from '../../services/skills.service';

@Component({
  selector: 'app-skill-create-page',
  templateUrl: './skill-create-page.component.html'
})
export class SkillCreatePageComponent {
  loading = false;
  error = '';

  form = this.fb.group({
    skillCode: ['', [Validators.required, Validators.maxLength(50)]],
    skillName: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', [Validators.maxLength(255)]],
    active: [true]
  });

  constructor(private fb: FormBuilder, private skillsService: SkillsService, private router: Router) {}

  submit(): void {
    if (this.form.invalid) return this.form.markAllAsTouched();

    const raw = this.form.getRawValue();
    const payload: SkillsRequest = {
      skillCode: raw.skillCode!,
      skillName: raw.skillName!,
      description: raw.description || null,
      active: raw.active ?? true
    };

    this.loading = true;
    this.skillsService.createSkill(payload).subscribe({
      next: () => this.router.navigate(['/skills']),
      error: (err) => {
        this.error = err?.message || 'Failed to create skill.';
        this.loading = false;
      }
    });
  }
}