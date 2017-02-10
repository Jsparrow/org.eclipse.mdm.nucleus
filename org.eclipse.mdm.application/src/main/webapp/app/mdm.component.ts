// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component( {
    selector: 'mdm-web',
    templateUrl: 'mdm.component.html',
})
export class MDMComponent {
    brand = 'openMDM5 Web';

    getLinks() {
        return [
            { name: 'Navigator', path: '/navigator' },
            { name: 'Administration', path: '/administration' },
        ];
    }
}
