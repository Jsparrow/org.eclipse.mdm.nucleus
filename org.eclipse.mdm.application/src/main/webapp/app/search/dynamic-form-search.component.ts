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
import {Component, Input} from '@angular/core';
import {ControlGroup} from '@angular/common';
import {SearchBase} from './search-base';
import {LocalizationService} from '../localization/localization.service';

@Component({
  selector:'df-search',
  template: require('../../templates/search/dynamic-form-search.component.html')
})
export class DynamicFormSearchComponent {
  @Input() search:SearchBase<any>;
  @Input() form:ControlGroup;

  constructor(private localservice : LocalizationService) {}
  get isValid() {
    return this.form.controls[this.search.key].valid;
  }
  getTrans(label: string){
    let a = label.split(".")
    return this.localservice.getTranslation(a[0], a[1])
  }
  removeItem(item){
    this.search.active = false;
  }
}
