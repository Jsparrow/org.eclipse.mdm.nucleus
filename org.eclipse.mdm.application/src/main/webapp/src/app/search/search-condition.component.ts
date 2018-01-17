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

import {Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {SearchBase} from './search-base';
import {LocalizationService} from '../localization/localization.service';
import {FilterService, SearchFilter, Condition, Operator, OperatorUtil} from './filter.service';
import {Node} from '../navigator/node';

import {PropertyService} from '../core/property.service';
import {QueryService} from '../tableview/query.service';
import {AutoCompleteModule} from 'primeng/primeng';
import {AutoComplete} from 'primeng/primeng';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component({
  selector: '[search-condition]',
  templateUrl: 'search-condition.component.html',
  styleUrls: ['search-condition.component.css'],
})
export class SearchConditionComponent implements OnChanges {

  readonly TtlSelectSearchOperator = 'Suchoperator auswählen';

  @Input() env: string;
  @Input() condition: Condition;
  @Input() form: FormGroup;
  @Input() disabled: boolean;
  @Input() selectedEnvs: Node[];
  @Output() onRemove = new EventEmitter<Condition>();

  suggestions: string[];
  displayedSuggestions: string[] = [];
  lastQuery: string;

  @ViewChild(AutoComplete) primeAutoCompleteComponent: AutoComplete;

  constructor(private localservice: LocalizationService,
              private prop: PropertyService,
              private queryService: QueryService,
              private notificationService: MDMNotificationService) { }

  ngAfterViewInit() {
    if (this.primeAutoCompleteComponent) {
      /* Workaround for missing onBlur handler in primeng version 2.x.
       * We overwrite the existing implementation and additional call our own event handler.
       * In later versions this feature was added https://github.com/primefaces/primeng/issues/2256
       * and this workaround should be removed when using primeng version 4 or later
       */
      this.primeAutoCompleteComponent.onBlur = function() {
        this.primeAutoCompleteComponent.focus = false;
        this.primeAutoCompleteComponent.onModelTouched();
        this.onAutocompleteBlur();
      }.bind(this);
    }
  }

  onAutocompleteBlur() {
    this.onEnter(new Event("blur"));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['selectedEnvs'] && this.condition.valueType === 'string') {
      this.setAutoCompleteValues();
    }
  }

  onEnter(e: Event) {
    let hl = this.primeAutoCompleteComponent.highlightOption;
    if (hl === undefined && this.lastQuery !== '' || hl !== undefined && this.displayedSuggestions.find(s => s === hl) === undefined) {
      if (this.primeAutoCompleteComponent.value[this.primeAutoCompleteComponent.value.length - 1] === hl) {
        this.primeAutoCompleteComponent.value.pop();
      }
      this.primeAutoCompleteComponent.selectItem(this.lastQuery);
      this.lastQuery = '';
    }
    this.primeAutoCompleteComponent.highlightOption = undefined;
    this.displayedSuggestions = [];
  }

  setAutoCompleteValues() {
    this.queryService.suggestValues(this.env === 'Global' ?
        this.selectedEnvs.map(env => env.sourceName) :
        [this.env], this.condition.type, this.condition.attribute)
      .subscribe(
        values => this.suggestions = Array.from(new Set<string>(values)),
        error => this.notificationService.notifyError('Autotvervollständigung kann nicht initialisiert werden.', error)
      );
  }

  updateSuggestions(e: any) {
    if (this.suggestions) {
    this.displayedSuggestions =
      [...this.suggestions.filter(sug => sug.toLowerCase().indexOf(e.query.toLowerCase()) > -1).slice(0, 10)];
    }
    this.lastQuery = e.query;
  }

  getTrans(label: string) {
    let a = label.split('.');
    return this.localservice.getTranslation(a[0], a[1]);
  }

  getOperators() {
    return OperatorUtil.values();
  }

  getOperatorName(op: Operator) {
    return OperatorUtil.toString(op);
  }

  setOperator(operator: Operator) {
    this.condition.operator = operator;
  }

  setValue(value: string) {
    this.condition.value = [value];
  }

  setValues(value: string[]) {
    this.condition.value = value;
  }

  remove() {
    this.onRemove.emit(this.condition);
  }
}
