import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentRequest } from '../../models/department.model';
import { DepartmentsService } from '../../services/departments.service';

@Component({
  selector: 'app-department-edit-page',
  templateUrl: './department-edit-page.component.html'
})
export class DepartmentEditPageComponent implements OnInit {
  loading = false;
  error = '';
  private departmentId = 0;

  form = this.fb.group({
    departmentCode: ['', [Validators.required, Validators.maxLength(30)]],
    departmentName: ['', [Validators.required, Validators.maxLength(150)]],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private departmentsService: DepartmentsService
  ) {}

  ngOnInit(): void {
    this.departmentId = Number(this.route.snapshot.paramMap.get('departmentId'));
    if (!this.departmentId) {
      this.error = 'Invalid department id.';
      return;
    }

    this.loading = true;
    this.departmentsService.getDepartmentById(this.departmentId).subscribe({
      next: (d) => {
        this.form.patchValue({
          departmentCode: d.departmentCode,
          departmentName: d.departmentName,
          active: d.active
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load department.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid || !this.departmentId) {
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
    this.departmentsService.updateDepartment(this.departmentId, payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/departments', this.departmentId]);
      },
      error: (err) => {
        this.error = err?.message || 'Failed to update department.';
        this.loading = false;
      }
    });
  }
}