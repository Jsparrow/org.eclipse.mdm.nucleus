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
import {Localization} from './localization';
import {Node} from './navigator/node';
import {PropertyService} from './properties';

@Injectable()
export class LocalizationService {
  constructor(private http: Http,
              private _prop: PropertyService){}

  private _host = this._prop.api_host
  private _port = this._prop.api_port
  private _url = 'http://' + this._host + ':' + this._port + this._prop.api_prefix
  private _nodeUrl = this._url + '/mdm/environments'

  private _cache : Localization[];
  private errorMessage: string;

  getLocalization(node: Node){
    let url = this._nodeUrl + "/" + node.sourceName
    if (node.sourceType === 'Environment') {
      url = url + "/localizations"
    } else {
      url = url + "/" + node.type.toLowerCase() + "s/localizations"
    }
    return this.get(url)
  }

  private get(url: string){
    return this.http.get(url)
    .map(res => <Localization[]> res.json().data)
    .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}
