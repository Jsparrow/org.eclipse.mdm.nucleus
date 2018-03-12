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

import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend } from '@angular/http/testing';

import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {PropertyService} from '../core/property.service';
import {HttpErrorHandler} from '../core/http-error-handler';
import {BasketService} from './basket.service';

describe('BasketService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        BasketService,
        PropertyService,
        PreferenceService,
        MockBackend,
        BaseRequestOptions,
        HttpErrorHandler,
        {
          provide: Http,
          useFactory: (mockBackend, options) => {
            return new Http(mockBackend, options);
          },
          deps: [MockBackend, BaseRequestOptions]
        }
      ]
    });
  });


  describe('getFileExtension()', () => {
    it('should return value configured in preference', async(inject([BasketService, MockBackend], (basketService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {

        let mockResponse = {
          preferences: [
          {
            id: 2,
            key: 'shoppingbasket.fileextensions',
            scope: Scope.SYSTEM,
            source: null,
            user: null,
            value: '{"default": "custom"}'
          }
        ]};
        conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
      });

      basketService.getFileExtension().subscribe(
        ext => expect(ext).toBe('custom'),
        err => expect(err).toBeUndefined());
    })));

    it('should return xml (default) if no preferences were found',
        async(inject([BasketService, MockBackend], (basketService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {
        conn.mockRespond(new Response(new ResponseOptions({ body: { preferences: [] } })));
      });

      basketService.getFileExtension().subscribe(
        ext => expect(ext).toBe('xml'),
        err => expect(err).toBeUndefined());
    })));

    it('should return xml (default) if no preferences value is invalid',
        async(inject([BasketService, MockBackend], (basketService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {
        let mockResponse = {
          preferences: [
          {
            id: 2,
            key: 'shoppingbasket.fileextensions',
            scope: Scope.SYSTEM,
            source: null,
            user: null,
            value: 'asdf'
          }
        ]};
        conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
      });

      basketService.getFileExtension().subscribe(
        ext => expect(ext).toBe('xml'));
    })));
  });
});
