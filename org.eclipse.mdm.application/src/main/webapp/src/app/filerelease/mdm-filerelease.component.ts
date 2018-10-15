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


import {Component, OnInit, ViewChild} from '@angular/core';
import {FilereleaseService, Release} from './filerelease.service';
import {MDMFilereleaseDisplayComponent} from './mdm-filerelease-display.component';
import {PropertyService} from '../core/property.service';
import { ModalDirective } from 'ngx-bootstrap';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component({
  selector: 'mdm-filerelease',
  templateUrl: 'mdm-filerelease.component.html',
  styles: ['.box {border: 1px solid #ddd; border-radius: 4px;}']
})
export class MDMFilereleaseComponent implements OnInit {

  incoming: Release[] = [];
  outgoing: Release[] = [];
  release: Release = new Release;
  event = 'display';
  dataHost: string;

  @ViewChild('lgModal')
  lgModal: ModalDirective;

  @ViewChild('smModal')
  smModal: ModalDirective;

  constructor(private service: FilereleaseService,
              private prop: PropertyService,
              private notificationService: MDMNotificationService) {
    this.dataHost = prop.getDataHost();
  }

  ngOnInit() {
    this.getReleases();
  }

  getReleases() {
    this.service.readOutgoging().subscribe(
      releases => this.outgoing = releases,
      error => this.notificationService.notifyError('Ausgehendes Release kann nicht gelesen werden.', error)
    );
    this.service.readIncomming().subscribe(
      releases => this.incoming = releases,
      error => this.notificationService.notifyError('Eingehendes Release kann nicht gelesen werden.', error)
    );
  }
  setData(release) {
    this.release = release;
  }
  getState(release: Release) {
    if (release.state === 'RELEASE_ORDERED') { return 'info'; }
    if (release.state === 'RELEASE_APPROVED') { return 'warning'; }
    if (release.state === 'RELEASE_RELEASED') { return 'success'; }
    if (release.state === 'RELEASE_EXPIRED') { return 'danger'; }
    if (release.state === 'RELEASE_REJECTED') { return 'danger'; }
    if (release.state === 'RELEASE_PROGRESSING_ERROR') { return 'danger'; }
    if (release.state === 'RELEASE_PROGRESSING') { return 'warning'; }
    return 'info';
  }
  rejectRelease(reason: string) {
    this.release.rejectMessage = reason;
    this.service.reject(this.release).subscribe(
      release => this.updateList(release),
      error => this.notificationService.notifyError('Fehler beim Zurückweisen des Release', error)
    );
  }
  approveRelease() {
    this.service.approve(this.release).subscribe(
      release => this.updateList(release),
      error => this.notificationService.notifyError('Fehler beim Bestätigen des Release', error)
    );
    this.release.state = 'RELEASE_PROGRESSING';
  }
  updateList(id) {
    let pos = this.outgoing.map(function(e) { return e.identifier; }).indexOf(id);
    if (pos !== -1) { this.outgoing.splice(pos, 1); }
    pos = this.incoming.map(function(e) { return e.identifier; }).indexOf(id);
    if (pos !== -1) { this.incoming.splice(pos, 1); }
  }
  isDeletable() {
    if (this.release.state !== 'RELEASE_PROGRESSING') { return false; }
    return true;
  }
  deleteRelease() {
    let id = this.release.identifier;
    this.service.delete(this.release).subscribe(data => this.updateList(id), err => console.log(err));
  }
  setEvent(event) {
    this.event = event;
  }
  isReleaseable() {
    if (this.release.state !== 'RELEASE_ORDERED') { return true; }
    return false;
  }
  getFormat(format) {
    return this.service.formatMap[format];
  }
  getTransState(state) {
    return this.service.stateMap[state];
  }
  getDate(date) {
    if (date === 0) { return; }
    return this.service.formatDate(date);
  }
}
