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
import {Component, Input} from '@angular/core';
import {Release} from './filerelease.service';

@Component({
  selector: 'mdm-filerelease-details',
  template: require('../../templates/filerelease/mdm-filerelease-details.component.html'),
  styles: [],
  directives: [],
  providers: []
})
export class MDMFilereleaseDetailsComponent {
  @Input() release: Release

}
