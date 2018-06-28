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

import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Localization} from './localization';
import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {PropertyService} from '../core/property.service';
import {HttpErrorHandler} from '../core/http-error-handler';
import {MDMNotificationService} from '../core/mdm-notification.service';

@Injectable()
export class LocalizationService {

  private _nodeUrl: string;

  private cache: Observable<Localization[]>;

  constructor(private http: Http,
              private httpErrorHandler: HttpErrorHandler,
              private _prop: PropertyService,
              private _node: NodeService,
              private notificationService: MDMNotificationService) {

    this._nodeUrl = _prop.getUrl('/mdm/environments');
  }

  // Cashes valueLists if cash is empty. Then returns observable containing cached valueLists.
  getLocalizations() {
    if (!this.cache) {
      this.cache = this._node.getNodes(undefined)
                .flatMap(envs => this.initLocalizations(envs))
                .publishReplay(1)
                .refCount();
    }
    return this.cache;
  }

  private initLocalizations(envs: Node[]) {
    return Observable.forkJoin(envs.map(env => this.getLocalization(env)))
                     .map(locs => locs.reduce((a, b) => a.concat(b), []));
  }

  private getLocalization(node: Node): Observable<Localization[]> {
    let url = this._nodeUrl + '/' + node.sourceName;
    if (node.sourceType === 'Environment') {
      url = url + '/localizations?all=true';
    } else {
      url = url + '/' + node.type.toLowerCase() + 's/localizations';
    }
    return this.get(url);
  }

  private get(url: string) {
    return this.http.get(url)
    .map(res => <Localization[]> res.json().data)
    .catch(this.httpErrorHandler.handleError);
  }
}
