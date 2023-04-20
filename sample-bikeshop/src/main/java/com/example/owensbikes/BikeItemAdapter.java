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

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.owensbikes.BikeItem.Category.ACCESSORY;
import static com.example.owensbikes.BikeItem.Category.MODIFIER;

public class BikeItemAdapter extends RecyclerView.Adapter<BikeItemAdapter.ViewHolder> {

  static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");

  public static BikeItemAdapter createWithAccessories(BikeItemManager.ItemState itemState,
      List<BikeItem> accessories) {
    return new BikeItemAdapter(itemState, accessories);
  }

  public static BikeItemAdapter create(BikeItemManager.ItemState itemState) {
    return new BikeItemAdapter(itemState, null);
  }

  private final List<Row> rows;
  private final List<BikeItem> accessories;
  private final BikeItemManager.ItemState itemState;

  private UpdateListener updateListener;
  private RecyclerView recyclerView;

  private BikeItemAdapter(BikeItemManager.ItemState itemState, List<BikeItem> accessories) {
    this.itemState = itemState;
    this.rows = new ArrayList<>();
    this.accessories = accessories;
  }

  interface UpdateListener {

    void onTotalUpdated();

    void onModifierUpdated(BikeItem modifier, Option selectedOption);
  }

  public void setUpdateListener(BikeItemAdapter.UpdateListener listener) {
    this.updateListener = listener;
  }

  public void addRows(List<Row> newRows) {
    this.rows.addAll(newRows);
  }

  public int getItemCount() {
    return rows.size();
  }

  public BikeItemManager.ItemState getState() {
    return itemState;
  }

  public int getTotal() {
    int runningTotal = 0;
    for (Row row : rows) {
      if (row.getType() == Row.ITEM_TYPE) {
        runningTotal += itemState.getSelectedOption((BikeItem) row.getData()).getPrice();
      }
    }
    return runningTotal;
  }

  public String getNote() {
    StringBuilder note = new StringBuilder();
    if (rows.get(1).getType() != Row.ADD_ACCESSORY_TYPE) {
      note.append("Bike order: \n Modifiers: \n");
    }
    boolean seenAccessory = false;
    for (Row row : rows) {
      if (row.getType() == Row.ITEM_TYPE) {
        BikeItem item = (BikeItem) row.getData();
        if (item.category == ACCESSORY && !seenAccessory) {
          note.append("Accessories: \n");
          seenAccessory = true;
        }
        Option selectedOption = itemState.getSelectedOption(item);
        note.append("\t\t\t")
            .append(item.name)
            .append(": ")
            .append(selectedOption.name)
            .append("- ")
            .append(selectedOption.getPrice())
            .append("\n");
      } else if (row.getType() == Row.ADD_ACCESSORY_TYPE) {
        seenAccessory = true;
      }
    }
    // Since we haven't seen any accessories, This adapter is just for modifiers, so don't append
    // total, we should concatenate the notes later
    if (seenAccessory) {
      note.append("Total: ").append(getTotal());
    }
    return note.toString();
  }

  public static String formatPrice(int amount) {
    return "$" + PRICE_FORMAT.format(((double) amount) / 100);
  }

  @Override public int getItemViewType(int position) {
    return rows.get(position).getType();
  }

