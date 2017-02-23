import {Component, Output, EventEmitter} from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: '[search-datepicker]',
  templateUrl: 'search-datepicker.component.html'
})
export class SearchDatepickerComponent {
  @Output() onSetValue = new EventEmitter<string>();

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
