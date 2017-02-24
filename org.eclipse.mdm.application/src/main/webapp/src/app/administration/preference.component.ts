import { Component, OnInit, ViewChild, Input, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PreferenceService, Preference } from '../core/preference.service';
import { EditPreferenceComponent } from './edit-preference.component';


@Component( {
    selector: 'mdm-preference',
    templateUrl: './preference.component.html',
    styleUrls: ['./preference.component.css']
})
export class PreferenceComponent implements OnInit, OnDestroy {

  brand = 'Scope';
  @Input() preferences: Preference[];
  scope: string;
  subscription: any;
  sub: any;

  @ViewChild( EditPreferenceComponent )
  private editPreferenceComponent: EditPreferenceComponent;

  constructor( private formBuilder: FormBuilder,
               private preferenceService: PreferenceService,
               private route: ActivatedRoute) { }

  ngOnInit() {
      this.sub = this.route.params.subscribe( params => this.onScopeChange(params) );
  }

  ngOnDestroy() {
      this.sub.unsubscribe();
  }

  onScopeChange(params: any) {
      this.scope = params['scope'];
      this.preferenceService.getPreference(this.scope)
        .subscribe(pref => this.preferences = pref);
  }

  editPreference( preference?: Preference ) {
      this.subscription = this.editPreferenceComponent.childModal.onHide.subscribe(() => this.handleDialogResult() );
      this.editPreferenceComponent.showDialog( preference );
  }

  removePreference( preference: Preference ) {
      this.preferenceService.deletePreference( preference.id );
      let index = this.preferences.findIndex( p => p === preference );
      this.preferences.splice( index, 1 );
  }

  handleDialogResult() {
      if ( !this.editPreferenceComponent.needSave ) {
          return;
      }

      let preference = this.editPreferenceComponent.preferenceForm.value;
      let index = this.preferences.findIndex( p => this.preferenceEqualsWithoutId( p, preference ) );

      this.preferenceService.savePreference( preference )
          .subscribe(() => index !== -1 ? this.preferences[index] = preference : this.reloadPreference( preference ));
      this.subscription.unsubscribe();
  }

  reloadPreference( preference: Preference ) {
      this.preferenceService.getPreference( preference.scope, preference.key )
          .subscribe(p => this.preferences.push(p[0]));
  }

  preferenceEqualsWithoutId( pref1: Preference, pref2: Preference ) {
      return pref1.key === pref2.key && pref1.source === pref2.source && pref1.user === pref2.user;
  }
}
