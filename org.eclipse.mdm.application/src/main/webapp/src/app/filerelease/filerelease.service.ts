/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {PropertyService} from '../core/property.service';

@Injectable()
export class FilereleaseService {
  url: string;
  stateMap = new Array();
  formatMap = new Array();
  month = new Array();

  constructor(private http: Http,
              private prop: PropertyService) {

    this.url = prop.getUrl() + '/mdm/filereleases';

    this.formatMap['PAK2RAW'] = 'original Daten';
    this.formatMap['PAK2ATFX'] = 'ATFX';

    this.stateMap['RELEASE_ORDERED'] = 'beauftragt';
    this.stateMap['RELEASE_APPROVED'] = 'genehmigt';
    this.stateMap['RELEASE_RELEASED'] = 'freigegeben';
    this.stateMap['RELEASE_EXPIRED'] = 'abgelaufen';
    this.stateMap['RELEASE_PROGRESSING_ERROR'] = 'Systemfehler';
    this.stateMap['RELEASE_PROGRESSING'] = 'In Bearbeitung';
    this.stateMap['RELEASE_REJECTED'] = 'abgelehnt';

    this.month[0] = '1';
    this.month[1] = '2';
    this.month[2] = '3';
    this.month[3] = '4';
    this.month[4] = '5';
    this.month[5] = '6';
    this.month[6] = '7';
    this.month[7] = '8';
    this.month[8] = '9';
    this.month[9] = '10';
    this.month[10] = '11';
    this.month[11] = '12';
  }

  readAll() {
    return this.read('');
  }

  readIncomming() {
    return this.read('?direction=incomming');
  }

  readOutgoging() {
    return this.read('?direction=outgoing');
  }

  create(release: Release) {
    let body = JSON.stringify(release);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this.url, body, options)
                    .map(this.extractData)
                    .catch(this.handleError);
  }

  delete(release: Release) {
    return this.http.delete(this.url + '/' + release.identifier);
  }

  approve(release: Release) {
    release.state = 'RELEASE_APPROVED';
    return this.update(release);
  }

  reject(release: Release) {
    release.state = 'RELEASE_REJECTED';
    return this.update(release);
  }

  formatDate(date) {
    let d = new Date(date);
    let day = d.getDate();
    let month = this.month[d.getMonth()];
    let year = d.getFullYear();
    let hours = (d.getHours() < 10 ? '0' : '') + d.getHours();
    let min = (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
    let sec = (d.getSeconds() < 10 ? '0' : '') + d.getSeconds();
    return day + '.' + month + '.' + year + ' ' + hours + ':' + min + ':' + sec;
  }

  private read(query: string) {
    return this.http.get(this.url + query)
    .map(res => <Release[]> res.json().data)
    .catch(this.handleError);
  }

  private update(release: Release) {
    let body = JSON.stringify(release);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this.url + '/' + release.identifier, body, options)
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
  validity: number;
  expire: number;
}
