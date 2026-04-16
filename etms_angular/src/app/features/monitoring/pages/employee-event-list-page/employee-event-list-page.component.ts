import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { EmployeeEventResponse, EmployeeEventSummaryResponse } from '../../models/employee-event.model';
import { EmployeeEventService } from '../../services/employee-event.service';

@Component({
  selector: 'app-employee-event-list-page',
  templateUrl: './employee-event-list-page.component.html'
})
export class EmployeeEventListPageComponent implements OnInit {
  pageTitle = 'Employee Events';
  rows: EmployeeEventResponse[] = [];
  summary?: EmployeeEventSummaryResponse;
  loading = false;
  summaryLoading = false;
  error = '';
  summaryError = '';
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizes = [5, 10, 20, 50];

  readonly eventTypes = [
    'PROMOTION',
    'VIOLATION',
    'CITATION',
    'PROJECT_ASSIGNMENT',
    'RESIGNATION',
    'SUSPENSION',
    'TERMINATION'
  ];
  readonly statuses = ['ACTIVE', 'CLOSED', 'PENDING', 'APPROVED', 'REJECTED'];
  readonly quickLinks = [
    { label: 'Promotions', route: '/monitoring/promotion', eventType: 'PROMOTION', restrictedForHr: false },
    { label: 'Violations', route: '/monitoring/violations', eventType: 'VIOLATION', restrictedForHr: false },
    { label: 'Citations', route: '/monitoring/citations', eventType: 'CITATION', restrictedForHr: false },
    { label: 'Project Assignments', route: '/monitoring/project-assignments', eventType: 'PROJECT_ASSIGNMENT', restrictedForHr: true },
    { label: 'Resignations', route: '/monitoring/resignations', eventType: 'RESIGNATION', restrictedForHr: true },
    { label: 'Suspensions', route: '/monitoring/suspensions', eventType: 'SUSPENSION', restrictedForHr: true },
    { label: 'Terminations', route: '/monitoring/terminations', eventType: 'TERMINATION', restrictedForHr: true }
  ];
  readonly canManage = this.authService.hasAnyRole(['ADMIN', 'MANAGER']);
  readonly canDelete = this.authService.hasAnyRole(['ADMIN']);
  readonly isHr = this.authService.hasAnyRole(['HR']);
  private presetEventType = '';

  filters = {
    employeeNumber: '',
    eventType: '',
    status: '',
    keyword: '',
    startDate: '',
    endDate: ''
  };

  constructor(
    private employeeEventService: EmployeeEventService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      this.presetEventType = data['presetEventType'] || '';
      this.pageTitle = data['title'] || 'Employee Events';
      this.filters.eventType = this.presetEventType;
      this.page = 0;
      this.loadSummary();
      this.load();
    });
  }

  isPresetView(): boolean {
    return !!this.presetEventType;
  }

  addEvent(): void {
    const commands = ['/monitoring/new'];
    const extras = this.presetEventType ? { queryParams: { eventType: this.presetEventType } } : {};
    this.router.navigate(commands, extras as never);
  }

  load(page = this.page): void {
    this.loading = true;
    this.error = '';
    this.page = page;
    const hasFilters = Object.values(this.filters).some((value) => !!value.trim());
    const hasPresetType = !!this.presetEventType;
    const request$ = hasPresetType
      ? (hasFilters
        ? this.employeeEventService.searchCategory(this.presetEventType, {
            employeeNumber: this.filters.employeeNumber,
            status: this.filters.status,
            keyword: this.filters.keyword,
            startDate: this.filters.startDate,
            endDate: this.filters.endDate
          }, this.page, this.size)
        : this.employeeEventService.getCategory(this.presetEventType, this.page, this.size))
      : (hasFilters
        ? this.employeeEventService.search(this.filters, this.page, this.size)
        : this.employeeEventService.getAll(this.page, this.size));

    request$.subscribe({
      next: (result) => {
        this.rows = result.items;
        this.totalElements = result.totalElements ?? result.items.length;
        this.totalPages = result.totalPages ?? 1;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load monitoring events.';
        this.loading = false;
      }
    });
  }

  loadSummary(): void {
    this.summaryLoading = true;
    this.summaryError = '';
    this.employeeEventService.getSummary().subscribe({
      next: (result) => {
        this.summary = result;
        this.summaryLoading = false;
      },
      error: (err) => {
        this.summaryError = err?.message || 'Failed to load monitoring summary.';
        this.summaryLoading = false;
      }
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      eventType: this.presetEventType,
      status: '',
      keyword: '',
      startDate: '',
      endDate: ''
    };
    this.page = 0;
    this.load();
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.load(0);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.page) {
      return;
    }
    this.load(page);
  }

  get visiblePages(): number[] {
    return Array.from({ length: this.totalPages }, (_, index) => index);
  }

  onDelete(employeeEventId: number): void {
    if (!this.canDelete) {
      return;
    }
    if (!confirm('Delete this monitoring event?')) {
      return;
    }
    this.employeeEventService.delete(employeeEventId).subscribe({
      next: () => this.load(this.page),
      error: (err) => {
        this.error = err?.message || 'Failed to delete monitoring event.';
      }
    });
  }

  eventTypeLabel(value: string): string {
    return value.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (m) => m.toUpperCase());
  }

  categoryCount(eventType: string): number {
    return this.summary?.categories.find((item) => item.eventType === eventType)?.count ?? 0;
  }

  canShowCategoryLink(restrictedForHr: boolean): boolean {
    return !restrictedForHr || !this.isHr;
  }
}
