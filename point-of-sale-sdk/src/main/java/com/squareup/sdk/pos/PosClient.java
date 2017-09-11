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

import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Provides methods for interacting with the Square Point of Sale app, such as by generating
 * {@code Intent}s that initiate transactions.
 */
public interface PosClient {

  /**
   * Creates an {@code Intent} that can be used to initiate a Square Point of Sale
   * transaction. Provide the created {@code Intent} to the
   * {@link android.app.Activity#startActivityForResult(Intent, int)} to
   * initiate the transaction.
   *
   * @throws android.content.ActivityNotFoundException if Square Point of Sale is not installed.
   * @throws NullPointerException if chargeRequest is null.
   * @see PosSdk
   */
  @NonNull Intent createChargeIntent(@NonNull ChargeRequest chargeRequest);

  /**
   * @return {@code true} if a version of Square Point of Sale that supports the Point of Sale API
   * is installed,
   * {@code false} otherwise.
   */
  boolean isPointOfSaleInstalled();

  /**
   * Launches the Square Point of Sale application. This is equivalent to pressing the home button
   * and opening the Point of Sale app. It is useful for handling errors that require the user to
   * complete an action within Point of Sale, such as completing a transaction after receiving a
   * {@link ChargeRequest.ErrorCode#TRANSACTION_ALREADY_IN_PROGRESS} error.
   *
   * @throws android.content.ActivityNotFoundException if Square Point of Sale is not installed.
   */
  void launchPointOfSale();

  /**
   * Opens the Square Point of Sale install page in the Google Play Store. The Play Store
   * activity is started using the application context.
   */
  void openPointOfSalePlayStoreListing();

  /**
   * Use this method to parse the data {@link Intent} passed in
   * {@link android.app.Activity#onActivityResult(int, int, Intent)} when {@code resultCode}
   * is equal to {@link android.app.Activity#RESULT_OK} (successful transaction).
   */
  @NonNull ChargeRequest.Success parseChargeSuccess(@NonNull Intent data);

  /**
   * Use this method to parse the data {@link Intent} passed in
   * {@link android.app.Activity#onActivityResult(int, int, Intent)} when {@code resultCode}
   * is equal to {@link android.app.Activity#RESULT_CANCELED} (canceled transaction).
   */
  @NonNull ChargeRequest.Error parseChargeError(@NonNull Intent data);
}
