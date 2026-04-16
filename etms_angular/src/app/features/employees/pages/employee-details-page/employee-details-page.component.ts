import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EmployeeResponse } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-employee-details-page',
  templateUrl: './employee-details-page.component.html'
})
export class EmployeeDetailsPageComponent implements OnInit {
  employee?: EmployeeResponse;
  loading = false;
  uploading = false;
  error = '';
  readonly canEdit = this.authService.hasAnyRole(['ADMIN', 'MANAGER']);
  selectedPhoto?: File;

  constructor(
    private route: ActivatedRoute,
    public employeesService: EmployeesService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const employeeId = Number(this.route.snapshot.paramMap.get('employeeId'));
    if (!employeeId) {
      this.error = 'Invalid employee id.';
      return;
    }

    this.loading = true;
    this.employeesService.getEmployeeById(employeeId).subscribe({
      next: (data) => {
        this.employee = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load employee.';
        this.loading = false;
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedPhoto = input.files && input.files.length > 0 ? input.files[0] : undefined;
  }

  uploadPhoto(): void {
    if (!this.employee?.employeeId || !this.selectedPhoto) {
      return;
    }

    this.uploading = true;
    this.error = '';
    this.employeesService.uploadPhoto(this.employee.employeeId, this.selectedPhoto).subscribe({
      next: (data) => {
        this.employee = data;
        this.selectedPhoto = undefined;
        this.uploading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || err?.message || 'Failed to upload employee photo.';
        this.uploading = false;
      }
    });
  }
}
