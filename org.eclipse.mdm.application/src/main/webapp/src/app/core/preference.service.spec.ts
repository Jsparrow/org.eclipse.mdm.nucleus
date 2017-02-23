import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend } from '@angular/http/testing';

import {PreferenceService, Preference} from './preference.service';
import {PropertyService} from './property.service';

describe('PreferenceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        PropertyService,
        PreferenceService,
        MockBackend,
        BaseRequestOptions,
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


  describe('getPreference()', () => {
    it('should return preferences', async(inject([PreferenceService, MockBackend], (prefService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {

        let mockResponse = {
          preferences: [
          {
            id: 2,
            key: 'preference.prefix.',
            scope: 'System',
            source: null,
            user: null,
            value: 'Test'
          }
        ]};
        conn.mockRespond(new Response(new ResponseOptions({ body: mockResponse })));
      });

      prefService.getPreference('System', 'preference.prefix.').subscribe(prefs => {
        expect(prefs.length).toBe(1);
        expect(prefs[0].scope).toBe('System');
        expect(prefs[0].value).toBe('Test');
      });
    })));

    it('should return empty array if no preferences were found', async(inject([PreferenceService, MockBackend], (prefService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {
        conn.mockRespond(new Response(new ResponseOptions({ body: { preferences: [] } })));
      });

      prefService.getPreference('System', 'preference.prefix.').subscribe(prefs => {
        expect(prefs.length).toBe(0);
      });
    })));
  });

  describe('savePreference()', () => {
    it('should post preference', async(inject([PreferenceService, MockBackend], (prefService, mockBackend) => {

      mockBackend.connections.subscribe(conn => {
        if (conn.request.url.endsWith('/preference') && conn.request.method === RequestMethod.Put) {
          conn.mockRespond(new Response(new ResponseOptions({ body: { preferences: [] } })));
        }
      });
      let newPref = new Preference();
      newPref.scope = 'System';
      newPref.key = 'prefix.';
      newPref.value = 'testValue';

      prefService.savePreference(newPref).subscribe(prefs => {
        expect(prefs).toBeDefined();
      });
    })));
  });
});
