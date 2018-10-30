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


import {Component, Input} from '@angular/core';
import {Release, FilereleaseService} from './filerelease.service';

@Component({
  selector: 'mdm-filerelease-display',
  templateUrl: 'mdm-filerelease-display.component.html',
  styles: [],
  providers: []
})
export class MDMFilereleaseDisplayComponent {
  @Input() release: Release;

  constructor(private service: FilereleaseService) {}

  getFormat(format) {
    return this.service.formatMap[format];
  }

  getState(state) {
    return this.service.stateMap[state];
  }

  getDate(date) {
    if (date === 0) { return; }
    return this.service.formatDate(date);
  }
}
