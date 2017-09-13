package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Represents an order associated with a completed transaction.
 */
@AutoValue //
public abstract class Order implements Parcelable {
  /**
   * The total amount collected for this order.
   *
   * This is the sum of the totalMoney amounts for all tenders in the transaction.
   */
  public abstract Money totalMoney();

  /**
   * The total tip amount applied to this order from each {@link Tender} and included as a part of
   * {@link #totalMoney}.
   */
  public abstract Money totalTipMoney();

  /**
   * The total taxes applied to this order and included as a part of {@link #totalMoney}.
   */
  public abstract Money totalTaxMoney();

  public static Order.Builder builder() {
    return new AutoValue_Order.Builder();
  }

  @AutoValue.Builder //
  public abstract static class Builder {
    public abstract Builder totalMoney(Money totalMoney);

    public abstract Builder totalTipMoney(Money totalTipMoney);

    public abstract Builder totalTaxMoney(Money totalTaxMoney);

    public abstract Order build();
  }

  public static TypeAdapter<Order> typeAdapter(Gson gson) {
    return new AutoValue_Order.GsonTypeAdapter(gson);
  }
}
