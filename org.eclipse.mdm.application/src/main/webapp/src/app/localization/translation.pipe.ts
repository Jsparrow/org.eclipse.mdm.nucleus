/*******************************************************************************
*  Copyright (c) 2018 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Johannes Stamm - initial implementation                                     *
*******************************************************************************/

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
