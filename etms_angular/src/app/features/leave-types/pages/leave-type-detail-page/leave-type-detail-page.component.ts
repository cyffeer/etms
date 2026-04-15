import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LeaveType } from '../../models/leave-type.models';
import { LeaveTypeService } from '../../services/leave-type.service';

@Component({
  selector: 'app-leave-type-detail-page',
  templateUrl: './leave-type-detail-page.component.html',
})
export class LeaveTypeDetailPageComponent implements OnInit {
  item?: LeaveType;
  loading = false;
  error = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly leaveTypeService: LeaveTypeService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('leaveTypeId'));
    if (!id) {
      this.error = 'Invalid leave type id.';
      return;
    }

    this.loading = true;
    this.leaveTypeService.getById(id).subscribe({
      next: (item) => {
        this.item = item;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load leave type.';
        this.loading = false;
      },
    });
  }
}
