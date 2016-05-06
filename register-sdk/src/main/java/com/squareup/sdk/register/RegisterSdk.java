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

package com.squareup.sdk.register;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * <p>The Register SDK lets you start the Square Register app to take transactions with the Square
 * hardware.
 *
 * <h1>Usage</h1>
 *
 * <h2>Starting a charge request</h2>
 *
 * <pre class="code"><code class="java">
 * RegisterClient registerClient = RegisterSdk.createClient(context, CLIENT_ID);
 *
 * ChargeRequest request = new ChargeRequest.Builder(550, CurrencyCode.USD)
 *   .note("Super Burrito, no cilantro")
 *   .enforceBusinessLocation(locationId)
 *   .autoReturn(4, TimeUnit.SECONDS)
 *   .requestMetadata("#329")
 *   .restrictTenderTypesTo(ChargeRequest.TenderType.CARD)
 *   .build();
 *
 * try {
 *   Intent chargeIntent = registerClient.createChargeIntent(request);
 *   activity.startActivityForResult(chargeIntent, CHARGE_REQUEST_CODE);
 * } catch (ActivityNotFoundException e) {
 *   registerClient.openRegisterPlayStoreListing();
 * }
 * </code></pre>
 *
 * <h2>Handling a charge result</h2>
 *
 * <pre class="code"><code class="java">
 * {@literal @}Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *   if (requestCode == CHARGE_REQUEST_CODE) {
 *     if (data == null) {
 *       // This happens if Register was uninstalled or crashed while we're waiting for a result.
 *       return;
 *     }
 *     if (resultCode == Activity.RESULT_OK) {
 *       onTransactionSuccess(registerClient.parseChargeSuccess(data));
 *     } else {
 *       onTransactionError(registerClient.parseChargeError(data));
 *     }
 *   }
 * }
 * </code></pre>
 */
public final class RegisterSdk {

  /**
   * Creates a new instance of {@link RegisterClient} that can then be used to create charge
   * intents.
   *
   * @param context Any {@link Context} will work. It is safe to pass in an activity context, as
   * the {@link RegisterClient} instance will only hold on to the result from {@link
   * Context#getApplicationContext()}.
   * @param clientId Client ID provided by Square.
   * @return a unique {@link RegisterClient} instance.
   * @throws NullPointerException if context or clientId are null.
   */
  public static @NonNull RegisterClient createClient(@NonNull Context context,
      @NonNull String clientId) {
    context = RegisterSdkHelper.nonNull(context, "context").getApplicationContext();
    RegisterSdkHelper.nonNull(clientId, "clientId");
    return new RealRegisterClient(context, clientId);
  }

  private RegisterSdk() {
    throw new AssertionError();
  }
}
