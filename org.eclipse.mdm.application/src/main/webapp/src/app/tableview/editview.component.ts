import {Component, Input, ViewChild} from '@angular/core';

import {View, ViewColumn, ViewService} from './tableview.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {SearchService, SearchAttribute} from '../search/search.service';
import {SearchattributeTreeService} from '../searchattribute-tree/searchattribute-tree.service';
import {SearchattributeTreeComponent} from '../searchattribute-tree/searchattribute-tree.component';
import {MDMNotificationService} from '../core/mdm-notification.service';

import {TreeNode} from 'primeng/primeng';
import {ModalDirective} from 'ng2-bootstrap';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {Observable} from 'rxjs/Observable';
import {classToClass} from 'class-transformer';

@Component({
  selector: 'edit-view',
  templateUrl: './editview.component.html',
  styles: ['.remove {color:black; cursor: pointer; float: right}', '.icon { cursor: pointer; margin: 0px 5px; }']
})
export class EditViewComponent {
  @ViewChild('lgModal') public childModal: ModalDirective;

  envs: Node[] = [];
  isReadOnly = false;
  currentView: View = new View();
  searchableFields = [];
  selectedAttribute: SearchAttribute;

  constructor(private nodeService: NodeService,
    private viewService: ViewService,
    private searchService: SearchService,
    private treeService: SearchattributeTreeService,
    private notificationService: MDMNotificationService) {

    this.treeService.onNodeSelect$.subscribe(node => this.selectNode(node));

    this.nodeService.getNodes().subscribe(
      envs => {
        this.envs = envs;
        let types = ['tests', 'teststeps', 'measurements'];
        let mapedEnvs = this.envs.map(env => env.sourceName);
        let crossProd: {envName: string, type: string}[] = [];

        for (let iii = 0; iii < this.envs.length; ++iii) {
          for (let jjj = 0; jjj < types.length; ++jjj) {
            crossProd.push({envName: mapedEnvs[iii], type: types[jjj]});
          }
        }

        Observable.forkJoin(crossProd.map(kr => this.searchService.loadSearchAttributes(kr.type, kr.envName)))
            .map(attrs => <SearchAttribute[]>[].concat.apply([], attrs))
            .map(attrs => attrs.map(sa => { return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa }; }))
            .map(sa => this.uniqueBy(sa, p => p.label))
            .subscribe(attrs => this.searchableFields = this.searchableFields.concat(attrs));
      }
    );
  }

  showDialog(currentView: View) {
    this.currentView = classToClass(currentView);
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
    this.pushViewCol(new ViewColumn(node.parent.label, node.label));
  }

  pushViewCol(viewCol: ViewColumn) {
    if (viewCol && this.currentView.columns.findIndex(c => viewCol.equals(c)) === -1 ) {
      this.currentView.columns.push(viewCol);
    } else {
      this.notificationService.notifyInfo('Das Attribut wurde bereits ausgewählt!', 'Info');
    }
  }

  isAsc(col: ViewColumn) {
    return col.sortOrder === 1;
  }
  isDesc(col: ViewColumn) {
    return col.sortOrder === -1;
  }
  isNone(col: ViewColumn) {
    return col.sortOrder === null;
  }

  toggleSort(col: ViewColumn) {
    if (col.sortOrder === null) {
      col.sortOrder = 1;
    } else if (col.sortOrder === 1) {
      col.sortOrder = -1;
    } else if (col.sortOrder === -1) {
      col.sortOrder = null;
    }
  }

  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.pushViewCol(new ViewColumn(match.item.attribute.boType, match.item.attribute.attrName));
    this.selectedAttribute = undefined;
  }

  private isNameReadOnly() {
    return this.isReadOnly = (this.currentView.name === '') ? false : true;
  }
}
