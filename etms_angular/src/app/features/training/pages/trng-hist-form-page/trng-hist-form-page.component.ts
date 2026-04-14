import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TrngHistoryService } from '../../services/trng-history.service';

@Component({
  selector: 'app-trng-hist-form-page',
  templateUrl: './trng-hist-form-page.component.html',
})
export class TrngHistFormPageComponent implements OnInit {
  form!: FormGroup;
  saving = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private service: TrngHistoryService,
    private router: Router
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      employeeNumber: ['', Validators.required],
      trngId: [null, Validators.required],
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.saving = true;
    const req = {
      employeeNumber: this.form.value.employeeNumber,
      trngId: Number(this.form.value.trngId),
    };

    this.service.assign(req).subscribe({
      next: () => {
        this.router.navigate(['training/history']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Save failed';
        this.saving = false;
      },
    });
  }

  cancel() {
    this.router.navigate(['training/history']);
  }
}
