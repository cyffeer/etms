import { Component, OnInit } from '@angular/core';
import { LeaveBalance } from '../../models/leave.models';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-leave-balance-page',
  templateUrl: './leave-balance-page.component.html',
})
export class LeaveBalancePageComponent implements OnInit {
  loading = false;
  error = '';
  items: LeaveBalance[] = [];
  filters = {
    employeeNumber: '',
    year: new Date().getFullYear(),
  };

  constructor(private readonly leaveService: LeaveService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.leaveService.getBalances(this.filters.employeeNumber, this.filters.year).subscribe({
      next: (rows) => {
        this.items = rows;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load leave balances.';
        this.loading = false;
      },
    });
  }

  reset(): void {
    this.filters = {
      employeeNumber: '',
      year: new Date().getFullYear(),
    };
    this.load();
  }
}
