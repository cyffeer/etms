import { Component, OnInit } from '@angular/core';
import { EmployeeResponse } from '../../../employees/models/employee.model';
import { EmployeeSelfService } from '../../services/employee-self.service';

@Component({
  selector: 'app-employee-profile-page',
  templateUrl: './employee-profile-page.component.html',
  styleUrls: ['./employee-profile-page.component.css']
})
export class EmployeeProfilePageComponent implements OnInit {
  profile: EmployeeResponse | null = null;
  loading = false;
  saving = false;
  error = '';
  success = '';

  form = {
    firstName: '',
    lastName: '',
    email: ''
  };

  constructor(private readonly employeeSelfService: EmployeeSelfService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  save(): void {
    if (!this.profile) {
      return;
    }
    this.saving = true;
    this.error = '';
    this.success = '';
    this.employeeSelfService.updateMyProfile(this.form).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.form.firstName = profile.firstName ?? '';
        this.form.lastName = profile.lastName ?? '';
        this.form.email = profile.email ?? '';
        this.success = 'Profile updated successfully.';
        this.saving = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Unable to update profile.';
        this.saving = false;
      }
    });
  }

  private loadProfile(): void {
    this.loading = true;
    this.error = '';
    this.employeeSelfService.getMyProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.form.firstName = profile.firstName ?? '';
        this.form.lastName = profile.lastName ?? '';
        this.form.email = profile.email ?? '';
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Unable to load profile.';
        this.loading = false;
      }
    });
  }
}
