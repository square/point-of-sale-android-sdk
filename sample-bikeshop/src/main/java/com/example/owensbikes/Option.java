package com.example.owensbikes;

import android.support.annotation.Nullable;
import java.io.Serializable;

/**
 * Represents an option for a BikeItem. In Register, this would correspond to a variation of an
 * item or an option for a modifier.
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
