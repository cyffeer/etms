import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NpTypeService } from '../../services/np-type.service';

@Component({
  selector: 'app-nihongo-type-form-page',
  templateUrl: './nihongo-type-form-page.component.html',
})
export class NihongoTypeFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private npTypeId = 0;

  form = this.fb.group({
    npTypeCode: ['', [Validators.required, Validators.maxLength(30)]],
    npTypeName: ['', [Validators.required, Validators.maxLength(150)]],
    active: [true],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly npTypeService: NpTypeService
  ) {}

  ngOnInit(): void {
    this.npTypeId = Number(this.route.snapshot.paramMap.get('npTypeId'));
    this.isEdit = !!this.npTypeId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.npTypeService.getById(this.npTypeId).subscribe({
      next: (item) => {
        this.form.patchValue(item);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load Nihongo type.';
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
    const payload = {
      npTypeCode: raw.npTypeCode?.trim() || '',
      npTypeName: raw.npTypeName?.trim() || '',
      active: raw.active ?? true,
    };

    this.loading = true;
    this.error = '';
    const request$ = this.isEdit
      ? this.npTypeService.update(this.npTypeId, payload)
      : this.npTypeService.create(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/nihongo/types']),
      error: (err) => {
        this.error = err?.message || 'Failed to save Nihongo type.';
        this.loading = false;
      },
    });
  }
}
