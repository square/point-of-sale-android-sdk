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

package com.squareup.sdk.pos;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * <p>The Point of Sale SDK lets you start the Square Point of Sale app to take transactions with the Square
 * hardware.
 *
 * <h1>Usage</h1>
 *
 * <h2>Starting a charge request</h2>
 *
 * <pre class="code"><code class="java">
 * PosClient posClient = PosSdk.createClient(context, CLIENT_ID);
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
 *   Intent chargeIntent = posClient.createChargeIntent(request);
 *   activity.startActivityForResult(chargeIntent, CHARGE_REQUEST_CODE);
 * } catch (ActivityNotFoundException e) {
 *   posClient.openPointOfSalePlayStoreListing();
 * }
 * </code></pre>
 *
 * <h2>Handling a charge result</h2>
 *
 * <pre class="code"><code class="java">
 * {@literal @}Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *   if (requestCode == CHARGE_REQUEST_CODE) {
 *     if (data == null) {
 *       // This happens if Point of Sale was uninstalled or crashed while we're waiting for a result.
 *       return;
 *     }
 *     if (resultCode == Activity.RESULT_OK) {
 *       onTransactionSuccess(posClient.parseChargeSuccess(data));
 *     } else {
 *       onTransactionError(posClient.parseChargeError(data));
 *     }
 *   }
 * }
 * </code></pre>
 */
public final class PosSdk {

  /**
   * Creates a new instance of {@link PosClient} that can then be used to create charge
   * intents.
   *
   * @param context Any {@link Context} will work. It is safe to pass in an activity context, as
   * the {@link PosClient} instance will only hold on to the result from {@link
   * Context#getApplicationContext()}.
   * @param clientId Client ID provided by Square.
   * @return a unique {@link PosClient} instance.
   * @throws NullPointerException if context or clientId are null.
   */
  public static @NonNull PosClient createClient(@NonNull Context context,
      @NonNull String clientId) {
    context = PosSdkHelper.nonNull(context, "context").getApplicationContext();
    PosSdkHelper.nonNull(clientId, "clientId");
    return new RealPosClient(context, clientId);
  }

  private PosSdk() {
    throw new AssertionError();
  }
}
