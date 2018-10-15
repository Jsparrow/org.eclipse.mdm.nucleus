/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


import {Component, OnInit, Output, Input, EventEmitter} from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: '[search-datepicker]',
  templateUrl: 'search-datepicker.component.html'
})
export class SearchDatepickerComponent implements OnInit {

  readonly TtlSelectDate = 'Datum auswählen';

  @Output() onSetValue = new EventEmitter<string>();
  @Input() disabled = false;
  @Input() initialValue: string[];

  date: any;
  constructor(private datePipe: DatePipe) {}

  ngOnInit() {
    if (this.initialValue.length > 0) {
      this.date = this.initialValue[0];
      this.setDate(this.initialValue[0]);
    }
  }

  setDate(inputDate: string) {
    let dateString = inputDate.split(' ')[0];
    let days = dateString.split('.')[0];
    let month = dateString.split('.')[1];
    let year = dateString.split('.')[2];
    let timeString = inputDate.split(' ')[1];
    let date = new Date([month, days, year].join('.') + ' ' + timeString);
    if (date.toString() !== 'Invalid Date') { this.onSelectionDone(date); };
  }

  onSelectionDone(date: Date) {
    this.onSetValue.emit(this.datePipe.transform(date, 'yyyy-MM-dd' + 'T' + 'HH:mm:ss'));
  }

}
