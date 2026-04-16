import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { EmployeeEventResponse } from '../../../monitoring/models/employee-event.model';
import { EmployeeEventService } from '../../../monitoring/services/employee-event.service';

@Component({
  selector: 'app-employee-cases-page',
  templateUrl: './employee-cases-page.component.html',
  styleUrls: ['./employee-cases-page.component.css']
})
export class EmployeeCasesPageComponent implements OnInit {
  allCases: EmployeeEventResponse[] = [];
  cases: EmployeeEventResponse[] = [];
  loading = false;
  error = '';
  page = 0;
  readonly size = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private readonly authService: AuthService,
    private readonly employeeEventService: EmployeeEventService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.username) {
      this.cases = [];
      return;
    }

    this.loading = true;
    this.error = '';
    forkJoin([
      this.employeeEventService.searchCategory('SUSPENSION', { employeeNumber: user.username }),
      this.employeeEventService.searchCategory('TERMINATION', { employeeNumber: user.username })
    ]).subscribe({
      next: ([suspensions, terminations]) => {
        this.allCases = [...suspensions.items, ...terminations.items]
          .sort((a, b) => (a.effectiveDate > b.effectiveDate ? -1 : 1));
        this.totalElements = this.allCases.length;
        this.totalPages = this.totalElements === 0 ? 0 : Math.ceil(this.totalElements / this.size);
        this.applyPageSlice();
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Unable to load your cases.';
        this.loading = false;
      }
    });
  }

  prevPage(): void {
    if (this.page <= 0) return;
    this.page -= 1;
    this.applyPageSlice();
  }

  nextPage(): void {
    if (!this.hasNextPage()) return;
    this.page += 1;
    this.applyPageSlice();
  }

  hasPrevPage(): boolean {
    return this.page > 0;
  }

  hasNextPage(): boolean {
    return this.totalPages > 0 && this.page + 1 < this.totalPages;
  }

  private applyPageSlice(): void {
    if (this.totalPages > 0 && this.page >= this.totalPages) {
      this.page = this.totalPages - 1;
    }
    const from = this.page * this.size;
    const to = from + this.size;
    this.cases = this.allCases.slice(from, to);
  }
}
