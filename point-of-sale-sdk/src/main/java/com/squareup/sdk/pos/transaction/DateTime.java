package com.squareup.sdk.pos.transaction;

import android.os.Parcel;
import com.ryanharter.auto.value.parcel.TypeAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.squareup.sdk.pos.internal.PosSdkHelper.nonNull;

/** The date and time at which the transaction was completed. */
public final class DateTime {
  private static final DateFormat ISO_8601 =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

  private final String dateString;

  public DateTime(String dateString) {
    this.dateString = nonNull(dateString, "dateString");
    try {
      // Fail fast as a sanity check.
      parse();
    } catch (ParseException pe) {
      throw new IllegalArgumentException(pe);
    }
  }

  public DateTime(Date date) {
    this.dateString = ISO_8601.format(date);
  }

  public String asIso8601String() {
    return dateString;
  }

  public Date asJavaDate() {
    try {
      return parse();
    } catch (ParseException ise) {
      throw new IllegalStateException("this should never happen");
    }
  }

  // Cribbed from Times.parseIso8601Date(String).
  private Date parse() throws ParseException {
    // Add zeroed zone to UTC dates.
    String temp = dateString.replace("Z", "-0000");

    // Remove colon from time zone if present.
    int zoneColonLocation = temp.length() - 3;
    if (temp.length() > 2 && temp.charAt(zoneColonLocation) == ':') {
      temp = temp.substring(0, zoneColonLocation) + temp.substring(zoneColonLocation + 1,
          temp.length());
    }

    return ISO_8601.parse(temp);
  }

  // TODO RA-21232: make equals timezone-agnostic
  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DateTime dateTime = (DateTime) o;

    if (!dateString.equals(dateTime.dateString)) {
      return false;
    }

    return true;
  }

  @Override public int hashCode() {
    return dateString.hashCode();
  }

  @Override public String toString() {
    return dateString;
  }

  public static class DateTimeTypeAdapter implements TypeAdapter<DateTime> {
    @Override public DateTime fromParcel(Parcel in) {
      return new DateTime(in.readString());
    }

    @Override public void toParcel(DateTime value, Parcel dest) {
      dest.writeString(value.dateString);
    }
  }
}
