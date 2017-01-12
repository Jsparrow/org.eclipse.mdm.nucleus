///<reference path='../../node_modules/@types/jasmine/index.d.ts' />

import { TestBed, async, inject } from '@angular/core/testing';
import {
  BaseRequestOptions,
  HttpModule,
  Http,
  Response,
  ResponseOptions
} from '@angular/http';
import { MockBackend } from '@angular/http/testing';

import {PreferenceService} from './preference.service';
import {PropertyService} from './properties';

describe('PreferenceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
          imports: [HttpModule],
          providers: [
            //{ provide: VIMEO_API_URL, useValue: 'http://example.com' },
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
    it('should have name property set', () => {
      expect(this.service.name).toBe('Injected Service');
    });
  });

});
