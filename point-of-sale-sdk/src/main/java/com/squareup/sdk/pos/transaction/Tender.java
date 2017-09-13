package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.ryanharter.auto.value.parcel.ParcelAdapter;

/**
 * Represents a payment method used in a transaction.
 */
@AutoValue //
public abstract class Tender implements Parcelable {
  /**
   * The device-generated ID of the tender.
   */
  public abstract String clientId();

  /**
   * The server-generated ID of the tender.
   *
   * This ID can be used to perform tender level actions, such as refunds, via the Square Connect v2
   * API at a later time.
   *
   * Currently, serverId is the mechanism by which developers can query Connect API's endpoints.
   * Eventually, these endpoints shall be queryable by clientId so we mark this field as
   * deprecated.
   *
   * In addition, cash and offline payments will not return a serverId so we mark this nullable.
   */
  @Deprecated @Nullable public abstract String serverId();

  /**
   * The date and time when the tender was created, as determined by the client device.
   */
  @ParcelAdapter(DateTime.DateTimeTypeAdapter.class) //
  public abstract DateTime createdAt();

  /**
   * The total amount of this tender.
   */
  public abstract Money totalMoney();

  /**
   * The tip amount added to this tender and included as a part of {@link #totalMoney}.
   */
  public abstract Money tipMoney();

  /**
   * If the tender is associated with a customer or represents a customer's card on file, this is
   * the ID of the associated customer.
   */
  @Nullable public abstract String customerId();

  /**
   * The type of tender as specified in {@link Type}.
   */
  public abstract Type type();

  /**
   * Details related to a card payment.
   *
   * This value is present only if {@link #type} is {@link Type#CARD} or {@link
   * Type#SQUARE_GIFT_CARD}.
   */
  @Nullable public abstract TenderCardDetails cardDetails();

  /**
   * Details related to a cash payment.
   *
   * This value is present only if {@link #type} is {@link Type#CASH}.
   */
  @Nullable public abstract TenderCashDetails cashDetails();

  public static Tender.Builder builder() {
    return new AutoValue_Tender.Builder();
  }

  public enum Type {
    CARD, CASH, SQUARE_GIFT_CARD, OTHER
  }

  @AutoValue.Builder //
  public abstract static class Builder {
    public abstract Builder clientId(String clientId);

    public abstract Builder serverId(String serverId);

    public abstract Builder createdAt(DateTime createdAt);

    public abstract Builder totalMoney(Money totalMoney);

    public abstract Builder tipMoney(Money tipMoney);

    public abstract Builder customerId(String customerId);

    public abstract Builder type(Type type);

    public abstract Builder cardDetails(TenderCardDetails cardDetails);

    public abstract Builder cashDetails(TenderCashDetails cashDetails);

    public abstract Tender build();
  }

  public static TypeAdapter<Tender> typeAdapter(Gson gson) {
    return new AutoValue_Tender.GsonTypeAdapter(gson);
  }
}
