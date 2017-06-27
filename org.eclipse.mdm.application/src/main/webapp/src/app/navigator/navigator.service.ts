/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import {Injectable, Output, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {MDMItem} from '../core/mdm-item';
import {Node} from './node';
import {PropertyService} from '../core/property.service';
import {NodeService} from './node.service';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Injectable()
export class NavigatorService {

  @Output() onOpenInTree = new EventEmitter<MDMItem[]>();

  public selectedNodeChanged: EventEmitter<Node> = new EventEmitter<Node>();
  private selectedNode: Node;

  constructor(private nodeService: NodeService,
              private notificationService: MDMNotificationService) {

  }

  setSelectedNode(node: Node) {
    this.selectedNode = node;
    this.fireSelectedNodeChanged(node);
  }

  fireSelectedNodeChanged(node: Node) {
    this.selectedNodeChanged.emit(node);
  }

  setSelectedItem(item: MDMItem) {
    this.nodeService.getNodeFromItem(item)
        .subscribe(
          node => this.setSelectedNode(node),
          error => this.notificationService.notifyError('Item konnte nicht gesetzt werden.', error)
        );
  }

  getSelectedNode(): Node {
    return this.selectedNode;
  }

  fireOnOpenInTree(items: MDMItem[]) {
    this.onOpenInTree.emit(items);
  }
}
