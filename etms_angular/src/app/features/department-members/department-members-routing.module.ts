import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { DepartmentMemberDetailPageComponent } from './pages/department-member-detail-page/department-member-detail-page.component';
import { DepartmentMemberFormPageComponent } from './pages/department-member-form-page/department-member-form-page.component';
import { DepartmentMemberListPageComponent } from './pages/department-member-list-page/department-member-list-page.component';

const routes: Routes = [
  { path: '', component: DepartmentMemberListPageComponent },
  { path: 'new', component: DepartmentMemberFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
  { path: ':deptMemberId/edit', component: DepartmentMemberFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
  { path: ':deptMemberId', component: DepartmentMemberDetailPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DepartmentMembersRoutingModule { }
