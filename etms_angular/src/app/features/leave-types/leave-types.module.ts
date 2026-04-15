import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LeaveTypesRoutingModule } from './leave-types-routing.module';
import { LeaveTypeListPageComponent } from './pages/leave-type-list-page/leave-type-list-page.component';
import { LeaveTypeFormPageComponent } from './pages/leave-type-form-page/leave-type-form-page.component';
import { LeaveTypeDetailPageComponent } from './pages/leave-type-detail-page/leave-type-detail-page.component';

@NgModule({
  declarations: [
    LeaveTypeListPageComponent,
    LeaveTypeFormPageComponent,
    LeaveTypeDetailPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    LeaveTypesRoutingModule,
  ],
})
export class LeaveTypesModule {}
