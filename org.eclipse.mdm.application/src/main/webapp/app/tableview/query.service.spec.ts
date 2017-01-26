import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';

import { ComponentFixture, TestBed, inject } from '@angular/core/testing';

import { HttpModule } from '@angular/http';
import { PropertyService } from '../core/property.service';
import { QueryService, Query } from './query.service';

describe ( 'QueryService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [PropertyService, QueryService]
    });
  });

  it('query', () => {
    inject([QueryService], (queryService) => {
      let query = new Query();
      queryService.query(query).subscribe((results) => {
        expect(results.rows).toBe(4);
        expect(results.rows[0].columns[0].name).toEqual('Video 0');
      });
    });
  });
});
