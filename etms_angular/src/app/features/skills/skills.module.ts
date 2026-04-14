import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SkillsRoutingModule } from './skills-routing.module';
import { SkillListPageComponent } from './pages/skill-list-page/skill-list-page.component';
import { SkillCreatePageComponent } from './pages/skill-create-page/skill-create-page.component';
import { SkillEditPageComponent } from './pages/skill-edit-page/skill-edit-page.component';

@NgModule({
  declarations: [SkillListPageComponent, SkillCreatePageComponent, SkillEditPageComponent],
  imports: [CommonModule, ReactiveFormsModule, SkillsRoutingModule]
})
export class SkillsModule {}