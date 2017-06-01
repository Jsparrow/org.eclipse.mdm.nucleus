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
  selector: 'sensors',
  templateUrl: 'sensor.component.html',
})
export class SensorComponent implements OnInit {

  readonly LblMeasured = 'Gemessen';
  readonly LblName = 'Name';
  readonly LblOrdered = 'Beauftragt';
  readonly StatusLoading = 'Lädt..';
  readonly StatusNoNodes = 'Keine Knoten verfügbar.';
  readonly StatusNoDescriptiveData = 'Keine beschreibenden Daten verfügbar.';

  selectedNode: Node;
  context: String;

  _diff = false;
  contexts: Context[];
  sensors: Sensor[];
  errorMessage: string;
  status: string;

  uut = 'Prüfling';
  ts = 'Testablauf';
  te = 'Messgerät';
  s = 'Sensoren';

  constructor(private route: ActivatedRoute,
    private localService: LocalizationService,
              private _contextService: ContextService,
              private navigatorService: NavigatorService) {}

  ngOnInit() {
    this.status = this.StatusLoading;
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
        this.status = this.StatusLoading;
        this._contextService.getSensors(node).subscribe(
            sensors => this.sensors = sensors,
            error => this.errorMessage = <any>error);
      } else {
        this.status = this.StatusNoDescriptiveData;
      }
    } else {
      this.status = this.StatusNoNodes;
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
  //
  // getTrans(type: string, attr: string) {
  //   return this.localService.getTranslation(type, attr);
  // }
}
