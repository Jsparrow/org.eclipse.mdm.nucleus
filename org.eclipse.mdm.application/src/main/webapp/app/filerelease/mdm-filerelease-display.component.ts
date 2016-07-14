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
  selector: 'mdm-filerelease-display',
  template: require('../../templates/filerelease/mdm-filerelease-display.component.html'),
  styles: [],
  directives: [],
  providers: []
})
export class MDMFilereleaseDisplayComponent {
  @Input() release: Release

  getFormat(format){
    if (format == 'PAK2RAW') {return 'PAK'}
    if (format == 'PAK2ATFX') {return 'ATFX'}
    return format
  }
  getDate(date){
    var d = new Date(date*1000);
    return d.getDate() + "." + d.getMonth() + "." + d.getFullYear() + " " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds()
  }
}
