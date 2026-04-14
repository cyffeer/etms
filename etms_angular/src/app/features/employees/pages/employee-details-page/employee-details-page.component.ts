import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EmployeeResponse } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';

@Component({
  selector: 'app-employee-details-page',
  templateUrl: './employee-details-page.component.html'
})
export class EmployeeDetailsPageComponent implements OnInit {
  employee?: EmployeeResponse;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private employeesService: EmployeesService
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
}