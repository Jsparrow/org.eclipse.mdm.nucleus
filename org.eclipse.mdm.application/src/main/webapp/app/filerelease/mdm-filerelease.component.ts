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
import {MDMFilereleaseDisplayComponent} from './mdm-filerelease-display.component';

import {MODAL_DIRECTVES, BS_VIEW_PROVIDERS} from 'ng2-bootstrap/ng2-bootstrap';

@Component({
  selector: 'mdm-filerelease',
  template: require('../../templates/filerelease/mdm-filerelease.component.html'),
  styles: ['.box1 {border: 1px solid #ddd; border-radius: 4px;}',
           '.box2 {border: 1px solid #ddd; border-radius: 4px; margin-top: 20px}'],
  directives: [MODAL_DIRECTVES, MDMFilereleaseDisplayComponent],
  viewProviders: [BS_VIEW_PROVIDERS],
  providers: []
})
export class MDMFilereleaseComponent implements OnInit{
  constructor(private service: FilereleaseService){}

  incoming: Release[] =[]
  outgoing: Release[] =[]
  errorMessage: string
  release: Release = new Release
  event: string = "display"

  todo: string

  ngOnInit(){
    this.getReleases()
  }

  getReleases(){
    this.service.readOutgoging().subscribe(
      releases => this.outgoing = releases,
      error => this.errorMessage = <any>error);
    this.service.readIncomming().subscribe(
      releases => this.incoming = releases,
      error => this.errorMessage = <any>error);
  }
  setData(release){
    this.release = release
  }
  getState(release: Release){
    if (release.state == "RELEASE_ACTION_APPROVE") {return "success"}
    if (release.state == "RELEASE_ACTION_REJECT") {return "danger"}
    return "info"
  }
  isDownloadable(state){
    if (state == "RELEASE_ACTION_APPROVE") {return false}
    return false
  }
  rejectRelease(reason: string){
    this.release.rejectMessage = reason
    this.service.reject(this.release).subscribe(
      release => this.todo = release,
      error => this.errorMessage = <any>error);
  }
  approveRelease(){
    this.service.approve(this.release).subscribe(
      release => this.todo = release,
      error => this.errorMessage = <any>error);
  }
  isDeletable(){
    if (this.release.state == "RELEASE_ORDERED") {return false}
    return true
  }
  deleteRelease(){
    this.service.delete(this.release)
  }
  setEvent(event){
    this.event = event
  }
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
