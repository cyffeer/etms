import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DepartmentResponse } from '../../models/department.model';
import { DepartmentsService } from '../../services/departments.service';

@Component({
  selector: 'app-department-details-page',
  templateUrl: './department-details-page.component.html'
})
export class DepartmentDetailsPageComponent implements OnInit {
  department?: DepartmentResponse;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private departmentsService: DepartmentsService
  ) {}

  ngOnInit(): void {
    const departmentId = Number(this.route.snapshot.paramMap.get('departmentId'));
    if (!departmentId) {
      this.error = 'Invalid department id.';
      return;
    }

    this.loading = true;
    this.departmentsService.getDepartmentById(departmentId).subscribe({
      next: (data) => {
        this.department = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load department.';
        this.loading = false;
      }
    });
  }
}