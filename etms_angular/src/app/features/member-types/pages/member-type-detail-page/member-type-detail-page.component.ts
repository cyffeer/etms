import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MemberType } from '../../models/member-type.models';
import { MemberTypeService } from '../../services/member-type.service';

@Component({
  selector: 'app-member-type-detail-page',
  templateUrl: './member-type-detail-page.component.html',
  styleUrls: ['./member-type-detail-page.component.css']
})
export class MemberTypeDetailPageComponent implements OnInit {
  memberType?: MemberType;
  loading = false;
  error = '';

  constructor(private route: ActivatedRoute, private service: MemberTypeService) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('memberTypeId'));
    if (!id) {
      this.error = 'Invalid member type id.';
      return;
    }

    this.loading = true;
    this.service.getById(id).subscribe({
      next: (item) => {
        this.memberType = item;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load member type.';
        this.loading = false;
      }
    });
  }
}
