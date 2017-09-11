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

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;

public class App extends Application {

  private ChargeActivity resumedChargeActivity;

  @Override public void onCreate() {
    super.onCreate();
    Log.d("ChargeAssistant", "Token: " + FirebaseInstanceId.getInstance().getToken());

    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {

      @Override public void onActivityResumed(Activity activity) {
        if (activity instanceof ChargeActivity) {
          resumedChargeActivity = (ChargeActivity) activity;
        }
      }

      @Override public void onActivityPaused(Activity activity) {
        if (resumedChargeActivity == activity) {
          resumedChargeActivity = null;
        }
      }
    });
  }

  public ChargeActivity getResumedChargeActivity() {
    return resumedChargeActivity;
  }
}
