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
import com.squareup.sdk.pos.TransactionRequest;
import com.squareup.sdk.pos.CurrencyCode;
import com.squareup.sdk.pos.PosClient;
import com.squareup.sdk.pos.PosSdk;
import java.util.EnumSet;
import java.util.Set;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class HelloChargeActivity extends AppCompatActivity {

  private static final String TAG = HelloChargeActivity.class.getSimpleName();

  private static final int CHARGE_REQUEST_CODE = 0xF00D;

  private EditText transactionAmountEditText;
  private EditText currencyCodeEditText;
  private EditText noteEditText;
  private CheckBox cardCheckbox;
  private CheckBox cashCheckbox;
  private CheckBox cardOnFileCheckbox;
  private CheckBox otherTenderCheckbox;
  private CheckBox autoReturnCheckbox;
  private EditText locationIdEditText;
  private EditText customerIdEditText;
  private EditText stateEditText;

  private PosClient posClient;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.hello_charge_activity);

    transactionAmountEditText = findView(R.id.transaction_amount_edit_text);
    currencyCodeEditText = findView(R.id.currency_code_edit_text);
    noteEditText = findView(R.id.note_edit_text);
    cardCheckbox = findView(R.id.card_tender_checkbox);
    cashCheckbox = findView(R.id.cash_tender_checkbox);
    cardOnFileCheckbox = findView(R.id.card_on_file_checkbox);
    otherTenderCheckbox = findView(R.id.other_tender_checkbox);
    locationIdEditText = findView(R.id.location_id_edit_text);
    customerIdEditText = findView(R.id.customer_id_edit_text);
    autoReturnCheckbox = findView(R.id.auto_return_checkbox);
    stateEditText = findView(R.id.state_edit_text);

    findView(R.id.start_transaction_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startTransaction();
      }
    });

    posClient = PosSdk.createClient(this, BuildConfig.CLIENT_ID);
  }

  private void startTransaction() {
    if (!posClient.isPointOfSaleInstalled()) {
      new AlertDialog.Builder(this).setTitle(R.string.install_point_of_sale_title)
          .setMessage(getString(R.string.install_point_of_sale_message))
          .setPositiveButton(getString(R.string.install_point_of_sale_confirm),
              new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  posClient.openPointOfSalePlayStoreListing();
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
    Set<TransactionRequest.TenderType> tenderTypes = EnumSet.noneOf(TransactionRequest.TenderType.class);
    if (cardCheckbox.isChecked()) {
      tenderTypes.add(TransactionRequest.TenderType.CARD);
    }
    if (cashCheckbox.isChecked()) {
      tenderTypes.add(TransactionRequest.TenderType.CASH);
    }
    if (cardOnFileCheckbox.isChecked()) {
      tenderTypes.add(TransactionRequest.TenderType.CARD_ON_FILE);
    }
    if (otherTenderCheckbox.isChecked()) {
      tenderTypes.add(TransactionRequest.TenderType.OTHER);
    }
    String locationId = locationIdEditText.getText().toString();
    String customerId = customerIdEditText.getText().toString();
    boolean shouldAutoReturn = autoReturnCheckbox.isChecked();

    String state = stateEditText.getText().toString();

    TransactionRequest transactionRequest =
        new TransactionRequest.Builder(amount, CurrencyCode.valueOf(currencyCode)).note(note)
            .enforceBusinessLocation(locationId)
            .customerId(customerId)
            .autoReturn(shouldAutoReturn)
            .state(state)
            .restrictTendersTo(tenderTypes)
            .build();
    try {
      Intent chargeIntent = posClient.createTransactionIntent(transactionRequest);
      startActivityForResult(chargeIntent, CHARGE_REQUEST_CODE);
    } catch (ActivityNotFoundException e) {
      showSnackbar("Square Point of Sale was just uninstalled.");
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CHARGE_REQUEST_CODE) {
      if (data == null) {
        // This can happen if Square Point of Sale was uninstalled or crashed while we're waiting for a
        // result.
        showSnackbar("No Result from Square Point of Sale");
        return;
      }
      if (resultCode == Activity.RESULT_OK) {
        TransactionRequest.Success success = posClient.parseTransactionSuccess(data);
        onTransactionSuccess(success);
      } else {
        TransactionRequest.Error error = posClient.parseTransactionError(data);
        onTransactionError(error);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void onTransactionSuccess(TransactionRequest.Success successResult) {
    CharSequence message = Html.fromHtml("<b><font color='#00aa00'>Success</font></b><br><br>"
        + "<b>Client RealTransaction Id</b><br>"
        + successResult.transaction.clientId()
        + "<br><br><b>Server RealTransaction Id</b><br>"
        + successResult.transaction.serverId()
        + "<br><br><b>Request Metadata</b><br>"
        + successResult.state);
    showResult(message);
    Log.d(TAG, message.toString());
  }

  private void onTransactionError(TransactionRequest.Error errorResult) {
    CharSequence message = Html.fromHtml("<b><font color='#aa0000'>Error</font></b><br><br>"
        + "<b>Error Key</b><br>"
        + errorResult.code
        + "<br><br><b>Error Description</b><br>"
        + errorResult.debugDescription
        + "<br><br><b>Request Metadata</b><br>"
        + errorResult.state);
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
