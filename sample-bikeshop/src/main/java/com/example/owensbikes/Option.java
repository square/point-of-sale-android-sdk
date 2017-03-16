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

import android.support.annotation.Nullable;
import java.io.Serializable;

/**
 * Represents an option for a BikeItem. In Point of Sale, this would
 * correspond to a variation of an item or an option for a modifier.
 */
class Option implements Serializable {
  public String name;
  private @Nullable Price price_money;

  Option(String name, Price priceMoney) {
    this.name = name;
    this.price_money = priceMoney;
  }

  public int getPrice() {
    // By default new variations have a null price.
    if (price_money == null) {
      return 0;
    }
    return price_money.amount;
  }
}
