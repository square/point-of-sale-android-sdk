package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Represents the non-confidential details of a card.
 */
@AutoValue //
public abstract class Card implements Parcelable {
  public static Card create(Brand cardBrand, String last4) {
    return new AutoValue_Card(cardBrand, last4);
  }

  /**
   * The brand (for example, VISA) of this card.
   */
  public abstract Brand cardBrand();

  /**
   * The last 4 digits of this card's number.
   */
  public abstract String last4();

  public enum Brand {
    VISA, MASTERCARD, AMERICAN_EXPRESS, DISCOVER, DISCOVER_DINERS, JCB, CHINA_UNIONPAY, //
    SQUARE_GIFT_CARD, OTHER_BRAND
  }

  public static TypeAdapter<Card> typeAdapter(Gson gson) {
    return new AutoValue_Card.GsonTypeAdapter(gson);
  }
}
