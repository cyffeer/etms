import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { NpLvlInfoRoutingModule } from './np-lvl-info-routing.module';
import { NpLvlInfoListPageComponent } from './pages/np-lvl-info-list-page/np-lvl-info-list-page.component';
import { NpLvlInfoFormPageComponent } from './pages/np-lvl-info-form-page/np-lvl-info-form-page.component';
import { NpLvlInfoDetailPageComponent } from './pages/np-lvl-info-detail-page/np-lvl-info-detail-page.component';


@NgModule({
  declarations: [
    NpLvlInfoListPageComponent,
    NpLvlInfoFormPageComponent,
    NpLvlInfoDetailPageComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NpLvlInfoRoutingModule
  ]
})
export class NpLvlInfoModule { }
