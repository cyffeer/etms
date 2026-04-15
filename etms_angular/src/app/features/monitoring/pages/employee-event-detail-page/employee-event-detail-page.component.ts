import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { EmployeeEventResponse } from '../../models/employee-event.model';
import { EmployeeEventService } from '../../services/employee-event.service';

@Component({
  selector: 'app-employee-event-detail-page',
  templateUrl: './employee-event-detail-page.component.html'
})
export class EmployeeEventDetailPageComponent implements OnInit {
  item?: EmployeeEventResponse;
  loading = false;
  error = '';
  readonly canEdit = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);

  constructor(
    private route: ActivatedRoute,
    private employeeEventService: EmployeeEventService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const employeeEventId = Number(this.route.snapshot.paramMap.get('employeeEventId'));
    if (!employeeEventId) {
      this.error = 'Invalid employee event id.';
      return;
    }

    this.loading = true;
    this.employeeEventService.getById(employeeEventId).subscribe({
      next: (row) => {
        this.item = row;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load employee event.';
        this.loading = false;
      }
    });
  }
}
