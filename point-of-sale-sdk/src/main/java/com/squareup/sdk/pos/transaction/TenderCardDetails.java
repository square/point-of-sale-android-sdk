package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Represents additional details of a card payment.
 */
@AutoValue //
public abstract class TenderCardDetails implements Parcelable {
  /**
   * Details about the card used in this tender, such as the brand and last four digits of
   * the card number.
   */
  public abstract Card card();

  /**
   * The method used to provide this card's details.
   */
  public abstract EntryMethod entryMethod();

  public static Builder builder() {
    return new AutoValue_TenderCardDetails.Builder();
  }

  public enum EntryMethod {
    /** The entry method is unknown. **/
    UNKNOWN,

    /** The card information was keyed-in manually. **/
    KEYED,

    /** The card was swiped through a Square reader or Square stand. **/
    SWIPED,

    /** The card was processed via EMV with a Square reader. **/
    EMV,

    /** The buyer's card details were already on file with Square. **/
    ON_FILE,

    /** The card was processed via a contactless (i.e., NFC) transaction with a Square reader. **/
    CONTACTLESS
  }

  @AutoValue.Builder //
  public abstract static class Builder {

    public abstract Builder card(Card card);

    public abstract Builder entryMethod(EntryMethod entryMethod);

    public abstract TenderCardDetails build();
  }

  public static TypeAdapter<TenderCardDetails> typeAdapter(Gson gson) {
    return new AutoValue_TenderCardDetails.GsonTypeAdapter(gson);
  }
}
