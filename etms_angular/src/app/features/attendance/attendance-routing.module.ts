import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { AttendanceDetailPageComponent } from './pages/attendance-detail-page/attendance-detail-page.component';
import { AttendanceFormPageComponent } from './pages/attendance-form-page/attendance-form-page.component';
import { AttendanceListPageComponent } from './pages/attendance-list-page/attendance-list-page.component';

const routes: Routes = [
  { path: '', component: AttendanceListPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } },
  { path: 'new', component: AttendanceFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'EMPLOYEE'] } },
  { path: ':attendanceId/edit', component: AttendanceFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: ':attendanceId', component: AttendanceDetailPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AttendanceRoutingModule { }
