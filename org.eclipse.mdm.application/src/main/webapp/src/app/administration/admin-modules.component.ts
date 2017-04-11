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
  selector: 'admin-modules',
  templateUrl: 'admin-modules.component.html',
  providers: []
})
export class AdminModulesComponent {

  brand = 'Scope';
  links = [
    { name: 'System', path: 'system'},
    { name: 'Source', path: 'source'},
    { name: 'User', path: 'user'}
  ];
  constructor(private router: Router) {}
}
