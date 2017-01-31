import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Node } from './node';
import { MockEnvNodes, MockNodeProvider } from './mdm-navigator.MockNodes';
import { NodeService } from './node.service';
import { NavigatorService } from './navigator.service';
import { MDMNavigatorComponent } from './mdm-navigator.component';
import { MDMNodeProviderComponent } from './mdm-node-provider.component';

describe ( 'Navigator Tree, navigator component', () => {

    class NodeServiceMock {
        getNodes(): Observable<Node[]> { return Observable.of(MockEnvNodes.data); };

        nodeProviderChanged: Observable<any> = Observable.of(MockNodeProvider);

        compareNode() { return true; };
    }

    let fixture: ComponentFixture<MDMNavigatorComponent>;
    let comp: MDMNavigatorComponent;
    let de, listElement_span, listElement_a: DebugElement;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [MDMNavigatorComponent,
                           MDMNodeProviderComponent
                           ],
            providers: [NavigatorService],
            imports: []
        });
        TestBed.overrideComponent(MDMNavigatorComponent, {
            set: {
                providers: [{provide: NodeService, useClass: NodeServiceMock}]
            }
        });

        fixture = TestBed.createComponent(MDMNavigatorComponent);
        comp = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should show environment Node', () => {
       expect(comp.nodes).toEqual(MockEnvNodes.data);
       de = fixture.debugElement.query(By.css('li'));
       expect(de.nativeElement.textContent).toContain(comp.nodes[0].name);
    });

    it('should open Node after click and emit active node', () => {
        listElement_span = fixture.debugElement.query(By.css('span'));
        listElement_span.nativeElement.click();
        expect(comp.nodes[0].active).toBeTruthy();

        listElement_a = fixture.debugElement.query(By.css('a'));
        listElement_a.nativeElement.click();
        comp.onActive.subscribe(node => {
            expect(node).toEqual(comp.nodes);
        });
     });
});
