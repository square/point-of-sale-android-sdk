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
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Activity for displaying the result of a successful transaction. It receives the order number
 * from the intent and displays it, then links back to the Main Activity with a button.
 */
public class TransactionSuccessActivity extends AppCompatActivity {

  private static final String ORDER_NUMBER = "ORDER_NUMBER";

  public static void start(Context context, String orderNumber) {
    Intent intent = new Intent(context, TransactionSuccessActivity.class);
    intent.putExtra(ORDER_NUMBER, orderNumber);
    context.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.order_complete);
    TextView orderNumberMessage = (TextView) findViewById(R.id.order_complete_subtitle);
    View customizeNewBike = findViewById(R.id.customize_new_bike);

    Intent intent = getIntent();
    String orderNumber = intent.getStringExtra(ORDER_NUMBER);
    orderNumberMessage.setText(getString(R.string.order_info, orderNumber));

    customizeNewBike.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });
  }
}
