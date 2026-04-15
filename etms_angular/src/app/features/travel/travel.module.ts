import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TravelRoutingModule } from './travel-routing.module';
import { TravelPassportListPageComponent } from './pages/travel-passport-list-page/travel-passport-list-page.component';
import { TravelVisaListPageComponent } from './pages/travel-visa-list-page/travel-visa-list-page.component';
import { TravelVisaTypeListPageComponent } from './pages/travel-visa-type-list-page/travel-visa-type-list-page.component';
import { TravelVisaTypeFormPageComponent } from './pages/travel-visa-type-form-page/travel-visa-type-form-page.component';

@NgModule({
  declarations: [
    TravelPassportListPageComponent,
    TravelVisaListPageComponent,
    TravelVisaTypeListPageComponent,
    TravelVisaTypeFormPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TravelRoutingModule,
  ],
})
export class TravelModule {}
