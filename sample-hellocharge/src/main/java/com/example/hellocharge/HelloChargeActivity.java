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
package com.example.hellocharge;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.squareup.sdk.register.ChargeRequest;
import com.squareup.sdk.register.CurrencyCode;
import com.squareup.sdk.register.RegisterClient;
import com.squareup.sdk.register.RegisterSdk;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.ACTION_VIEW;
import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.squareup.sdk.register.RegisterApi.AUTO_RETURN_NO_TIMEOUT;

public class HelloChargeActivity extends AppCompatActivity {

  private static final String TAG = HelloChargeActivity.class.getSimpleName();

  private static final int CHARGE_REQUEST_CODE = 0xF00D;

  private static final Uri OAUTH_LINK = Uri.parse(
      "https://connect.squareup.com/oauth2/authorize?client_id="
          + BuildConfig.CLIENT_ID
          + "&response_type=token&scope=PAYMENTS_WRITE");

  private EditText transactionAmountEditText;
  private EditText currencyCodeEditText;
  private EditText noteEditText;
  private CheckBox cardCheckbox;
  private CheckBox cashCheckbox;
  private CheckBox otherTenderCheckbox;
  private EditText locationIdEditText;
  private EditText autoReturnTimeoutEditText;
  private EditText requestMetadataEditText;

  private RegisterClient registerClient;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.hello_charge_activity);

    transactionAmountEditText = findView(R.id.transaction_amount_edit_text);
    currencyCodeEditText = findView(R.id.currency_code_edit_text);
    noteEditText = findView(R.id.note_edit_text);
    cardCheckbox = findView(R.id.card_tender_checkbox);
    cashCheckbox = findView(R.id.cash_tender_checkbox);
    otherTenderCheckbox = findView(R.id.other_tender_checkbox);
    locationIdEditText = findView(R.id.location_id_edit_text);
    autoReturnTimeoutEditText = findView(R.id.auto_return_timeout_edit_text);
    requestMetadataEditText = findView(R.id.request_metadata_edit_text);

    findView(R.id.oauth_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startOAuth();
      }
    });

    findView(R.id.start_transaction_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startTransaction();
      }
    });

    registerClient = RegisterSdk.createClient(this, BuildConfig.CLIENT_ID);
  }

  private void startOAuth() {
    Intent browserIntent = new Intent(ACTION_VIEW, OAUTH_LINK);
    startActivity(browserIntent);
  }

  private void startTransaction() {
    if (!registerClient.isRegisterInstalled()) {
      new AlertDialog.Builder(this).setTitle(R.string.install_register_title)
          .setMessage(getString(R.string.install_register_message))
          .setPositiveButton(getString(R.string.install_register_confirm),
              new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  registerClient.openRegisterPlayStoreListing();
                }
              })
          .setNegativeButton(android.R.string.cancel, null)
          .show();
      return;
    }

    String amountString = transactionAmountEditText.getText().toString();
    int amount = isBlank(amountString) ? 0 : Integer.valueOf(amountString);
    String currencyCode = currencyCodeEditText.getText().toString();
    String note = noteEditText.getText().toString();
    Set<ChargeRequest.TenderType> tenderTypes = EnumSet.noneOf(ChargeRequest.TenderType.class);
    if (cardCheckbox.isChecked()) {
      tenderTypes.add(ChargeRequest.TenderType.CARD);
    }
    if (cashCheckbox.isChecked()) {
      tenderTypes.add(ChargeRequest.TenderType.CASH);
    }
    if (otherTenderCheckbox.isChecked()) {
      tenderTypes.add(ChargeRequest.TenderType.OTHER);
    }
    String locationId = locationIdEditText.getText().toString();
    String timeoutString = autoReturnTimeoutEditText.getText().toString();
    long timeout = isBlank(timeoutString) ? AUTO_RETURN_NO_TIMEOUT : Integer.valueOf(timeoutString);

    String requestMetadata = requestMetadataEditText.getText().toString();

    ChargeRequest chargeRequest =
        new ChargeRequest.Builder(amount, CurrencyCode.valueOf(currencyCode)).note(note)
            .enforceBusinessLocation(locationId)
            .autoReturn(timeout, TimeUnit.MILLISECONDS)
            .requestMetadata(requestMetadata)
            .restrictTendersTo(tenderTypes)
            .build();
    try {
      Intent chargeIntent = registerClient.createChargeIntent(chargeRequest);
      startActivityForResult(chargeIntent, CHARGE_REQUEST_CODE);
    } catch (ActivityNotFoundException e) {
      showSnackbar("Square Register was just uninstalled.");
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CHARGE_REQUEST_CODE) {
      if (data == null) {
        // This can happen if Square Register was uninstalled or crashed while we're waiting for a
        // result.
        showSnackbar("No Result from Square Register");
        return;
      }
      if (resultCode == Activity.RESULT_OK) {
        ChargeRequest.Success success = registerClient.parseChargeSuccess(data);
        onTransactionSuccess(success);
      } else {
        ChargeRequest.Error error = registerClient.parseChargeError(data);
        onTransactionError(error);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void onTransactionSuccess(ChargeRequest.Success successResult) {
    CharSequence message = Html.fromHtml("<b><font color='#00aa00'>Success</font></b><br><br>"
        + "<b>Client RealTransaction Id</b><br>"
        + successResult.clientTransactionId
        + "<br><br><b>Server RealTransaction Id</b><br>"
        + successResult.serverTransactionId
        + "<br><br><b>Request Metadata</b><br>"
        + successResult.requestMetadata);
    showResult(message);
    Log.d(TAG, message.toString());
  }

  private void onTransactionError(ChargeRequest.Error errorResult) {
    CharSequence message = Html.fromHtml("<b><font color='#aa0000'>Error</font></b><br><br>"
        + "<b>Error Key</b><br>"
        + errorResult.code
        + "<br><br><b>Error Description</b><br>"
        + errorResult.debugDescription
        + "<br><br><b>Request Metadata</b><br>"
        + errorResult.requestMetadata);
    showResult(message);
    Log.d(TAG, message.toString());
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  private void showSnackbar(CharSequence text) {
    Snackbar.make(noteEditText, text, LENGTH_LONG).show();
  }

  private void showResult(CharSequence message) {
    new AlertDialog.Builder(this).setTitle(getString(R.string.result_title))
        .setMessage(message)
        .setPositiveButton(android.R.string.ok, null)
        .show();
  }

  /** Helper method to remove the need for casting and avoid @Nullable warnings. */
  private <T extends View> T findView(@IdRes int id) {
    //noinspection unchecked
    return (T) findViewById(id);
  }
}
