/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
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

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component({
  selector: 'mdm-detail-context',
  templateUrl: 'mdm-detail-descriptive-data.component.html',
})

export class MDMDescriptiveDataComponent implements OnInit {

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
  status: string = this.StatusLoading;

  uut = 'Prüfling';
  ts = 'Testablauf';
  te = 'Messgerät';
  s = 'Sensoren';

  constructor(private route: ActivatedRoute,
    private localService: LocalizationService,
              private _contextService: ContextService,
              private navigatorService: NavigatorService,
              private notificationService: MDMNotificationService) {}

  ngOnInit() {
    this.status = this.StatusLoading;

    this.loadContext(this.navigatorService.getSelectedNode());

    this.route.params
        .subscribe(
          params => this.setContext(params['context']),
          error => this.notificationService.notifyError('Bereich kann nicht geladen werden.', error)
    );

    this.navigatorService.selectedNodeChanged
        .subscribe(
          node => this.loadContext(node),
          error => this.notificationService.notifyError('Daten können nicht geladen werden.', error));
  }

  setContext(context: string) {
    this.context = context;
  }

  loadContext(node: Node) {
    if (node) {
      this.selectedNode = node;
      this.contexts = undefined;
      if (node.name !== undefined && (node.type.toLowerCase() === 'measurement' || node.type.toLowerCase() === 'teststep')) {
        this.status = this.StatusLoading;
        this._contextService.getContext(node).subscribe(
          contexts => {
            if (contexts.hasOwnProperty('UNITUNDERTEST')
                || contexts.hasOwnProperty('TESTEQUIPMENT')
                || contexts.hasOwnProperty('TESTSEQUENCE')) {
                  this.contexts = contexts;
                } else {
                  this.status = this.StatusNoDescriptiveData;
                }
          },
          error => this.notificationService.notifyError('Kontext kann nicht geladen werden.', error)
        );
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
