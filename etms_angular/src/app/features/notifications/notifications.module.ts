import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotificationsRoutingModule } from './notifications-routing.module';
import { NotificationListPageComponent } from './pages/notification-list-page/notification-list-page.component';

@NgModule({
  declarations: [NotificationListPageComponent],
  imports: [
    CommonModule,
    FormsModule,
    NotificationsRoutingModule,
  ],
})
export class NotificationsModule {}
