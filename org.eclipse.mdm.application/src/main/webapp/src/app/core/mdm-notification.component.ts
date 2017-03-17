import { Component, OnInit, OnDestroy } from '@angular/core';
import { Message } from 'primeng/primeng';
import { MDMNotificationService } from './mdm-notification.service';
import { Subscription } from 'rxjs/Subscription';

@Component({
  selector: 'mdm-notifications',
  template: '<p-growl [value]="msgs"></p-growl>'
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
