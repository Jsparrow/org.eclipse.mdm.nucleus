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
import {Component} from '@angular/core';

import {MDMDetailViewComponent} from './mdm-detail-view.component';
import {MDMDescriptiveDataComponent} from './mdm-detail-descriptive-data.component';

@Component({
  selector: 'mdm-detail',
  templateUrl: 'mdm-detail.component.html',
  providers: []
})
export class MDMDetailComponent {

  brand = 'Details';
}
