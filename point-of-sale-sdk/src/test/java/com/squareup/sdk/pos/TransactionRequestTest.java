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

import static com.squareup.sdk.pos.TransactionRequest.TenderType.CARD_FROM_READER;
import static com.squareup.sdk.pos.TransactionRequest.TenderType.CARD_ON_FILE;
import static com.squareup.sdk.pos.TransactionRequest.TenderType.CASH;
import static com.squareup.sdk.pos.TransactionRequest.TenderType.SQUARE_GIFT_CARD;
import static com.squareup.sdk.pos.TransactionRequest.TenderType.KEYED_IN_CARD;
import static com.squareup.sdk.pos.TransactionRequest.TenderType.OTHER;
import static com.squareup.sdk.pos.CurrencyCode.CAD;
import static com.squareup.sdk.pos.CurrencyCode.USD;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRequestTest {

  @Test(expected = IllegalArgumentException.class) public void negativeAmountThrows() {
    new TransactionRequest.Builder(-2, USD);
  }

  @Test(expected = NullPointerException.class) public void nullCurrencyThrows() {
    //noinspection ConstantConditions
    new TransactionRequest.Builder(1_00, null);
  }

  @Test public void requestHasAllTenderTypesByDefault() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).build();
    assertThat(request.tenderTypes).containsOnly(CARD_FROM_READER, SQUARE_GIFT_CARD, KEYED_IN_CARD,
        CARD_ON_FILE, CASH, OTHER);
  }

  @Test public void requestHasNoTimeoutByDefault() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).build();
    assertThat(request.autoReturn).isFalse();
  }

  @Test public void requestHasAmountAndCurrency() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).build();
    assertThat(request.totalAmount).isEqualTo(1_00);
    assertThat(request.currencyCode).isEqualTo(USD);
  }

  @Test(expected = IllegalArgumentException.class) public void noteTooLongThrows() {
    new TransactionRequest.Builder(1_00, USD).note(longNote());
  }

  @Test public void requestHasNote() {
    TransactionRequest request =
        new TransactionRequest.Builder(1_00, USD).note("Some Note").build();
    assertThat(request.note).isEqualTo("Some Note");
  }

  @Test public void requestHasAutoReturn() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).autoReturn(true).build();
    assertThat(request.autoReturn).isTrue();
  }

  @Test public void requestHasSkipReceipt() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).skipReceipt(true).build();
    assertThat(request.skipReceipt).isTrue();
  }

  @Test public void requestHasAllowSplitTender() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).allowSplitTender(true).build();
    assertThat(request.allowSplitTender).isTrue();
  }

  @Test public void requestHasEnforcedBusinessLocation() {
    TransactionRequest request =
        new TransactionRequest.Builder(1_00, USD).enforceBusinessLocation("location").build();
    assertThat(request.locationId).isEqualTo("location");
  }

  @Test public void requestHasState() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD).state("state").build();
    assertThat(request.state).isEqualTo("state");
  }

  @Test public void requestHasCustomerId() {
    TransactionRequest request =
        new TransactionRequest.Builder(1_00, USD).customerId("customerId").build();
    assertThat(request.customerId).isEqualTo("customerId");
  }

  @Test public void copyKeepingAmount() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD)
        .restrictTendersTo(CARD_FROM_READER)
        .autoReturn(false)
        .enforceBusinessLocation("location")
        .state("state")
        .customerId("customerId")
        .note("note")
        .build();
    TransactionRequest updatedRequest = request.newBuilder()
        .restrictTendersTo(CASH)
        .autoReturn(true)
        .enforceBusinessLocation("location2")
        .state("state2")
        .customerId("customerId2")
        .note("note2")
        .build();
    assertThat(updatedRequest.totalAmount).isEqualTo(1_00);
    assertThat(updatedRequest.currencyCode).isEqualTo(USD);
    assertThat(updatedRequest.tenderTypes).containsExactly(CASH);
    assertThat(updatedRequest.autoReturn).isTrue();
    assertThat(updatedRequest.locationId).isEqualTo("location2");
    assertThat(updatedRequest.state).isEqualTo("state2");
    assertThat(updatedRequest.customerId).isEqualTo("customerId2");
    assertThat(updatedRequest.note).isEqualTo("note2");
  }

  @Test public void copyUpdatingAmount() {
    TransactionRequest request = new TransactionRequest.Builder(1_00, USD)
        .restrictTendersTo(CARD_FROM_READER)
        .autoReturn(true)
        .enforceBusinessLocation("location")
        .state("state")
        .customerId("customerId")
        .note("note")
        .build();
    TransactionRequest updatedRequest = request.newBuilder(2_00, CAD).build();
    assertThat(updatedRequest.totalAmount).isEqualTo(2_00);
    assertThat(updatedRequest.currencyCode).isEqualTo(CAD);
    assertThat(updatedRequest.tenderTypes).containsExactly(CARD_FROM_READER);
    assertThat(updatedRequest.autoReturn).isTrue();
    assertThat(updatedRequest.locationId).isEqualTo("location");
    assertThat(updatedRequest.state).isEqualTo("state");
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
