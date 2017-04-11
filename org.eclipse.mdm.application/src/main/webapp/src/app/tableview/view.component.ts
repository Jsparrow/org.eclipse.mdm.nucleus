import { Component, ViewChild, OnInit, EventEmitter} from '@angular/core';

import { PreferenceView, View, ViewService} from './tableview.service';
import { EditViewComponent } from './editview.component';

import {classToClass} from 'class-transformer';
import { ModalDirective } from 'ng2-bootstrap';
import { OverwriteDialogComponent } from '../core/overwrite-dialog.component';

@Component({
  selector: 'mdm-view',
  templateUrl: 'view.component.html'
})
export class ViewComponent implements OnInit {

  public selectedView: View;
  public groupedViews: {scope: string, view: View[]}[] = [];
  public viewChanged$ = new EventEmitter();
  public viewName = '';

  @ViewChild(EditViewComponent)
  private editViewComponent: EditViewComponent;

  @ViewChild('lgSaveModal')
  childSaveModal: ModalDirective;

  @ViewChild(OverwriteDialogComponent)
  private overwriteDialogComponent: OverwriteDialogComponent;

  constructor(private viewService: ViewService) {
  }

  ngOnInit() {
    this.viewService.getViews().subscribe(views => this.setViews(views));
    this.viewService.viewsChanged$.subscribe(view => this.onViewsChanged(view));
  }

  selectView(view: View) {
    this.selectedView = view;
    this.viewChanged$.emit();
  }

  public editSelectedView(e: Event) {
    e.stopPropagation();
    this.editViewComponent.showDialog(this.selectedView).subscribe( v => this.selectedView = v);
  }

  public newView(e: Event) {
    e.stopPropagation();
    this.editViewComponent.showDialog(new View()).subscribe( v => this.selectedView = v);
  }

  private onViewsChanged(view: View) {
    this.viewService.getViews().subscribe(prefViews => this.getGroupedView(prefViews));
    if (this.selectedView.name === view.name) {
      this.selectView(classToClass(view));
    }
  }

  private setViews(prefViews: PreferenceView[]) {
    this.getGroupedView(prefViews);
    this.selectView(prefViews[0].view);
  }

  private getGroupedView(prefViews: PreferenceView[]) {
    this.groupedViews = [];
    for (let i = 0; i < prefViews.length; i++) {
      let pushed = false;
      for (let j = 0; j < this.groupedViews.length; j++) {
        if (prefViews[i].scope === this.groupedViews[j].scope) {
          this.groupedViews[j].view.push(prefViews[i].view);
          pushed = true;
        }
      }
      if (pushed === false) { this.groupedViews.push({scope: prefViews[i].scope, view: [prefViews[i].view]}); }
    }
  }

  saveView(e: Event) {
    e.stopPropagation();
    if (this.groupedViews.find(gv => gv.view.find(v => v.name === this.viewName) !== undefined)) {
      this.childSaveModal.hide();
      this.overwriteDialogComponent.showOverwriteModal('eine Ansicht').subscribe(ovw => this.saveView2(ovw));
    } else {
      this.saveView2(true);
    }
  }

  saveView2(save: boolean) {
    if (save) {
      this.selectedView.name = this.viewName;
      this.viewService.saveView(this.selectedView);
      this.childSaveModal.hide();
    } else {
      this.childSaveModal.show();
    }
  }

  showSaveModal(e: Event) {
    e.stopPropagation();
    this.viewName = this.selectedView.name === 'Neue Ansicht' ? '' : this.selectedView.name;
    this.childSaveModal.show();
  }
}
