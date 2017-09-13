package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Represents additional details of a cash payment.
 */
@AutoValue //
public abstract class TenderCashDetails implements Parcelable {
  /**
   * The total amount of cash provided by the buyer, before change is given.
   */
  public abstract Money buyerTenderedMoney();

  /**
   * The amount of change returned to the buyer.
   */
  public abstract Money changeBackMoney();

  public static Builder builder() {
    return new AutoValue_TenderCashDetails.Builder();
  }

  @AutoValue.Builder //
  public abstract static class Builder {

    public abstract Builder buyerTenderedMoney(Money buyerTenderedMoney);

    public abstract Builder changeBackMoney(Money changeBackMoney);

    public abstract TenderCashDetails build();
  }

  public static TypeAdapter<TenderCashDetails> typeAdapter(Gson gson) {
    return new AutoValue_TenderCashDetails.GsonTypeAdapter(gson);
  }
}
