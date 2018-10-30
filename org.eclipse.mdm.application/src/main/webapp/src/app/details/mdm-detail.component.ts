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


import {Component} from '@angular/core';

import {MDMDetailViewComponent} from './mdm-detail-view.component';
import {MDMDescriptiveDataComponent} from './mdm-detail-descriptive-data.component';
import {SensorComponent} from './sensor.component';

@Component({
  selector: 'mdm-detail',
  templateUrl: 'mdm-detail.component.html',
  providers: []
})
export class MDMDetailComponent {

  readonly uut = 'Prüfling';
  readonly ts = 'Testablauf';
  readonly te = 'Messgerät';
  readonly s = 'Sensoren';
  readonly brand = 'Details';
  readonly LblGeneral = 'General';
  readonly LblSensors = 'Sensoren';

}
