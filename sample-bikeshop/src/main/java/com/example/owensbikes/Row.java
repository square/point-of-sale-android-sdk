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
