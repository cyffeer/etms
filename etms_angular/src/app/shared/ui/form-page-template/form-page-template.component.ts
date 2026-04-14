import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-form-page-template',
  templateUrl: './form-page-template.component.html',
  styleUrls: ['./form-page-template.component.css']
})
export class FormPageTemplateComponent {
  @Input() title = 'Form';
  @Input() submitLabel = 'Save';
  @Input() cancelLabel = 'Cancel';

  @Output() submitted = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();
}