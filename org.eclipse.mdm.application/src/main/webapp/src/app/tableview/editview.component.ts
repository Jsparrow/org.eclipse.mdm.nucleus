import {Component, Input, ViewChild} from '@angular/core';

import {View, ViewColumn, SortOrder, ViewService} from './tableview.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {SearchService, SearchAttribute} from '../search/search.service';
import {SearchattributeTreeService} from '../searchattribute-tree/searchattribute-tree.service';
import {SearchattributeTreeComponent} from '../searchattribute-tree/searchattribute-tree.component';

import {TreeNode} from 'primeng/primeng';
import {ModalDirective} from 'ng2-bootstrap';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'edit-view',
  templateUrl: './editview.component.html',
  styles: ['.remove {color:black; cursor: pointer; float: right}', '.icon { cursor: pointer; margin: 0px 5px; }']
})
export class EditViewComponent {
  @ViewChild('lgModal') public childModal: ModalDirective;

  nodes: Node[] = [];
  isReadOnly = false;
  currentView: View = new View();
  searchableFields = [];

  constructor(private nodeService: NodeService,
    private viewService: ViewService,
    private searchService: SearchService,
    private treeService: SearchattributeTreeService) {

      this.treeService.onNodeSelect$.subscribe(node => this.selectNode(node));
      this.nodeService.getNodes().subscribe(
        envs => {
          let types = ['tests', 'teststeps', 'measurements'];
          types.forEach(type =>
            Observable.forkJoin(envs.map(env => env.sourceName)
              .map(env => this.searchService.loadSearchAttributes(type, env)))
              .map(attrs => <SearchAttribute[]>[].concat.apply([], attrs))
              .map(attrs => attrs.map(sa => { return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa }; }))
              .map(sa => this.uniqueBy(sa, p => p.label))
              .subscribe(attrs => this.searchableFields = this.searchableFields.concat(attrs))
          );
        }
      );
  }

  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }

  showDialog(currentView: View) {
    currentView.columns.forEach(c => {
      if (c.sort === undefined) {
        c.sort = SortOrder.None;
      }
    });
    this.currentView = currentView;
    this.isNameReadOnly();
    this.childModal.show();
  }

  closeDialog() {
    this.childModal.hide();
  }

  save() {
      this.viewService.saveView(this.currentView);
      this.closeDialog();
  }

  remove(col: ViewColumn) {
    this.currentView.columns = this.currentView.columns.filter(c => c !== col);
  }

  isLast(col: ViewColumn) {
    return this.currentView.columns.indexOf(col) === this.currentView.columns.length - 1;
  }

  isFirst(col: ViewColumn) {
    return this.currentView.columns.indexOf(col) === 0;
  }

  isAsc(col: ViewColumn) {
    return col.sort === SortOrder.Asc;
  }
  isDesc(col: ViewColumn) {
    return col.sort === SortOrder.Desc;
  }
  isNone(col: ViewColumn) {
    return col.sort === SortOrder.None;
  }

  toggleSort(col: ViewColumn) {
    if (col.sort === SortOrder.None) {
      col.sort = SortOrder.Asc;
    } else if (col.sort === SortOrder.Asc) {
      col.sort = SortOrder.Desc;
    } else if (col.sort === SortOrder.Desc) {
      col.sort = SortOrder.None;
    }
  }

  moveUp(col: ViewColumn) {
    if (!this.isFirst(col)) {
      let oldIndex = this.currentView.columns.indexOf(col);
      let otherCol = this.currentView.columns[oldIndex - 1];
      this.currentView.columns[oldIndex] = otherCol;
      this.currentView.columns[oldIndex - 1] = col;
    }
  }

  moveDown(col: ViewColumn) {
    if (!this.isLast(col)) {
      let oldIndex = this.currentView.columns.indexOf(col);
      let otherCol = this.currentView.columns[oldIndex + 1];
      this.currentView.columns[oldIndex] = otherCol;
      this.currentView.columns[oldIndex + 1] = col;
    }
  }

  selectNode(node: TreeNode) {
    if (node.type !== 'attribute') {
      return;
    }
    let viewCol = new ViewColumn(node.parent.label, node.label, SortOrder.None);
    if (this.currentView.columns.findIndex(c => c.equals(viewCol)) === -1 ) {
      this.currentView.columns.push(viewCol);
    }
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.currentView.columns.push(new ViewColumn(match.item.attribute.boType, match.item.attribute.attrName, SortOrder.None));
  }

  private isNameReadOnly() {
    return this.isReadOnly = (this.currentView.name === '') ? false : true;
  }
}
