import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { NihongoResultsPageComponent } from './pages/nihongo-results-page/nihongo-results-page.component';
import { NihongoTestsPageComponent } from './pages/nihongo-tests-page/nihongo-tests-page.component';
import { NihongoTypeFormPageComponent } from './pages/nihongo-type-form-page/nihongo-type-form-page.component';
import { NihongoTypesPageComponent } from './pages/nihongo-types-page/nihongo-types-page.component';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'results' },
  { path: 'results', component: NihongoResultsPageComponent },
  { path: 'tests', component: NihongoTestsPageComponent },
  { path: 'types', component: NihongoTypesPageComponent },
  { path: 'types/new', component: NihongoTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: 'types/:npTypeId/edit', component: NihongoTypeFormPageComponent, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NihongoRoutingModule {}
