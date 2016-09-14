package com.example.owensbikes;

import android.app.Application;
import android.content.Context;

public class BikeApplication extends Application {

  public static BikeApplication from(Context context) {
    return (BikeApplication) context.getApplicationContext();
  }

  private DataLoader dataLoader;

  @Override public void onCreate() {
    super.onCreate();
    dataLoader = new DataLoader(this);
  }

  public DataLoader getDataLoader() {
    return dataLoader;
  }
}
