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

import {Condition, Operator} from './filter.service';

import {PropertyService} from '../core/property.service';
import {Node} from '../navigator/node';
import {LocalizationService} from '../localization/localization.service';

export class SearchAttribute {
  source: string;
  boType: string;
  attrName: string;
  valueType: string;
  criteria: string;

  constructor(source: string, boType: string, attrName: string, valueType?: string, criteria?: string) {
    this.source = source;
    this.boType = boType;
    this.attrName = attrName;
    this.valueType = valueType || 'string';
    this.criteria = criteria || '';
  }
}

export class SearchDefinition {
  key: string;
  value: string;
  type: string;
  label: string;
}

@Injectable()
export class SearchService {

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

  getDefinitionsSimple() {
    return [
      <SearchDefinition> {key: '1', value: 'tests', type: 'Test', label: 'Versuche'},
      <SearchDefinition> {key: '2', value: 'teststeps', type: 'TestStep', label: 'Versuchsschritte'},
      <SearchDefinition> {key: '3', value: 'measurements', type: 'Measurement', label: 'Messungen'}
    ];
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



  toConditions(defs: SearchAttribute[]) {
    return defs.map(a => new Condition(a.boType, a.attrName, Operator.EQUALS, []));
  }

  getSearchables(envs: string[], type: string) {
    let searchables: Condition[] = [];

    envs.forEach(env => this.loadSearchAttributes(type, env)
        .toPromise()
        .then(defs => searchables = searchables.concat(this.toConditions(defs)))
        .catch(error => this.errorMessage = error));

    return searchables;
  }

  getSearchAttributesPerEnvs(envs: string[], type: string) {

    return Observable.forkJoin(envs.map(env => this.loadSearchAttributes(type, env)
        .map(sas => sas.map(sa => { sa.source = env; return sa; }))))
        .map(x => x.reduce(function(explored, toExplore) {
          return explored.concat(toExplore);
        }, []));
  }

  flatten<T>(arr: T[][]): T[] {
    return arr.reduce(function(explored, toExplore) {
      return explored.concat(
        Array.isArray(toExplore) ?
        this.flatten(toExplore) : toExplore
      );
    }, []);
  }

  get(envs: string[], type: string) {
    let searchables: [string, SearchAttribute[]][] = [];

    envs.forEach(env => this.loadSearchAttributes(type, env)
        .toPromise()
        .then(defs => searchables.push([env, defs]))
        .catch(error => this.errorMessage = error));

    return searchables;
  }


  conditionMap(conditions: Condition[], sas: [String, SearchAttribute[]][]) {
    let model: [string[], Condition[]][];

    // conditions.forEach(c => model.push([this.findEnvironments(c, sas), ]);

    console.log(model);
  }

  findEnvironments(condition: Condition, sas: [String, SearchAttribute[]][]): String[] {
    return sas
      .filter(entry => entry[1].find(sa => sa.boType === condition.type && sa.attrName === condition.attribute))
      .map(entry => entry[0]);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}
