import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SkillsRequest } from '../../models/skill.model';
import { SkillsService } from '../../services/skills.service';

@Component({
  selector: 'app-skill-edit-page',
  templateUrl: './skill-edit-page.component.html'
})
export class SkillEditPageComponent implements OnInit {
  loading = false;
  error = '';
  private skillId = 0;

  form = this.fb.group({
    skillCode: ['', [Validators.required, Validators.maxLength(50)]],
    skillName: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', [Validators.maxLength(255)]],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private skillsService: SkillsService
  ) {}

  ngOnInit(): void {
    this.skillId = Number(this.route.snapshot.paramMap.get('skillId'));
    if (!this.skillId) return;

    this.loading = true;
    this.skillsService.getSkillById(this.skillId).subscribe({
      next: (s) => {
        this.form.patchValue(s);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load skill.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid || !this.skillId) return this.form.markAllAsTouched();

    const raw = this.form.getRawValue();
    const payload: SkillsRequest = {
      skillCode: raw.skillCode!,
      skillName: raw.skillName!,
      description: raw.description || null,
      active: raw.active ?? true
    };

    this.skillsService.updateSkill(this.skillId, payload).subscribe({
      next: () => this.router.navigate(['/skills']),
      error: (err) => (this.error = err?.message || 'Failed to update skill.')
    });
  }
}