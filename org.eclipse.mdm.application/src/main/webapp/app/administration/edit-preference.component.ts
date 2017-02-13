import { Component, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, FormArray, Validators } from '@angular/forms';

import { ModalDirective } from 'ng2-bootstrap';

import { PreferenceService, Preference } from '../core/preference.service';
import { NodeService } from '../navigator/node.service';
import { Node } from '../navigator/node';

@Component( {
    selector: 'edit-preference',
    templateUrl: './edit-preference.component.html',
    styleUrls: ['./edit-preference.component.css']
})
export class EditPreferenceComponent {

    @Input() scope: string;
    showSource: boolean;
    showUser: boolean;
    isKeyEmpty: boolean;
    isScopeEmpty: boolean;
    isUserEmpty: boolean;
    preferenceForm: FormGroup;
    needSave = false;
    envs: Node[];
    errorMessage = 'Could not load environments.';

    @ViewChild( 'lgModal' ) public childModal: ModalDirective;

    constructor( private formBuilder: FormBuilder,
                 private nodeService: NodeService ) { }

    ngOnInit() {
        let node: Node;
        this.nodeService.getNodes(node).subscribe(
                env => this.envs = env,
                error => this.errorMessage = <any>error
                );
        this.setupForm( new Preference() );
    }

    setupForm( preference: Preference ) {
        this.setOptions(preference);
        this.preferenceForm = this.formBuilder
            .group( {
                scope: [preference.scope],
                source: [preference.source],
                user: [preference.user],
                key: [preference.key, Validators.required],
                value: [preference.value, Validators.required],
                id: [preference.id]
            });
    }

    setOptions(preference: Preference) {
        this.needSave = false;
        this.isKeyEmpty = preference.key === '';
        this.isScopeEmpty = preference.scope === '';
        switch ( this.scope.toLowerCase() ) {
        case 'system':
            this.showSource = false;
            this.showUser = false;
            break;
        case 'source':
            this.showSource = true;
            this.showUser = false;
            break;
        case 'user':
            this.showSource = false;
            this.showUser = true;
            break;
        }
    }

    showDialog( preference?: Preference) {
        if (preference == null) {
            preference = new Preference();
            preference.scope = this.scope.charAt(0).toUpperCase() + this.scope.slice(1);
            if (this.scope.toLowerCase() === 'source') {
                preference.source = this.envs[0].sourceName;
            }
        }
        this.setupForm( preference);
        this.childModal.show();
    }

    onSave() {
        this.needSave = true;
        this.childModal.hide();
    }

    closeDialog() {
        this.childModal.hide();
    }
}
