/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import {Component, ViewEncapsulation, OnInit, OnDestroy} from '@angular/core';

import { BsDropdownModule, AccordionConfig, BsDropdownConfig } from 'ngx-bootstrap';

import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {NodeproviderService} from '../navigator/nodeprovider.service';
import { SplitPaneModule } from 'ng2-split-pane/lib/ng2-split-pane';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component({
  selector: 'mdm-navigator-view',
  templateUrl: 'mdm-navigator-view.component.html',
  styleUrls: [ './mdm-navigator-view.component.css' ],
  providers: [BsDropdownConfig, AccordionConfig],
  encapsulation: ViewEncapsulation.None
})
export class MDMNavigatorViewComponent implements OnInit, OnDestroy {

  readonly LblNavigator = 'Navigator';
  readonly TtlScrollUp = 'Zum Seitenanfang';
  readonly TtlSelectNodeprovider = 'Baumdarstellung auswählen';

  selectedNode = new Node;
  activeNode: Node;
  closeOther = false;

  activeNodeprovider: any;
  _comp = 'Navigation';
  subscription: any;

  div: any;
  scrollBtnVisible = false;

  constructor(private nodeProviderService: NodeproviderService,
              private notificationService: MDMNotificationService) {}

  onScrollTop() {
    this.div.scrollTop = 0;
  }

  onScroll(event: any) {
    if (event.target.scrollTop > 0) {
      this.scrollBtnVisible = true;
    } else {
      this.scrollBtnVisible = false;
    }
    this.div = event.target;
  }

  updateSelectedNode(node: Node) {
    this.selectedNode = node;
  }
  updateActiveNode(node: Node) {
    this.activeNode = node;
  }
  activateNodeProvider(nodeprovider: any) {
    this.nodeProviderService.setActiveNodeprovider(nodeprovider);
  }

  getNodeproviders() {
    return this.nodeProviderService.getNodeproviders();
  }

  ngOnInit() {
    this.activeNodeprovider = this.nodeProviderService.getActiveNodeprovider();
    this.subscription = this.nodeProviderService.nodeProviderChanged
        .subscribe(
          np => this.activeNodeprovider = np,
          error => this.notificationService.notifyError('Nodeprovider kann nicht aktualisiert werden.', error)
        );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  activate(comp: string) {
    this._comp = comp;
  }

  isActive(comp: string) {
    if (comp === this._comp) {
      return 'active';
    }
  }

  isDropActive(comp: string) {
    if (comp === this._comp) {
      return 'open ';
    }
  }

   minWidthLeft() {
     return 180;
   }

   minWidthRight() {
     return 0.20 * window.innerWidth;
   }

   initRatio() {
     return Math.max(250 / window.innerWidth, 0.20);
   }
}
