package com.squareup.sdk.pos.transaction;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

@GsonTypeAdapterFactory //
public abstract class PosApiTransactionAdapterFactory
    implements TypeAdapterFactory {

  public static TypeAdapterFactory create() {
    return new AutoValueGson_PosApiTransactionAdapterFactory();
  }
}

