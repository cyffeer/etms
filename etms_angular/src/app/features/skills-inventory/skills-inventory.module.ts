import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SkillsInventoryRoutingModule } from './skills-inventory-routing.module';
import { SkillsInventoryListPageComponent } from './pages/skills-inventory-list-page/skills-inventory-list-page.component';
import { SkillsInventoryCreatePageComponent } from './pages/skills-inventory-create-page/skills-inventory-create-page.component';
import { SkillsInventoryEditPageComponent } from './pages/skills-inventory-edit-page/skills-inventory-edit-page.component';

@NgModule({
  declarations: [
    SkillsInventoryListPageComponent,
    SkillsInventoryCreatePageComponent,
    SkillsInventoryEditPageComponent
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SkillsInventoryRoutingModule]
})
export class SkillsInventoryModule {}
