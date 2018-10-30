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


import { Injectable, EventEmitter } from '@angular/core';
import { Message } from 'primeng/primeng';

type Severities = 'success' | 'info' | 'warn' | 'error';

@Injectable()
export class MDMNotificationService {
  notificationChange = new EventEmitter<Message>();

  notify(severity: Severities, summary: string, detail: any) {
    this.notificationChange.emit({ severity, summary, detail });
  }

  notifyError(summary: string, detail: any) {
    this.notify('error', summary, detail);
  }

  notifyWarn(summary: string, detail: any) {
    this.notify('warn', summary, detail );
  }

  notifyInfo(summary: string, detail: any) {
    this.notify('info', summary, detail);
  }

  notifySuccess(summary: string, detail: any) {
    this.notify('success', summary, detail);
  }
}
