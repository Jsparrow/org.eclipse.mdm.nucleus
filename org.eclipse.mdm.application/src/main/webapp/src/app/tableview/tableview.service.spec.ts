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
import { Observable } from 'rxjs/Observable';

import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions } from '@angular/http';
import { MockBackend } from '@angular/http/testing';

import { ViewService } from './tableview.service';
import { PropertyService } from '../core/property.service';
import { PreferenceService, Scope } from '../core/preference.service';
import {HttpErrorHandler} from '../core/http-error-handler';

import {MDMNotificationService} from '../core/mdm-notification.service';

describe ( 'TableviewService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        ViewService,
        PropertyService,
        PreferenceService,
        MockBackend,
        BaseRequestOptions,
        HttpErrorHandler,
        MDMNotificationService,
        {
          provide: Http,
          useFactory: (backend, options) => new Http(backend, options),
          deps: [MockBackend, BaseRequestOptions]
        }]
    });
  });

  describe('getViews()', () => {
    it('should return view from preference', async(inject([ViewService, MockBackend], (tableviewService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {
        const mockResponse = {
          preferences: [
          {
            id: 22,
            key: 'tableview.view.Test',
            scope: Scope.USER,
            source: null,
            user: 'sa',
            value: '{"columns":[{"type":"Test","name":"Name"},{"type":"TestStep","name":"Name"}],"name":"Test"}'
          }
        ]};
        conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
      });

      tableviewService.getViews().subscribe(prefViews => {
        expect(prefViews.length).toBe(1);
        expect(prefViews[0].scope).toBe(Scope.USER);
        expect(prefViews[0].view.columns.length).toBe(2);
      });
    })));

    it('should return default view, if no view preferences are available',
        async(inject([ViewService, MockBackend], (tableviewService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {
        const mockResponse = { preferences: [] };
        conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
      });

      tableviewService.getViews().subscribe(prefViews => {
        expect(prefViews.length).toBe(1);
        expect(prefViews[0].scope).toBe(Scope.SYSTEM);
        expect(prefViews[0].view.columns.length).toBe(1);
      });
    })));
  });
});
