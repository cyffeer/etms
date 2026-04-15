import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { TrngTypeListPageComponent } from './pages/trng-type-list-page/trng-type-list-page.component';
import { TrngTypeFormPageComponent } from './pages/trng-type-form-page/trng-type-form-page.component';
import { TrngInfoListPageComponent } from './pages/trng-info-list-page/trng-info-list-page.component';
import { TrngInfoFormPageComponent } from './pages/trng-info-form-page/trng-info-form-page.component';
import { TrngHistListPageComponent } from './pages/trng-hist-list-page/trng-hist-list-page.component';
import { TrngHistFormPageComponent } from './pages/trng-hist-form-page/trng-hist-form-page.component';
import { VendorListPageComponent } from './pages/vendor-list-page/vendor-list-page.component';
import { VendorFormPageComponent } from './pages/vendor-form-page/vendor-form-page.component';

const routes: Routes = [
  { path: '', redirectTo: 'types', pathMatch: 'full' },
  { path: 'types', component: TrngTypeListPageComponent },
  { path: 'types/new', component: TrngTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: 'types/:trngTypeId/edit', component: TrngTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: 'info', component: TrngInfoListPageComponent },
  { path: 'info/new', component: TrngInfoFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: 'info/:trngInfoId/edit', component: TrngInfoFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: 'history', component: TrngHistListPageComponent },
  { path: 'history/new', component: TrngHistFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: 'history/:trngHistId/edit', component: TrngHistFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: 'vendors', component: VendorListPageComponent },
  { path: 'vendors/new', component: VendorFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: 'vendors/:vendorId/edit', component: VendorFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TrainingRoutingModule {}
