import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AttendanceResponse } from '../../attendance.model';
import { AttendanceService } from '../../services/attendance.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-attendance-detail-page',
  templateUrl: './attendance-detail-page.component.html',
  styleUrls: ['./attendance-detail-page.component.css']
})
export class AttendanceDetailPageComponent implements OnInit {
  item?: AttendanceResponse;
  loading = false;
  error = '';
  readonly canEdit = this.authService.hasAnyRole(['ADMIN', 'HR', 'MANAGER']);

  constructor(
    private route: ActivatedRoute,
    private service: AttendanceService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('attendanceId'));
    if (!id) {
      this.error = 'Invalid attendance id.';
      return;
    }

    this.loading = true;
    this.service.getById(id).subscribe({
      next: (item) => {
        this.item = item;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load attendance record.';
        this.loading = false;
      }
    });
  }
}
