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

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.owensbikes.BikeItem.Category.ACCESSORY;
import static com.example.owensbikes.BikeItem.Category.MODIFIER;

/**
 * This class sets up the RecyclerViews and adapters on the main screen of the app. It accepts a
 * list of BikeItem and converts each of them into an {@link Row} for passing to the
 * {@link BikeItemAdapter}.
 */
public class BikeItemManager {

  private static final String ITEM_STATE_KEY = "itemState";

  private final Map<String, BikeModifierView> bikeModifierButtonViewMap;
  private final Map<String, BikeItem> itemMap;
  private final ViewGroup firstModifierRow;
  private final ViewGroup secondModifierRow;
  private final TextView totalAmount;
  private final AppCompatActivity activity;
  private ItemState state;

  private AdapterController adapterController;

  BikeItemManager(AppCompatActivity activity, AdapterController adapterController) {
    this.activity = activity;
    this.adapterController = adapterController;
    totalAmount = (TextView) activity.findViewById(R.id.total_amount);
    itemMap = new HashMap<>();
    bikeModifierButtonViewMap = new HashMap<>();
    firstModifierRow = (ViewGroup) activity.findViewById(R.id.first_modifier_row);
    secondModifierRow = (ViewGroup) activity.findViewById(R.id.second_modifier_row);
  }

  public void loadState(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      state = (ItemState) savedInstanceState.getSerializable(ITEM_STATE_KEY);
    } else {
      state = new ItemState();
    }
  }

  public void saveState(Bundle outState) {
    ItemState state = adapterController.getState();
    outState.putSerializable(ITEM_STATE_KEY, state);
  }

  public void clearStateAndReloadItems() {
    this.state = new ItemState();
    loadItems(new ArrayList<>(itemMap.values()));
  }

  public void loadItems(List<BikeItem> items) {
    state.setDefaultOptionsIfNoneSelected(items);
    List<BikeItem> loadedModifiers = new ArrayList<>();
    List<BikeItem> loadedAccessories = new ArrayList<>();
    for (BikeItem item : items) {
      if (item.category == MODIFIER) {
        loadedModifiers.add(item);
      } else if (item.category == ACCESSORY) {
        loadedAccessories.add(item);
      }
      itemMap.put(item.id, item);
    }
    adapterController.setupAdapters(state, loadedAccessories);
    adapterController.setListener(new ItemUpdateListener());
    loadModifiersIntoViews(loadedModifiers);
    loadAccessoriesIntoViews();
  }

  public int getTotal() {
    return adapterController.getTotal();
  }

  public String getNote() {
    return adapterController.getNote();
  }

  private void loadModifiersIntoViews(List<BikeItem> loadedModifiers) {
    List<Row> bikeModifierRows = new ArrayList<>();
    bikeModifierRows.add(new Row(Row.HEADER_TYPE, activity.getString(R.string.bike)));

    for (int i = 0; i < loadedModifiers.size(); i++) {
      final BikeItem modifier = loadedModifiers.get(i);
      bikeModifierRows.add(new Row(Row.ITEM_TYPE, modifier));

      // Attach first five modifiers to the buttons on top of the image if not on mobile.
      if (activity.getResources().getBoolean(R.bool.isTablet) && i < 5) {
        BikeModifierView modifierView;
        ViewGroup modifierContainer = (i < 3) ? firstModifierRow : secondModifierRow;
        int modifierIndex = i % 3;
        modifierView = (BikeModifierView) modifierContainer.getChildAt(modifierIndex);
        modifierView.setModifier(modifier, state.getSelectedOption(modifier));
        bikeModifierButtonViewMap.put(modifier.id, modifierView);
      }
    }
    BikeItemAdapter modifierAdapter = adapterController.getModifierAdapter();
    modifierAdapter.addRows(bikeModifierRows);
    modifierAdapter.notifyDataSetChanged();
  }

  private void loadAccessoriesIntoViews() {
    List<Row> accessoryRows = new ArrayList<>();
    accessoryRows.add(new Row(Row.HEADER_TYPE, activity.getString(R.string.accessories)));

    // Add rows for any accessories the user has already added.
    for (String accessoryId : state.selectedAccessoryIds) {
      accessoryRows.add(new Row(Row.ITEM_TYPE, itemMap.get(accessoryId)));
    }
    // The add accessory row should always be at the bottom.
    accessoryRows.add(new Row(Row.ADD_ACCESSORY_TYPE, null));

    BikeItemAdapter adapter = adapterController.getAccessoryAdapter();
    adapter.addRows(accessoryRows);
    adapter.notifyDataSetChanged();
    totalAmount.setText(BikeItemAdapter.formatPrice(getTotal()));
  }

  /**
   * Class for the state that is saved when the activity is recreated. It consists of the list of
   * accessories that the user has added and the selected options for all the BikeItems on the
   * screen.
   */
  static class ItemState implements Serializable {
    final List<String> selectedAccessoryIds = new ArrayList<>();
    final Map<String, Option> accessorySelectedOptionsMap = new HashMap<>();
    final Map<String, Option> modifierSelectedOptionsMap = new HashMap<>();

    void setDefaultOptionsIfNoneSelected(List<BikeItem> items) {
      if (accessorySelectedOptionsMap.isEmpty() && modifierSelectedOptionsMap.isEmpty()) {
        for (BikeItem item : items) {
          if (item.category == ACCESSORY) {
            // default option is the first option in the options list.
            accessorySelectedOptionsMap.put(item.id, item.options.get(0));
          } else if (item.category == MODIFIER) {
            modifierSelectedOptionsMap.put(item.id, item.options.get(0));
          }
        }
      }
    }

    Option getSelectedOption(BikeItem item) {
      if (item.category == ACCESSORY) {
        return accessorySelectedOptionsMap.get(item.id);
      } else {
        return modifierSelectedOptionsMap.get(item.id);
      }
    }

    void setSelectedOption(BikeItem item, Option option) {
      if (item.category == ACCESSORY) {
        accessorySelectedOptionsMap.put(item.id, option);
      } else {
        modifierSelectedOptionsMap.put(item.id, option);
      }
    }
  }

  private class ItemUpdateListener implements BikeItemAdapter.UpdateListener {

    @Override public void onTotalUpdated() {
      totalAmount.setText(BikeItemAdapter.formatPrice(getTotal()));
    }

    @Override public void onModifierUpdated(BikeItem m, Option selectedOption) {
      // Update the modifier views on top of the bike image.
      BikeModifierView modifierView = bikeModifierButtonViewMap.get(m.id);
      if (modifierView != null) {
        modifierView.updateSelectedOption(m, selectedOption);
      }
    }
  }
}
