import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AttendanceRequest } from '../../attendance.model';
import { AttendanceService } from '../../services/attendance.service';

@Component({
  selector: 'app-attendance-form-page',
  templateUrl: './attendance-form-page.component.html',
  styleUrls: ['./attendance-form-page.component.css']
})
export class AttendanceFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  private attendanceId = 0;

  form = this.fb.group({
    employeeNumber: ['', [Validators.required, Validators.maxLength(30)]],
    attendanceDate: ['', Validators.required],
    timeIn: [''],
    timeOut: [''],
    status: ['']
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private service: AttendanceService
  ) {}

  ngOnInit(): void {
    this.attendanceId = Number(this.route.snapshot.paramMap.get('attendanceId'));
    this.isEdit = !!this.attendanceId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.service.getById(this.attendanceId).subscribe({
      next: (item) => {
        this.form.patchValue({
          employeeNumber: item.employeeNumber,
          attendanceDate: item.attendanceDate?.slice(0, 10),
          timeIn: item.timeIn ?? '',
          timeOut: item.timeOut ?? '',
          status: item.status ?? ''
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load attendance record.';
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
    const payload: AttendanceRequest = {
      employeeNumber: raw.employeeNumber!,
      attendanceDate: raw.attendanceDate!,
      timeIn: raw.timeIn || null,
      timeOut: raw.timeOut || null,
      status: raw.status || null
    };

    const request$ = this.isEdit
      ? this.service.update(this.attendanceId, payload)
      : this.service.create(payload);

    this.loading = true;
    this.error = '';
    request$.subscribe({
      next: () => this.router.navigate(['/attendance']),
      error: (err) => {
        this.error = err?.message || 'Failed to save attendance record.';
        this.loading = false;
      }
    });
  }
}
