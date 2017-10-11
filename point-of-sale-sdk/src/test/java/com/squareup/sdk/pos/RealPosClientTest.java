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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.squareup.sdk.pos.PosApi.EXTRA_API_VERSION;
import static com.squareup.sdk.pos.PosApi.EXTRA_AUTO_RETURN;
import static com.squareup.sdk.pos.PosApi.EXTRA_CURRENCY_CODE;
import static com.squareup.sdk.pos.PosApi.EXTRA_CUSTOMER_ID;
import static com.squareup.sdk.pos.PosApi.EXTRA_LOCATION_ID;
import static com.squareup.sdk.pos.PosApi.EXTRA_NOTE;
import static com.squareup.sdk.pos.PosApi.EXTRA_POINT_OF_SALE_CLIENT_ID;
import static com.squareup.sdk.pos.PosApi.EXTRA_REQUEST_STATE;
import static com.squareup.sdk.pos.PosApi.EXTRA_SDK_VERSION;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CARD;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_TYPES;
import static com.squareup.sdk.pos.PosApi.EXTRA_TOTAL_AMOUNT;
import static com.squareup.sdk.pos.PosApi.INTENT_ACTION_CHARGE;
import static com.squareup.sdk.pos.TestData.INVALID_SIGNATURE;
import static com.squareup.sdk.pos.TestData.POINT_OF_SALE_SIGNATURE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings({ "WrongConstant", "deprecation" }) //
@RunWith(RobolectricTestRunner.class) //
public class RealPosClientTest {

  private static final String CLIENT_ID = "CLIENT_ID";

  @Mock(answer = Answers.RETURNS_DEEP_STUBS) Context context;
  List<ResolveInfo> chargeActivities;
  RealPosClient client;

  @Before public void setUp() {
    initMocks(this);
    chargeActivities = new ArrayList<>();

    installApp("com.squareup", 2, POINT_OF_SALE_SIGNATURE);

    when(context.getPackageManager()
        .queryIntentActivities(new Intent(INTENT_ACTION_CHARGE), 0)).thenReturn(chargeActivities);

    client = new RealPosClient(context, CLIENT_ID);
  }

  @Test public void opensPlayStoreWhenInstalled() throws Exception {
    client.openPointOfSalePlayStoreListing();

    Intent expectedIntent = new Intent(ACTION_VIEW, Uri.parse("market://details?id=com.squareup"));
    expectedIntent.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    verify(context).startActivity(expectedIntent);
  }

  @Test public void opensUrlWhenPlayStoreNotInstalled() throws Exception {
    when(context.getPackageManager().getPackageInfo("com.android.vending", 0)).thenThrow(
        new PackageManager.NameNotFoundException());

    client.openPointOfSalePlayStoreListing();

    Intent expectedIntent = new Intent(ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=com.squareup"));
    expectedIntent.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    verify(context).startActivity(expectedIntent);
  }

  @Test public void launchPointOfSale() {
    client.launchPointOfSale();

    Intent expectedIntent = context.getPackageManager().getLaunchIntentForPackage("com.squareup");
    verify(context).startActivity(expectedIntent);
  }

  @Test public void pointOfSaleInstalled() {
    assertThat(client.isPointOfSaleInstalled()).isTrue();
  }

  @Test public void pointOfSaleNotInstalled() {
    chargeActivities.clear();

    assertThat(client.isPointOfSaleInstalled()).isFalse();
  }

  @Test public void betaFlavorInstalled() {
    chargeActivities.clear();
    installApp("com.squareup.beta", 2, POINT_OF_SALE_SIGNATURE);

    assertThat(client.isPointOfSaleInstalled()).isTrue();
  }

  @Test public void invalidPackagePrefix() {
    chargeActivities.clear();
    installApp("com.triangleup", 2, POINT_OF_SALE_SIGNATURE);

    assertThat(client.isPointOfSaleInstalled()).isFalse();
  }

  @Test public void invalidSignature() {
    chargeActivities.clear();
    installApp("com.squareup", 2, INVALID_SIGNATURE);

    assertThat(client.isPointOfSaleInstalled()).isFalse();
  }

  @Test public void multipleFlavorsInstalled() {
    chargeActivities.clear();
    installApp("com.squareup", 2, POINT_OF_SALE_SIGNATURE);
    installApp("com.squareup.beta", 3, POINT_OF_SALE_SIGNATURE);

    assertThat(client.isPointOfSaleInstalled()).isTrue();
  }

  @Test public void intentCreatedWithRequestParams() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, CurrencyCode.USD).restrictTendersTo(
        TransactionRequest.TenderType.CARD)
        .autoReturn(true)
        .enforceBusinessLocation("location")
        .customerId("customerId")
        .state("state")
        .note("note")
        .build();

