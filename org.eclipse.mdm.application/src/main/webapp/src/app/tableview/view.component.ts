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

  public selectedView: View;
  public groupedViews: {scope: string, view: View[]}[] = [];

  @ViewChild(EditViewComponent)
  private editViewComponent: EditViewComponent;

  constructor(private viewService: ViewService) {
  }

  ngOnInit() {
    this.viewService.getViews().subscribe(views => this.setViews(views));
    this.viewService.viewsChanged$.subscribe(view => this.onViewsChanged(view));
  }

  selectView(view: View) {
    this.selectedView = view;
    this.onViewSelected.emit(view);
  }

  public editSelectedView() {
    this.editViewComponent.showDialog(this.selectedView);
  }

  public newView() {
    this.editViewComponent.showDialog(new View());
  }

  private onViewsChanged(view: View) {
    this.viewService.getViews().subscribe(prefViews => this.getGroupedView(prefViews));
    this.selectView(view);
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

  saveView() {
    this.viewService.saveView(this.selectedView);
  }
}
