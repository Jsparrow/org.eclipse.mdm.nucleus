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
import { HttpErrorHandler } from './http-error-handler';

export class Scope {
   public static readonly SYSTEM = 'SYSTEM';
   public static readonly SOURCE = 'SOURCE';
   public static readonly USER = 'USER';

   static toLabel(scope: string) {
     return scope.charAt(0).toUpperCase() + scope.slice(1).toLowerCase();
   }
}

export class Preference {
  scope: string;
  source?: string;
  user?: string;
  key: string;
  value: string;
  id: number;

  static sortByScope(p1: Preference, p2: Preference) {
    let getPriority = (scope: string) => {
      switch (scope) {
        case Scope.SYSTEM: return 1;
        case Scope.SOURCE: return 2;
        case Scope.USER: return 3;
        default: return 4;
      }
    };
    return getPriority(p1.scope) - getPriority(p2.scope);
  }

  constructor() {
      this.key = '';
  }
}

@Injectable()
export class PreferenceService {

  private prefEndpoint: string;

  constructor(private http: Http,
              private httpErrorHandler: HttpErrorHandler,
              private _prop: PropertyService) {
    this.prefEndpoint = _prop.getUrl('/mdm/preferences');
  }

  getPreferenceForScope(scope: string, key?: string) {
      if (key == null) {
          key = '';
      }

      return this.http.get(this.prefEndpoint + '?scope=' + scope + '&key=' + key)
          .map(response => plainToClass(Preference, response.json().preferences))
          .catch(this.httpErrorHandler.handleError);
  }

  getPreference(key?: string) {
      if (key == null) {
          key = '';
      }
      return this.http.get(this.prefEndpoint + '?key=' + key)
          .map(response => plainToClass(Preference, response.json().preferences))
          .catch(this.httpErrorHandler.handleError);
  }
  savePreference(preference: Preference) {
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this.prefEndpoint, JSON.stringify(preference), options)
      .catch(this.httpErrorHandler.handleError);
  }

  deletePreference(id: number) {
    return this.http.delete(this.prefEndpoint + '/' + id)
      .catch(this.httpErrorHandler.handleError);
  }

  deletePreferenceByScopeAndKey(scope: string, key: string) {
    return this.getPreferenceForScope(scope, key).flatMap(p => this.deletePreference(p[0].id));
  }
}
