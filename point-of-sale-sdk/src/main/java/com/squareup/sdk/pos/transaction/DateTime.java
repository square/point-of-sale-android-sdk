package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@AutoValue
public abstract class DateTime implements Parcelable {
  private static final DateFormat ISO_8601 =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

  public abstract String iso8601DateString();

  public static DateTime create(String iso8601DateString) {
    try {
      // Fail fast as a sanity check.
      parse(iso8601DateString);
    } catch (ParseException pe) {
      throw new IllegalArgumentException(pe);
    }
    return new AutoValue_DateTime(iso8601DateString);
  }

  public static DateTime create(Date date) {
    return create(ISO_8601.format(date));
  }

  public Date asJavaDate() {
    try {
      return parse(iso8601DateString());
    } catch (ParseException ise) {
      throw new IllegalStateException("this should never happen");
    }
  }

  // Cribbed from Times.parseIso8601Date(String).
  private static Date parse(String dateString) throws ParseException {
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

  public static TypeAdapter<DateTime> typeAdapter(Gson gson) {
    return new AutoValue_DateTime.GsonTypeAdapter(gson);
  }
}
