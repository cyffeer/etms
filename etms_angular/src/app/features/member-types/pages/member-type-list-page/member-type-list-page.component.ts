import { Component, OnInit } from '@angular/core';
import { MemberType } from '../../models/member-type.models';
import { MemberTypeService } from '../../services/member-type.service';

@Component({
  selector: 'app-member-type-list-page',
  templateUrl: './member-type-list-page.component.html',
  styleUrls: ['./member-type-list-page.component.css']
})
export class MemberTypeListPageComponent implements OnInit {
  memberTypes: MemberType[] = [];
  loading = false;
  error = '';

  constructor(private service: MemberTypeService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.service.getAll().subscribe({
      next: (data) => {
        this.memberTypes = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load member types.';
        this.loading = false;
      }
    });
  }

  onDelete(id: number): void {
    if (!confirm('Delete this member type?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.message || 'Failed to delete member type.')
    });
  }
}
