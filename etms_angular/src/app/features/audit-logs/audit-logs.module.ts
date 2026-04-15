import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditLogsRoutingModule } from './audit-logs-routing.module';
import { AuditLogListPageComponent } from './pages/audit-log-list-page/audit-log-list-page.component';

@NgModule({
  declarations: [AuditLogListPageComponent],
  imports: [CommonModule, FormsModule, AuditLogsRoutingModule]
})
export class AuditLogsModule {}
