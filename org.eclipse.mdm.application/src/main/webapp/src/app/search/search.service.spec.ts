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
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions } from '@angular/http';
import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { MockBackend } from '@angular/http/testing';

import {SearchService, SearchAttribute, SearchLayout} from './search.service';
import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {PropertyService} from '../core/property.service';
import {LocalizationService} from '../localization/localization.service';
import {NodeService} from '../navigator/node.service';
import {QueryService} from '../tableview/query.service';

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
      value: '[\"*.MimeType\", \"TestStep.Sortindex\"]'
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
        NodeService,
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
      let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, ['car']);

      let attributes = [
          new SearchAttribute('env1', 'Test', 'Name'),
          new SearchAttribute('env1', 'Vehicle', 'Name'),
          new SearchAttribute('env2', 'Test', 'Name'),
          new SearchAttribute('env2', 'Uut', 'Name'),
        ];

      let filter = service.convertEnv('env1', [cond1, cond2], attributes, 'test');

      expect(filter.sourceName).toEqual('env1');
      expect(filter.filter).toEqual('Test.Name lk PBN* and Vehicle.Name eq car');
      expect(filter.searchString).toEqual('test');

    })));
  });
});
