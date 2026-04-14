import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AttendanceDetailPageComponent } from './pages/attendance-detail-page/attendance-detail-page.component';
import { AttendanceFormPageComponent } from './pages/attendance-form-page/attendance-form-page.component';
import { AttendanceListPageComponent } from './pages/attendance-list-page/attendance-list-page.component';

const routes: Routes = [
  { path: '', component: AttendanceListPageComponent },
  { path: 'new', component: AttendanceFormPageComponent },
  { path: ':attendanceId/edit', component: AttendanceFormPageComponent },
  { path: ':attendanceId', component: AttendanceDetailPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AttendanceRoutingModule { }
