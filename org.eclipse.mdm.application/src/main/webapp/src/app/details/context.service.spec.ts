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
import { BaseRequestOptions, Http, HttpModule } from '@angular/http';
import { MockBackend } from '@angular/http/testing';

import {ContextService, ContextAttribute, MergedContextAttribute} from './context.service';
import {PropertyService} from '../core/property.service';
import {HttpErrorHandler} from '../core/http-error-handler';

describe('ContextService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        MockBackend,
        BaseRequestOptions,
        HttpErrorHandler,
        {
          provide: Http,
          useFactory: (mockBackend, options) => {
            return new Http(mockBackend, options);
          },
          deps: [MockBackend, BaseRequestOptions]
        },
        PropertyService,
        ContextService
      ]
    });
  });

  describe('mergeAttributes()', () => {
    it('should merge value of one attribute', async(inject([ContextService], (contextService) => {
      let attribute1 : ContextAttribute = {
        "name" : "size",
        "value" : "95R16",
        "unit" : "",
        "dataType" : "STRING"
      }
      let attributes = [ attribute1 ];
      let contextIndex = 2;
      let resultAttributes: MergedContextAttribute[] = [];

      expect(contextService.mergeAttributes(attributes, contextIndex, resultAttributes)).toEqual(
        [{
          "name" : "size",
          "value" : [undefined, undefined, "95R16"],
          "unit" : "",
          "dataType" : "STRING"
        }]
      );
    })))

    it('should merge values of multiple attributes', async(inject([ContextService], (contextService) => {
      let attribute1 : ContextAttribute = {
        "name" : "size",
        "value" : "95R16",
        "unit" : "",
        "dataType" : "STRING"
      }
      let attribute2 : ContextAttribute = {
        "name" : "side",
        "value" : "Left",
        "unit" : "",
        "dataType" : "STRING"
      }

      let attributes = [ attribute1, attribute2 ];
      let contextIndex = 0;
      let resultAttributes: MergedContextAttribute[] = [];

      expect(contextService.mergeAttributes(attributes, contextIndex, resultAttributes)).toEqual(
        [{
          "name" : "size",
          "value" : ["95R16"],
          "unit" : "",
          "dataType" : "STRING"
        }, {
          "name" : "side",
          "value" : ["Left"],
          "unit" : "",
          "dataType" : "STRING"
        }]
      );
    })))
  });

  describe('mergeContextRoots()', () => {
    it('should merge attributes values of all context components', async(inject([ContextService], (contextService) => {
      let data = {
        "contextOrdered" : {
          "UNITUNDERTEST" : [{
              "name" : "FL_tyre",
              "id" : "38",
              "type" : "ContextComponent",
              "sourceType" : "tyre",
              "sourceName" : "MDM",
              "attributes" : [{
                  "name" : "size",
                  "value" : "95R16",
                  "unit" : "",
                  "dataType" : "STRING"
                }
              ]
            }
          ]
        },
        "contextMeasured" : {
          "UNITUNDERTEST" : [{
              "name" : "FL_tyre",
              "id" : "39",
              "type" : "ContextComponent",
              "sourceType" : "tyre",
              "sourceName" : "MDM",
              "attributes" : [{
                  "name" : "size",
                  "value" : "95R17",
                  "unit" : "",
                  "dataType" : "STRING"
                }
              ]
            }, {
              "name" : "engine",
              "id" : "12",
              "type" : "ContextComponent",
              "sourceType" : "engine",
              "sourceName" : "MDM",
              "attributes" : [{
                  "name" : "cylinders",
                  "value" : "2",
                  "unit" : "",
                  "dataType" : "STRING"
                }
              ]
            }
          ]
        }
      };

      let mergedData = contextService.mergeContextRoots([data.contextOrdered, data.contextMeasured]);

      expect(mergedData).toEqual({
        "UNITUNDERTEST": [{
          "name" : "FL_tyre",
          "id" : "38",
          "type" : "ContextComponent",
          "sourceType" : "tyre",
          "sourceName" : "MDM",
          "attributes" : [{
            "name" : "size",
            "value" : ["95R16", "95R17"],
            "unit" : "",
            "dataType" : "STRING"
          }]
        }, {
          "name" : "engine",
          "id" : "12",
          "type" : "ContextComponent",
          "sourceType" : "engine",
          "sourceName" : "MDM",
          "attributes" : [{
            "name" : "cylinders",
            "value" : [undefined, "2"],
            "unit" : "",
            "dataType" : "STRING"
          }]
        }]
      });
    })));
  });
});
