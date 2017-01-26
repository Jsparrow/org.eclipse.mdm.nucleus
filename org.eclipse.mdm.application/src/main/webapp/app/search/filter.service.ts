import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {DynamicForm} from './dynamic-form.component';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

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

  constructor(type: string, attribute: string, operator: Operator, value: string[]) {
    this.type = type;
    this.attribute = attribute;
    this.operator = operator;
    this.value = value;
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

  constructor(private http: Http,
              private _prop: PropertyService) {
    this.filters = [new SearchFilter('Standard', [], 'Test', '', [
        new Condition('Test', 'Name', Operator.EQUALS, ['PBN*']),
        new Condition('TestStep', 'Name', Operator.EQUALS, ['*'])
      ]), new SearchFilter('Test', [], 'tests', '', [
        new Condition('Channel', 'Name', Operator.EQUALS, ['Standard_*'])
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

  groupByEnv(attrs: SearchAttribute[]) {
    let attributesPerEnv: { [environment: string]: SearchAttribute[]} = {};

    attrs.forEach(attr => {
      attributesPerEnv[attr.source] = attributesPerEnv[attr.source] || [];
      attributesPerEnv[attr.source].push(attr);
    });
    return attributesPerEnv;
  }

  group(conditions: Condition[], attributesPerEnv: { [environment: string]: SearchAttribute[]}) {
    let attribute2Envs: { [attribute: string]: string[] } = {};

    Object.keys(attributesPerEnv).forEach(env =>
      attributesPerEnv[env].forEach(sa => {
        let attr = sa.boType + '.' + sa.attrName;

        attribute2Envs[attr] = attribute2Envs[attr] || [];
        attribute2Envs[attr].push(env);
    }));

    return attribute2Envs;
  }

  env2Conditions(envs: string[], conditions: Condition[], attributesPerEnv: { [environment: string]: SearchAttribute[]}) {
    let result: { [environments: string]: Condition[] } = {};
    let attribute2Envs = this.group(conditions, attributesPerEnv);
    let globalEnv = 'Global';
    Object.keys(attribute2Envs).forEach(attr => {
      let c = conditions.find(cond => cond.type + '.' + cond.attribute === attr);
      if (c) {
        if (attribute2Envs[attr].length === envs.length) {
          result[globalEnv] = result[globalEnv] || [];
          result[globalEnv].push(c);
        } else {
          attribute2Envs[attr].forEach(e => {
            result[e] = result[e] || [];
            result[e].push(c);
          });
        }
      }
    });

    return result;
  }

  convertToQuery(searchFilter: SearchFilter, attr: SearchAttribute[], view: View) {
    let q = new Query();
    q.resultType = searchFilter.resultType;
    q.filters = this.convert(searchFilter.environments, searchFilter.conditions, attr, searchFilter.fulltextQuery); // TODO
    q.columns = view.cols.map(c => c.type + '.' + c.name);
    console.log('Query', q);

    return q;
  }

  convert(envs: string[], conditions: Condition[], attr: SearchAttribute[], fullTextQuery: string): Filter[] {
    return envs
      .map(e => this.convertEnv(e, conditions, attr, fullTextQuery));
  }

  convertEnv(env: string, conditions: Condition[], attrs: SearchAttribute[], fullTextQuery: string): Filter {

    let filterString = conditions
      .map(c => c.value.map(value => c.type + '.' + c.attribute + ' ' + Operator.toFilterString(c.operator) + ' ' + value).join(' or '))
      .filter(c => c.length > 0)
      .join(' and ');

    return new Filter(env, filterString, fullTextQuery);
  }
}
