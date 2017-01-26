import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Node } from './node';
import { MockTestNodes, MockNodeProvider } from './mdm-navigator.MockNodes';
import { NodeService } from './node.service';
import { NavigatorService } from './navigator.service';
import { MDMNodeProviderComponent } from './mdm-node-provider.component';

describe ( 'Navigator Tree, node-provider component', () => {

    class NodeServiceMock {
        nodeProviderChanged: Observable<any> = Observable.of(MockNodeProvider);

        getNodes(): Observable<Node[]> { return Observable.of(MockTestNodes.data); };

        compareNode() { return true; };
    }

    let fixture: ComponentFixture<MDMNodeProviderComponent>;
    let comp: MDMNodeProviderComponent;
    let de, listElement_span, listElement_a: DebugElement;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [MDMNodeProviderComponent],
            providers: [NavigatorService],
            imports: []
        });
        TestBed.overrideComponent(MDMNodeProviderComponent, {
            set: {
                providers: [{provide: NodeService, useClass: NodeServiceMock}],
                template: `
                  <ul class="list-group">
                    <div *ngFor="let node of nodes">
                      <li class="list-group-item" [ngClass]="isActive(node)">
                        <span style="cursor: pointer;"
                          [style.margin-left.px]="getMargin()"
                          [ngClass]="isOpen(node)" (click)="onOpenNode(node)"> </span>
                        <a [ngClass]="getNodeClass(node)" style="color:black; cursor: pointer;"
                        (click)="onSelectNode(node)" title="getNodeLabel(node)">{{getNodeLabel(node)}} </a>
                      </li>
                      <mdm-node-provider *ngIf="node.active" [rootNode]="openNode"
                        [indent]="getMargin()" [activeNode]="activeNode"
                        (selectingNode)="updateSelectedNode($event)" (onActive)="updateActiveNode($event)">
                        Loading... </mdm-node-provider>
                    </div>
                  </ul>`
            }
        });

        fixture = TestBed.createComponent(MDMNodeProviderComponent);
        comp = fixture.componentInstance;
        comp.rootNode = new Node();
        fixture.detectChanges();
    });

    it('should show test Nodes', () => {
       expect(comp.nodes).toEqual(MockTestNodes.data);
       de = fixture.debugElement.query(By.css('ul'));
       for ( let i = 0; i < comp.nodes.length; i++ ) {
           expect(de.nativeElement.textContent).toContain(comp.nodes[i].name);
       }
    });

});
