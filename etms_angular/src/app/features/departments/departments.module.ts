import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DepartmentsRoutingModule } from './departments-routing.module';
import { DepartmentListPageComponent } from './pages/department-list-page/department-list-page.component';
import { DepartmentDetailsPageComponent } from './pages/department-details-page/department-details-page.component';
import { DepartmentCreatePageComponent } from './pages/department-create-page/department-create-page.component';
import { DepartmentEditPageComponent } from './pages/department-edit-page/department-edit-page.component';

@NgModule({
  declarations: [
    DepartmentListPageComponent,
    DepartmentDetailsPageComponent,
    DepartmentCreatePageComponent,
    DepartmentEditPageComponent
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DepartmentsRoutingModule]
})
export class DepartmentsModule {}
