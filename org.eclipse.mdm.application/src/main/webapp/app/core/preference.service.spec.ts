///<reference path='../../node_modules/@types/jasmine/index.d.ts' />

import { TestBed, async, inject, getTestBed } from '@angular/core/testing';
import {
  BaseRequestOptions,
  HttpModule,
  Http,
  Response,
  ResponseOptions
} from '@angular/http';
import { MockBackend } from '@angular/http/testing';

import {PreferenceService} from './preference.service';
import {PropertyService} from './property.service';

describe('PreferenceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
          imports: [HttpModule],
          providers: [
            PropertyService,
            PreferenceService,
            {
              provide: Http,
              useFactory: (mockBackend, options) => {
                return new Http(mockBackend, options);
              },
              deps: [MockBackend, BaseRequestOptions]
            },
            MockBackend,
            BaseRequestOptions
          ]
        });
  });
  describe('getPreference', () => {
    it('should have name property set', inject([PreferenceService, MockBackend], (prefService, mockBackend) => {
//      expect(this.service.name).toBe('Injected Service');
      prefService = getTestBed().get(PreferenceService);
      expect(prefService).toBeDefined();
    }));
  });

});
