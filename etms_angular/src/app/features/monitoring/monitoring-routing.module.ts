import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { ROLE_GROUPS } from '../../core/config/permissions.config';
import { EmployeeEventDetailPageComponent } from './pages/employee-event-detail-page/employee-event-detail-page.component';
import { EmployeeEventFormPageComponent } from './pages/employee-event-form-page/employee-event-form-page.component';
import { EmployeeEventListPageComponent } from './pages/employee-event-list-page/employee-event-list-page.component';

const routes: Routes = [
  { path: '', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ALL } },
  { path: 'promotion', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ALL, presetEventType: 'PROMOTION', title: 'Promotions' } },
  { path: 'violations', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ALL, presetEventType: 'VIOLATION', title: 'Violations' } },
  { path: 'citations', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ALL, presetEventType: 'CITATION', title: 'Citations' } },
  { path: 'project-assignments', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER_EMPLOYEE, presetEventType: 'PROJECT_ASSIGNMENT', title: 'Project Assignments' } },
  { path: 'resignations', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER_EMPLOYEE, presetEventType: 'RESIGNATION', title: 'Resignations' } },
  { path: 'suspensions', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER_EMPLOYEE, presetEventType: 'SUSPENSION', title: 'Suspensions' } },
  { path: 'terminations', component: EmployeeEventListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER_EMPLOYEE, presetEventType: 'TERMINATION', title: 'Terminations' } },
  { path: 'new', component: EmployeeEventFormPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER } },
  { path: ':employeeEventId/edit', component: EmployeeEventFormPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER } },
  { path: ':employeeEventId', component: EmployeeEventDetailPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ALL } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MonitoringRoutingModule {}
