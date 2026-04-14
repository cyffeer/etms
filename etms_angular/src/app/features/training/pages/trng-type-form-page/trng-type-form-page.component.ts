import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TrngTypeService } from '../../services/trng-type.service';

@Component({
  selector: 'app-trng-type-form-page',
  templateUrl: './trng-type-form-page.component.html',
})
export class TrngTypeFormPageComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  saving = false;
  error = '';
  isEdit = false;
  id?: number;

  constructor(
    private fb: FormBuilder,
    private service: TrngTypeService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      trngTypeNm: ['', [Validators.required, Validators.maxLength(25)]],
      description: ['', Validators.maxLength(150)],
    });

    this.id = this.route.snapshot.params['trngTypeId'];
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
    const req = this.form.value;

    const call = this.isEdit
      ? this.service.update(this.id!, req)
      : this.service.create(req);

    call.subscribe({
      next: () => {
        this.router.navigate(['training/types']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Save failed';
        this.saving = false;
      },
    });
  }

  cancel() {
    this.router.navigate(['training/types']);
  }
}