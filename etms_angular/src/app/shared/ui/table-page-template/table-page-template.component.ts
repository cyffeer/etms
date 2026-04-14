import { Component, ContentChild, Input, TemplateRef } from '@angular/core';

export interface TableColumn {
  key: string;
  label: string;
}

@Component({
  selector: 'app-table-page-template',
  templateUrl: './table-page-template.component.html',
  styleUrls: ['./table-page-template.component.css']
})
export class TablePageTemplateComponent {
  @Input() title = 'List';
  @Input() columns: TableColumn[] = [];
  @Input() rows: any[] = [];

  @ContentChild('rowActions', { read: TemplateRef }) rowActions?: TemplateRef<any>;
}