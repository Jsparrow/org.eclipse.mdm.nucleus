/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import { Component, ViewChild, OnInit, EventEmitter} from '@angular/core';

import { PreferenceView, View, ViewService} from './tableview.service';
import { EditViewComponent } from './editview.component';

import {classToClass} from 'class-transformer';
import { ModalDirective } from 'ng2-bootstrap';

import { OverwriteDialogComponent } from '../core/overwrite-dialog.component';
import { MDMNotificationService } from '../core/mdm-notification.service';
import { Scope } from '../core/preference.service';
@Component({
  selector: 'mdm-view',
  templateUrl: 'view.component.html'
})
export class ViewComponent implements OnInit {

  readonly LblSave = 'Speichern';
  readonly LblSaveViewAs = 'Ansicht speichern unter';
  readonly TtlDeleteView = 'Ansicht löschen';
  readonly TtlEditView = 'Ansicht bearbeiten';
  readonly TtlNewView = 'Neue Ansicht erstellen';
  readonly TtlNoNameSet = 'Name nicht gesetzt!';
  readonly TtlSaveView = 'Ansicht speichern';
  readonly TtlSelectView = 'Ansicht auswählen';

  public selectedView: View;
  public groupedViews: { scope: string, view: View[], label: string }[] = [];
  public viewChanged$ = new EventEmitter();
  public viewName = '';

  @ViewChild(EditViewComponent)
  private editViewComponent: EditViewComponent;

  @ViewChild('lgSaveModal')
  childSaveModal: ModalDirective;

  @ViewChild(OverwriteDialogComponent)
  private overwriteDialogComponent: OverwriteDialogComponent;

  constructor(private viewService: ViewService,
    private notificationService: MDMNotificationService) {
  }

  ngOnInit() {
    this.viewService.getViews().subscribe(views => this.setViews(views));
    this.viewService.viewSaved$.subscribe(view => this.onViewSaved(view));
    this.viewService.viewDeleted$.subscribe(() => this.onDeleteView());
  }

  selectView(view: View) {
    this.selectedView = view;
    this.viewChanged$.emit();
  }

  public editSelectedView(e: Event) {
    e.stopPropagation();
    this.editViewComponent.showDialog(this.selectedView).subscribe(v => this.selectedView = v);
  }

  public newView(e: Event) {
    e.stopPropagation();
    this.editViewComponent.showDialog(new View()).subscribe(v => this.selectedView = v);
  }

  private onDeleteView() {
    this.viewService.getViews().subscribe(prefViews => {
      this.getGroupedView(prefViews);
      if (prefViews.find(pv => pv.view.name === this.selectedView.name) === undefined
          && this.viewService.defaultPrefViews.find(pv => pv.view.name === this.selectedView.name) === undefined) {
        this.selectView(prefViews[0].view);
      }
    });
  }

 private onViewSaved(view: View) {
   this.viewService.getViews().subscribe(prefViews => this.getGroupedView(prefViews));
    // if (this.selectedView.name === view.name) {
    //   this.selectView(classToClass(view));
    // }
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
      if (pushed === false) { this.groupedViews.push({
        scope: prefViews[i].scope,
        view: [prefViews[i].view],
        label: Scope.toLabel(prefViews[i].scope)
      }); }
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
      let view = classToClass(this.selectedView);
      view.name = this.viewName;
      this.viewService.saveView(view);
      this.childSaveModal.hide();
      this.selectView(classToClass(view));
    } else {
      this.childSaveModal.show();
    }
  }

  showSaveModal(e: Event) {
    e.stopPropagation();
    this.viewName = this.selectedView.name === 'Neue Ansicht' ? '' : this.selectedView.name;
    this.childSaveModal.show();
  }

  deleteView(e: Event) {
    e.stopPropagation();
    let userGroup = this.groupedViews.find(gv => gv.scope === Scope.USER);
    if (userGroup && userGroup.view.length > 0) {
      this.viewService.deleteView(this.selectedView.name).subscribe(() =>
        this.viewService.getViews().subscribe(views => {
          this.setViews(views);
          this.viewService.viewDeleted$.emit();
        },
          () => this.notificationService.notifyError('Server Error.',
            'Die Ansicht konnte auf Grund eine Server Fehlers nicht gelöscht werden!'))
      );
    } else {
      this.notificationService.notifyError('Forbidden.',
        'System Einstellungen können nicht aus der Anwendung geändert werden.'
        + ' Sollten sie entsprechende Zugriffsrechte besitzen,'
        + ' können Sie diese Einstellungen über den Administrationsbereich bearbeiten');
    }
  }

  getSaveBtnTitle() {
    return this.viewName ? this.TtlSaveView : this.TtlNoNameSet;
  }
}
