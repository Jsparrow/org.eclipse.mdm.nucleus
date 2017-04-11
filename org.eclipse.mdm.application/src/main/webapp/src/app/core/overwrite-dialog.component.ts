import { Component, ViewChild, EventEmitter} from '@angular/core';
import { ModalDirective } from 'ng2-bootstrap';

@Component({
  selector: 'overwrite-dialog',
  templateUrl: 'overwrite-dialog.component.html'
})
export class OverwriteDialogComponent {

  label: string;
  overwriteEvent = new EventEmitter<boolean>();

  @ViewChild('lgOverwriteModal')
  private childOverwriteModal: ModalDirective;

  constructor () {}

  showOverwriteModal(label: string) {
    this.label = label;
    this.childOverwriteModal.show();
    return this.overwriteEvent;
  }

  overwrite(b: boolean) {
    this.childOverwriteModal.hide();
    this.overwriteEvent.emit(b);
  }
}
