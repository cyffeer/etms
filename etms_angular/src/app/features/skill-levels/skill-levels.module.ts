import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SkillLevelFormPageComponent } from './pages/skill-level-form-page/skill-level-form-page.component';
import { SkillLevelListPageComponent } from './pages/skill-level-list-page/skill-level-list-page.component';
import { SkillLevelsRoutingModule } from './skill-levels-routing.module';

@NgModule({
  declarations: [SkillLevelListPageComponent, SkillLevelFormPageComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SkillLevelsRoutingModule]
})
export class SkillLevelsModule {}
