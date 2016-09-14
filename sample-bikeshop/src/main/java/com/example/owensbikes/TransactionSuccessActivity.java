package com.example.owensbikes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.squareup.sdk.register.ChargeRequest;

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
