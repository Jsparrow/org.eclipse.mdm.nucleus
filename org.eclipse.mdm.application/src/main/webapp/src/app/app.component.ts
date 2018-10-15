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
