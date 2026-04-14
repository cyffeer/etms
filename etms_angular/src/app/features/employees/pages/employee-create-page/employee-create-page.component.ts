import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { EmployeeRequest } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';

@Component({
  selector: 'app-employee-create-page',
  templateUrl: './employee-create-page.component.html'
})
export class EmployeeCreatePageComponent {
  loading = false;
  error = '';

  form = this.fb.group({
    employeeCode: ['', [Validators.required, Validators.maxLength(30)]],
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    hireDate: [''],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private employeesService: EmployeesService,
    private router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: EmployeeRequest = {
      employeeCode: raw.employeeCode!,
      firstName: raw.firstName!,
      lastName: raw.lastName!,
      email: raw.email!,
      hireDate: raw.hireDate ? raw.hireDate : null,
      active: raw.active ?? true
    };

    this.loading = true;
    this.error = '';
    this.employeesService.createEmployee(payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/employees']);
      },
      error: (err) => {
        this.error = err?.message || 'Failed to create employee.';
        this.loading = false;
      }
    });
  }
}