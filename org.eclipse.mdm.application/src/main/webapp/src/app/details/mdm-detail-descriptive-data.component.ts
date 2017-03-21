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
import {LocalizationService} from '../localization/localization.service';

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

  _diff = false;
  contexts: Context[];
  sensors: Sensor[];
  errorMessage: string;
  status = 'loading...';

  uut = 'Prüfling';
  ts = 'Messgerät';
  te = 'Testablauf';
  s = 'Sensoren';

  constructor(private route: ActivatedRoute,
    private localService: LocalizationService,
              private _contextService: ContextService,
              private navigatorService: NavigatorService) {}

  ngOnInit() {
    this.route.params
        .subscribe(params => this.setContext(params['context'])
    );

    this.navigatorService.selectedNodeChanged
        .subscribe(node => this.loadContext(node));
  }

  setContext(context: string) {
    this.context = context;
    this.loadContext(this.navigatorService.getSelectedNode());
  }

  loadContext(node: Node) {
    if (node) {
      this.selectedNode = node;
      this.contexts = undefined;
      if (node.name !== undefined && (node.type.toLowerCase() === 'measurement' || node.type.toLowerCase() === 'teststep')) {
        this.status = 'loading...';
        this._contextService.getContext(node).subscribe(
          contexts => {
            if (contexts.hasOwnProperty('UNITUNDERTEST')
                || contexts.hasOwnProperty('TESTEQUIPMENT')
                || contexts.hasOwnProperty('TESTSEQUENCE')) {
                  this.contexts = contexts;
                } else {
                  this.status = 'Keine beschreibende Daten verfügbar';
                }
          },
          error => this.errorMessage = <any>error);
        this._contextService.getSensors(node).subscribe(
            sensors => this.sensors = sensors,
            error => this.errorMessage = <any>error);
      } else {
        this.status = 'Keine beschreibende Daten verfügbar';
      }
    } else {
      this.status = 'Kein Knoten ausgewählt';
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

  isUUT() {
    return this.context.toLowerCase() === 'uut';
  }

  isTE() {
    return this.context.toLowerCase() === 'te';
  }

  isTS() {
    return this.context.toLowerCase() === 'ts';
  }

  getTrans(type: string, attr: string) {
    return this.localService.getTranslation(type, attr);
  }
}
