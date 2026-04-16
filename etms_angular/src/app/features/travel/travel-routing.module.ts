import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { TravelPassportListPageComponent } from './pages/travel-passport-list-page/travel-passport-list-page.component';
import { TravelVisaListPageComponent } from './pages/travel-visa-list-page/travel-visa-list-page.component';
import { TravelVisaTypeFormPageComponent } from './pages/travel-visa-type-form-page/travel-visa-type-form-page.component';
import { TravelVisaTypeListPageComponent } from './pages/travel-visa-type-list-page/travel-visa-type-list-page.component';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'passports' },
  { path: 'passports', component: TravelPassportListPageComponent },
  { path: 'visas', component: TravelVisaListPageComponent },
  { path: 'visa-types', component: TravelVisaTypeListPageComponent },
  { path: 'visa-types/new', component: TravelVisaTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR'] } },
  { path: 'visa-types/:visaTypeId/edit', component: TravelVisaTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR'] } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TravelRoutingModule {}
