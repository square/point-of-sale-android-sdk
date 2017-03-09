/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.homecharge;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class ChargeService extends FirebaseMessagingService {

  final Handler handler = new Handler(Looper.getMainLooper());

  @Override public void onMessageReceived(RemoteMessage remoteMessage) {
    Map<String, String> data = remoteMessage.getData();
    final int dollarAmount = Integer.parseInt(data.get("amount"));
    final String note = data.get("note");

    handler.post(new Runnable() {
      @Override public void run() {
        startTransaction(dollarAmount, note);
      }
    });
  }

  private void startTransaction(int dollarAmount, String note) {
    App app = (App) getApplicationContext();
    ChargeActivity chargeActivity = app.getResumedChargeActivity();
    if (chargeActivity != null) {
      chargeActivity.startTransaction(dollarAmount, note);
    } else {
      Toast.makeText(app, "No resumed activity to start transaction", Toast.LENGTH_LONG).show();
    }
  }
}
