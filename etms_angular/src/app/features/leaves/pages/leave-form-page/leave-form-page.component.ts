import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LeaveRequest } from '../../models/leave.models';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-leave-form-page',
  templateUrl: './leave-form-page.component.html',
})
export class LeaveFormPageComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  saving = false;
  error = '';
  isEdit = false;
  id?: number;

  constructor(
    private fb: FormBuilder,
    private service: LeaveService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      employeeNumber: ['', [Validators.required, Validators.maxLength(20)]],
      leaveType: ['', [Validators.required, Validators.maxLength(50)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      status: [''],
      remarks: ['', Validators.maxLength(255)],
    }, { validators: [this.dateRangeValidator] });

    const raw = this.route.snapshot.params['leaveId'];
    if (raw) {
      this.id = Number(raw);
      this.isEdit = true;
      this.load(this.id);
    }
  }

  private load(id: number): void {
    this.loading = true;
    this.service.getById(id).subscribe({
      next: (x) => {
        this.form.patchValue({
          employeeNumber: x.employeeNumber,
          leaveType: x.leaveType,
          startDate: x.startDate?.slice(0, 10),
          endDate: x.endDate?.slice(0, 10),
          status: x.status ?? '',
          remarks: x.remarks ?? '',
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load leave record.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.saving = true;

    const payload: LeaveRequest = {
      employeeNumber: this.form.value.employeeNumber,
      leaveType: this.form.value.leaveType,
      startDate: this.form.value.startDate,
      endDate: this.form.value.endDate,
      status: this.form.value.status || null,
      remarks: this.form.value.remarks || null,
    };

    const req$ = this.isEdit
      ? this.service.update(this.id!, payload)
      : this.service.create(payload);

    req$.subscribe({
      next: () => this.router.navigate(['leaves']),
      error: () => {
        this.error = 'Save failed.';
        this.saving = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['leaves']);
  }

  private dateRangeValidator(ctrl: AbstractControl) {
    const start = ctrl.get('startDate')?.value;
    const end = ctrl.get('endDate')?.value;
    if (!start || !end) return null;
    return start <= end ? null : { dateRange: true };
    }
}
