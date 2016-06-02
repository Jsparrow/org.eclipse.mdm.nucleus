import {SearchBase} from './search-base';

export class TextboxSearch extends SearchBase<string>{
  controlType = 'textbox';
  type:string;

  constructor(options:{} = {}){
    super(options);
    this.type = options['type'] || '';
  }
}
