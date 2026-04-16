import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { ROLE_GROUPS } from '../../core/config/permissions.config';
import { EmployeeCreatePageComponent } from './pages/employee-create-page/employee-create-page.component';
import { EmployeeDetailsPageComponent } from './pages/employee-details-page/employee-details-page.component';
import { EmployeeEditPageComponent } from './pages/employee-edit-page/employee-edit-page.component';
import { EmployeeListPageComponent } from './pages/employee-list-page/employee-list-page.component';

const routes: Routes = [
  { path: '', component: EmployeeListPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER } },
  { path: 'new', component: EmployeeCreatePageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER } },
  { path: ':employeeId/edit', component: EmployeeEditPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_ONLY } },
  { path: ':employeeId', component: EmployeeDetailsPageComponent, canActivate: [AuthGuard], data: { roles: ROLE_GROUPS.ADMIN_MANAGER_EMPLOYEE, selfOnly: true } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmployeesRoutingModule {}
