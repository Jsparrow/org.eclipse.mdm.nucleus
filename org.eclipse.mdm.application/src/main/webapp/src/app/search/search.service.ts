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

import {PropertyService} from '../core/property.service';
import {LocalizationService} from '../localization/localization.service';

import {SearchFilter, Condition, Operator, OperatorUtil} from './filter.service';
import {Query, Filter} from '../tableview/query.service';
import {View} from '../tableview/tableview.service';

export class SearchLayout {
  map: { [environments: string]: Condition[] } = {};

  getEnvironments() {
    return Object.keys(this.map);
  }

  getConditions(environment: string) {
    return this.map[environment] || [];
  }

  set(environment: string, conditions: Condition[]) {
    this.map[environment] = conditions;
  }
  add(environment: string, condition: Condition) {
    this.map[environment] = this.map[environment] || [];
    this.map[environment].push(condition);
  }
}

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
  }

  loadSearchAttributes(type: string, env: string) {
    return this.http.get(this._prop.getUrl() + '/mdm/environments/' + env + '/' + type + '/searchattributes')
      .map(response => <SearchAttribute[]>response.json().data)
      .map(sas => sas.map(sa => { sa.source = env; return sa; }));
  }

  getDefinitionsSimple() {
    return [
      <SearchDefinition>{ key: '1', value: 'tests', type: 'Test', label: 'Versuche' },
      <SearchDefinition>{ key: '2', value: 'teststeps', type: 'TestStep', label: 'Versuchsschritte' },
      <SearchDefinition>{ key: '3', value: 'measurements', type: 'Measurement', label: 'Messungen' }
    ];
  }

  getSearchAttributesPerEnvs(envs: string[], type: string) {

    return Observable.forkJoin(envs.map(env => this.loadSearchAttributes(type, env)
      .map(sas => sas.map(sa => { sa.source = env; return sa; }))))
      .map(x => x.reduce(function(explored, toExplore) {
        return explored.concat(toExplore);
      }, []));
  }

  groupByEnv(attrs: SearchAttribute[]) {
    let attributesPerEnv: { [environment: string]: SearchAttribute[] } = {};

    attrs.forEach(attr => {
      attributesPerEnv[attr.source] = attributesPerEnv[attr.source] || [];
      attributesPerEnv[attr.source].push(attr);
    });
    return attributesPerEnv;
  }

  group(conditions: Condition[], attributesPerEnv: { [environment: string]: SearchAttribute[] }) {
    let attribute2Envs: { [attribute: string]: string[] } = {};

    Object.keys(attributesPerEnv).forEach(env =>
      attributesPerEnv[env].forEach(sa => {
        let attr = sa.boType + '.' + sa.attrName;

        attribute2Envs[attr] = attribute2Envs[attr] || [];
        attribute2Envs[attr].push(env);
      }));

    return attribute2Envs;
  }

  getSearchLayout(envs: string[], conditions: Condition[], type: string) {
    return this.getSearchAttributesPerEnvs(envs, type)
      .map(attrs => this.createSearchLayout(envs, conditions, this.groupByEnv(attrs)));
  }

  createSearchLayout(envs: string[], conditions: Condition[], attributesPerEnv: { [environment: string]: SearchAttribute[] }) {
    let result = new SearchLayout();
    let attribute2Envs = this.group(conditions, attributesPerEnv);
    let globalEnv = 'Global';
    Object.keys(attribute2Envs).forEach(attr => {
      let c = conditions.find(cond => cond.type + '.' + cond.attribute === attr);
      if (c) {
        if (attribute2Envs[attr].length === envs.length) {
          result.add(globalEnv, c);
        } else {
          attribute2Envs[attr].forEach(e => result.add(e, c));
        }
      }
    });

    return result;
  }

  convertToQuery(searchFilter: SearchFilter, attr: SearchAttribute[], view: View) {
    let q = new Query();
    q.resultType = searchFilter.resultType;
    q.filters = this.convert(searchFilter.environments, searchFilter.conditions, attr, searchFilter.fulltextQuery); // TODO
    q.columns = view.columns.map(c => c.type + '.' + c.name);
    console.log('Query', q);

    return q;
  }

  convert(envs: string[], conditions: Condition[], attr: SearchAttribute[], fullTextQuery: string): Filter[] {
    return envs
      .map(e => this.convertEnv(e, conditions, attr, fullTextQuery));
  }

  convertEnv(env: string, conditions: Condition[], attrs: SearchAttribute[], fullTextQuery: string): Filter {

    let filterString = conditions
      .map(c => c.value.map(value => c.type + '.' + c.attribute + ' ' + OperatorUtil.toFilterString(c.operator) + ' ' + value).join(' or '))
      .filter(c => c.length > 0)
      .join(' and ');

    return new Filter(env, filterString, fullTextQuery);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}
