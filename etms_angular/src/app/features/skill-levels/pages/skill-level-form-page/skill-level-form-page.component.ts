import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SkillsResponse } from '../../../skills/models/skill.model';
import { SkillsService } from '../../../skills/services/skills.service';
import { SkillLevelRequest } from '../../models/skill-level.model';
import { SkillLevelsService } from '../../services/skill-levels.service';

@Component({
  selector: 'app-skill-level-form-page',
  templateUrl: './skill-level-form-page.component.html'
})
export class SkillLevelFormPageComponent implements OnInit {
  skills: SkillsResponse[] = [];
  loading = false;
  error = '';
  isEdit = false;
  private skillLvlId = 0;

  form = this.fb.group({
    skillId: [null as number | null, Validators.required],
    lvlCode: ['', [Validators.required, Validators.maxLength(30)]],
    lvlName: ['', [Validators.required, Validators.maxLength(100)]],
    lvlRank: [null as number | null],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private skillLevelsService: SkillLevelsService,
    private skillsService: SkillsService
  ) {}

  ngOnInit(): void {
    this.skillsService.getSkills().subscribe({
      next: (rows) => {
        this.skills = rows;
      }
    });

    this.skillLvlId = Number(this.route.snapshot.paramMap.get('skillLvlId'));
    this.isEdit = !!this.skillLvlId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.skillLevelsService.getSkillLevelById(this.skillLvlId).subscribe({
      next: (row) => {
        this.form.patchValue({
          skillId: row.skillId,
          lvlCode: row.lvlCode,
          lvlName: row.lvlName,
          lvlRank: row.lvlRank ?? null,
          active: row.active
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load skill level.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: SkillLevelRequest = {
      skillId: raw.skillId!,
      lvlCode: raw.lvlCode!,
      lvlName: raw.lvlName!,
      lvlRank: raw.lvlRank,
      active: raw.active ?? true
    };

    this.loading = true;
    this.error = '';

    const request$ = this.isEdit
      ? this.skillLevelsService.updateSkillLevel(this.skillLvlId, payload)
      : this.skillLevelsService.createSkillLevel(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/skill-levels']),
      error: (err) => {
        this.error = err?.message || 'Failed to save skill level.';
        this.loading = false;
      }
    });
  }
}
