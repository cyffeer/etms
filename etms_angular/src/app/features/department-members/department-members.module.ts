import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DepartmentMembersRoutingModule } from './department-members-routing.module';
import { DepartmentMemberListPageComponent } from './pages/department-member-list-page/department-member-list-page.component';
import { DepartmentMemberFormPageComponent } from './pages/department-member-form-page/department-member-form-page.component';
import { DepartmentMemberDetailPageComponent } from './pages/department-member-detail-page/department-member-detail-page.component';


@NgModule({
  declarations: [
    DepartmentMemberListPageComponent,
    DepartmentMemberFormPageComponent,
    DepartmentMemberDetailPageComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    DepartmentMembersRoutingModule
  ]
})
export class DepartmentMembersModule { }
