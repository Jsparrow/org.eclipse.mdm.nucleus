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
import {Component, Input, Output, EventEmitter} from '@angular/core';
import {ControlGroup} from '@angular/common';
import {Release, FilereleaseService} from './filerelease.service';
import {Node} from '../navigator/node';

@Component({
  selector: 'mdm-filerelease-create',
  template: require('../../templates/filerelease/mdm-filerelease-create.component.html'),
  styles: [],
  directives: [],
  providers: []
})
export class MDMFilereleaseCreateComponent {

  constructor(private service : FilereleaseService){}

  @Input() node: Node;
  @Output() onSubmit = new EventEmitter<boolean>();
  release : Release = new Release;
  errorMessage: string;
  options = ["PAK2RAW","PAK2ATFX"]
  expire = [1,2,3,4,5,6,7,8,9,10]

  getFormat(key){
      return this.service.formatMap[key]
  }

  createRelease(){
    this.release.identifier = ""
    this.release.state = ""
    this.release.name = this.node.name
    this.release.sourceName = this.node.sourceName
    this.release.typeName = this.node.type
    this.release.id = this.node.id
    this.release.sender = ""
    this.release.receiver = ""
    this.release.rejectMessage = ""
    this.release.errorMessage = ""
    this.release.fileLink = ""
    this.release.expire = 0
    this.service.create(this.release).subscribe(
      release => this.release = release,
      error => this.errorMessage = <any>error);
    this.clear()
    this.onSubmit.emit(true)
  }
  clear(){
    this.release = new Release()
  }
}