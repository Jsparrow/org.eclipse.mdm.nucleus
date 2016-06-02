import {SearchBase} from './search-base';

export class DropdownSearch extends SearchBase<string>{
  controlType = 'dropdown';
  options:{key:string, value:string}[] = [];

  constructor(options:{} = {}){
    super(options);
    this.options = options['options'] || [];
  }
}
