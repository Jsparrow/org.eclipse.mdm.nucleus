// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Injectable, EventEmitter} from '@angular/core';

import {Type, Exclude, plainToClass, serialize, deserialize} from 'class-transformer';

import {MDMItem} from '../core/mdm-item';
import {PreferenceService, Preference} from '../core/preference.service';

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

  items: MDMItem[] = [];

  constructor(private _pref: PreferenceService) {
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
    return this._pref.getPreferenceForScope('', 'basket.nodes.')
      .map(preferences => preferences.map(p => this.preferenceToBasket(p)));
  }

  getItems() {
    return this.items;
  }

  setItems(items: MDMItem[]) {
    this.items = items;
  }

  private preferenceToBasket(pref: Preference) {
    return deserialize(Basket, pref.value);
  }

  private basketToPreference(basket: Basket) {
    const pref = new Preference();
    pref.value = serialize(basket);
    pref.key = this.preferencePrefix + basket.name;
    pref.scope = 'User';
    return pref;
  }
}
