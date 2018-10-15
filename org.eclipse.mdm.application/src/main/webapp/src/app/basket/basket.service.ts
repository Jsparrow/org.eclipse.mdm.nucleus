/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


import {Injectable, EventEmitter} from '@angular/core';

import {Type, Exclude, plainToClass, serialize, deserialize} from 'class-transformer';

import {MDMItem} from '../core/mdm-item';
import {PreferenceService, Preference, Scope} from '../core/preference.service';

import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { HttpErrorHandler } from '../core/http-error-handler';
import { PropertyService } from '../core/property.service';
import {Observable} from 'rxjs/Observable';

export class Basket {
  name: string;
  @Type(() => MDMItem)
  items: MDMItem[] = [];

  constructor(name: string, items: MDMItem[]) {
    this.name = name;
    this.items = items;
  }
}

@Injectable()
export class BasketService {

  public itemsAdded$ = new EventEmitter<MDMItem[]>();
  public itemsRemoved$ = new EventEmitter<MDMItem[]>();
  readonly preferencePrefix = 'basket.nodes.';
  readonly preferenceFileextension = 'shoppingbasket.fileextension';
  items: MDMItem[] = [];

  constructor(private _pref: PreferenceService,
              private http: Http,
              private httpErrorHandler: HttpErrorHandler,
              private _prop: PropertyService) {
  }

  public add(item: MDMItem) {
    let existingItem = this.items.find(i => i.equals(item));

    if (!existingItem) {
      this.items.push(item);
      this.itemsAdded$.emit([item]);
    }
  }

  public addAll(items: MDMItem[]) {
    let newItemsWithoutExisting = items.filter(newItem => this.items.findIndex(existingItem => existingItem.equals(newItem)) < 0);

    if (newItemsWithoutExisting) {
      newItemsWithoutExisting.forEach(item => this.items.push(item));
      this.itemsAdded$.emit(newItemsWithoutExisting);
    }
  }

  public remove(item: MDMItem) {
    let itemsToRemove = this.items.filter(i => i.equals(item));

    if (itemsToRemove.length >= 0) {
      itemsToRemove.forEach(i => this.items = this.items.filter(it => !it.equals(i)));
      this.itemsRemoved$.emit(itemsToRemove);
    }
  }

  removeAll() {
    this.items = [];
  }

  saveBasketWithName(name: string) {
    return this.saveBasket(new Basket(name, this.items));
  }

  saveBasket(basket: Basket) {
    return this._pref.savePreference(this.basketToPreference(basket)).subscribe();
  }

  getBaskets() {
    return this._pref.getPreference(this.preferencePrefix)
      .map(preferences => preferences.map(p => this.preferenceToBasket(p)));
  }

  getItems() {
    return this.items;
  }

  setItems(items: MDMItem[]) {
    this.items = items;
  }

  getBasketAsXml(basket: Basket) {
    return this.http.post(this._prop.getUrl('/mdm/shoppingbasket'), basket)
      .map(r => r.text());
  }

  getFileExtension() {
    return this._pref.getPreferenceForScope(Scope.SYSTEM, this.preferenceFileextension)
      .flatMap(prefs => prefs)
      .map(pref => JSON.parse(pref.value).default + '')
      .catch(e => {
        console.log("Unable to parse value of preference '" + this.preferenceFileextension + "'!");
        return Observable.of("xml");
      })
      .defaultIfEmpty("xml");
  }

  private preferenceToBasket(pref: Preference) {
    return deserialize(Basket, pref.value);
  }

  private basketToPreference(basket: Basket) {
    const pref = new Preference();
    pref.value = serialize(basket);
    pref.key = this.preferencePrefix + basket.name;
    pref.scope = Scope.USER;
    return pref;
  }
}
