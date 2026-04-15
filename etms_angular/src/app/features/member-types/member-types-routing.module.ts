import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { MemberTypeListPageComponent } from './pages/member-type-list-page/member-type-list-page.component';
import { MemberTypeFormPageComponent } from './pages/member-type-form-page/member-type-form-page.component';
import { MemberTypeDetailPageComponent } from './pages/member-type-detail-page/member-type-detail-page.component';

const routes: Routes = [
  { path: '', component: MemberTypeListPageComponent },
  { path: 'new', component: MemberTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: ':memberTypeId', component: MemberTypeDetailPageComponent },
  { path: ':memberTypeId/edit', component: MemberTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
];

@NgModule({ imports: [RouterModule.forChild(routes)], exports: [RouterModule] })
export class MemberTypesRoutingModule {}
