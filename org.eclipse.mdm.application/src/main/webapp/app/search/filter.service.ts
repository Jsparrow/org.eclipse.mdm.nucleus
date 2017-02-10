import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {DynamicForm} from './dynamic-form.component';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

import {SearchService} from './search.service';
import {PropertyService} from '../core/property.service';
import {Node} from '../navigator/node';
import {SearchAttribute} from './search.service';
import {QueryService, Query, SearchResult, Filter} from '../tableview/query.service';
import {View} from '../tableview/tableview.service';

export enum Operator {
  EQUALS,
  LESS_THAN,
  GREATER_THAN,
  LIKE
}

export namespace Operator {
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
    this.valueType = valueType || 'text';
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
  private filters: SearchFilter[];
  private selectedFilter: SearchFilter;

  // private searchService: SearchService;
  constructor(private http: Http,
              private _prop: PropertyService) {
    this.filters = [new SearchFilter('Standard', [], 'Test', '', [
        new Condition('Test', 'Name', Operator.EQUALS, ['PBN*'], 'text'),
        new Condition('TestStep', 'Name', Operator.EQUALS, [], 'number')
      ]), new SearchFilter('Test', [], 'tests', '', [
        new Condition('Channel', 'Name', Operator.EQUALS, ['Standard_*'], 'text')
      ])
    ];
  }

  setSelectedFilter(filter: SearchFilter) {
    this.selectedFilter = filter;
    this.filterChanged$.emit(this.selectedFilter);
  }

  getActiveFilter() {
    return this.filters[0];
  }
  getFilters() {
    return this.filters;
  }

  saveFilter(filter: SearchFilter) {
    this.filters.push(filter);
  }
}
