import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, FormArray, Validators } from '@angular/forms';

import { PreferenceService, Preference } from '../core/preference.service';
import { EditPreferenceComponent } from './edit-preference.component';


@Component( {
    selector: 'mdm-preference',
    templateUrl: './preference.component.html',
    styleUrls: ['./preference.component.css']
})
export class PreferenceComponent implements OnInit {

    brand: string = 'Scope';
    @Input() preferences: Preference[];
    @Input() scope: string;
    subscription: any;

    @ViewChild( EditPreferenceComponent )
    private editPreferenceComponent: EditPreferenceComponent;

    constructor( private formBuilder: FormBuilder,
                 private preferenceService: PreferenceService) { }

    ngOnInit() {
        this.preferenceService.getPreference( this.scope )
            .then( pref => this.preferences = pref );
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
            .then(() => index !== -1 ? this.preferences[index] = preference : this.reloadPreference( preference ));
        this.subscription.unsubscribe();
    }

    reloadPreference( preference: Preference ) {
        this.preferenceService.getPreference( preference.scope, preference.key )
            .then( p => this.preferences.push( p[0] ) );
    }

    preferenceEqualsWithoutId( pref1: Preference, pref2: Preference ) {
        return pref1.key === pref2.key && pref1.source === pref2.source && pref1.user === pref2.user;
    }
}
