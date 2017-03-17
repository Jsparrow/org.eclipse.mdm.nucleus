import { Injectable, EventEmitter } from '@angular/core';
import { Message } from 'primeng/primeng';

type Severities = 'success' | 'info' | 'warn' | 'error';

@Injectable()
export class MDMNotificationService {
  notificationChange = new EventEmitter<Message>();

  notify(severity: Severities, summary: string, detail: string) {
    this.notificationChange.emit({ severity, summary, detail });
  }

  notifyError(summary: string, detail: string) {
    this.notify('error', summary, detail);
  }

  notifyWarn(summary: string, detail: string) {
    this.notify('warn', summary, detail );
  }

  notifyInfo(summary: string, detail: string) {
    this.notify('info', summary, detail);
  }

  notifySuccess(summary: string, detail: string) {
    this.notify('success', summary, detail);
  }
}
