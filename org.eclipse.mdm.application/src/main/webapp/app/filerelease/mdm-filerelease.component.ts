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
import {Component, OnInit} from '@angular/core';
import {FilereleaseService, Release} from './filerelease.service';
import {MDMFilereleaseDetailsComponent} from './mdm-filerelease-details.component';

import {MODAL_DIRECTVES, BS_VIEW_PROVIDERS} from 'ng2-bootstrap/ng2-bootstrap';

@Component({
  selector: 'mdm-filerelease',
  template: require('../../templates/filerelease/mdm-filerelease.component.html'),
  styles: [],
  directives: [MODAL_DIRECTVES, MDMFilereleaseDetailsComponent],
  viewProviders: [BS_VIEW_PROVIDERS],
  providers: []
})
export class MDMFilereleaseComponent implements OnInit{
  constructor(private service: FilereleaseService){}

  incomming: Release[] =[]
  outgoing: Release[] =[]
  errorMessage: string
  release: Release = new Release

  ngOnInit(){
    this.getReleases()
  }

  getReleases(){
    this.service.readOutgoging().subscribe(
      releases => this.outgoing = releases,
      error => this.errorMessage = <any>error);
    this.service.readIncomming().subscribe(
      releases => this.incomming = releases,
      error => this.errorMessage = <any>error);
  }
  setData(release){
    this.release = release
  }
  rejectRelease(){
    this.service.reject(this.release)
  }
  approveRelease(){
    this.service.approve(this.release)
  }
}
