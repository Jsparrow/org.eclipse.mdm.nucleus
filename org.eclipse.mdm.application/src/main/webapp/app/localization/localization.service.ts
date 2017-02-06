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
import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {PropertyService} from '../properties';

@Injectable()
export class LocalizationService{
  constructor(private http: Http,
              private _prop: PropertyService,
              private _node: NodeService){
                if (this._prop.api_host){this._nodeUrl = this._prop.api_host + this._nodeUrl}
                this.init()
              }

  private _nodeUrl = 'mdm/environments'

  private _cache : Localization[] = [];
  private errorMessage: string;

  private init(){
    let node: Node;
    this._node.getNodes(node).subscribe(
      envs => this.initLocalization(envs),
      error => this.errorMessage = <any>error);
  }

  private initLocalization(envs: Node[]){
    envs.forEach((env) => {
      this.getLocalization(env).subscribe(
        locals => this.mergeLocalizations(locals),
        error => this.errorMessage = <any>error);
    })
  }

  private mergeLocalizations(locals: Localization[]){
    let t_local = this._cache
    locals.forEach(function(local){
      let pos = t_local.map(function(e) { return e.name; }).indexOf(local.name);
      if (pos == -1) {
        t_local.push(local)
      }
    })
    this._cache = t_local
  }

  private getLocalization(node: Node){
    let url = this._nodeUrl + "/" + node.sourceName
    if (node.sourceType === 'Environment') {
      url = url + "/localizations?all=true"
    } else {
      url = url + "/" + node.type.toLowerCase() + "s/localizations"
    }
    return this.get(url)
  }

  getTranslation(type:string, comp: string){
    let trans: string
    if (comp) {
      trans = type + "." + comp
    } else {
      trans = type
    }
    let pos = this._cache.map(function(e) { return e.name; }).indexOf(trans);
    if (pos != -1) {
      return this._cache[pos].localizedName
    }
    return trans;
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
