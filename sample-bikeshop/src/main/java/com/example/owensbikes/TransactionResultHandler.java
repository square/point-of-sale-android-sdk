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

package com.example.owensbikes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.squareup.sdk.register.ChargeRequest;
import com.squareup.sdk.register.RegisterClient;

public class TransactionResultHandler {

  private final Activity activity;
  private final RegisterClient registerClient;
  private final DialogComposer dialogComposer;

  TransactionResultHandler(Activity activity, RegisterClient registerClient,
      DialogComposer dialogComposer) {
    this.activity = activity;
    this.registerClient = registerClient;
    this.dialogComposer = dialogComposer;
  }

  public void onNoResult()  {
    dialogComposer.showErrorDialog(R.string.error_no_result, R.string.error_no_result_message);
  }

  public void onSuccess(Intent data) {
    ChargeRequest.Success success = registerClient.parseChargeSuccess(data);
    TransactionSuccessActivity.start(activity, success.requestMetadata);
  }

  public void onError(Intent data) {
    ChargeRequest.Error error = registerClient.parseChargeError(data);
    showErrorDialog(error);
  }

  public void showErrorDialog(ChargeRequest.Error errorResult) {
    Log.e(String.valueOf(errorResult.code), errorResult.debugDescription);
    switch (errorResult.code) {
      case DISABLED:
        dialogComposer.showErrorDialog(R.string.error_api_disabled, R.string.error_api_disabled_message);
        break;
      case ILLEGAL_LOCATION_ID:
        throw new IllegalStateException(
            "This sample app never passes a location id to the Register API.");
      case INVALID_REQUEST:
        dialogComposer.showErrorDialog(R.string.error_unspecified,
            R.string.error_invalid_request_message);
        break;
      case NO_NETWORK:
        dialogComposer.showErrorDialogWithRetry(R.string.error_network,
            R.string.error_network_message);
        break;
      case TRANSACTION_ALREADY_IN_PROGRESS:
        dialogComposer.showTransactionInProgressDialog();
        break;
      case TRANSACTION_CANCELED:
        dialogComposer.showErrorDialogWithRetry(R.string.error_transaction_cancelled,
            R.string.error_transaction_cancelled_message);
        break;
      case UNSUPPORTED_API_VERSION:
        dialogComposer.showUpdateRegisterDialog();
        break;
      case USER_NOT_ACTIVATED:
        dialogComposer.showErrorDialogWithRetry(R.string.error_not_activated,
            R.string.error_not_activated_message);
        break;
      case USER_NOT_LOGGED_IN:
        dialogComposer.showErrorDialogWithRetry(R.string.error_not_logged_in,
            R.string.error_not_logged_in_message);
        break;
      case NO_RESULT:
      case UNEXPECTED:
      default:
        dialogComposer.showErrorDialogWithRetry(R.string.error_unspecified, R.string.try_again);
    }
  }
}
