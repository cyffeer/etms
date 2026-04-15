import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EmployeeEventDetailPageComponent } from './pages/employee-event-detail-page/employee-event-detail-page.component';
import { EmployeeEventFormPageComponent } from './pages/employee-event-form-page/employee-event-form-page.component';
import { EmployeeEventListPageComponent } from './pages/employee-event-list-page/employee-event-list-page.component';
import { MonitoringRoutingModule } from './monitoring-routing.module';

@NgModule({
  declarations: [
    EmployeeEventListPageComponent,
    EmployeeEventFormPageComponent,
    EmployeeEventDetailPageComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MonitoringRoutingModule
  ]
})
export class MonitoringModule {}
