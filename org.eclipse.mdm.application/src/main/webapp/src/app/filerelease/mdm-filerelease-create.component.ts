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


import {Component, Input, Output, EventEmitter, ViewChild} from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap';
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
