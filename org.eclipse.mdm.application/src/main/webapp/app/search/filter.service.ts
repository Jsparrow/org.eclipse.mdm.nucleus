import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {DynamicForm} from './dynamic-form.component';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

import {PropertyService} from '../core/properties';
import {Node} from '../navigator/node';


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
}

export class Condition {
  type: string;
  attribute: string;
  operator: Operator;
  value: string;
  constructor(type: string, attribute: string, operator: Operator, value: string) {
    this.type = type;
    this.attribute = attribute;
    this.operator = operator;
    this.value = value;
  }
}

export class EnvFilter {
  name: string;
  conditions: Array<Condition>;
  constructor(name: string, conditions: Array<Condition>) {
    this.name = name;
    this.conditions = conditions;
  }
}
export class SearchFilter {
  name: string;
  envs: Array<EnvFilter>;
  constructor(name: string, envs: Array<EnvFilter>) {
    this.name = name;
    this.envs = envs;
  }
}

@Injectable()
export class FilterService {
  public filterChanged$ = new EventEmitter<SearchFilter>();
  private filters: SearchFilter[];
  private selectedFilter: SearchFilter;

  constructor(private http: Http,
              private _prop: PropertyService) {
    this.filters = [new SearchFilter('Standard', [
      new EnvFilter('Global', [new Condition('Test', 'Name', Operator.EQUALS, 'PBN*')]),
      new EnvFilter('MDM-NVH', [new Condition('TestStep', 'Name', Operator.EQUALS, '*')])
    ]), new SearchFilter('Test', [
      new EnvFilter('Global', [new Condition('Channel', 'Name', Operator.EQUALS, 'Standard_*')])
    ])];
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
}
