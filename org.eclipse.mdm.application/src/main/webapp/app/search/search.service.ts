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
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {DynamicForm} from './dynamic-form.component';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

import {PropertyService} from '../properties';
import {Node} from '../navigator/node';

class Definition {
  attrName: string;
  boType: string;
  criteria: string;
  valueType: string;
}

@Injectable()
export class SearchService {

  private _host = this._prop.api_host;
  private _port = this._prop.api_port;
  private _url = 'http://' + this._host + ':' + this._port + this._prop.api_prefix;
  private _searchUrl = this._url + '/mdm/environments';
  private errorMessage: string;

  private defs: Definition[];

  constructor(private http: Http,
              private _prop: PropertyService) {}

  loadDefinitions(type: string, env: string) {
    return this.http.get(this._searchUrl + '/' + env + '/' + type + '/searchattributes')
               .toPromise()
               .then(response => response.json().data)
               .catch(this.handleError);
  }

  getDefinitions() {
    let definitions: SearchBase<any> =
      new DropdownSearch({
        key: 'definitions',
        label: 'definitions',
        options: [
          {key: '1', value: 'tests', label: 'Versuche'},
          {key: '2', value: 'teststeps', label: 'Versuchsschritte'},
          {key: '3', value: 'measurements', label: 'Messungen'}
        ],
        order: 1
      });
    return definitions;
  }

  buildSearchForm(defs) {
    let searchesForm: SearchBase<any>[] = [];
    defs.forEach(function(def, i) {
      searchesForm.push(new TextboxSearch({
        key: def.boType + '.' + def.attrName,
        label: def.boType + '.' + def.attrName,
        value: '',
        required: false,
        order: i,
        type: def.valueType === 'INTEGER' ? 'number' : 'text'
      }));
    });
    return searchesForm;
  }

  getSearches(type: string, env: string) {
    if (!env) {
      return;
    }
    return this.loadDefinitions(type, env)
        .then((defs) => {
          return this.buildSearchForm(defs);
        })
        .catch(error => this.errorMessage = error); // todo: Display error message
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}
