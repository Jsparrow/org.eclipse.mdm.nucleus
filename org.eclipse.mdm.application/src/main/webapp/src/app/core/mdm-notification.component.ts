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


import { Component, OnInit, OnDestroy } from '@angular/core';
import { Message } from 'primeng/primeng';
import { MDMNotificationService } from './mdm-notification.service';
import { Subscription } from 'rxjs/Subscription';

@Component({
  selector: 'mdm-notifications',
  template: '<p-growl [value]="msgs" sticky="true"></p-growl>'
})
export class MDMNotificationComponent implements OnInit, OnDestroy {
  msgs: Message[] = [];
  subscription: Subscription;

  constructor(private notificationsService: MDMNotificationService) { }

  ngOnInit() {
    this.subscribeToNotifications();
  }

  subscribeToNotifications() {
    this.subscription = this.notificationsService.notificationChange
      .subscribe(notification => this.msgs.push(notification));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
