import { Component } from '@angular/core';

@Component( {
    selector: 'admin-system',
    template: '<mdm-preference [scope]="scope"></mdm-preference>'
})
export class AdminSystemComponent {

    scope: string = 'System';

}
