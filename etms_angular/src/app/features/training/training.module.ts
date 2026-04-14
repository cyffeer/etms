import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TrainingRoutingModule } from './training-routing.module';
import { TrngTypeListPageComponent } from './pages/trng-type-list-page/trng-type-list-page.component';
import { TrngTypeFormPageComponent } from './pages/trng-type-form-page/trng-type-form-page.component';
import { TrngInfoListPageComponent } from './pages/trng-info-list-page/trng-info-list-page.component';
import { TrngInfoFormPageComponent } from './pages/trng-info-form-page/trng-info-form-page.component';
import { TrngHistListPageComponent } from './pages/trng-hist-list-page/trng-hist-list-page.component';
import { TrngHistFormPageComponent } from './pages/trng-hist-form-page/trng-hist-form-page.component';

@NgModule({
  declarations: [
    TrngTypeListPageComponent,
    TrngTypeFormPageComponent,
    TrngInfoListPageComponent,
    TrngInfoFormPageComponent,
    TrngHistListPageComponent,
    TrngHistFormPageComponent,
  ],
  imports: [CommonModule, ReactiveFormsModule, FormsModule, TrainingRoutingModule],
})
export class TrainingModule {}