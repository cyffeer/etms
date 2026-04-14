import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { LoginComponent } from './features/auth/login/login.component';

// Move your current page routes here (same paths/components as today)
const appPages: Routes = [
  {
    path: 'dashboard',
    data: { title: 'Dashboard', nav: true, description: 'Overview' },
    loadChildren: () =>
      import('./features/dashboard/dashboard.module').then((m) => m.DashboardModule),
  },
  {
    path: 'employees',
    data: { title: 'Employees', nav: true, description: 'Administration' },
    loadChildren: () =>
      import('./features/employees/employees.module').then((m) => m.EmployeesModule),
  },
  {
    path: 'departments',
    data: { title: 'Departments', nav: true, description: 'Administration' },
    loadChildren: () =>
      import('./features/departments/departments.module').then((m) => m.DepartmentsModule),
  },
  {
    path: 'skills',
    data: { title: 'Skills', nav: true, description: 'Skills Inventory' },
    loadChildren: () =>
      import('./features/skills/skills.module').then((m) => m.SkillsModule),
  },
  {
    path: 'skills-inventory',
    data: { title: 'Skills Inventory', nav: true, description: 'Skills Inventory' },
    loadChildren: () =>
      import('./features/skills-inventory/skills-inventory.module').then((m) => m.SkillsInventoryModule),
  },
  {
    path: 'training',
    data: { title: 'Training', nav: true, description: 'Monitoring & Management' },
    loadChildren: () =>
      import('./features/training/training.module').then((m) => m.TrainingModule),
  },
  {
    path: 'leaves',
    data: { title: 'Leaves', nav: true, description: 'Leave Monitoring' },
    loadChildren: () =>
      import('./features/leaves/leaves.module').then((m) => m.LeavesModule),
  },
  {
    path: 'member-types',
    data: { title: 'Member Types', nav: true, description: 'Administration' },
    loadChildren: () =>
      import('./features/member-types/member-types.module').then((m) => m.MemberTypesModule),
  },
  {
    path: 'np-lvl-info',
    data: { title: 'Nihongo Levels', nav: true, description: 'Nihongo Proficiency' },
    loadChildren: () =>
      import('./features/np-lvl-info/np-lvl-info.module').then((m) => m.NpLvlInfoModule),
  },
  {
    path: 'department-members',
    data: { title: 'Department Members', nav: true, description: 'Administration' },
    loadChildren: () =>
      import('./features/department-members/department-members.module').then((m) => m.DepartmentMembersModule),
  },
  {
    path: 'attendance',
    data: { title: 'Attendance', nav: true, description: 'Attendance Monitoring' },
    loadChildren: () =>
      import('./features/attendance/attendance.module').then((m) => m.AttendanceModule),
  },
];

const routes: Routes = [
  { path: 'login', component: LoginComponent, data: { title: 'Login', nav: false } },
  {
    path: '',
    component: MainLayoutComponent,
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
