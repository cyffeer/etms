import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LeaveTypeRequest } from '../../models/leave-type.models';
import { LeaveTypeService } from '../../services/leave-type.service';

@Component({
  selector: 'app-leave-type-form-page',
  templateUrl: './leave-type-form-page.component.html',
})
export class LeaveTypeFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private leaveTypeId = 0;

  form = this.fb.group({
    leaveTypeCode: ['', [Validators.required, Validators.maxLength(30)]],
    leaveTypeName: ['', [Validators.required, Validators.maxLength(120)]],
    description: ['', Validators.maxLength(255)],
    active: [true],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly leaveTypeService: LeaveTypeService
  ) {}

  ngOnInit(): void {
    this.leaveTypeId = Number(this.route.snapshot.paramMap.get('leaveTypeId'));
    this.isEdit = !!this.leaveTypeId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.leaveTypeService.getById(this.leaveTypeId).subscribe({
      next: (item) => {
        this.form.patchValue(item);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load leave type.';
        this.loading = false;
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: LeaveTypeRequest = {
      leaveTypeCode: raw.leaveTypeCode?.trim() || '',
      leaveTypeName: raw.leaveTypeName?.trim() || '',
      description: raw.description?.trim() || null,
      active: raw.active ?? true,
    };

    this.loading = true;
    this.error = '';
    const request$ = this.isEdit
      ? this.leaveTypeService.update(this.leaveTypeId, payload)
      : this.leaveTypeService.create(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/leave-types']),
      error: (err) => {
        this.error = err?.message || 'Failed to save leave type.';
        this.loading = false;
      },
    });
  }
}
