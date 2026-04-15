import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VendorRequest } from '../../models/training.models';
import { VendorService } from '../../services/vendor.service';

@Component({
  selector: 'app-vendor-form-page',
  templateUrl: './vendor-form-page.component.html',
})
export class VendorFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private vendorId = 0;

  form = this.fb.group({
    vendorCode: ['', [Validators.required, Validators.maxLength(30)]],
    vendorName: ['', [Validators.required, Validators.maxLength(150)]],
    vendorTypeCode: ['', [Validators.required, Validators.maxLength(30)]],
    contactEmail: ['', [Validators.email, Validators.maxLength(150)]],
    contactPhone: ['', Validators.maxLength(30)],
    addressLine: ['', Validators.maxLength(255)],
    active: [true],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly vendorService: VendorService
  ) {}

  ngOnInit(): void {
    this.vendorId = Number(this.route.snapshot.paramMap.get('vendorId'));
    this.isEdit = !!this.vendorId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.vendorService.getById(this.vendorId).subscribe({
      next: (item) => {
        this.form.patchValue(item);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load vendor.';
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
    const payload: VendorRequest = {
      vendorCode: raw.vendorCode?.trim() || '',
      vendorName: raw.vendorName?.trim() || '',
      vendorTypeCode: raw.vendorTypeCode?.trim() || '',
      contactEmail: raw.contactEmail?.trim() || null,
      contactPhone: raw.contactPhone?.trim() || null,
      addressLine: raw.addressLine?.trim() || null,
      active: raw.active ?? true,
    };

    this.loading = true;
    this.error = '';
    const request$ = this.isEdit
      ? this.vendorService.update(this.vendorId, payload)
      : this.vendorService.create(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/training/vendors']),
      error: (err) => {
        this.error = err?.message || 'Failed to save vendor.';
        this.loading = false;
      },
    });
  }
}
