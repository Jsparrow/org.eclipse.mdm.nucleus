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

import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

import {SearchService} from './search.service';
import {PropertyService} from '../core/property.service';
import {Node} from '../navigator/node';
import {SearchAttribute} from './search.service';
import {QueryService, Query, SearchResult, Filter} from '../tableview/query.service';
import {View} from '../tableview/tableview.service';
import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {Type, Exclude, plainToClass, serialize, deserialize} from 'class-transformer';

export enum Operator {
  EQUALS,
  LESS_THAN,
  GREATER_THAN,
  LIKE
}

export namespace OperatorUtil {
  export function values() {
    return [ Operator.EQUALS, Operator.LESS_THAN, Operator.GREATER_THAN, Operator.LIKE ];
  }
  export function toString(operator: Operator) {
      switch (operator) {
          case Operator.EQUALS:
            return '=';
          case Operator.LESS_THAN:
            return '<';
          case Operator.GREATER_THAN:
            return '>';
          case Operator.LIKE:
            return 'like';
          default:
            return undefined;
      }
  }
  export function toFilterString(operator: Operator) {
      switch (operator) {
          case Operator.EQUALS:
            return 'eq';
          case Operator.LESS_THAN:
            return 'lt';
          case Operator.GREATER_THAN:
            return 'gt';
          case Operator.LIKE:
            return 'lk';
          default:
            return undefined;
      }
  }
}

export class Condition {
  type: string;
  attribute: string;
  operator: Operator;
  value: string[];
  valueType: string;

  constructor(type: string, attribute: string, operator: Operator, value: string[], valueType?: string) {
    this.type = type;
    this.attribute = attribute;
    this.operator = operator;
    this.value = value;
    if (valueType) {
      this.valueType = valueType.toLowerCase();
    } else {
      this.valueType = 'string';
    }
  }
}

export class SearchFilter {
  name: string;
  environments: string[];
  resultType: string;
  fulltextQuery: string;
  conditions: Condition[] = [];

  constructor(name: string, environments: string[], resultType: string, fulltextQuery: string, conditions: Condition[]) {
    this.name = name;
    this.environments = environments;
    this.resultType = resultType;
    this.fulltextQuery = fulltextQuery;
    this.conditions = conditions;
  }
}

@Injectable()
export class FilterService {
  public readonly NO_FILTER_NAME = 'Kein Filter ausgewÃ¤hlt';
  public readonly NEW_FILTER_NAME = 'Neuer Filter';
  public currentFilter = new SearchFilter(this.NO_FILTER_NAME, [], 'Test', '', []);
  public filterChanged$ = new EventEmitter<SearchFilter>();

  constructor(private http: Http,
              private _prop: PropertyService,
              private preferenceService: PreferenceService) {
  }

  setSelectedFilter(filter: SearchFilter) {
    if (filter) {
      this.filterChanged$.emit(filter);
    } else {
      this.filterChanged$.emit(new SearchFilter(this.NO_FILTER_NAME, [], 'Test', '', []));
    }
  }

  getFilters() {
    return this.preferenceService.getPreferenceForScope(Scope.USER, 'filter.nodes.')
      .map(preferences => preferences.map(p => this.preferenceToFilter(p)));
  }

  saveFilter(filter: SearchFilter) {
    return this.preferenceService.savePreference(this.filterToPreference(filter));
  }

  private preferenceToFilter(pref: Preference) {
    return deserialize(SearchFilter, pref.value);
  }

  private filterToPreference(filter: SearchFilter) {
    let pref = new Preference();
    pref.value = serialize(filter);
    pref.key = filter ? 'filter.nodes.' + filter.name : 'filter.nodes.';
    pref.scope = Scope.USER;
    return pref;
  }

  deleteFilter(name: string) {
    return this.preferenceService.deletePreferenceByScopeAndKey(Scope.USER, 'filter.nodes.' + name);
  }
}
