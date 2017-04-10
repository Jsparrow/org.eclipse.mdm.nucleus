import { ComponentFixture, async, TestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend } from '@angular/http/testing';
import { Observable } from 'rxjs/Observable';

import {PreferenceService, Preference} from '../core/preference.service';
import {PropertyService} from '../core/property.service';
import {DetailViewService} from './detail-view.service';

class TestPreferenceService {
  getPreference(key?: string): Observable<Preference[]> {
    return Observable.of([
    {
      id: 1,
      key: 'ignoredAttributes',
      scope: 'User',
      source: null,
      user: 'testUser',
      value: '[\"*.MimeType\", \"TestStep.Sortindex\"]'
    }, {
      id: 2,
      key: 'ignoredAttributes',
      scope: 'System',
      source: null,
      user: null,
      value: '[\"Project.*\"]'
    }, {
      id: 3,
      key: 'ignoredAttributes',
      scope: 'Source',
      source: 'MDMTEST',
      user: null,
      value: '[\"*.Id\"]'
    }, {
      id: 4,
      key: 'ignoredAttributes',
      scope: 'Source',
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
        DetailViewService
      ]
    });
  });


  describe('getFilters()', () => {
    it('should return filtered attributes', async(inject([DetailViewService], (detailViewService) => {

      expect(detailViewService.getFilters('MDMTEST')).toEqual(['Project.*', '*.Id', '*.MimeType', 'TestStep.Sortindex']);
    })));
  });
});
