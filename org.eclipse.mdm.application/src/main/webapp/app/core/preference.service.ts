import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { PropertyService } from './property.service';

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

  getPreference( scope: string, key?: string ): Promise<Preference[]> {
      if ( key == null ) {
          key = '';
      }
      return this.http.get( this.prefEndpoint + '?scope=' + scope + '&key=' + key )
          .toPromise()
          .then( response => <Preference[]>response.json().preferences )
          .catch( this.handleError );
  }

  getPreferencesBySource( source: string, key?: string  ): Promise<Preference[]> {
      if ( key == null ) {
          key = '';
      }
      return this.http.get( this.prefEndpoint + '/source?source=' + source + '&key=' + key)
          .toPromise()
          .then( response => <Preference[]>response.json().preferences )
          .catch( this.handleError );
  }

  savePreference( preference: Preference ) {
      let pref = {
          'scope': preference.scope,
          'source': preference.source,
          'key': preference.key,
          'value': preference.value,
          'id': preference.id
      };

  let headers = new Headers({ 'Content-Type': 'application/json' });
  let options = new RequestOptions({ headers: headers });

      return this.http.put( this.prefEndpoint, JSON.stringify( pref ), options )
          .toPromise()
          .catch( this.handleError );
  }

  deletePreference( id: number ) {
      return this.http.delete( this.prefEndpoint + '/' + id )
                  .toPromise()
                  .catch( this.handleError );
  }

  private handleError( error: Response ) {
      console.error( error );
      return Observable.throw( error.json().error || 'Server error' );
  }
}
