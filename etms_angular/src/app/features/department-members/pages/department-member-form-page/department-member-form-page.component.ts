import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentMemberRequest } from '../../department-member.model';
import { DepartmentMemberService } from '../../services/department-member.service';
import { MemberType } from '../../../member-types/models/member-type.models';
import { MemberTypeService } from '../../../member-types/services/member-type.service';

@Component({
  selector: 'app-department-member-form-page',
  templateUrl: './department-member-form-page.component.html',
  styleUrls: ['./department-member-form-page.component.css']
})
export class DepartmentMemberFormPageComponent implements OnInit {
  loading = false;
  error = '';
  isEdit = false;
  memberTypes: MemberType[] = [];
  private deptMemberId = 0;

  form = this.fb.group({
    departmentCode: ['', [Validators.required, Validators.maxLength(30)]],
    employeeNumber: ['', [Validators.required, Validators.maxLength(30)]],
    memberTypeId: [null as number | null],
    memberStart: ['', Validators.required],
    memberEnd: ['']
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private service: DepartmentMemberService,
    private memberTypeService: MemberTypeService
  ) {}

  ngOnInit(): void {
    this.memberTypeService.getAll().subscribe({
      next: (rows) => {
        this.memberTypes = rows;
      }
    });

    this.deptMemberId = Number(this.route.snapshot.paramMap.get('deptMemberId'));
    this.isEdit = !!this.deptMemberId;

    if (!this.isEdit) {
      return;
    }

    this.loading = true;
    this.service.getById(this.deptMemberId).subscribe({
      next: (item) => {
        this.form.patchValue({
          departmentCode: item.departmentCode,
          employeeNumber: item.employeeNumber,
          memberTypeId: item.memberTypeId ?? null,
          memberStart: item.memberStart?.slice(0, 10),
          memberEnd: item.memberEnd?.slice(0, 10) ?? ''
        });
        this.form.controls.departmentCode.disable();
        this.form.controls.employeeNumber.disable();
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load department member record.';
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
    const payload: DepartmentMemberRequest = {
      departmentCode: raw.departmentCode!,
      employeeNumber: raw.employeeNumber!,
      memberTypeId: raw.memberTypeId ?? null,
      memberStart: raw.memberStart!,
      memberEnd: raw.memberEnd || null
    };

    const request$ = this.isEdit
      ? this.service.update(this.deptMemberId, payload)
      : this.service.create(payload);

    this.loading = true;
    this.error = '';
    request$.subscribe({
      next: () => this.router.navigate(['/department-members']),
      error: (err) => {
        this.error = err?.message || 'Failed to save department member record.';
        this.loading = false;
      }
    });
  }
}
