/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import { Component, OnInit, ViewChild, Input, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PreferenceService, Preference, Scope } from '../core/preference.service';
import { EditPreferenceComponent } from './edit-preference.component';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component( {
    selector: 'mdm-preference',
    templateUrl: './preference.component.html',
    styleUrls: ['./preference.component.css']
})
export class PreferenceComponent implements OnInit, OnDestroy {

  readonly LblAddPreference = 'Einstellung hinzufügen';
  readonly LblKey = 'Schlüssel';
  readonly LblValue = 'Wert';
  readonly TtlEdit = 'Bearbeiten';
  readonly TtlDelete = 'Entfernen';

  @Input() preferences: Preference[];
  scope: string;
  subscription: any;
  sub: any;

  @ViewChild( EditPreferenceComponent )
  editPreferenceComponent: EditPreferenceComponent;

  constructor( private formBuilder: FormBuilder,
               private preferenceService: PreferenceService,
               private route: ActivatedRoute,
               private notificationService: MDMNotificationService) { }

  ngOnInit() {
      this.sub = this.route.params.subscribe(
        params => this.onScopeChange(params),
        error => this.notificationService.notifyError('Geltungsbereich kann nicht geladen werden.', error)
      );
  }

  ngOnDestroy() {
      this.sub.unsubscribe();
  }

  onScopeChange(params: any) {
      this.scope = params['scope'].toUpperCase();
      this.preferenceService.getPreferenceForScope(this.scope)
        .subscribe(pref => this.preferences = pref);
  }

  editPreference( preference?: Preference ) {
      this.subscription = this.editPreferenceComponent.childModal.onHide.subscribe(() => this.handleDialogResult() );
      this.editPreferenceComponent.showDialog( preference );
  }

  removePreference( preference: Preference ) {
      if (preference.id) {
        this.preferenceService.deletePreference( preference.id )
        .subscribe();
      }
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
          .subscribe(
            () => index !== -1 ? this.preferences[index] = preference : this.reloadPreference( preference ),
            error => this.notificationService.notifyError('Einstellung kann nicht gespeichert werden.', error)
          );
      this.subscription.unsubscribe();
  }

  reloadPreference( preference: Preference ) {
      this.preferenceService.getPreferenceForScope( preference.scope, preference.key )
          .subscribe(
            p => this.preferences.push(p[0]),
            error => this.notificationService.notifyError('Einstellung kann nicht aktualisiert werden.', error)
          );
  }

  preferenceEqualsWithoutId( pref1: Preference, pref2: Preference ) {
      return pref1.key === pref2.key && pref1.source === pref2.source && pref1.user === pref2.user;
  }
}
