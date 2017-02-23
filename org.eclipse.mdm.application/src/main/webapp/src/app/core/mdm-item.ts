import {Node} from '../navigator/node';

export class MDMItem {
  source: string;
  type: string;
  id: number;

  constructor(source: string, type: string, id: number) {
    this.source = source;
    this.type = type;
    this.id = id;
  }

  equalsNode(node: Node) {
    return this.source === node.sourceName && this.type === node.type && this.id === node.id;
  }

  equals(item: MDMItem) {
    return this.source === item.source && this.type === item.type && this.id === item.id;
  }
}
