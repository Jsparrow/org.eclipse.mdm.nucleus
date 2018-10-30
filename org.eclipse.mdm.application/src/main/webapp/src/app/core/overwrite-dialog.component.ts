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


import { Component, ViewChild, EventEmitter} from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap';

@Component({
  selector: 'overwrite-dialog',
  templateUrl: 'overwrite-dialog.component.html'
})
export class OverwriteDialogComponent {

  readonly LblCancel = 'Abbrechen';
  readonly LblOverwrite = 'Überschreiben';

  label: string;
  overwriteEvent = new EventEmitter<boolean>();

  @ViewChild('lgOverwriteModal')
  childOverwriteModal: ModalDirective;

  constructor () {}

  showOverwriteModal(label: string) {
    this.label = label;
    this.childOverwriteModal.show();
    return this.overwriteEvent;
  }

  overwrite(b: boolean) {
    this.childOverwriteModal.hide();
    this.overwriteEvent.emit(b);
  }
}
