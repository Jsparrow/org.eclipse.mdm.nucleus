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

import {Component, Output, Input, EventEmitter} from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: '[search-datepicker]',
  templateUrl: 'search-datepicker.component.html'
})
export class SearchDatepickerComponent {
  @Output() onSetValue = new EventEmitter<string>();
  @Input() disabled = false;

  date: any;
  constructor(private datePipe: DatePipe) {}

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
