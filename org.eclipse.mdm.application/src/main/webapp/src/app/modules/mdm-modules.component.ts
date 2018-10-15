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
import {Router} from '@angular/router';

@Component({
  selector: 'modules',
  templateUrl: 'mdm-modules.component.html',
  providers: []
})
export class MDMModulesComponent {

  links = [
    { name: 'Details', path: 'details'},
    { name: 'MDM Suche', path: 'search'}
  ];
  constructor(private router: Router) {}
}
