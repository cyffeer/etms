import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { AuditLogListPageComponent } from './pages/audit-log-list-page/audit-log-list-page.component';

const routes: Routes = [
  {
    path: '',
    component: AuditLogListPageComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'HR', 'MANAGER'] }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuditLogsRoutingModule {}
