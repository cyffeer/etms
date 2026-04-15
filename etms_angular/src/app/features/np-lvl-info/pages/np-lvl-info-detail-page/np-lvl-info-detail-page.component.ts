import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NpLvlInfoResponse } from '../../np-lvl-info.model';
import { NpLvlInfoService } from '../../services/np-lvl-info.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-np-lvl-info-detail-page',
  templateUrl: './np-lvl-info-detail-page.component.html',
  styleUrls: ['./np-lvl-info-detail-page.component.css']
})
export class NpLvlInfoDetailPageComponent implements OnInit {
  item?: NpLvlInfoResponse;
  loading = false;
  error = '';
  readonly canManage = this.authService.hasAnyRole(['ADMIN']);

  constructor(
    private route: ActivatedRoute,
    private service: NpLvlInfoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('npLvlInfoId'));
    if (!id) {
      this.error = 'Invalid NP level info id.';
      return;
    }

    this.loading = true;
    this.service.getById(id).subscribe({
      next: (item) => {
        this.item = item;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load NP level info.';
        this.loading = false;
      }
    });
  }
}
