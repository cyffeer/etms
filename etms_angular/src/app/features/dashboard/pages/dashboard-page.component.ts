import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AttendanceService } from '../../attendance/services/attendance.service';
import { DepartmentsService } from '../../departments/services/departments.service';
import { EmployeesService } from '../../employees/services/employees.service';
import { LeaveService } from '../../leaves/services/leave.service';
import { SkillsService } from '../../skills/services/skills.service';

type DashboardCard = {
  label: string;
  count: number;
  route: string;
  note: string;
};

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html'
})
export class DashboardPageComponent implements OnInit {
  loading = false;
  error = '';

  cards: DashboardCard[] = [
    { label: 'Employees', count: 0, route: '/employees', note: 'Administration records' },
    { label: 'Departments', count: 0, route: '/departments', note: 'Org structure' },
    { label: 'Skills', count: 0, route: '/skills', note: 'Skills master data' },
    { label: 'Attendance', count: 0, route: '/attendance', note: 'Attendance monitoring' },
    { label: 'Leaves', count: 0, route: '/leaves', note: 'Leave monitoring' }
  ];

  constructor(
    private employeesService: EmployeesService,
    private departmentsService: DepartmentsService,
    private skillsService: SkillsService,
    private attendanceService: AttendanceService,
    private leaveService: LeaveService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.loading = true;
    this.error = '';

    forkJoin({
      employees: this.employeesService.getEmployees(),
      departments: this.departmentsService.getDepartments(),
      skills: this.skillsService.getSkills(),
      attendance: this.attendanceService.getAll(),
      leaves: this.leaveService.getAll()
    }).subscribe({
      next: ({ employees, departments, skills, attendance, leaves }) => {
        this.cards = [
          { label: 'Employees', count: employees.items.length, route: '/employees', note: 'Administration records' },
          { label: 'Departments', count: departments.length, route: '/departments', note: 'Org structure' },
          { label: 'Skills', count: skills.length, route: '/skills', note: 'Skills master data' },
          { label: 'Attendance', count: attendance.length, route: '/attendance', note: 'Attendance monitoring' },
          { label: 'Leaves', count: leaves.length, route: '/leaves', note: 'Leave monitoring' }
        ];
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load dashboard summary.';
        this.loading = false;
      }
    });
  }
}
