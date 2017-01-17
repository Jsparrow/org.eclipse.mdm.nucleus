import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {PropertyService} from './property.service';

export class Preference {

}

@Injectable()
export class PreferenceService {
  private prefEndpoint: string;

  constructor(private http: Http,
              private _prop: PropertyService) {
    this.prefEndpoint = _prop.getUrl + '/preferences';
  }

  getPreference(key: string): Preference {
    return this.http.get(this.prefEndpoint + '?filter=' + key)
               .toPromise()
               .then(response => response.json().preferences[0]) // check array
               .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}
