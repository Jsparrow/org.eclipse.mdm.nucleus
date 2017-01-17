import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {Node} from './node';
import {PropertyService} from '../core/property.service';

@Injectable()
export class NavigatorService {

  public selectedNodeChanged: EventEmitter<Node> = new EventEmitter<Node>();
  private selectedNode: Node;

  setSelectedNode(node: Node) {
    this.selectedNode = node;
    this.selectedNodeChanged.emit(this.selectedNode);
  }

  getSelectedNode(): Node {
    return this.selectedNode;
  }
}
