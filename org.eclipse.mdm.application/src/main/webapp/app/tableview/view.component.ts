import { Component, ViewChild, OnInit, Output, EventEmitter} from '@angular/core';

import { PreferenceView, View, ViewService } from './tableview.service';

import { EditViewComponent } from './editview.component';

@Component({
  selector: 'mdm-view',
  templateUrl: 'view.component.html'
})
export class ViewComponent implements OnInit {
  @Output()
  public onViewSelected = new EventEmitter<View>();

  private selectedView: View;
  private groupedViews: {scope: string, view: View[]}[] = [];

  @ViewChild(EditViewComponent)
  private editViewComponent: EditViewComponent;

  constructor(private viewService: ViewService) {
  }

  ngOnInit() {
    this.viewService.getViews().then(views => this.setViews(views));
    this.viewService.viewsChanged$.subscribe(view => this.onViewsChanged(view));
  }

  selectView(view: View) {
    this.selectedView = view;
    this.onViewSelected.emit(view);
  }

  private onViewsChanged(view: View) {
    this.viewService.getViews().then(prefViews => this.getGroupedView(prefViews));
    this.selectView(view);
  }

  private editSelectedView() {
    this.editViewComponent.showDialog(this.selectedView);
  }

  private newView() {
    this.editViewComponent.showDialog(new View());
  }

  private setViews(prefViews: PreferenceView[]) {
    this.getGroupedView(prefViews);
    this.selectView(prefViews[0].view);
  }

  private getGroupedView(prefViews: PreferenceView[]) {
    this.groupedViews = [];
    for (let i = 0; i < prefViews.length; i++) {
      let pushed = false;
      for (let j = 0; j < this.groupedViews.length; j++) {
        if (prefViews[i].scope === this.groupedViews[j].scope) {
          this.groupedViews[j].view.push(prefViews[i].view);
          pushed = true;
        }
      }
      if (pushed === false) { this.groupedViews.push({scope: prefViews[i].scope, view: [prefViews[i].view]}); }
    }
  }
}
