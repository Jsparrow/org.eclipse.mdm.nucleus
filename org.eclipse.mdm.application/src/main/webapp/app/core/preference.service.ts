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

  getPreference(scope: string, key?: string) {
      if (key == null) {
          key = '';
      }
      return this.http.get(this.prefEndpoint + '?scope=' + scope + '&key=' + key)
          .map(response => plainToClass(Preference, response.json().preferences));
  }

  savePreference(preference: Preference) {
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this.prefEndpoint, JSON.stringify(preference), options)
      .catch(this.handleError);
  }

  deletePreference(id: number) {
    return this.http.delete(this.prefEndpoint + '/' + id)
      .catch(this.handleError);
  }

  private handleError(error: Response) {
      console.error(error);
      return Observable.throw(error.json().error || 'Server error');
  }
}
