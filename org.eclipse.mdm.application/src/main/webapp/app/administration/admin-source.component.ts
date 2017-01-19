import { Component } from '@angular/core';

@Component( {
    selector: 'admin-source',
    template: '<mdm-preference [scope]="scope"></mdm-preference>'
})
export class AdminSourceComponent  {

    scope: string = 'Source';

}
