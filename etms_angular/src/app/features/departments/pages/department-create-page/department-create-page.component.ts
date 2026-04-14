import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DepartmentRequest } from '../../models/department.model';
import { DepartmentsService } from '../../services/departments.service';

@Component({
  selector: 'app-department-create-page',
  templateUrl: './department-create-page.component.html'
})
export class DepartmentCreatePageComponent {
  loading = false;
  error = '';

  form = this.fb.group({
    departmentCode: ['', [Validators.required, Validators.maxLength(30)]],
    departmentName: ['', [Validators.required, Validators.maxLength(150)]],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private departmentsService: DepartmentsService,
    private router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: DepartmentRequest = {
      departmentCode: raw.departmentCode!,
      departmentName: raw.departmentName!,
      active: raw.active ?? true
    };

    this.loading = true;
    this.error = '';
    this.departmentsService.createDepartment(payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/departments']);
      },
      error: (err) => {
        this.error = err?.message || 'Failed to create department.';
        this.loading = false;
      }
    });
  }
}