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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.squareup.sdk.pos.TransactionRequest;
import com.squareup.sdk.pos.transaction.Tender;
import com.squareup.sdk.pos.transaction.Transaction;

/**
 * Activity for displaying the result of a successful transaction. It receives the order number
 * from the intent and displays it, then links back to the Main Activity with a button.
 */
public class TransactionSuccessActivity extends AppCompatActivity {

  private static final String ORDER_NUMBER = "ORDER_NUMBER";
  private static final String CARD_INFO = "CARD_INFO";

  public static void start(Context context, TransactionRequest.Success transactionResult) {
    Intent intent = new Intent(context, TransactionSuccessActivity.class);
    String orderNumber = transactionResult.requestMetadata;
    Transaction transaction = transactionResult.transaction;
    for (Tender tender : transaction.tenders()) {
      if (tender.cardDetails() != null) {
        String cardBrand = tender.cardDetails().card().cardBrand().name();
        String cardNumber = tender.cardDetails().card().last4();
        intent.putExtra(CARD_INFO, cardBrand + " " + cardNumber);
      }
    }
    intent.putExtra(ORDER_NUMBER, orderNumber);
    context.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.order_complete);
    TextView orderCompleteMessageView = (TextView) findViewById(R.id.order_complete_subtitle);
    View customizeNewBike = findViewById(R.id.customize_new_bike);

    Intent intent = getIntent();
    String orderNumber = intent.getStringExtra(ORDER_NUMBER);
    String cardInfo = intent.getStringExtra(CARD_INFO);

    String orderNumberMessage = getString(R.string.order_info, orderNumber);
    String orderCompleteMessage;
    if (cardInfo != null) {
      orderCompleteMessage =
          getString(R.string.order_complete_card_message, cardInfo) + " " + orderNumberMessage;
    } else {
      orderCompleteMessage = orderNumberMessage;
    }

    orderCompleteMessageView.setText(orderCompleteMessage);
    customizeNewBike.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });
  }
}
