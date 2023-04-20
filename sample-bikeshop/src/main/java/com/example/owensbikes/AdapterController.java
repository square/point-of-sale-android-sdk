/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.owensbikes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * The adapter controller manages either one or two RecyclerAdapters depending on the device
 * orientation and type. In some cases there is a single adapter containing both modifiers
 * and accessories. Otherwise there are separate views and adapters for the categories.
 */
public interface AdapterController {

  void setupAdapters(BikeItemManager.ItemState state, List<BikeItem> accessories);

  BikeItemAdapter getAccessoryAdapter();

  BikeItemAdapter getModifierAdapter();

  int getTotal();

  String getNote();

  BikeItemManager.ItemState getState();

  void setListener(BikeItemAdapter.UpdateListener listener);

  class SingleList implements AdapterController {

    private BikeItemAdapter bikeItemAdapter;
    private AppCompatActivity activity;

    SingleList(AppCompatActivity activity) {
      this.activity = activity;
    }

    @Override
    public void setupAdapters(BikeItemManager.ItemState state, List<BikeItem> accessories) {
      RecyclerView bikeItemList = (RecyclerView) activity.findViewById(R.id.bike_item_list);
      bikeItemAdapter = BikeItemAdapter.createWithAccessories(state, accessories);
      bikeItemList.setAdapter(bikeItemAdapter);
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(bikeItemList.getContext(), LinearLayoutManager.VERTICAL, false);
      bikeItemList.setLayoutManager(layoutManager);
      bikeItemList.setVerticalFadingEdgeEnabled(true);
    }

    @Override public BikeItemAdapter getAccessoryAdapter() {
      return bikeItemAdapter;
    }

    @Override public BikeItemAdapter getModifierAdapter() {
      return bikeItemAdapter;
    }

    @Override public int getTotal() {
      return bikeItemAdapter.getTotal();
    }

    @Override public String getNote() {
      return bikeItemAdapter.getNote();
    }

    @Override public BikeItemManager.ItemState getState() {
      return bikeItemAdapter.getState();
    }

    @Override public void setListener(BikeItemAdapter.UpdateListener listener) {
      bikeItemAdapter.setUpdateListener(listener);
    }
  }

  class TwoLists implements AdapterController {

    private BikeItemAdapter modifierAdapter;
    private BikeItemAdapter accessoryAdapter;
    private AppCompatActivity activity;

    TwoLists(AppCompatActivity activity) {
      this.activity = activity;
    }

    @Override
    public void setupAdapters(BikeItemManager.ItemState state, List<BikeItem> accessories) {
      RecyclerView accessoryList = (RecyclerView) activity.findViewById(R.id.accessory_list);
      RecyclerView modifierList = (RecyclerView) activity.findViewById(R.id.modifier_list);

      modifierAdapter = BikeItemAdapter.create(state);
      modifierList.setAdapter(modifierAdapter);
      modifierList.setLayoutManager(
          new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
      modifierList.setVerticalFadingEdgeEnabled(true);

      accessoryAdapter = BikeItemAdapter.createWithAccessories(state, accessories);
      accessoryList.setAdapter(accessoryAdapter);
      accessoryList.setLayoutManager(
          new LinearLayoutManager(modifierList.getContext(), LinearLayoutManager.VERTICAL, false));
      accessoryList.setVerticalFadingEdgeEnabled(true);
    }

    @Override public BikeItemAdapter getAccessoryAdapter() {
      return accessoryAdapter;
    }

    @Override public BikeItemAdapter getModifierAdapter() {
      return modifierAdapter;
    }

    @Override public int getTotal() {
      return modifierAdapter.getTotal() + accessoryAdapter.getTotal();
    }

    @Override public String getNote() {
      return modifierAdapter.getNote() + accessoryAdapter.getNote();
    }

    @Override public BikeItemManager.ItemState getState() {
      BikeItemManager.ItemState modifierState = modifierAdapter.getState();
      BikeItemManager.ItemState accessoryState = accessoryAdapter.getState();
      BikeItemManager.ItemState itemState = new BikeItemManager.ItemState();

      // Take the selected accessories from the accessory adapter only.
      itemState.selectedAccessoryIds.addAll(accessoryState.selectedAccessoryIds);
      // Take the modifier entries from the modifier adapter, and the accessory entries from the
      // accessory adapter and combine them.
      itemState.modifierSelectedOptionsMap.putAll(modifierState.modifierSelectedOptionsMap);
      itemState.accessorySelectedOptionsMap.putAll(accessoryState.accessorySelectedOptionsMap);
      return itemState;
    }

    @Override public void setListener(BikeItemAdapter.UpdateListener listener) {
      accessoryAdapter.setUpdateListener(listener);
      modifierAdapter.setUpdateListener(listener);
    }
  }
}
