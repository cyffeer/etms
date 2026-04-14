import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DepartmentMemberResponse } from '../../department-member.model';
import { DepartmentMemberService } from '../../services/department-member.service';

@Component({
  selector: 'app-department-member-detail-page',
  templateUrl: './department-member-detail-page.component.html',
  styleUrls: ['./department-member-detail-page.component.css']
})
export class DepartmentMemberDetailPageComponent implements OnInit {
  item?: DepartmentMemberResponse;
  loading = false;
  error = '';

  constructor(private route: ActivatedRoute, private service: DepartmentMemberService) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('deptMemberId'));
    if (!id) {
      this.error = 'Invalid department member id.';
      return;
    }

    this.loading = true;
    this.service.getById(id).subscribe({
      next: (item) => {
        this.item = item;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load department member record.';
        this.loading = false;
      }
    });
  }
}
