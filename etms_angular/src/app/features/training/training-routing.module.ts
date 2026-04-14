import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TrngTypeListPageComponent } from './pages/trng-type-list-page/trng-type-list-page.component';
import { TrngTypeFormPageComponent } from './pages/trng-type-form-page/trng-type-form-page.component';
import { TrngInfoListPageComponent } from './pages/trng-info-list-page/trng-info-list-page.component';
import { TrngInfoFormPageComponent } from './pages/trng-info-form-page/trng-info-form-page.component';
import { TrngHistListPageComponent } from './pages/trng-hist-list-page/trng-hist-list-page.component';
import { TrngHistFormPageComponent } from './pages/trng-hist-form-page/trng-hist-form-page.component';

const routes: Routes = [
  { path: '', redirectTo: 'types', pathMatch: 'full' },
  { path: 'types', component: TrngTypeListPageComponent },
  { path: 'types/new', component: TrngTypeFormPageComponent },
  { path: 'types/:trngTypeId/edit', component: TrngTypeFormPageComponent },
  { path: 'info', component: TrngInfoListPageComponent },
  { path: 'info/new', component: TrngInfoFormPageComponent },
  { path: 'info/:trngInfoId/edit', component: TrngInfoFormPageComponent },
  { path: 'history', component: TrngHistListPageComponent },
  { path: 'history/new', component: TrngHistFormPageComponent },
  { path: 'history/:trngHistId/edit', component: TrngHistFormPageComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TrainingRoutingModule {}