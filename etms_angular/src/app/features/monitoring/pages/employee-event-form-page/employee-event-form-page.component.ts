import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentResponse } from '../../../departments/models/department.model';
import { DepartmentsService } from '../../../departments/services/departments.service';
import { EmployeeEventRequest } from '../../models/employee-event.model';
import { EmployeeEventService } from '../../services/employee-event.service';

@Component({
  selector: 'app-employee-event-form-page',
  templateUrl: './employee-event-form-page.component.html'
})
export class EmployeeEventFormPageComponent implements OnInit {
  readonly eventTypes = [
    'PROMOTION',
    'VIOLATION',
    'CITATION',
    'PROJECT_ASSIGNMENT',
    'RESIGNATION',
    'SUSPENSION',
    'TERMINATION'
  ];
  readonly statuses = ['ACTIVE', 'CLOSED', 'PENDING', 'APPROVED', 'REJECTED'];

  departments: DepartmentResponse[] = [];
  loading = false;
  error = '';
  isEdit = false;
  private employeeEventId = 0;
  private presetEventType = '';

  form = this.fb.group({
    employeeNumber: ['', [Validators.required, Validators.maxLength(30)]],
    eventType: ['PROJECT_ASSIGNMENT', [Validators.required, Validators.maxLength(40)]],
    title: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', [Validators.maxLength(1000)]],
    departmentCode: [''],
    referenceCode: ['', [Validators.maxLength(50)]],
    effectiveDate: ['', Validators.required],
    endDate: [''],
    status: ['ACTIVE', [Validators.required, Validators.maxLength(20)]]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private employeeEventService: EmployeeEventService,
    private departmentsService: DepartmentsService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((queryParams) => {
      const requestedType = queryParams.get('eventType') || '';
      if (requestedType && this.eventTypes.includes(requestedType)) {
        this.presetEventType = requestedType;
        this.form.patchValue({
          eventType: requestedType,
          title: this.defaultTitleFor(requestedType),
        });
      }
    });

    this.departmentsService.getDepartments().subscribe({
      next: (rows) => {
        this.departments = rows;
      }
    });

    this.employeeEventId = Number(this.route.snapshot.paramMap.get('employeeEventId'));
    this.isEdit = !!this.employeeEventId;
    if (!this.isEdit) {
      if (this.presetEventType) {
        this.form.patchValue({
          eventType: this.presetEventType,
          title: this.defaultTitleFor(this.presetEventType),
        });
      }
      return;
    }

    this.loading = true;
    this.employeeEventService.getById(this.employeeEventId).subscribe({
      next: (row) => {
        this.form.patchValue({
          employeeNumber: row.employeeNumber,
          eventType: row.eventType,
          title: row.title,
          description: row.description ?? '',
          departmentCode: row.departmentCode ?? '',
          referenceCode: row.referenceCode ?? '',
          effectiveDate: row.effectiveDate?.slice(0, 10),
          endDate: row.endDate?.slice(0, 10) ?? '',
          status: row.status
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load employee event.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: EmployeeEventRequest = {
      employeeNumber: raw.employeeNumber!,
      eventType: raw.eventType!,
      title: raw.title!,
      description: raw.description || null,
      departmentCode: raw.departmentCode || null,
      referenceCode: raw.referenceCode || null,
      effectiveDate: raw.effectiveDate!,
      endDate: raw.endDate || null,
      status: raw.status!
    };

    this.loading = true;
    this.error = '';

    const request$ = this.isEdit
      ? this.employeeEventService.update(this.employeeEventId, payload)
      : this.employeeEventService.create(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/monitoring']),
      error: (err) => {
        this.error = this.resolveErrorMessage(err);
        this.loading = false;
      }
    });
  }

  private defaultTitleFor(eventType: string): string {
    const titles: Record<string, string> = {
      PROMOTION: 'Promotion Record',
      VIOLATION: 'Violation Record',
      CITATION: 'Citation Record',
      PROJECT_ASSIGNMENT: 'Project Assignment',
      RESIGNATION: 'Resignation Record',
      SUSPENSION: 'Suspension Record',
      TERMINATION: 'Termination Record'
    };
    return titles[eventType] || 'Employee Event';
  }

  private resolveErrorMessage(err: any): string {
    const detailData = err?.details?.data;
    if (Array.isArray(detailData) && detailData.length > 0) {
      return detailData.join(', ');
    }

    const detailErrors = err?.details?.errors;
    if (Array.isArray(detailErrors) && detailErrors.length > 0) {
      return detailErrors.join(', ');
    }

    return err?.message || 'Failed to save employee event.';
  }
}
