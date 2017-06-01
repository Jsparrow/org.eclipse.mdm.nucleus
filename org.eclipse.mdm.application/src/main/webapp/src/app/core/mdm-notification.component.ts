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
    .subscribe(notification => {
      this.msgs.length = 0;
      this.msgs.push(notification);
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
