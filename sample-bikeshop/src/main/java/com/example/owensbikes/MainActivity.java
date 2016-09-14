package com.example.owensbikes;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.sdk.register.ChargeRequest;
import com.squareup.sdk.register.CurrencyCode;
import com.squareup.sdk.register.RegisterApi;
import com.squareup.sdk.register.RegisterClient;
import com.squareup.sdk.register.RegisterSdk;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.squareup.sdk.register.ChargeRequest.TenderType.CARD;
import static com.squareup.sdk.register.ChargeRequest.TenderType.CASH;

public class MainActivity extends AppCompatActivity {

  private static final String ORDER_INFO = "ORDER_INFO";
  private static final String ORDER_NUMBER = "ORDER_NUMBER";

  private static final int FIRST_ORDER_NUMBER = 1;
  private static final int CHARGE_REQUEST_CODE = 0xCAFE;

  private RegisterClient registerClient;
  private DataLoader dataLoader;
  private SharedPreferences orderInfoPrefs;
  private ProgressBar spinner;
  private TextView loadingMessage;
  private BikeItemManager itemManager;
  private DialogComposer dialogComposer;
  private TransactionResultHandler transactionResultHandler;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    spinner = (ProgressBar) findViewById(R.id.progress_bar);
    loadingMessage = (TextView) findViewById(R.id.progress_bar_text);

    orderInfoPrefs = getSharedPreferences(ORDER_INFO, MODE_PRIVATE);
    registerClient = RegisterSdk.createClient(this, BuildConfig.CLIENT_ID);
    dialogComposer = new DialogComposer(this, registerClient);
    transactionResultHandler = new TransactionResultHandler(this, registerClient, dialogComposer);
    dataLoader = BikeApplication.from(this).getDataLoader();
    boolean isTablet = getResources().getBoolean(R.bool.isTablet);
    boolean isLandscape =
        getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    boolean twoLists = isTablet ^ isLandscape;

    AdapterController adapterController =
        twoLists ? new AdapterController.TwoLists(this) : new AdapterController.SingleList(this);

    itemManager = new BikeItemManager(this, adapterController);
    itemManager.loadState(savedInstanceState);

    loadItems();

    View checkoutButton = findViewById(R.id.checkout_button);
    checkoutButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        checkout();
      }
    });
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    itemManager.saveState(outState);
  }

  private void loadItems() {
    spinner.setVisibility(View.VISIBLE);
    loadingMessage.setVisibility(View.VISIBLE);
    dataLoader.loadItemsFromAssets(new DataLoader.Listener() {
      @Override public void onDataLoaded(List<BikeItem> loadedItems) {
        spinner.setVisibility(View.INVISIBLE);
        loadingMessage.setVisibility(View.INVISIBLE);
        // Item manager will loadState data into the recycler views.
        itemManager.loadItems(loadedItems);
      }

      @Override public void onLoadFailed(CharSequence message) {
        spinner.setVisibility(View.INVISIBLE);
        loadingMessage.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
      }
    });
  }

  public void checkout() {
    int amount = itemManager.getTotal();
    String note = itemManager.getNote();
    Set<ChargeRequest.TenderType> tenderTypes = EnumSet.of(CARD, CASH);
    // Order number is an integer stored in a SharedPreference as an example of
    // requestMetadata usage.
    long orderNumber = orderInfoPrefs.getLong(ORDER_NUMBER, FIRST_ORDER_NUMBER) + 1;
    orderInfoPrefs.edit().putLong(ORDER_NUMBER, orderNumber).apply();
    String requestMetadata = String.valueOf(orderNumber);

    ChargeRequest.Builder chargeRequest = new ChargeRequest.Builder(amount, CurrencyCode.USD) //
        .note(note) //
        .autoReturn(RegisterApi.AUTO_RETURN_TIMEOUT_MIN_MILLIS, TimeUnit.MILLISECONDS) //
        .requestMetadata(requestMetadata) //
        .restrictTendersTo(tenderTypes);
    try {
      Intent chargeIntent = registerClient.createChargeIntent(chargeRequest.build());
      startActivityForResult(chargeIntent, CHARGE_REQUEST_CODE);
    } catch (ActivityNotFoundException e) {
      dialogComposer.showRegisterUninstalledDialog();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CHARGE_REQUEST_CODE) {
      if (data == null) {
        transactionResultHandler.onNoResult();
      }
      if (resultCode == RESULT_OK) {
        itemManager.clearStateAndReloadItems();
        transactionResultHandler.onSuccess(data);
      } else {
        transactionResultHandler.onError(data);
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    BikeApplication.from(this).getDataLoader().clearListener();
  }
}



