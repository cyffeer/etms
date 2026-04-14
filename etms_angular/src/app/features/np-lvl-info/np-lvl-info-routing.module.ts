import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NpLvlInfoDetailPageComponent } from './pages/np-lvl-info-detail-page/np-lvl-info-detail-page.component';
import { NpLvlInfoFormPageComponent } from './pages/np-lvl-info-form-page/np-lvl-info-form-page.component';
import { NpLvlInfoListPageComponent } from './pages/np-lvl-info-list-page/np-lvl-info-list-page.component';

const routes: Routes = [
  { path: '', component: NpLvlInfoListPageComponent },
  { path: 'new', component: NpLvlInfoFormPageComponent },
  { path: ':npLvlInfoId/edit', component: NpLvlInfoFormPageComponent },
  { path: ':npLvlInfoId', component: NpLvlInfoDetailPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NpLvlInfoRoutingModule { }
