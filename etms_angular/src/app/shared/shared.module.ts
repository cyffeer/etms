import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormPageTemplateComponent } from './ui/form-page-template/form-page-template.component';
import { TablePageTemplateComponent } from './ui/table-page-template/table-page-template.component';

@NgModule({
  declarations: [FormPageTemplateComponent, TablePageTemplateComponent],
  imports: [CommonModule],
  exports: [FormPageTemplateComponent, TablePageTemplateComponent]
})
export class SharedModule {}