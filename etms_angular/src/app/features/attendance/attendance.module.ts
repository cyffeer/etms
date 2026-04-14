import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AttendanceRoutingModule } from './attendance-routing.module';
import { AttendanceListPageComponent } from './pages/attendance-list-page/attendance-list-page.component';
import { AttendanceFormPageComponent } from './pages/attendance-form-page/attendance-form-page.component';
import { AttendanceDetailPageComponent } from './pages/attendance-detail-page/attendance-detail-page.component';


@NgModule({
  declarations: [
    AttendanceListPageComponent,
    AttendanceFormPageComponent,
    AttendanceDetailPageComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AttendanceRoutingModule
  ]
})
export class AttendanceModule { }
