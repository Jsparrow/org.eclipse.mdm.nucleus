import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchattributeTreeComponent } from './searchattribute-tree.component';

describe('SearchattributeTreeComponent', () => {
  let component: SearchattributeTreeComponent;
  let fixture: ComponentFixture<SearchattributeTreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchattributeTreeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchattributeTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
