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
    showSource: boolean = true;
    showUser: boolean = true;
    isKeyEmpty: boolean;
    isScopeEmpty: boolean;
    isUserEmpty: boolean;
    preferenceForm: FormGroup;
    needSave: boolean = false;
    envs: Node[];
    errorMessage: string = 'Could not load environments.';

    @ViewChild( 'lgModal' ) public childModal: ModalDirective;

    constructor( private formBuilder: FormBuilder,
                 private nodeService: NodeService) { }

    ngOnInit() {
        let node: Node;
        this.nodeService.getNodes(node).subscribe(
                env => this.envs = env,
                error => this.errorMessage = <any>error
                );
        switch ( this.scope.toLowerCase() ) {
            case 'system':
                this.showSource = false;
                this.showUser = false;
                break;
            case 'source':
                this.showUser = false;
                break;
            case 'user':
                this.showSource = false;
                break;
        }
        this.setupForm( new Preference() );
    }

    setupForm( preference: Preference ) {
        this.isKeyEmpty = preference.key === '';
        this.isScopeEmpty = preference.scope === '';
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

    showDialog( preference?: Preference) {
        this.needSave = false;
        if (preference == null) {
            preference = new Preference();
            preference.scope = this.scope;
            if (this.scope === 'Source') {
                preference.source = this.envs[0].name;
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
