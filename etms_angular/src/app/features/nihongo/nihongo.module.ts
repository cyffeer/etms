import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NihongoRoutingModule } from './nihongo-routing.module';
import { NihongoResultsPageComponent } from './pages/nihongo-results-page/nihongo-results-page.component';
import { NihongoTestsPageComponent } from './pages/nihongo-tests-page/nihongo-tests-page.component';
import { NihongoTypeFormPageComponent } from './pages/nihongo-type-form-page/nihongo-type-form-page.component';
import { NihongoTypesPageComponent } from './pages/nihongo-types-page/nihongo-types-page.component';

@NgModule({
  declarations: [
    NihongoResultsPageComponent,
    NihongoTestsPageComponent,
    NihongoTypesPageComponent,
    NihongoTypeFormPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NihongoRoutingModule,
  ],
})
export class NihongoModule {}
