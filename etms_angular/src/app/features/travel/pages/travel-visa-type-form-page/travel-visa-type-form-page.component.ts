import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VisaTypeRequest } from '../../models/travel.models';
import { VisaTypeService } from '../../services/visa-type.service';

@Component({
  selector: 'app-travel-visa-type-form-page',
  templateUrl: './travel-visa-type-form-page.component.html',
})
export class TravelVisaTypeFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private visaTypeId = 0;

  form = this.fb.group({
    visaTypeCode: ['', [Validators.required, Validators.maxLength(30)]],
    visaTypeName: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', Validators.maxLength(255)],
    active: [true],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly visaTypeService: VisaTypeService
  ) {}

  ngOnInit(): void {
    this.visaTypeId = Number(this.route.snapshot.paramMap.get('visaTypeId'));
    this.isEdit = !!this.visaTypeId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.visaTypeService.getById(this.visaTypeId).subscribe({
      next: (item) => {
        this.form.patchValue(item);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load visa type.';
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
    const payload: VisaTypeRequest = {
      visaTypeCode: raw.visaTypeCode?.trim() || '',
      visaTypeName: raw.visaTypeName?.trim() || '',
      description: raw.description?.trim() || null,
      active: raw.active ?? true,
    };

    this.loading = true;
    this.error = '';
    const request$ = this.isEdit
      ? this.visaTypeService.update(this.visaTypeId, payload)
      : this.visaTypeService.create(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/travel/visa-types']),
      error: (err) => {
        this.error = err?.message || 'Failed to save visa type.';
        this.loading = false;
      },
    });
  }
}
