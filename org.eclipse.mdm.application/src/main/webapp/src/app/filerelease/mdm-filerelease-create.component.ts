/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import {Component, Input, Output, EventEmitter, ViewChild} from '@angular/core';
import { ModalDirective } from 'ng2-bootstrap';
import {Release, FilereleaseService} from './filerelease.service';
import {Node} from '../navigator/node';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component({
  selector: 'mdm-filerelease-create',
  templateUrl: 'mdm-filerelease-create.component.html'
})
export class MDMFilereleaseCreateComponent {

  @Input() disabled: boolean;
  @Input() node: Node;
  @Output() onSubmit = new EventEmitter<boolean>();
  release: Release = new Release;
  options = ['PAK2RAW', 'PAK2ATFX'];
  expire = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  @ViewChild('lgModal')
  lgModal: ModalDirective;

  constructor(private service: FilereleaseService,
              private notificationService: MDMNotificationService) {}

  getFormat(key) {
      return this.service.formatMap[key];
  }

  createRelease() {
    this.release.identifier = '';
    this.release.state = '';
    this.release.name = this.node.name;
    this.release.sourceName = this.node.sourceName;
    this.release.typeName = this.node.type;
    this.release.id = this.node.id;
    this.release.sender = '';
    this.release.receiver = '';
    this.release.rejectMessage = '';
    this.release.errorMessage = '';
    this.release.fileLink = '';
    this.release.expire = 0;
    this.service.create(this.release).subscribe(
      release => this.release = release,
      error => this.notificationService.notifyError('Release kann nicht erzeugt werden.', error)
    );
    this.clear();
    this.onSubmit.emit(true);
    this.lgModal.hide();
  }

  clear() {
    this.release = new Release();
  }
}
