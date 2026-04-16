import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';
import { APP_ROLES, ROLE_GROUPS } from './core/config/permissions.config';

// Move your current page routes here (same paths/components as today)
const appPages: Routes = [
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    data: { title: 'Dashboard', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/dashboard/dashboard.module').then((m) => m.DashboardModule),
  },
  {
    path: 'employees',
    canActivate: [AuthGuard],
    data: {
      title: 'Employees',
      nav: true,
      roles: ROLE_GROUPS.ADMIN_MANAGER
    },
    loadChildren: () =>
      import('./features/employees/employees.module').then((m) => m.EmployeesModule),
  },
  {
    path: 'departments',
    canActivate: [AuthGuard],
    data: { title: 'Departments', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/departments/departments.module').then((m) => m.DepartmentsModule),
  },
  {
    path: 'skills',
    canActivate: [AuthGuard],
    data: { title: 'Skills', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/skills/skills.module').then((m) => m.SkillsModule),
  },
  {
    path: 'skill-levels',
    canActivate: [AuthGuard],
    data: { title: 'Skill Levels', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/skill-levels/skill-levels.module').then((m) => m.SkillLevelsModule),
  },
  {
    path: 'skills-inventory',
    canActivate: [AuthGuard],
    data: { title: 'Skills Inventory', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/skills-inventory/skills-inventory.module').then((m) => m.SkillsInventoryModule),
  },
  {
    path: 'training',
    canActivate: [AuthGuard],
    data: { title: 'Training', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/training/training.module').then((m) => m.TrainingModule),
  },
  {
    path: 'travel',
    canActivate: [AuthGuard],
    data: { title: 'Travel', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/travel/travel.module').then((m) => m.TravelModule),
  },
  {
    path: 'leaves',
    canActivate: [AuthGuard],
    data: { title: 'Leaves', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/leaves/leaves.module').then((m) => m.LeavesModule),
  },
  {
    path: 'leave-types',
    canActivate: [AuthGuard],
    data: { title: 'Leave Types', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/leave-types/leave-types.module').then((m) => m.LeaveTypesModule),
  },
  {
    path: 'member-types',
    canActivate: [AuthGuard],
    data: { title: 'Member Types', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/member-types/member-types.module').then((m) => m.MemberTypesModule),
  },
  {
    path: 'np-lvl-info',
    canActivate: [AuthGuard],
    data: { title: 'Nihongo Levels', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/np-lvl-info/np-lvl-info.module').then((m) => m.NpLvlInfoModule),
  },
  {
    path: 'nihongo',
    canActivate: [AuthGuard],
    data: { title: 'Nihongo', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/nihongo/nihongo.module').then((m) => m.NihongoModule),
  },
  {
    path: 'department-members',
    canActivate: [AuthGuard],
    data: { title: 'Department Members', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/department-members/department-members.module').then((m) => m.DepartmentMembersModule),
  },
  {
    path: 'monitoring',
    canActivate: [AuthGuard],
    data: { title: 'Monitoring', nav: true, roles: ROLE_GROUPS.ADMIN_MANAGER },
    loadChildren: () =>
      import('./features/monitoring/monitoring.module').then((m) => m.MonitoringModule),
  },
  {
    path: 'audit-logs',
    canActivate: [AuthGuard],
    data: { title: 'Audit Trail', nav: true, roles: ROLE_GROUPS.ADMIN_ONLY },
    loadChildren: () =>
      import('./features/audit-logs/audit-logs.module').then((m) => m.AuditLogsModule),
  },
  {
    path: 'notifications',
    canActivate: [AuthGuard],
    data: { title: 'Notifications', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/notifications/notifications.module').then((m) => m.NotificationsModule),
  },
  {
    path: 'attendance',
    canActivate: [AuthGuard],
    data: { title: 'Attendance', nav: true, roles: ROLE_GROUPS.ADMIN_HR_MANAGER },
    loadChildren: () =>
      import('./features/attendance/attendance.module').then((m) => m.AttendanceModule),
  },
  {
    path: 'employee',
    canActivate: [AuthGuard],
    data: {
      roles: [APP_ROLES.EMPLOYEE],
      nav: false,
      navChildren: [
        { path: 'profile', title: 'My Profile', roles: [APP_ROLES.EMPLOYEE], navSection: 'Employee' },
        { path: 'resignation', title: 'My Resignation', roles: [APP_ROLES.EMPLOYEE], navSection: 'Employee' },
        { path: 'cases', title: 'My Cases', roles: [APP_ROLES.EMPLOYEE], navSection: 'Employee' },
        { path: 'attendance', title: 'My Attendance', roles: [APP_ROLES.EMPLOYEE], navSection: 'Employee' },
        { path: 'leave', title: 'My Leave', roles: [APP_ROLES.EMPLOYEE], navSection: 'Employee' }
      ]
    },
    loadChildren: () =>
      import('./features/employee-self/employee-self.module').then((m) => m.EmployeeSelfModule),
  },
];

const routes: Routes = [
  { path: 'login', component: LoginComponent, data: { title: 'Login', nav: false } },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      ...appPages,
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
