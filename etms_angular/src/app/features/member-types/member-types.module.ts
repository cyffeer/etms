import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { MemberTypesRoutingModule } from './member-types-routing.module';
import { MemberTypeListPageComponent } from './pages/member-type-list-page/member-type-list-page.component';
import { MemberTypeFormPageComponent } from './pages/member-type-form-page/member-type-form-page.component';
import { MemberTypeDetailPageComponent } from './pages/member-type-detail-page/member-type-detail-page.component';


@NgModule({
  declarations: [
    MemberTypeListPageComponent,
    MemberTypeFormPageComponent,
    MemberTypeDetailPageComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MemberTypesRoutingModule
  ]
})
export class MemberTypesModule { }
