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

import org.junit.Test;

import static com.squareup.sdk.pos.ChargeRequest.TenderType.CARD;
import static com.squareup.sdk.pos.ChargeRequest.TenderType.CARD_ON_FILE;
import static com.squareup.sdk.pos.ChargeRequest.TenderType.CASH;
import static com.squareup.sdk.pos.ChargeRequest.TenderType.OTHER;
import static com.squareup.sdk.pos.ChargeRequest.TenderType.PAYPAY;
import static com.squareup.sdk.pos.CurrencyCode.CAD;
import static com.squareup.sdk.pos.CurrencyCode.USD;
import static com.squareup.sdk.pos.PosApi.AUTO_RETURN_NO_TIMEOUT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class ChargeRequestTest {

  @Test(expected = IllegalArgumentException.class) public void negativeAmountThrows() {
    new ChargeRequest.Builder(-2, USD);
  }

  @Test(expected = NullPointerException.class) public void nullCurrencyThrows() {
    //noinspection ConstantConditions
    new ChargeRequest.Builder(1_00, null);
  }

  @Test public void requestHasAllTenderTypesByDefault() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).build();
    assertThat(request.tenderTypes).containsOnly(CARD, CARD_ON_FILE, CASH, OTHER, PAYPAY);
  }

  @Test public void requestHasNoTimeoutByDefault() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).build();
    assertThat(request.autoReturnMillis).isEqualTo(AUTO_RETURN_NO_TIMEOUT);
  }

  @Test public void requestHasAmountAndCurrency() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).build();
    assertThat(request.totalAmount).isEqualTo(1_00);
    assertThat(request.currencyCode).isEqualTo(USD);
  }

  @Test(expected = IllegalArgumentException.class) public void noteTooLongThrows() {
    new ChargeRequest.Builder(1_00, USD).note(longNote());
  }

  @Test public void requestHasNote() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).note("Some Note").build();
    assertThat(request.note).isEqualTo("Some Note");
  }

  @Test(expected = IllegalArgumentException.class) public void autoReturnTooHighThrows() {
    new ChargeRequest.Builder(1_00, USD).autoReturn(10_001, MILLISECONDS);
  }

  @Test(expected = IllegalArgumentException.class) public void autoReturnTooLowThrows() {
    new ChargeRequest.Builder(1_00, USD).autoReturn(3_199L, MILLISECONDS);
  }

  @Test(expected = NullPointerException.class) public void autoReturnNullUnitThrows() {
    new ChargeRequest.Builder(1_00, USD).autoReturn(3_500L, null);
  }

  @Test public void noAutoReturnTimeoutNullUnitPasses() {
    new ChargeRequest.Builder(1_00, USD).autoReturn(AUTO_RETURN_NO_TIMEOUT, null);
  }

  @Test public void requestHasAutoReturn() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).autoReturn(4, SECONDS).build();
    assertThat(request.autoReturnMillis).isEqualTo(4_000);
  }

  @Test public void requestHasEnforcedBusinessLocation() {
    ChargeRequest request =
        new ChargeRequest.Builder(1_00, USD).enforceBusinessLocation("location").build();
    assertThat(request.locationId).isEqualTo("location");
  }

  @Test public void requestHasMetadata() {
    ChargeRequest request =
        new ChargeRequest.Builder(1_00, USD).requestMetadata("metadata").build();
    assertThat(request.requestMetadata).isEqualTo("metadata");
  }

  @Test public void requestHasCustomerId() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).customerId("customerId").build();
    assertThat(request.customerId).isEqualTo("customerId");
  }

  @Test public void copyKeepingAmount() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).restrictTendersTo(CARD)
        .autoReturn(4, SECONDS)
        .enforceBusinessLocation("location")
        .requestMetadata("metadata")
        .customerId("customerId")
        .note("note")
        .build();
    ChargeRequest updatedRequest = request.newBuilder()
        .restrictTendersTo(CASH)
        .autoReturn(5, SECONDS)
        .enforceBusinessLocation("location2")
        .requestMetadata("metadata2")
        .customerId("customerId2")
        .note("note2")
        .build();
    assertThat(updatedRequest.totalAmount).isEqualTo(1_00);
    assertThat(updatedRequest.currencyCode).isEqualTo(USD);
    assertThat(updatedRequest.tenderTypes).containsExactly(CASH);
    assertThat(updatedRequest.autoReturnMillis).isEqualTo(5_000);
    assertThat(updatedRequest.locationId).isEqualTo("location2");
    assertThat(updatedRequest.requestMetadata).isEqualTo("metadata2");
    assertThat(updatedRequest.customerId).isEqualTo("customerId2");
    assertThat(updatedRequest.note).isEqualTo("note2");
  }

  @Test public void copyUpdatingAmount() {
    ChargeRequest request = new ChargeRequest.Builder(1_00, USD).restrictTendersTo(CARD)
        .autoReturn(4, SECONDS)
        .enforceBusinessLocation("location")
        .requestMetadata("metadata")
        .customerId("customerId")
        .note("note")
        .build();
    ChargeRequest updatedRequest = request.newBuilder(2_00, CAD).build();
    assertThat(updatedRequest.totalAmount).isEqualTo(2_00);
    assertThat(updatedRequest.currencyCode).isEqualTo(CAD);
    assertThat(updatedRequest.tenderTypes).containsExactly(CARD);
    assertThat(updatedRequest.autoReturnMillis).isEqualTo(4_000);
    assertThat(updatedRequest.locationId).isEqualTo("location");
    assertThat(updatedRequest.requestMetadata).isEqualTo("metadata");
    assertThat(updatedRequest.customerId).isEqualTo("customerId");
    assertThat(updatedRequest.note).isEqualTo("note");
  }

  private String longNote() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 501; i++) {
      sb.append("m");
    }
    return sb.toString();
  }
}
