import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TrngInfoService } from '../../services/trng-info.service';

@Component({
  selector: 'app-trng-info-form-page',
  templateUrl: './trng-info-form-page.component.html',
})
export class TrngInfoFormPageComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  saving = false;
  error = '';
  certificateFile: File | null = null;
  certificateMessage = '';
  isEdit = false;
  id?: number;

  constructor(
    private fb: FormBuilder,
    private service: TrngInfoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      trngCode: ['', [Validators.required, Validators.maxLength(30)]],
      trngName: ['', [Validators.required, Validators.maxLength(150)]],
      trngTypeCode: ['', Validators.required],
      vendorCode: [''],
      startDate: [''],
      endDate: [''],
      location: ['', Validators.maxLength(255)],
      active: [true],
    });

    this.id = this.route.snapshot.params['trngInfoId'];
    if (this.id) {
      this.isEdit = true;
      this.loadData(this.id);
    }
  }

  loadData(id: number) {
    this.loading = true;
    this.service.getById(id).subscribe({
      next: (data) => {
      this.form.patchValue(data);
        this.certificateMessage = data.certificatePath ? 'Certificate uploaded' : '';
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load';
        this.loading = false;
      },
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.saving = true;
    this.error = '';
    this.certificateMessage = '';
    const req = this.form.value;

    const call = this.isEdit
      ? this.service.update(this.id!, req)
      : this.service.create(req);

    call.subscribe({
      next: (saved) => {
        if (!this.certificateFile) {
          this.router.navigate(['training/info']);
          return;
        }

        this.service.uploadCertificate(saved.trngInfoId, this.certificateFile).subscribe({
          next: () => this.router.navigate(['training/info']),
          error: (err) => {
            this.error = err.error?.message || 'Training saved, but certificate upload failed';
            this.saving = false;
          }
        });
      },
      error: (err) => {
        this.error = err.error?.message || 'Save failed';
        this.saving = false;
      },
    });
  }

  onCertificateSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.certificateFile = input.files && input.files.length ? input.files[0] : null;
  }

  cancel() {
    this.router.navigate(['training/info']);
  }
}
