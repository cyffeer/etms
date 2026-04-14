import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeRequest } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';

@Component({
  selector: 'app-employee-edit-page',
  templateUrl: './employee-edit-page.component.html'
})
export class EmployeeEditPageComponent implements OnInit {
  loading = false;
  error = '';
  private employeeId = 0;

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
    private route: ActivatedRoute,
    private router: Router,
    private employeesService: EmployeesService
  ) {}

  ngOnInit(): void {
    this.employeeId = Number(this.route.snapshot.paramMap.get('employeeId'));
    if (!this.employeeId) {
      this.error = 'Invalid employee id.';
      return;
    }

    this.loading = true;
    this.employeesService.getEmployeeById(this.employeeId).subscribe({
      next: (e) => {
        this.form.patchValue({
          employeeCode: e.employeeCode,
          firstName: e.firstName,
          lastName: e.lastName,
          email: e.email,
          hireDate: e.hireDate ?? '',
          active: e.active
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load employee.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid || !this.employeeId) {
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
    this.employeesService.updateEmployee(this.employeeId, payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/employees', this.employeeId]);
      },
      error: (err) => {
        this.error = err?.message || 'Failed to update employee.';
        this.loading = false;
      }
    });
  }
}