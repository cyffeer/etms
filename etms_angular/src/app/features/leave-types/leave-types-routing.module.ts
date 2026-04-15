import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LeaveTypeListPageComponent } from './pages/leave-type-list-page/leave-type-list-page.component';
import { LeaveTypeFormPageComponent } from './pages/leave-type-form-page/leave-type-form-page.component';
import { LeaveTypeDetailPageComponent } from './pages/leave-type-detail-page/leave-type-detail-page.component';

const routes: Routes = [
  { path: '', component: LeaveTypeListPageComponent },
  { path: 'new', component: LeaveTypeFormPageComponent },
  { path: ':leaveTypeId', component: LeaveTypeDetailPageComponent },
  { path: ':leaveTypeId/edit', component: LeaveTypeFormPageComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LeaveTypesRoutingModule {}
