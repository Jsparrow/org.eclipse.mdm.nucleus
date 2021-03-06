/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


import { Component, ViewChild, OnInit, EventEmitter} from '@angular/core';

import { PreferenceView, View, ViewService} from './tableview.service';
import { EditViewComponent } from './editview.component';

import {classToClass} from 'class-transformer';
import { ModalDirective } from 'ngx-bootstrap';

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
  readonly LblExistingUserNames = 'Vorhandene Ansichtsnamen';
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
  public userViewNames: string[];
  public selectedRow: string;
  public lazySelectedRow: string;

  @ViewChild(EditViewComponent)
  editViewComponent: EditViewComponent;

  @ViewChild('lgSaveModal')
  childSaveModal: ModalDirective;

  @ViewChild(OverwriteDialogComponent)
  overwriteDialogComponent: OverwriteDialogComponent;

  constructor(private viewService: ViewService,
              private notificationService: MDMNotificationService) {
  }

  ngOnInit() {
    this.viewService.getViews().subscribe(
      views => this.setViews(views),
      error => this.notificationService.notifyError('Ansichten können nicht geladen werden.', error)
    );
    this.viewService.viewSaved$.subscribe(
      view => this.onViewSaved(view),
      error => this.notificationService.notifyError('Es ist ein Fehler beim Speichern der Ansicht aufgetreten.', error)
    );
    this.viewService.viewDeleted$.subscribe(
      () => this.onDeleteView(),
      error => this.notificationService.notifyError('Es ist ein Fehler beim Löschen der Ansicht aufgetreten.', error)
    );
  }

  selectView(view: View) {
    this.selectedView = view;
    this.viewChanged$.emit();
  }

  public editSelectedView(e: Event) {
    e.stopPropagation();
    this.editViewComponent.showDialog(this.selectedView).subscribe(
      v => this.selectedView = v,
      error => this.notificationService.notifyError('Ansicht kann nicht ausgewählt werden.', error)
    );
  }

  public newView(e: Event) {
    e.stopPropagation();
    this.editViewComponent.showDialog(new View()).subscribe(
      v => this.selectedView = v,
      error => this.notificationService.notifyError('Ansichtseditor kann nicht angezeigt werden.', error)
    );
  }

  private onDeleteView() {
    this.viewService.getViews().subscribe(
      prefViews => {
        this.getGroupedView(prefViews);
        if (prefViews.find(pv => pv.view.name === this.selectedView.name) === undefined
            && this.viewService.defaultPrefViews.find(pv => pv.view.name === this.selectedView.name) === undefined) {
          this.selectView(prefViews[0].view);
        }
      },
      error => this.notificationService.notifyError('Ansichten konnten nicht aktualisiert werden.', error)
    );
  }

 private onViewSaved(view: View) {
   this.viewService.getViews().subscribe(
     prefViews => this.getGroupedView(prefViews),
     error => this.notificationService.notifyError('Ansichten konnten nicht aktualisiert werden.', error)
   );
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
    this.updateUserViewNames();
  }

  private updateUserViewNames() {
    this.viewService.getViews().subscribe(
      prefViews => this.userViewNames = prefViews.filter(pv => pv.scope === Scope.USER)
                                                 .map(pv => pv.view.name),
      error => this.notificationService.notifyError('Ansichten konnten nicht aktualisiert werden.', error)
    );
  }

  saveView(e: Event) {
    e.stopPropagation();
    if (this.groupedViews.find(gv => gv.view.find(v => v.name === this.viewName) !== undefined)) {
      this.childSaveModal.hide();
      this.overwriteDialogComponent.showOverwriteModal('eine Ansicht').subscribe(
        needSave => this.saveView2(needSave),
        error => {
          this.saveView2(false);
          this.notificationService.notifyError('Ansicht konnte nicht gespeichert werden.', error);
        }
      );
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
        this.viewService.getViews().subscribe(
          views => {
            this.setViews(views);
            this.viewService.viewDeleted$.emit();
          },
          error => this.notificationService.notifyError('Die Ansicht kann nicht gelöscht werden.', error))
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

  onRowSelect(e: any) {
    if (this.lazySelectedRow !== e.data) {
      this.selectedRow = e.data;
      this.viewName = e.data;
    } else {
      this.selectedRow = undefined;
      this.viewName = '';
    }
    this.lazySelectedRow = this.selectedRow;
  }
}
