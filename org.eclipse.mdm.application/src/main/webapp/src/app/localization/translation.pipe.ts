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


import { Pipe, PipeTransform } from '@angular/core';
import {LocalizationService} from '../localization/localization.service';
import {Localization} from './localization';

@Pipe({
  name: 'translate',
  pure: false
})
export class TranslationPipe implements PipeTransform {

  private translation: string;

  constructor(private localService: LocalizationService) {}

  transform(type: string, attr?: string): any {
    return type ? this.getTrans(type, attr) : type;
  }

  private getTrans(type: string, attr: string) {
    this.localService.getLocalizations()
                     .map(locs => this.getTranslation(locs, type, attr))
                     .subscribe(t => this.translation = t);
    return this.translation;
  }

  private getTranslation(locs: Localization[], type: string, attr: string) {
    let trans = attr ? type + '.' + attr : type;
    let temp = locs.find(l => l.name === trans);
    return temp ? temp.localizedName : trans;
  }

}
