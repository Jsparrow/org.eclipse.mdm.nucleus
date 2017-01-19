import { Component } from '@angular/core';

@Component( {
    selector: 'admin-user',
    template: '<mdm-preference [scope]="scope"></mdm-preference>'
})
export class AdminUserComponent {

    scope: string = 'User';

}
