package com.example.owensbikes;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataLoader {

  private static final String itemsFilename = "items.json";

  private final ExecutorService executor;
  private final Handler mainHandler;
  private Listener listener;
  private Data data;
  private final Application application;
  private boolean requestInFlight;

  public interface Listener {
    void onDataLoaded(List<BikeItem> items);

    void onLoadFailed(CharSequence errorMessage);
  }

  DataLoader(Application application) {
    this.application = application;
    executor = Executors.newSingleThreadExecutor();
    mainHandler = new Handler(Looper.getMainLooper());
  }

  public void loadItemsFromAssets(final Listener listener) {
    this.listener = listener;
    if (data != null) {
      requestInFlight = false;
      mainHandler.post(new Runnable() {
        @Override public void run() {
          if (listener != null) {
            listener.onDataLoaded(data.items);
            clearListener();
          }
        }
      });
      return;
    }
    if (requestInFlight) {
      return;
    }
    requestInFlight = true;
    executor.execute(new Runnable() {
      @Override public void run() {
        Gson gson = new Gson();
        String itemsJson = "";
        try {
          itemsJson = loadJsonStringFromAssets(itemsFilename);
        } catch (IOException e) {
          onError("Error loading the items and modifiers from file.");
        }

        Type itemsType = new TypeToken<List<BikeItem>>() {
        }.getType();
        List<BikeItem> items = gson.fromJson(itemsJson, itemsType);

        onDataLoaded(new Data(items));
      }
    });
  }

  private String loadJsonStringFromAssets(String filename) throws IOException {
    InputStream inputStream = application.getAssets().open(filename);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder jsonString = new StringBuilder();
    String nextLine;
    while ((nextLine = reader.readLine()) != null) {
      jsonString.append(nextLine);
    }
    return jsonString.toString();
  }

  public void clearListener() {
    listener = null;
  }

  private void onDataLoaded(final Data data) {
    DataLoader.this.data = data;
    mainHandler.post(new Runnable() {
      @Override public void run() {
        requestInFlight = false;
        listener.onDataLoaded(data.items);
        clearListener();
      }
    });
  }

  private void onError(final CharSequence message) {
    mainHandler.post(new Runnable() {
      @Override public void run() {
        requestInFlight = false;
        if (listener != null) {
          listener.onLoadFailed(message);
          clearListener();
        }
      }
    });
  }

  static class Data {
    private final List<BikeItem> items;

    Data(List<BikeItem> items) {
      this.items = items;
    }
  }
}
