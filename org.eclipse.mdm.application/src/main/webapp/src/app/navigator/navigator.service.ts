import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {MDMItem} from '../core/mdm-item';
import {Node} from './node';
import {PropertyService} from '../core/property.service';
import {NodeService} from './node.service';


@Injectable()
export class NavigatorService {

  public selectedNodeChanged: EventEmitter<Node> = new EventEmitter<Node>();
  private selectedNode: Node;

  constructor(private nodeService: NodeService) {

  }
  setSelectedNode(node: Node) {
    this.selectedNode = node;
    this.selectedNodeChanged.emit(this.selectedNode);
  }

  setSelectedItem(item: MDMItem) {
    this.nodeService.getNodeFromItem(item)
        .subscribe(node => this.setSelectedNode(node));
  }

  getSelectedNode(): Node {
    return this.selectedNode;
  }
}
