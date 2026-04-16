import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LeavesRoutingModule } from './leaves-routing.module';
import { LeaveListPageComponent } from './pages/leave-list-page/leave-list-page.component';
import { LeaveFormPageComponent } from './pages/leave-form-page/leave-form-page.component';
import { LeaveDetailPageComponent } from './pages/leave-detail-page/leave-detail-page.component';
import { LeaveBalancePageComponent } from './pages/leave-balance-page/leave-balance-page.component';

@NgModule({
  declarations: [
    LeaveListPageComponent,
    LeaveFormPageComponent,
    LeaveDetailPageComponent,
    LeaveBalancePageComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    LeavesRoutingModule
  ]
})
export class LeavesModule {}
