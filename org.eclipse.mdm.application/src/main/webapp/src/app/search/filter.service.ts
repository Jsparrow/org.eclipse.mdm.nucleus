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
import {PreferenceService, Preference} from '../core/preference.service';

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
  public filterChanged$ = new EventEmitter<SearchFilter>();
  public currentFilter: SearchFilter;

  // private searchService: SearchService;
  constructor(private http: Http,
              private _prop: PropertyService,
              private preferenceService: PreferenceService) {
  }

  setSelectedFilter(filter: SearchFilter) {
    if (filter) {
      this.filterChanged$.emit(filter);
    }
  }

  getFilters() {
    return this.preferenceService.getPreferenceForScope('User', 'filter.nodes.')
      .map(preferences => preferences.map(p => this.preferenceToFilter(p)));
  }

  saveFilter(filter: SearchFilter) {
    return this.preferenceService.savePreference(this.filterToPreference(filter));
  }

  private preferenceToFilter(pref: Preference) {
    return <SearchFilter> JSON.parse(pref.value);
  }

  private filterToPreference(filter: SearchFilter) {
    let pref = new Preference();
    pref.value = JSON.stringify(filter);
    pref.key = 'filter.nodes.' + filter.name;
    pref.scope = 'User';
    return pref;
  }
}
