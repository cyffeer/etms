import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { LeaveListPageComponent } from './pages/leave-list-page/leave-list-page.component';
import { LeaveFormPageComponent } from './pages/leave-form-page/leave-form-page.component';
import { LeaveDetailPageComponent } from './pages/leave-detail-page/leave-detail-page.component';
import { LeaveBalancePageComponent } from './pages/leave-balance-page/leave-balance-page.component';

const routes: Routes = [
  { path: '', component: LeaveListPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } },
  { path: 'balances', component: LeaveBalancePageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } },
  { path: 'new', component: LeaveFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } },
  { path: ':leaveId', component: LeaveDetailPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } },
  { path: ':leaveId/edit', component: LeaveFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'] } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LeavesRoutingModule {}
