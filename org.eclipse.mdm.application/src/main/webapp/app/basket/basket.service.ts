import {Injectable} from '@angular/core';

import {Node} from '../navigator/node';

@Injectable()
export class BasketService {
  Nodes: Node[] = [];

  addNode(node){
    var index = this.Nodes.indexOf(node);
    if (index == -1) {
      this.Nodes.push(node)
    }
  }
  removeNode(node){
    var index = this.Nodes.indexOf(node);
    if (index > -1) {
      this.Nodes.splice(index, 1);
    }
  }
  removeAll(){
    this.Nodes = [];
  }
}
