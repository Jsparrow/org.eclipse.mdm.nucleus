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

import { Component, ViewChild, EventEmitter} from '@angular/core';
import { ModalDirective } from 'ng2-bootstrap';

@Component({
  selector: 'overwrite-dialog',
  templateUrl: 'overwrite-dialog.component.html'
})
export class OverwriteDialogComponent {

  label: string;
  overwriteEvent = new EventEmitter<boolean>();

  @ViewChild('lgOverwriteModal')
  private childOverwriteModal: ModalDirective;

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
