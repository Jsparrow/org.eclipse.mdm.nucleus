/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs/Observable';

import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions, RequestMethod} from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import {HttpErrorHandler} from '../core/http-error-handler';

import { MDMItem } from '../core/mdm-item';
import { PropertyService } from '../core/property.service';
import { QueryService, Query, SearchResult } from './query.service';

describe ( 'QueryService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        PropertyService,
        QueryService,
        MockBackend,
        BaseRequestOptions,
        HttpErrorHandler,
        {
          provide: Http,
          useFactory: (backend, options) => new Http(backend, options),
          deps: [MockBackend, BaseRequestOptions]
        }]
    });
  });

  describe('query()', () => {
    it('should return result for simple query', async(inject([QueryService, MockBackend], (queryService, mockBackend) => {
      mockBackend.connections.subscribe((conn: MockConnection) => {
        if (conn.request.url.endsWith('/query') && conn.request.method === RequestMethod.Post) {
          let mockResponse = { rows: [
            {source: 'MDMNVH', type: 'Test', id: 'id1', columns: [{type: 'Test', attribute: 'Name', value: 'TestNumberOne'}]}
          ]};
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        }
        return;
      });

      queryService.query(new Query()).subscribe(results => {
        expect(results.rows.length).toBe(1);
        expect(results.rows[0].columns[0].value).toEqual('TestNumberOne');
      });
    })));
  });

  describe('queryType()', () => {
    it('should quote IDs', async(inject([QueryService], (queryService) => {
      var spy = spyOn(queryService, 'query').and.returnValue(Observable.of(new SearchResult()));

      var query = new Query();
      query.resultType = 'TestStep';
      query.addFilter('MDM', 'Test.Id eq \'id1\'');
      query.columns = ['TestStep.Name', 'TestStep.Id'];

      queryService.queryType('TestStep', [{ source: 'MDM', type: 'Test', id: 'id1'}], ['TestStep.Name']).subscribe(results => {
        expect(queryService.query).toHaveBeenCalledWith(query);
      });
    })));
  });

  describe('queryItems()', () => {
    it('should return result for simple query', async(inject([QueryService, MockBackend], (queryService, mockBackend) => {
      mockBackend.connections.subscribe((conn: MockConnection) => {
        if (conn.request.url.endsWith('/query') && conn.request.method === RequestMethod.Post) {
          let queryObject = <Query>JSON.parse(conn.request.getBody());
          let mockResponse = { rows: [
            {source: 'MDMNVH', type: queryObject.resultType, id: 'id1', columns: [{type: 'Test', attribute: 'Name', value: 'TestNumberOne'}]}
          ]};
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        }
        return;
      });
      let item = new MDMItem('MDMNVH', 'Test', 'id1');

      let result = queryService.queryItems([item], ['Test.Name']);
      expect(result.length).toBe(1);
      result[0].subscribe(results => {
        expect(results.rows.length).toBe(1);
        expect(results.rows[0].type).toEqual('Test');
        expect(results.rows[0].columns[0].value).toEqual('TestNumberOne');
      });
    })));
  });
});