    Intent intent = client.createTransactionIntent(request);

    assertThat(intent.getIntExtra(EXTRA_TOTAL_AMOUNT, -1)).isEqualTo(1_00);
    assertThat(intent.getStringExtra(EXTRA_CURRENCY_CODE)).isEqualTo("USD");
    assertThat(intent.getStringExtra(EXTRA_POINT_OF_SALE_CLIENT_ID)).isEqualTo(CLIENT_ID);
    assertThat(intent.getStringExtra(EXTRA_NOTE)).isEqualTo("note");
    assertThat(intent.getStringExtra(EXTRA_API_VERSION)).isEqualTo("v3.0");
    assertThat(intent.getStringExtra(EXTRA_REQUEST_STATE)).isEqualTo("state");
    assertThat(intent.getStringExtra(EXTRA_LOCATION_ID)).isEqualTo("location");
    assertThat(intent.getStringExtra(EXTRA_CUSTOMER_ID)).isEqualTo("customerId");
    assertThat(intent.getStringArrayListExtra(EXTRA_TENDER_TYPES)).containsExactly(
        EXTRA_TENDER_CARD);
    assertThat(intent.getBooleanExtra(EXTRA_AUTO_RETURN, false)).isTrue();
    assertThat(intent.getPackage()).isEqualTo("com.squareup");
  }

  @Test public void fieldsSetAsExpectedForDefaultRequest() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, CurrencyCode.USD).build();

    Intent intent = client.createTransactionIntent(request);

    assertThat(intent.hasExtra(EXTRA_POINT_OF_SALE_CLIENT_ID)).isTrue();
    assertThat(intent.hasExtra(EXTRA_TOTAL_AMOUNT)).isTrue();
    assertThat(intent.hasExtra(EXTRA_API_VERSION)).isTrue();
    assertThat(intent.hasExtra(EXTRA_SDK_VERSION)).isTrue();
    assertThat(intent.hasExtra(EXTRA_CURRENCY_CODE)).isTrue();
    assertThat(intent.hasExtra(EXTRA_REQUEST_STATE)).isTrue();
    assertThat(intent.hasExtra(EXTRA_TENDER_TYPES)).isTrue();

    assertThat(intent.hasExtra(EXTRA_CUSTOMER_ID)).isFalse();
    assertThat(intent.hasExtra(EXTRA_LOCATION_ID)).isFalse();
    assertThat(intent.getBooleanExtra(EXTRA_AUTO_RETURN, false)).isFalse();
  }

  @Test public void pinsToHighestVersionNumber() {
    chargeActivities.clear();
    installApp("com.squareup", 2, POINT_OF_SALE_SIGNATURE);
    installApp("com.squareup.beta", 3, POINT_OF_SALE_SIGNATURE);

    Intent intent =
        client.createTransactionIntent(new TransactionRequest.Builder(1_00, CurrencyCode.USD).build());

    assertThat(intent.getPackage()).isEqualTo("com.squareup.beta");
  }

  @Test(expected = ActivityNotFoundException.class)
  public void createIntentWithNoPointOfSaleThrows() {
    chargeActivities.clear();
    client.createTransactionIntent(new TransactionRequest.Builder(1_00, CurrencyCode.USD).build());
  }

  @Test(expected = ActivityNotFoundException.class)
  public void createIntentWithInvalidPointOfSaleThrows() {
    chargeActivities.clear();
    installApp("com.squareup", 2, INVALID_SIGNATURE);
    client.createTransactionIntent(new TransactionRequest.Builder(1_00, CurrencyCode.USD).build());
  }

  @Test(expected = ActivityNotFoundException.class)
  public void launchPointOfSaleThrowsWithNoPointOfSaleInstalled() {
    chargeActivities.clear();
    client.launchPointOfSale();
  }

  @Test(expected = ActivityNotFoundException.class)
  public void launchPointOfSaleThrowsWithInvalidSignature() {
    chargeActivities.clear();
    installApp("com.squareup", 2, INVALID_SIGNATURE);
    client.launchPointOfSale();
  }

  private void installApp(String packageName, int versionCode, Signature signature) {
    ResolveInfo resolveInfo = new ResolveInfo();
    resolveInfo.activityInfo = new ActivityInfo();
    resolveInfo.activityInfo.packageName = packageName;
    chargeActivities.add(resolveInfo);
    PackageInfo packageInfo = new PackageInfo();
    packageInfo.versionCode = versionCode;
    packageInfo.packageName = packageName;
    packageInfo.signatures = new Signature[1];
    packageInfo.signatures[0] = signature;
    try {
      when(context.getPackageManager().getPackageInfo(eq(packageName), anyInt())).thenReturn(
          packageInfo);
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
