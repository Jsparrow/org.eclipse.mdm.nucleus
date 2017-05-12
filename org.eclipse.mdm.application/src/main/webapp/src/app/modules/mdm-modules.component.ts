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
