import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Leave } from '../../models/leave.models';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-leave-detail-page',
  templateUrl: './leave-detail-page.component.html',
})
export class LeaveDetailPageComponent implements OnInit {
  loading = false;
  error = '';
  item?: Leave;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: LeaveService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.params['leaveId']);
    this.loading = true;
    this.service.getById(id).subscribe({
      next: (x) => { this.item = x; this.loading = false; },
      error: () => { this.error = 'Failed to load details.'; this.loading = false; }
    });
  }

  back(): void {
    this.router.navigate(['leaves']);
  }
}