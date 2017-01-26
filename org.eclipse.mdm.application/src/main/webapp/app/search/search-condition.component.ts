import {Component, Input, Output, EventEmitter} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {SearchBase} from './search-base';
import {LocalizationService} from '../localization/localization.service';
import {FilterService, SearchFilter, Condition, Operator} from './filter.service';

@Component({
  selector: '[search-condition]',
  templateUrl: 'search-condition.component.html',
})
export class SearchConditionComponent {
  @Input() env: string;
  @Input() condition: Condition;
  @Input() form: FormGroup;
  @Output() onRemove = new EventEmitter<Condition>();

  constructor(private localservice: LocalizationService) {}

  getTrans(label: string) {
    let a = label.split('.');
    return this.localservice.getTranslation(a[0], a[1]);
  }

  getOperators() {
    return Operator.values();
  }

  getOperatorName(op: Operator) {
    return Operator.toString(op);
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
  /* get isValid() {
    return this.form.controls[this.search.key].valid;
  }
  removeItem(item){
    this.search.active = false;
  }*/
}
