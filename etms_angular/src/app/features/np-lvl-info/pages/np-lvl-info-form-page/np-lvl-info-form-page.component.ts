import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NpLvlInfoRequest } from '../../np-lvl-info.model';
import { NpLvlInfoService } from '../../services/np-lvl-info.service';

@Component({
  selector: 'app-np-lvl-info-form-page',
  templateUrl: './np-lvl-info-form-page.component.html',
  styleUrls: ['./np-lvl-info-form-page.component.css']
})
export class NpLvlInfoFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private npLvlInfoId = 0;

  form = this.fb.group({
    npLvlInfoCode: ['', [Validators.required, Validators.maxLength(30)]],
    npLvlInfoName: ['', [Validators.required, Validators.maxLength(150)]],
    npTypeCode: ['', [Validators.required, Validators.maxLength(30)]],
    validFrom: [''],
    validTo: [''],
    allowanceAmount: [''],
    allowanceCurrency: ['', Validators.maxLength(10)],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private service: NpLvlInfoService
  ) {}

  ngOnInit(): void {
    this.npLvlInfoId = Number(this.route.snapshot.paramMap.get('npLvlInfoId'));
    this.isEdit = !!this.npLvlInfoId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.service.getById(this.npLvlInfoId).subscribe({
      next: (item) => {
        this.form.patchValue({
          npLvlInfoCode: item.npLvlInfoCode,
          npLvlInfoName: item.npLvlInfoName,
          npTypeCode: item.npTypeCode,
          validFrom: item.validFrom?.slice(0, 10) ?? '',
          validTo: item.validTo?.slice(0, 10) ?? '',
          allowanceAmount: item.allowanceAmount != null ? String(item.allowanceAmount) : '',
          allowanceCurrency: item.allowanceCurrency ?? '',
          active: item.active
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load NP level info.';
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
    const payload: NpLvlInfoRequest = {
      npLvlInfoCode: raw.npLvlInfoCode!,
      npLvlInfoName: raw.npLvlInfoName!,
      npTypeCode: raw.npTypeCode!,
      validFrom: raw.validFrom || null,
      validTo: raw.validTo || null,
      allowanceAmount: raw.allowanceAmount === '' ? null : Number(raw.allowanceAmount),
      allowanceCurrency: raw.allowanceCurrency || null,
      active: raw.active ?? true
    };

    const request$ = this.isEdit
      ? this.service.update(this.npLvlInfoId, payload)
      : this.service.create(payload);

    this.loading = true;
    this.error = '';
    request$.subscribe({
      next: () => this.router.navigate(['/np-lvl-info']),
      error: (err) => {
        this.error = err?.message || 'Failed to save NP level info.';
        this.loading = false;
      }
    });
  }
}
