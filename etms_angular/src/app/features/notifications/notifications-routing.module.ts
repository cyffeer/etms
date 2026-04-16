import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { NotificationListPageComponent } from './pages/notification-list-page/notification-list-page.component';

const routes: Routes = [
  { path: '', component: NotificationListPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NotificationsRoutingModule {}
