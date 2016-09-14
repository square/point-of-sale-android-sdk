package com.example.owensbikes;

import java.io.Serializable;

/**
 * Price for a BikeItem's Option.
 */
class Price implements Serializable {
  public String currency_code;
  public int amount;

  Price(String currency_code, int amount) {
    this.currency_code = currency_code;
    this.amount = amount;
  }
}
