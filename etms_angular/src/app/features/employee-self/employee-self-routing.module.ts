import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { APP_ROLES } from '../../core/config/permissions.config';
import { EmployeeAttendancePageComponent } from './pages/employee-attendance-page/employee-attendance-page.component';
import { EmployeeCasesPageComponent } from './pages/employee-cases-page/employee-cases-page.component';
import { EmployeeLeavePageComponent } from './pages/employee-leave-page/employee-leave-page.component';
import { EmployeeProfilePageComponent } from './pages/employee-profile-page/employee-profile-page.component';
import { EmployeeResignationPageComponent } from './pages/employee-resignation-page/employee-resignation-page.component';

const EMPLOYEE_ROUTE_DATA = {
  nav: true,
  roles: [APP_ROLES.EMPLOYEE],
  navSection: 'Employee'
};

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'profile' },
  {
    path: 'profile',
    component: EmployeeProfilePageComponent,
    canActivate: [AuthGuard],
    data: { ...EMPLOYEE_ROUTE_DATA, title: 'My Profile' }
  },
  {
    path: 'resignation',
    component: EmployeeResignationPageComponent,
    canActivate: [AuthGuard],
    data: { ...EMPLOYEE_ROUTE_DATA, title: 'My Resignation' }
  },
  {
    path: 'cases',
    component: EmployeeCasesPageComponent,
    canActivate: [AuthGuard],
    data: { ...EMPLOYEE_ROUTE_DATA, title: 'My Cases' }
  },
  {
    path: 'attendance',
    component: EmployeeAttendancePageComponent,
    canActivate: [AuthGuard],
    data: { ...EMPLOYEE_ROUTE_DATA, title: 'My Attendance' }
  },
  {
    path: 'leave',
    component: EmployeeLeavePageComponent,
    canActivate: [AuthGuard],
    data: { ...EMPLOYEE_ROUTE_DATA, title: 'My Leave' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmployeeSelfRoutingModule {}
