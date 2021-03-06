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


import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend } from '@angular/http/testing';
import { Observable } from 'rxjs/Observable';

import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {PropertyService} from '../core/property.service';
import {DetailViewService} from './detail-view.service';
import {MDMNotificationService} from '../core/mdm-notification.service';

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
    }, {
      id: 2,
      key: 'ignoredAttributes',
      scope: Scope.SYSTEM,
      source: null,
      user: null,
      value: '[\"Project.*\"]'
    }, {
      id: 3,
      key: 'ignoredAttributes',
      scope: Scope.SOURCE,
      source: 'MDMTEST',
      user: null,
      value: '[\"*.Id\"]'
    }, {
      id: 4,
      key: 'ignoredAttributes',
      scope: Scope.SOURCE,
      source: 'MDM_OTHER',
      user: null,
      value: '[\"Pool.*\"]'
    }
  ]);
  }
}

describe('DetailViewService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        {
          provide: PreferenceService,
          useClass: TestPreferenceService
        },
        DetailViewService,
        MDMNotificationService
      ]
    });
  });


  describe('getFilters()', () => {
    it('should return filtered attributes', async(inject([DetailViewService], (detailViewService) => {

      expect(detailViewService.getFilters('MDMTEST')).toEqual(['Project.*', '*.Id', '*.MimeType', 'TestStep.Sortindex']);

    })));
  });
});
