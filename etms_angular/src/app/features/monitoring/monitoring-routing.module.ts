import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { EmployeeEventDetailPageComponent } from './pages/employee-event-detail-page/employee-event-detail-page.component';
import { EmployeeEventFormPageComponent } from './pages/employee-event-form-page/employee-event-form-page.component';
import { EmployeeEventListPageComponent } from './pages/employee-event-list-page/employee-event-list-page.component';

const routes: Routes = [
  { path: '', component: EmployeeEventListPageComponent },
  { path: 'new', component: EmployeeEventFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: ':employeeEventId/edit', component: EmployeeEventFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: ':employeeEventId', component: EmployeeEventDetailPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MonitoringRoutingModule {}
