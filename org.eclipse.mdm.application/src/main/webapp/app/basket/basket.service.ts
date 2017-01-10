// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Injectable} from '@angular/core';

import {Node} from '../navigator/node';

@Injectable()
export class BasketService {
  Nodes: Node[] = [];

  addNode(node) {
    let index = this.Nodes.indexOf(node);
    if (index === -1) {
      this.Nodes.push(node);
    }
  }
  removeNode(node) {
    let index = this.Nodes.indexOf(node);
    if (index > -1) {
      this.Nodes.splice(index, 1);
    }
  }
  removeAll() {
    this.Nodes = [];
  }
}