  @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    switch (viewType) {
      case Row.HEADER_TYPE:
        return new HeaderViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_header, parent, false));
      case Row.ITEM_TYPE:
        return new BikeItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
      case Row.ADD_ACCESSORY_TYPE:
        return new AddAccessoryViewHolder(
            LayoutInflater.from(context).inflate(R.layout.new_accessory_view, parent, false));
      default:
        return new AddAccessoryViewHolder(
            LayoutInflater.from(context).inflate(R.layout.new_accessory_view, parent, false));
    }
  }

  /** Holds the header row, which displays category name "BIKE" or "ACCESSORIES" **/
  private class HeaderViewHolder extends ViewHolder {
    public TextView title;

    public HeaderViewHolder(View view) {
      super(view);
      title = (TextView) view.findViewById(R.id.list_header);
    }

    @Override public void bind() {
      title.setText((String) rows.get(getAdapterPosition()).getData());
    }
  }

  /**
   * Displays a BikeItem (modifier or accessory) row. When clicked, the row displays an alert
   * dialog with a list of the item's options. When an option is selected, the row updates its text
   * and notifies the BikeItemManager via the UpdateListener.
   **/
  private class BikeItemViewHolder extends ViewHolder {
    public TextView itemTitle;
    public TextView selectedOptionView;
    public TextView itemPrice;

    public BikeItemViewHolder(View view) {
      super(view);
      itemTitle = (TextView) view.findViewById(R.id.item_name);
      selectedOptionView = (TextView) view.findViewById(R.id.item_variation);
      itemPrice = (TextView) view.findViewById(R.id.item_price);
    }

    @Override public void bind() {
      Row row = rows.get(getAdapterPosition());
      final BikeItem item = (BikeItem) row.getData();
      final Context context = itemView.getContext();
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          TextView titleView =
              (TextView) LayoutInflater.from(context).inflate(R.layout.popup_header, null);
          titleView.setText(context.getString(R.string.item_option_message, item.name));

          new AlertDialog.Builder(context, R.style.OptionsDialogTheme).setCustomTitle(titleView)
              .setAdapter(new PopupOptionsAdapter(context, item.options),
                  new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                      Option selectedOption = item.options.get(i);
                      itemState.setSelectedOption(item, selectedOption);
                      selectedOptionView.setText(selectedOption.name);
                      selectedOptionView.startAnimation(
                          AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));
                      itemPrice.setText(formatPrice(selectedOption.getPrice()));
                      updateListener.onTotalUpdated();
                      if (item.category == MODIFIER) {
                        updateListener.onModifierUpdated(item, selectedOption);
                      }
                    }
                  })
              .show();
        }
      });
      if (item.category == ACCESSORY) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override public boolean onLongClick(View view) {
            TextView titleView =
                (TextView) LayoutInflater.from(context).inflate(R.layout.popup_header, null);
            titleView.setText(context.getString(R.string.remove_accessory));
            new AlertDialog.Builder(context).setCustomTitle(titleView)
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialogInterface, int i) {
                    rows.remove(getAdapterPosition());
                    updateListener.onTotalUpdated();
                    recyclerView.scrollToPosition(getItemCount() - 1);
                    itemState.selectedAccessoryIds.remove(item.id);
                  }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
            return true;
          }
        });
      }
      itemTitle.setText(item.name.toUpperCase());
      itemPrice.setText(formatPrice(itemState.getSelectedOption(item).getPrice()));
      selectedOptionView.setText(itemState.getSelectedOption(item).name);
    }
  }

  /**
   * Displays a row for adding a new accessory. When clicked, displays an alert dialog containing
   * all the available accessory options. If one of these is selected a new row is added for that
   * accessory
   **/
  private class AddAccessoryViewHolder extends ViewHolder {

    public AddAccessoryViewHolder(View view) {
      super(view);
    }

    @Override public void bind() {
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          Context context = itemView.getContext();
          final String[] accessoryList = new String[accessories.size()];
          for (int i = 0; i < accessoryList.length; i++) {
            accessoryList[i] = accessories.get(i).name;
          }

          TextView titleView =
              (TextView) LayoutInflater.from(context).inflate(R.layout.popup_header, null);
          titleView.setText(context.getString(R.string.add_accessory_message));
          new AlertDialog.Builder(context, R.style.OptionsDialogTheme).setCustomTitle(titleView)
              .setAdapter(new ArrayAdapter<>(context, R.layout.accessory_textview, accessoryList),
                  new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                      BikeItem selectedAccessory = accessories.get(i);
                      rows.add(getAdapterPosition(), new Row(Row.ITEM_TYPE, selectedAccessory));
                      updateListener.onTotalUpdated();
                      itemState.selectedAccessoryIds.add(selectedAccessory.id);
                      notifyItemInserted(rows.size() - 1);
                      recyclerView.scrollToPosition(getItemCount() - 1);
                    }
                  })
              .show();
        }
      });
    }
  }

  public static abstract class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(View itemView) {
      super(itemView);
    }

    public abstract void bind();
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.bind();
  }

  /**
   * Adapter for displaying a BikeItem's options in an Alert Dialog when the item is clicked in the
   * RecyclerView.
   **/
  private class PopupOptionsAdapter extends ArrayAdapter<Option> {

    public PopupOptionsAdapter(Context context, List<Option> options) {
      super(context, 0, options);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      Context context = parent.getContext();
      if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.option_row, parent, false);
      }
      TextView optionName = (TextView) convertView.findViewById(R.id.option_name);
      TextView optionPrice = (TextView) convertView.findViewById(R.id.option_price);
      optionName.setText(getItem(position).name);
      optionPrice.setText(formatPrice(getItem(position).getPrice()));
      return convertView;
    }
  }
}

