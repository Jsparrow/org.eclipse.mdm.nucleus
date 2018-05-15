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

import { Component } from '@angular/core';
import {DialogModule} from 'primeng/primeng';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  readonly TtlLogout = 'Logout';
  readonly TtlAbout = 'About';

  links = [
      { name: 'openMDM5 Web', path: '/navigator' },
      { name: 'Administration', path: '/administration' }
  ];
  displayAboutDialog: boolean = false;

  showAboutDialog() {
    this.displayAboutDialog = true;
  }
}
