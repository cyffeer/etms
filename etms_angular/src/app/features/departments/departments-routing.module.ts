import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DepartmentCreatePageComponent } from './pages/department-create-page/department-create-page.component';
import { DepartmentDetailsPageComponent } from './pages/department-details-page/department-details-page.component';
import { DepartmentEditPageComponent } from './pages/department-edit-page/department-edit-page.component';
import { DepartmentListPageComponent } from './pages/department-list-page/department-list-page.component';

const routes: Routes = [
  { path: '', component: DepartmentListPageComponent },
  { path: 'new', component: DepartmentCreatePageComponent },
  { path: ':departmentId/edit', component: DepartmentEditPageComponent },
  { path: ':departmentId', component: DepartmentDetailsPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DepartmentsRoutingModule {}