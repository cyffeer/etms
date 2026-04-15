import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { SkillLevelFormPageComponent } from './pages/skill-level-form-page/skill-level-form-page.component';
import { SkillLevelListPageComponent } from './pages/skill-level-list-page/skill-level-list-page.component';

const routes: Routes = [
  { path: '', component: SkillLevelListPageComponent },
  { path: 'new', component: SkillLevelFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: ':skillLvlId/edit', component: SkillLevelFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SkillLevelsRoutingModule {}
