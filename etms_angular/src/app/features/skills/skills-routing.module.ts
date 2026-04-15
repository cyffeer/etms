import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { SkillCreatePageComponent } from './pages/skill-create-page/skill-create-page.component';
import { SkillEditPageComponent } from './pages/skill-edit-page/skill-edit-page.component';
import { SkillListPageComponent } from './pages/skill-list-page/skill-list-page.component';

const routes: Routes = [
  { path: '', component: SkillListPageComponent },
  { path: 'new', component: SkillCreatePageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: ':skillId/edit', component: SkillEditPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } }
];

@NgModule({ imports: [RouterModule.forChild(routes)], exports: [RouterModule] })
export class SkillsRoutingModule {}
