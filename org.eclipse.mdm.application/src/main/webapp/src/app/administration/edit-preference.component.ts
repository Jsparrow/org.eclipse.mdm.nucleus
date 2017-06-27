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

import { Component, Input, Output, ViewChild, EventEmitter, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, FormArray, Validators } from '@angular/forms';

import { ModalDirective } from 'ng2-bootstrap';

import { PreferenceService, Preference, Scope } from '../core/preference.service';
import { NodeService } from '../navigator/node.service';
import { Node } from '../navigator/node';

import {MDMNotificationService} from '../core/mdm-notification.service';

@Component( {
    selector: 'edit-preference',
    templateUrl: './edit-preference.component.html',
    styleUrls: ['./edit-preference.component.css']
})
export class EditPreferenceComponent implements OnInit {

    readonly LblCancel = 'Abbrechen';
    readonly LblKey = 'Schlüssel';
    readonly LblPreferenceEditor = 'Einstellungseditor';
    readonly LblSave = 'Speichern';
    readonly LblScope = 'Geltungsbereich';
    readonly LblSource = 'Quelle';
    readonly LblUser = 'Benutzer';
    readonly LblValue = 'Wert';
    readonly TtlClose = 'Schließen';

    @Input() scope: string;
    showSource: boolean;
    showUser: boolean;
    isKeyEmpty: boolean;
    isUserEmpty: boolean;
    preferenceForm: FormGroup;
    needSave = false;
    envs: Node[];

    @ViewChild( 'lgModal' ) public childModal: ModalDirective;

    constructor( private formBuilder: FormBuilder,
                 private nodeService: NodeService,
                 private notificationService: MDMNotificationService ) { }

    ngOnInit() {
        let node: Node;
        this.nodeService.getNodes(node).subscribe(
                env => this.envs = env,
                error => this.notificationService.notifyError('Datenquelle kann nicht geladen werden.', error)
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
        switch ( this.scope ) {
        case Scope.SYSTEM:
            this.showSource = false;
            this.showUser = false;
            break;
        case Scope.SOURCE:
            this.showSource = true;
            this.showUser = false;
            break;
        case Scope.USER:
            this.showSource = false;
            this.showUser = true;
            break;
        }
    }

    showDialog( preference?: Preference) {
        if (preference == null) {
            preference = new Preference();
            preference.scope = this.scope;
            if (this.scope === Scope.SOURCE) {
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
