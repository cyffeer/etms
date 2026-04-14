import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LeaveListPageComponent } from './pages/leave-list-page/leave-list-page.component';
import { LeaveFormPageComponent } from './pages/leave-form-page/leave-form-page.component';
import { LeaveDetailPageComponent } from './pages/leave-detail-page/leave-detail-page.component';

const routes: Routes = [
  { path: '', component: LeaveListPageComponent },
  { path: 'new', component: LeaveFormPageComponent },
  { path: ':leaveId', component: LeaveDetailPageComponent },
  { path: ':leaveId/edit', component: LeaveFormPageComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LeavesRoutingModule {}