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
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions } from '@angular/http';
import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { MockBackend } from '@angular/http/testing';

import {SearchService, SearchAttribute, SearchLayout} from './search.service';
import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {PropertyService} from '../core/property.service';
import {LocalizationService} from '../localization/localization.service';
import {NodeService} from '../navigator/node.service';
import {QueryService} from '../tableview/query.service';
import {MDMNotificationService} from '../core/mdm-notification.service';
import {HttpErrorHandler} from '../core/http-error-handler';

import {Condition, Operator} from './filter.service';

class TestPreferenceService {
  getPreference(key?: string): Observable<Preference[]> {
    return Observable.of([
    {
      id: 1,
      key: 'ignoredAttributes',
      scope: Scope.USER,
      source: null,
      user: 'testUser',
      value: '[\"*.Name\", \"TestStep.Id\", \"Measurement.*\"]'
      // value: '[\"*.MimeType\", \"TestStep.Sortindex\"]'
    }
  ]);
  }
}

describe ( 'SearchService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        LocalizationService,
        PropertyService,
        {
          provide: PreferenceService,
          useClass: TestPreferenceService
        },
        SearchService,
        MDMNotificationService,
        NodeService,
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

  describe('groupByEnv()', () => {
    it('should group attributes by environment', () => {
      let attributes = [
        new SearchAttribute('env1', 'Test', 'Name'),
        new SearchAttribute('env1', 'Vehicle', 'Name'),
        new SearchAttribute('env2', 'Test', 'Name'),
        new SearchAttribute('env2', 'Uut', 'Name'),
      ];

      let conditionsPerEnv = SearchLayout.groupByEnv(attributes);

      expect(Object.getOwnPropertyNames(conditionsPerEnv))
        .toContain('env1', 'env2');

      expect(conditionsPerEnv['env1'])
        .toContain(attributes[0], attributes[1]);

      expect(conditionsPerEnv['env2'])
        .toContain(attributes[2], attributes[3]);
    });
  });

  describe('createSearchLayout()', () => {
    it('should create a searchLayout object from conditions', () => {
      let cond1 = new Condition('Test', 'Name', Operator.EQUALS, []);
      let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, []);

      let attributes = {
        'env1': [
          new SearchAttribute('env1', 'Test', 'Name'),
          new SearchAttribute('env1', 'Vehicle', 'Name'),
        ],
        'env2': [
          new SearchAttribute('env2', 'Test', 'Name'),
          new SearchAttribute('env2', 'Uut', 'Name'),
        ]
      };

      let searchLayout = SearchLayout.createSearchLayout(['env1', 'env2'], attributes, [cond1, cond2]);

      expect(searchLayout.getSourceNames())
        .toEqual(['Global', 'env1']);

      expect(searchLayout.getConditions('Global'))
        .toEqual([cond1]);

      expect(searchLayout.getConditions('env1'))
        .toEqual([cond2]);

    });
  });

  describe('convert()', () => {
    it('should convert conditions to filter string',  async(inject([SearchService, MockBackend], (service, mockBackend) => {
      let cond1 = new Condition('Test', 'Name', Operator.LIKE, ['PBN*']);
      let cond2 = new Condition('Vehicle', 'Number', Operator.EQUALS, ['12']);
      let cond3 = new Condition('Vehicle', 'Created', Operator.EQUALS, ['2017-07-17T12:13:14']);

      let attributes = [
          new SearchAttribute('env1', 'Test', 'Name'),
          new SearchAttribute('env1', 'Vehicle', 'Number', 'LONG'),
          new SearchAttribute('env2', 'Test', 'Name'),
          new SearchAttribute('env2', 'Uut', 'Name'),
          new SearchAttribute('env2', 'Vehicle', 'Created', 'DATE'),
        ];

      let filter = service.convertEnv('env1', [cond1, cond2, cond3], attributes, 'test');

      expect(filter.sourceName).toEqual('env1');
      expect(filter.filter).toEqual("Test.Name ci_lk 'PBN*' and Vehicle.Number eq 12 and Vehicle.Created eq '2017-07-17T12:13:14'");
      expect(filter.searchString).toEqual('test');

    })));
  });

  describe('loadSearchAttributes()', () => {
    it('should return filtered search attributes for env',
      async(inject([SearchService, MockBackend],
        (searchService, mockBackend) => {
      mockBackend.connections.subscribe(conn => {
        let mockResponse = {
          data: [
            {
              boType: 'Test',
              attrName: 'Name',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'Test',
              attrName: 'Id',
              valueType: 'LONG',
              criteria: '*'
            },
            {
              boType: 'TestStep',
              attrName: 'Name',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'TestStep',
              attrName: 'MimeType',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'TestStep',
              attrName: 'Id',
              valueType: 'LONG',
              criteria: '*'
            },
            {
              boType: 'Measurement',
              attrName: 'Name',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'Measurement',
              attrName: 'Id',
              valueType: 'LONG',
              criteria: '*'
            }
        ]};
        if (conn.request.url === searchService._prop.getUrl() + '/mdm/environments/TestEnv//searchattributes') {
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        } else if (conn.request.url === searchService._prop.getUrl() + '/mdm/environments/' + 'TestEnv2' + '/' + '/searchattributes') {
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        }
      });
      let ans = [
        new SearchAttribute('TestEnv', 'Test', 'Id', 'LONG', '*'),
        new SearchAttribute('TestEnv', 'TestStep', 'MimeType', 'STRING', '*'),
      ];
      searchService.loadSearchAttributes('', 'TestEnv').subscribe(sas => {
        expect(sas).toEqual(ans);
      });
    })));
  });

  describe('getFilters()', () => {
    it('should retrun ignoredAttributes in a string array',
      async(inject([SearchService],
        (searchService) => {
      expect(searchService.getFilters(undefined)).toEqual(['*.Name', 'TestStep.Id', 'Measurement.*']);
    })));
  });

  describe('filterIgnoredAttributes', () => {
    it('should return searchAttributes without the ignored ones',
      async(inject([SearchService], (searchService) => {

    let input = [
      new SearchAttribute('TestEnv', 'Test', 'Name', 'STRING'),
      new SearchAttribute('TestEnv', 'Test', 'MimeType', 'STRING'),
      new SearchAttribute('TestEnv', 'Test', 'Id', 'LONG'),
      new SearchAttribute('TestEnv', 'TestStep', 'Name', 'STRING'),
      new SearchAttribute('TestEnv', 'TestStep', 'MimeType', 'STRING'),
      new SearchAttribute('TestEnv', 'TestStep', 'Id', 'LONG'),
      new SearchAttribute('TestEnv', 'Measurement', 'Name', 'STRING'),
      new SearchAttribute('TestEnv', 'Measurement', 'MimeType', 'STRING'),
      new SearchAttribute('TestEnv', 'Measurement', 'Id', 'LONG')
    ];

  let ans = [
    new SearchAttribute('TestEnv', 'Test', 'MimeType', 'STRING'),
    new SearchAttribute('TestEnv', 'Test', 'Id', 'LONG'),
    new SearchAttribute('TestEnv', 'TestStep', 'MimeType', 'STRING')
  ];

  expect(searchService.filterIgnoredAttributes(undefined, input)).toEqual(ans);
    })));
  });

  describe('getSearchAttributes()', () => {
    it('should return filtered search attributes for env',
      async(inject([SearchService, MockBackend],
        (searchService, mockBackend) => {
      mockBackend.connections.subscribe(conn => {
        let mockResponse = {
          data: [
            {
              boType: 'Test',
              attrName: 'Name',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'Test',
              attrName: 'Id',
              valueType: 'LONG',
              criteria: '*'
            },
            {
              boType: 'TestStep',
              attrName: 'Name',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'TestStep',
              attrName: 'MimeType',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'TestStep',
              attrName: 'Id',
              valueType: 'LONG',
              criteria: '*'
            },
            {
              boType: 'Measurement',
              attrName: 'Name',
              valueType: 'STRING',
              criteria: '*'
            },
            {
              boType: 'Measurement',
              attrName: 'Id',
              valueType: 'LONG',
              criteria: '*'
            }
        ]};
        if (conn.request.url === searchService._prop.getUrl() + '/mdm/environments/TestEnv//searchattributes') {
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        } else if (conn.request.url === searchService._prop.getUrl() + '/mdm/environments/TestEnv2//searchattributes') {
          conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
        }
      });
      let ans = [
        new SearchAttribute('TestEnv', 'Test', 'Id', 'LONG', '*'),
        new SearchAttribute('TestEnv', 'TestStep', 'MimeType', 'STRING', '*'),
        new SearchAttribute('TestEnv2', 'Test', 'Id', 'LONG', '*'),
        new SearchAttribute('TestEnv2', 'TestStep', 'MimeType', 'STRING', '*'),
      ];

      searchService.getSearchAttributesPerEnvs(['TestEnv', 'TestEnv2'], '').subscribe(sas => expect(sas).toEqual(ans));
    })));
  });
});
