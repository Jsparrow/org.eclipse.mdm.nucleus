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
import { Observable } from 'rxjs/Observable';

import {PropertyService} from '../core/property.service';
import {MDMItem} from '../core/mdm-item';
import {HttpErrorHandler} from '../core/http-error-handler';
import {Type, Exclude, plainToClass, serialize, deserialize} from 'class-transformer';

export class Filter {
  sourceName: string;
  filter: string;
  searchString: string;

  constructor(sourceName: string, filter: string, searchString: string) {
    this.sourceName = sourceName;
    this.filter = filter;
    this.searchString = searchString;
  }
}
export class Query {
  resultType: string;
  @Type(() => Filter)
  filters: Filter[] = [];
  columns: String[] = [];

  addFilter(sourceName: string, filter: string) {
    let f = this.filters.find(i => i.sourceName === sourceName);
    if (f) {
      f.filter += ' or ' + filter; // TODO
    } else {
      this.filters.push(new Filter(sourceName, filter, ''));
    }
  }
}

export class Columns {
  type: string;
  attribute: string;
  value: string;
}

export class Row {
  source: string;
  type: string;
  id: string;
  @Type(() => Columns)
  columns: Columns[] = [];

  getColumn(col: string) {
    let column = this.columns.find(c => c.type + '.' + c.attribute === col);
    if (column) {
      return column.value;
    } else {
      return '';
    }
  }

  equals (row: Row) {
    return this.source === row.source && this.type === row.type && this.id === row.id;
  }

  public getItem() {
    return new MDMItem(this.source, this.type, this.id);
  }
}

export class SearchResult {
  @Type(() => Row)
  rows: Row[] = [];
}

@Injectable()
export class QueryService {
  private queryUrl: string;

  constructor(private http: Http,
              private httpErrorHandler: HttpErrorHandler,
              private _prop: PropertyService) {
    this.queryUrl = _prop.getUrl('/mdm/query');
  }

  query(query: Query): Observable<SearchResult> {
    return this.http.post(this.queryUrl, query)
               .map(res => deserialize(SearchResult, res.text()))
               .catch(this.httpErrorHandler.handleError);
  }

  queryItems(items: MDMItem[], columns: string[]): Observable<SearchResult>[] {
    let byType = items.reduce((acc: [string, MDMItem[]], item: MDMItem) => {
      let key = item.type;
      acc[key] = acc[key] || [];
      acc[key].push(item);
      return acc;
    }, {});

    return Object.keys(byType).map(type => this.queryType(type, byType[type], columns));
  }

  queryType(type: string, items: MDMItem[], columns: string[]) {
    if (items && items.length > 0) {
      let query = new Query();
      query.resultType = type;
      query.columns = columns;

      query.columns.push(type + '.Id');
      items.forEach(i => query.addFilter(i.source, i.type + '.Id eq \'' + i.id + '\''));

      return this.query(query);
    } else {
      return Observable.of(new SearchResult());
    }
  }

  suggestValues(environments: string[], type: string, attribute: string) {
    let body = JSON.stringify({
      'sourceNames': environments,
      'type': type,
      'attrName': attribute
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    let url =  this._prop.getUrl('/mdm/suggestions');
    return this.http.post(url, body, options)
      .map(res => res.json().data)
      .catch(this.httpErrorHandler.handleError);
  }
}
