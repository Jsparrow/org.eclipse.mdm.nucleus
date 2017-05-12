/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {MDMNotificationService} from '../core/mdm-notification.service';
import {PropertyService} from '../core/property.service';
import {LocalizationService} from '../localization/localization.service';
import { Preference, PreferenceService, Scope } from '../core/preference.service';
import {deserializeArray, plainToClass} from 'class-transformer';

import {SearchFilter, Condition, Operator, OperatorUtil} from './filter.service';
import {Query, Filter} from '../tableview/query.service';
import {View} from '../tableview/tableview.service';

export class SearchLayout {
  map: { [sourceNames: string]: Condition[] } = {};

  public static createSearchLayout(envs: string[], attributesPerEnv: { [env: string]: SearchAttribute[] }, conditions: Condition[]) {
    let result = new SearchLayout();
    let attribute2Envs = SearchLayout.mapAttribute2Environments(envs, attributesPerEnv);
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

  public static groupByEnv(attrs: SearchAttribute[]) {
    let attributesPerEnv: { [environment: string]: SearchAttribute[] } = {};

    attrs.forEach(attr => {
      attributesPerEnv[attr.source] = attributesPerEnv[attr.source] || [];
      attributesPerEnv[attr.source].push(attr);
    });
    return attributesPerEnv;
  }

  private static mapAttribute2Environments(envs: string[], attributesPerEnv: { [environment: string]: SearchAttribute[] }) {
    let attribute2Envs: { [attribute: string]: string[] } = {};

    Object.keys(attributesPerEnv)
      .filter(env => envs.find(e => e === env))
      .forEach(env =>
        attributesPerEnv[env].forEach(sa => {
          let attr = sa.boType + '.' + sa.attrName;

          attribute2Envs[attr] = attribute2Envs[attr] || [];
          attribute2Envs[attr].push(env);
        })
      );

    return attribute2Envs;
  }

  getSourceNames() {
    return Object.keys(this.map).sort((s1, s2) => {
      if (s1 === 'Global') {
        return -1;
      } else if ( s2 === 'Global') {
        return 1;
      } else if (s1) {
        return s1.localeCompare(s2);
      } else {
        return -1;
      }
    });
  }

  getConditions(sourceName: string) {
    return this.map[sourceName] || [];
  }

  set(sourceName: string, conditions: Condition[]) {
    this.map[sourceName] = conditions;
  }

  add(sourceName: string, condition: Condition) {
    this.map[sourceName] = this.map[sourceName] || [];
    this.map[sourceName].push(condition);
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

  ignoreAttributesPrefs: Preference[] = [];

  private cachedAttributes: Observable<any>;

  constructor(private http: Http,
    private localService: LocalizationService,
    private _prop: PropertyService,
    private preferenceService: PreferenceService,
    private notificationService: MDMNotificationService) {
          this.preferenceService.getPreference('ignoredAttributes')
              .subscribe( prefs => this.ignoreAttributesPrefs = this.ignoreAttributesPrefs.concat(prefs));
    }

  loadSearchAttributes(type: string, env: string) {
    return this.http.get(this._prop.getUrl('/mdm/environments/' + env + '/' + type + '/searchattributes'))
      .map(response => <SearchAttribute[]>response.json().data)
      .map(sas => sas.map(sa => { sa.source = env; return sa; }))
      .map(sas => this.filterIgnoredAttributes(env, sas));
  }

  getDefinitionsSimple() {
    return Observable.of([
      <SearchDefinition>{ key: '1', value: 'tests', type: 'Test', label: 'Versuche' },
      <SearchDefinition>{ key: '2', value: 'teststeps', type: 'TestStep', label: 'Versuchsschritte' },
      <SearchDefinition>{ key: '3', value: 'measurements', type: 'Measurement', label: 'Messungen' }
    ]);
  }

  getSearchAttributesPerEnvs(envs: string[], type: string) {
    return Observable.forkJoin(envs.map(env => this.loadSearchAttributes(type, env)
      .map(sas => sas.map(sa => { sa.source = env; return sa; }))))
      .map(x => x.reduce(function(explored, toExplore) {
        return explored.concat(toExplore);
      }, []));
  }

  loadSearchAttributesStructured(environments: string[]) {
    if (!this.cachedAttributes) {
      this.cachedAttributes = this.getDefinitionsSimple()
        .map(defs => defs.map(d => d.value))
        .flatMap(defs => this.loadSearchAttributesForAllDefs(defs, environments))
        .publishReplay(1)
        .refCount();
    }
    return this.cachedAttributes;
  }

  loadSearchAttributesForAllDefs(types: string[], environments: string[]) {
    return Observable.forkJoin(types.map(type => this.loadSearchAttributesForDef(type, environments)))
      .map(type2AttributesPerEnv =>
        type2AttributesPerEnv.reduce(
          function(acc, value) {
            acc[value.type] = value.attributesPerEnv;
            return acc; },
          <{ [type: string]: { [env: string]: SearchAttribute[] }}> {})
        );
  }

  loadSearchAttributesForDef(type: string, environments: string[]) {
    return Observable.forkJoin(environments.map(env => this.loadSearchAttributes(type, env)
      .map(attrs => { return { 'env': env, 'attributes': attrs}; })))
      .map(attrsPerEnv => attrsPerEnv.reduce(
        function(acc, value) {acc[value.env] = value.attributes; return acc; },
         <{ [env: string]: SearchAttribute[] }> {})
       )
      .map(attrsPerEnv => { return { 'type': type, 'attributesPerEnv': attrsPerEnv}; });
  }


  getSearchLayout(envs: string[], conditions: Condition[], type: string) {
    return this.getSearchAttributesPerEnvs(envs, type)
      .map(attrs => SearchLayout.createSearchLayout(envs, SearchLayout.groupByEnv(attrs), conditions));
  }

  convertToQuery(searchFilter: SearchFilter, attr: { [type: string]: { [env: string]: SearchAttribute[] }}, view: View) {
    let q = new Query();

    q.resultType = searchFilter.resultType;
    if (attr['tests']) {
      q.filters = this.convert(searchFilter.environments, searchFilter.conditions, attr['tests'], searchFilter.fulltextQuery); // TODO
    }
    q.columns = view.columns.map(c => c.type + '.' + c.name);
    console.log('Query', q);

    return q;
  }

  convert(envs: string[], conditions: Condition[], attr: { [env: string]: SearchAttribute[] }, fullTextQuery: string): Filter[] {
    return envs.map(e => this.convertEnv(e, conditions, attr[e], fullTextQuery));
  }

  convertEnv(env: string, conditions: Condition[], attrs: SearchAttribute[], fullTextQuery: string): Filter {

    let filterString = conditions
      .map(c => c.value.map(value => c.type + '.' + c.attribute + ' ' + OperatorUtil.toFilterString(c.operator) + ' ' + value).join(' or '))
      .filter(c => c.length > 0)
      .join(' and ');

    return new Filter(env, filterString, fullTextQuery);
  }

  isAttributeIgnored(attributeName: string, sa: SearchAttribute) {
    let x = attributeName.split('.', 2);
    let fType = x[0];
    let fName = x[1];
    return ((fType === sa.boType || fType === '*') && (fName === sa.attrName || fName === '*'));
  }

  private filterIgnoredAttributes(environment: string, searchAttributes: SearchAttribute[]) {
    let filters = this.getFilters(environment);
    filters.forEach(f =>
      searchAttributes = searchAttributes.filter(sa => !this.isAttributeIgnored(f, sa))
    );
    return searchAttributes;
  }

  getFilters(source: string): string[] {
    return this.ignoreAttributesPrefs
      .filter(p => p.scope !== Scope.SOURCE || p.source === source)
      .sort(Preference.sortByScope)
      .map(p => this.parsePreference(p))
      .reduce((acc, value) => acc.concat(value), []);
  }

  private parsePreference(pref: Preference) {
    try {
        return <string[]> JSON.parse(pref.value);
    } catch (e) {
        this.notificationService.notifyError('Einstellung für zu ignorierende Attribute ist fehlerhaft.', e);
        return [];
    }
  }

}
