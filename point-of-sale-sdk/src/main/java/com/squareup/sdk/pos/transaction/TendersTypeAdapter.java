package com.squareup.sdk.pos.transaction;

import android.os.Parcel;
import android.os.Parcelable;
import com.ryanharter.auto.value.parcel.TypeAdapter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TendersTypeAdapter implements TypeAdapter<Set<Tender>> {
  @Override public Set<Tender> fromParcel(Parcel in) {
    List<AutoValue_Tender> tenders = new ArrayList<>();
    in.readTypedList(tenders, AutoValue_Tender.CREATOR);
    return new LinkedHashSet<Tender>(tenders);
  }

  @Override public void toParcel(Set<Tender> tenders, Parcel dest) {
    dest.writeTypedList(new ArrayList<Parcelable>(tenders));
  }
}
