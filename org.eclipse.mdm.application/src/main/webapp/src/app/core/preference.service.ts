/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { PropertyService } from './property.service';

import {Type, Exclude, plainToClass, serialize, deserializeArray} from 'class-transformer';


export class Preference {

    scope: string;
    source?: string;
    user?: string;
    key: string;
    value: string;
    id: number;

    constructor() {
        this.scope = '';
        this.key = '';
    }

}

@Injectable()
export class PreferenceService {

  private prefEndpoint: string;

  constructor(private http: Http,
              private _prop: PropertyService) {
    this.prefEndpoint = _prop.getUrl() + '/mdm/preferences';
  }

  getPreferenceForScope(scope: string, key?: string) {
      if (key == null) {
          key = '';
      }
      return this.http.get(this.prefEndpoint + '?scope=' + scope + '&key=' + key)
          .map(response => plainToClass(Preference, response.json().preferences));
  }

  getPreference(key?: string) {
      if (key == null) {
          key = '';
      }
      return this.http.get(this.prefEndpoint + '?key=' + key)
          .map(response => plainToClass(Preference, response.json().preferences));
  }
  savePreference(preference: Preference) {
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this.prefEndpoint, JSON.stringify(preference), options)
      .catch(this.handleError);
  }

  deletePreference(id: number) {
    return this.http.delete(this.prefEndpoint + '/' + id);
  }

  deletePreferenceByScopeAndKey(scope: string, key: string) {
    return this.getPreferenceForScope(scope, key).flatMap(p => this.deletePreference(p[0].id));

    // this.getPreferenceForScope(scope, key).subscribe( p => this.deletePreference(p[0].id).subscribe());
  }

  private handleError(error: Response) {
      console.error(error);
      return Observable.throw(error.json().error || 'Server error');
  }
}
