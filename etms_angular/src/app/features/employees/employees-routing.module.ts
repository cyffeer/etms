import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeCreatePageComponent } from './pages/employee-create-page/employee-create-page.component';
import { EmployeeDetailsPageComponent } from './pages/employee-details-page/employee-details-page.component';
import { EmployeeEditPageComponent } from './pages/employee-edit-page/employee-edit-page.component';
import { EmployeeListPageComponent } from './pages/employee-list-page/employee-list-page.component';

const routes: Routes = [
  { path: '', component: EmployeeListPageComponent },
  { path: 'new', component: EmployeeCreatePageComponent },
  { path: ':employeeId/edit', component: EmployeeEditPageComponent },
  { path: ':employeeId', component: EmployeeDetailsPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmployeesRoutingModule {}