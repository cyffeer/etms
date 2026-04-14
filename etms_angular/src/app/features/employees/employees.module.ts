import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EmployeesRoutingModule } from './employees-routing.module';
import { EmployeeListPageComponent } from './pages/employee-list-page/employee-list-page.component';
import { EmployeeDetailsPageComponent } from './pages/employee-details-page/employee-details-page.component';
import { EmployeeCreatePageComponent } from './pages/employee-create-page/employee-create-page.component';
import { EmployeeEditPageComponent } from './pages/employee-edit-page/employee-edit-page.component';

@NgModule({
  declarations: [
    EmployeeListPageComponent,
    EmployeeDetailsPageComponent,
    EmployeeCreatePageComponent,
    EmployeeEditPageComponent
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, EmployeesRoutingModule]
})
export class EmployeesModule {}
