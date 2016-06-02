import {Component, Input, OnInit} from '@angular/core';
import {ControlGroup} from '@angular/common';
import {SearchBase} from './search-base';
import {SearchControlService} from './search-control.service';
import {DynamicFormSearchComponent} from './dynamic-form-search.component';
// @Component({
//   selector:'dynamic-form',
//   templateUrl:'templates/search/dynamic-form.component.html',
//   directives: [DynamicFormSearchComponent],
//   providers:  [SearchControlService]
// })
// export class DefinitionSelector {
//   @Input() defintions: SearchBase<any>[] = [];
//   form: ControlGroup;
//   payLoad = '';
//   constructor(private scs: SearchControlService) {  }
//   ngOnInit(){
//     this.form = this.scs.toControlGroup(this.searches);
//   }
//   onSubmit() {
//     this.payLoad = JSON.stringify(this.form.value);
//   }
// }
