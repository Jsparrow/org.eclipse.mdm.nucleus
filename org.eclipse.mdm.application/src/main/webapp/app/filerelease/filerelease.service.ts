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

  readAll(){
    return this.read("")
  }
  readIncomming(){
    return this.read("?direction=incomming")
  }
  readOutgoging(){
    return this.read("?direction=outgoing")
  }
  private read(query: string){
    return this.http.get(this.url + query)
    .map(res => <Release[]> res.json().data)
    .catch(this.handleError);
  }

  create(release: Release){
    let body = JSON.stringify({ release });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this.url, body, options)
                    .map(this.extractData)
                    .catch(this.handleError);
  }
  delete(release: Release){
    return this.http.delete(this.url + "/" + release.id).subscribe(data => {}, err => console.log(err))
  }

  approve(release: Release){
    release.state = "RELEASE_ACTION_APPROVE"
    return this.update(release)
  }
  reject(release: Release){
    release.state = "RELEASE_ACTION_REJECT"
    return this.update(release)
  }
  private update(release: Release){
    let body = JSON.stringify({ release });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this.url + "/" + release.identifier, body, options)
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
  identifier: string;
  state: string;
  name: string;
  sourceName: string;
  typeName: string;
  id: number;
  sender: string;
  receiver: string;
  orderMessage: string;
  rejectMessage: string;
  errorMessage: string;
  format: string;
  fileLink: string;
  validity:number;
  expire: number;
}
