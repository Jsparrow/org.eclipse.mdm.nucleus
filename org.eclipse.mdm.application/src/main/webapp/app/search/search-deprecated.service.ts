import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {DynamicForm} from './dynamic-form.component';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

import {Condition, Operator} from './filter.service';

import {PropertyService} from '../core/property.service';
import {Node} from '../navigator/node';
import {LocalizationService} from '../localization/localization.service';

import {SearchDefinition, SearchAttribute} from './search.service';
import {SearchFilter} from './filter.service';
import {QueryService, Query, SearchResult, Filter} from '../tableview/query.service';
import {View} from '../tableview/tableview.service';

@Injectable()
export class SearchDeprecatedService {

  private _searchUrl: string;
  private errorMessage: string;

  private defs: SearchAttribute[];

  constructor(private http: Http,
              private localService: LocalizationService,
              private _prop: PropertyService) {
    this._searchUrl = _prop.getUrl() + '/mdm/environments';
  }

  loadSearchAttributes(type: string, env: string) {
    return this.http.get(this._searchUrl + '/' + env + '/' + type + '/searchattributes')
              .map(response => <SearchAttribute[]> response.json().data)
              .map(sas => sas.map(sa => { sa.source = env; return sa; }));
  }

  getDefinitionsSimple() {
    return [
      <SearchDefinition> {key: '1', value: 'tests', type: 'Test', label: 'Versuche'},
      <SearchDefinition> {key: '2', value: 'teststeps', type: 'TestStep', label: 'Versuchsschritte'},
      <SearchDefinition> {key: '3', value: 'measurements', type: 'Measurement', label: 'Messungen'}
    ];
  }
  /* =============================================== */
  getDefinitions() {
    let definitions: SearchBase<any> =
      new DropdownSearch({
        key: 'definitions',
        label: 'definitions',
        options: this.getDefinitionsSimple(),
        order: 1
      });
    return definitions;
  }

  buildSearchForm(defs: SearchAttribute[]) {
    let searchesForm: SearchBase<any>[] = [];
    defs.forEach(function(def: SearchAttribute, i) {
      searchesForm.push(new TextboxSearch({
        boType: def.boType,
        attrName: def.attrName,
        key: def.boType + '.' + def.attrName + '.' + def.valueType,
        label: def.boType + '.' + def.attrName,
        value: '',
        required: false,
        order: i,
        type: def.valueType === 'INTEGER' ? 'number' : 'text'
      }));
    });
    return searchesForm;
  }

  getSearches(type: string, env: string): Promise<SearchBase<any>[]> {
    if (!env) {
      return;
    }
    return this.loadSearchAttributes(type, env)
        .toPromise()
        .then(defs => this.buildSearchForm(defs))
        .catch(error => this.errorMessage = error); // todo: Display error message
  }

}
