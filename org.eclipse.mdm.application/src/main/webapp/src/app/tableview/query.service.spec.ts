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

import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs/Observable';

import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions, RequestMethod} from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';

import { MDMItem } from '../core/mdm-item';
import { PropertyService } from '../core/property.service';
import { QueryService, Query } from './query.service';

describe ( 'QueryService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        PropertyService,
        QueryService,
        MockBackend,
        BaseRequestOptions,
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
            {source: 'MDMNVH', type: 'Test', id: 1, columns: [{type: 'Test', attribute: 'Name', value: 'TestNumberOne'}]}
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

  describe('queryItems()', () => {
    it('should return result for simple query', async(inject([QueryService, MockBackend], (queryService, mockBackend) => {
      mockBackend.connections.subscribe((conn: MockConnection) => {
        if (conn.request.url.endsWith('/query') && conn.request.method === RequestMethod.Post) {
          let queryObject = <Query>JSON.parse(conn.request.getBody());
          let mockResponse = { rows: [
            {source: 'MDMNVH', type: queryObject.resultType, id: 1, columns: [{type: 'Test', attribute: 'Name', value: 'TestNumberOne'}]}
          ]};
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        }
        return;
      });
      let item = new MDMItem('MDMNVH', 'Test', 1);

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
