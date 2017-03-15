import {Injectable, EventEmitter} from '@angular/core';
import {TreeNode} from 'primeng/primeng';

@Injectable()
export class SearchattributeTreeService {

public onNodeSelect$ = new EventEmitter<TreeNode>();

  constructor() {
  }

  fireOnNodeSelect(node: TreeNode) {
    this.onNodeSelect$.emit(node);
  }
}
