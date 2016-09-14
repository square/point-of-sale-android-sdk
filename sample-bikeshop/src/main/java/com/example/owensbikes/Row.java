package com.example.owensbikes;

/**
 * Container for a row in the {@link BikeItemAdapter}. Each row is either a header row for the
 * modifier or accessory category, a BikeItem, or the "add accessory" row.
 */
public class Row {

  public static final int HEADER_TYPE = 1;
  public static final int ITEM_TYPE = 2;
  public static final int ADD_ACCESSORY_TYPE = 3;

  private int rowType;
  // This will contain either the BikeItem to be displayed or the String for the header row.
  // In the add accessory case, data is null.
  private Object data;

  public Row(int rowType, Object data) {
    this.rowType = rowType;
    this.data = data;
  }

  public Object getData() {
    return data;
  }

  public int getType() {
    return rowType;
  }
}
