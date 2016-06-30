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
import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {PropertyService} from '../properties';

@Injectable()
export class FilereleaseService {
  constructor(private http: Http,
              private prop: PropertyService){}

  url = "http://" + this.prop.api_host + ":" + this.prop.api_port + this.prop.api_prefix + "/mdm/filereleases"

  update(){

  }

  readAll(){
    return this.read("")
  }
  readIncomming(){
    return this.read("/incomming")
  }
  readOutgoging(){
    return this.read("/outgoing")
  }
  private read(type: string){
    return this.http.get(this.url + type)
    .map(res => <Release[]> res.json().data)
    .catch(this.handleError);
  }

  create(){

  }
  delete(){

  }

  approve(release: Release){
    let action = "RELEASE_ACTION_APPROVE"
    this.action(release, action)
  }
  reject(release: Release){
    let action = "RELEASE_ACTION_REJECT"
    this.action(release, action)
  }
  private action(release: Release, action: string){
    let body = JSON.stringify({ action });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    console.log(body);
    this.http.post(this.url + "/" + release.id, body, options)
                    .map(this.extractData)
                    .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
  private extractData(res: Response) {
  let body = res.json();
  return body.data || { };
}

}

export class Release {
  errorMessage: string;
  expire: number;
  fileLink: string;
  format: string;
  id: number;
  identifier: string;
  orderMessage: string;
  receiver: string;
  rejectMessage: string;
  sender: string;
  sourceName: string;
  state: string;
  typeName: string;
  validity: number;
}
