import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MemberTypeRequest } from '../../models/member-type.models';
import { MemberTypeService } from '../../services/member-type.service';

@Component({
  selector: 'app-member-type-form-page',
  templateUrl: './member-type-form-page.component.html',
  styleUrls: ['./member-type-form-page.component.css']
})
export class MemberTypeFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private memberTypeId = 0;

  form = this.fb.group({
    memberTypeCode: ['', [Validators.required, Validators.maxLength(30)]],
    memberTypeName: ['', [Validators.required, Validators.maxLength(150)]],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private service: MemberTypeService
  ) {}

  ngOnInit(): void {
    this.memberTypeId = Number(this.route.snapshot.paramMap.get('memberTypeId'));
    this.isEdit = !!this.memberTypeId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.service.getById(this.memberTypeId).subscribe({
      next: (item) => {
        this.form.patchValue(item);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load member type.';
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
    const payload: MemberTypeRequest = {
      memberTypeCode: raw.memberTypeCode!,
      memberTypeName: raw.memberTypeName!,
      active: raw.active ?? true
    };

    const request$ = this.isEdit
      ? this.service.update(this.memberTypeId, payload)
      : this.service.create(payload);

    this.loading = true;
    this.error = '';
    request$.subscribe({
      next: () => this.router.navigate(['/member-types']),
      error: (err) => {
        this.error = err?.message || 'Failed to save member type.';
        this.loading = false;
      }
    });
  }
}
