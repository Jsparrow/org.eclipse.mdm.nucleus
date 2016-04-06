// Copyright (c) 2016 Gigatronik Ingolstadt GmbH
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Localization} from './localization'

@Injectable()
export class LocalizationService {
  constructor(private http: Http){}

  private _host = "127.0.0.1"
  private _port = "8080"
  private _url = 'http://' + this._host + ':' + this._port
  private _nodeUrl = this._url + '/org.eclipse.mdm.application-1.0.0/mdm/environments'

  private _cache : Localization[];
  private test : {}
  private errorMessage: string;

  getLocalization(node: Node){
    let url = this._nodeUrl + "/" + node.sourceName
    if node.sourceType === 'Environment' {
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
