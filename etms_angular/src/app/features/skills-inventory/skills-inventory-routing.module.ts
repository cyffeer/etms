import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { SkillsInventoryCreatePageComponent } from './pages/skills-inventory-create-page/skills-inventory-create-page.component';
import { SkillsInventoryEditPageComponent } from './pages/skills-inventory-edit-page/skills-inventory-edit-page.component';
import { SkillsInventoryListPageComponent } from './pages/skills-inventory-list-page/skills-inventory-list-page.component';

const routes: Routes = [
  { path: '', component: SkillsInventoryListPageComponent },
  { path: 'new', component: SkillsInventoryCreatePageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } },
  { path: ':skillsInventoryId/edit', component: SkillsInventoryEditPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'HR', 'MANAGER'] } }
];

@NgModule({ imports: [RouterModule.forChild(routes)], exports: [RouterModule] })
export class SkillsInventoryRoutingModule {}
