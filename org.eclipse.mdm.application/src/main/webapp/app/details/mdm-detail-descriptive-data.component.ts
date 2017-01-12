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
import {Component, OnInit, Input, OnChanges, SimpleChange} from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { AccordionComponent, AccordionModule } from 'ng2-bootstrap';

import {NodeService} from '../navigator/node.service';
import {ContextService} from './context.service';
import {Context, Sensor} from './context';
import {Node} from '../navigator/node';
import {NavigatorService} from '../navigator/navigator.service';

@Component({
  selector: 'mdm-detail-context',
  templateUrl: 'mdm-detail-descriptive-data.component.html',
  providers: [ContextService]
})
export class MDMDescriptiveDataComponent implements OnInit {
  selectedNode: Node;
  context: String;

  _diff: boolean = false;
  contexts: Context[];
  sensors: Sensor[];
  errorMessage: string;
  status: string = 'loading...';

  uut: string = 'Prüfling';
  ts: string = 'Messgerät';
  te: string = 'Testablauf';
  s: string = 'Sensoren';

  constructor(private route: ActivatedRoute,
              private _contextService: ContextService,
              private navigatorService: NavigatorService) {}

  ngOnInit() {
    this.route.params.map(params => params['context'])
      .subscribe(context => this.context = context);

    this.navigatorService.selectedNodeChanged
        .subscribe(node => this.loadContext(node));
  }

  loadContext(node: Node) {
    this.selectedNode = node;
    this.contexts = undefined;
    if (node.name !== undefined && (node.type.toLowerCase() === 'measurement' || node.type.toLowerCase() === 'teststep')) {
      this.status = 'loading...';
      this._contextService.getContext(node).subscribe(
        contexts => this.contexts = contexts,
        error => this.errorMessage = <any>error);
      this._contextService.getSensors(node).subscribe(
          sensors => this.sensors = sensors,
          error => this.errorMessage = <any>error);
    } else {
      this.status = 'keine Beschreibende Daten verfügbar';
    }
  }

  diffToggle() {
    this._diff = !this._diff;
  }

  diff(attr1: string, attr2: string) {
    if (attr1 !== attr2 && this._diff) {
      return 'danger';
    }
  }
}
