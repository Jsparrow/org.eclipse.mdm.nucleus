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
