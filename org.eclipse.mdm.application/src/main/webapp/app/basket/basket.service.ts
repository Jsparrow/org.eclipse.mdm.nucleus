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
import {Injectable, EventEmitter} from '@angular/core';

import {Node} from '../navigator/node';

import {PreferenceService} from '../core/preference.service';
import {Preference} from '../core/preference.service';

@Injectable()
export class BasketService {
  Nodes: Node[] = [];
  public nodesChanged$ = new EventEmitter<Node>();

  constructor(private _pref: PreferenceService) {
  }

  addNode(node) {
    let index = this.Nodes.indexOf(node);
    if (index === -1) {
      this.Nodes.push(node);
    }
    this.nodesChanged$.emit(node);
  }
  removeNode(node) {
    let index = this.Nodes.indexOf(node);
    if (index > -1) {
      this.Nodes.splice(index, 1);
    }
    this.nodesChanged$.emit(node);
  }
  removeAll() {
    this.Nodes = [];
  }

  saveNodes(nodes: Node[], basketName: string) {
    let pref = new Preference();
    pref.value = JSON.stringify(nodes);
    pref.key = 'basket.nodes.' + basketName;
    pref.scope = 'User';
    return this._pref.savePreference(pref);
  }

  getBasketNodes() {
    return this._pref.getPreference('', 'basket.nodes.').then(preferences => this.preparePrefs(preferences));
  }

  preparePrefs(prefs: Preference[]) {
    let prefix = 'basket.nodes.';
    let basketNodes: any[] = [];
    for (let i = 0; i < prefs.length; i++) {
        basketNodes.push({ name: prefs[i].key.replace(prefix, ''), nodes: JSON.parse(prefs[i].value) });
    }
    return basketNodes;
  }
}
