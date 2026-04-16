import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeSelfRoutingModule } from './employee-self-routing.module';
import { EmployeeProfilePageComponent } from './pages/employee-profile-page/employee-profile-page.component';
import { EmployeeResignationPageComponent } from './pages/employee-resignation-page/employee-resignation-page.component';
import { EmployeeCasesPageComponent } from './pages/employee-cases-page/employee-cases-page.component';
import { EmployeeAttendancePageComponent } from './pages/employee-attendance-page/employee-attendance-page.component';
import { EmployeeLeavePageComponent } from './pages/employee-leave-page/employee-leave-page.component';

@NgModule({
  declarations: [
    EmployeeProfilePageComponent,
    EmployeeResignationPageComponent,
    EmployeeCasesPageComponent,
    EmployeeAttendancePageComponent,
    EmployeeLeavePageComponent
  ],
  imports: [CommonModule, FormsModule, EmployeeSelfRoutingModule]
})
export class EmployeeSelfModule {}
