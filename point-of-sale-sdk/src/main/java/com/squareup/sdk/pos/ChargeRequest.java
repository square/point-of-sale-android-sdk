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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CARD;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CARD_ON_FILE;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CASH;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_OTHER;
import static com.squareup.sdk.pos.PosSdkHelper.nonNull;
import static java.util.Collections.unmodifiableSet;

/**
 * Represents the details of a transaction to initiate with the Point of Sale API.
 * After building a {@code ChargeRequest} instance with {@link Builder#Builder(int, CurrencyCode)},
 * pass it to the {@link PosClient#createChargeIntent(ChargeRequest)} method to initiate
 * the transaction.
 *
 * @see PosSdk code sample
 */
public final class ChargeRequest {

  /** @see Builder#Builder(int, CurrencyCode) */
  public final int totalAmount;

  /** @see Builder#Builder(int, CurrencyCode) */
  @NonNull public final CurrencyCode currencyCode;

  /** @see Builder#restrictTendersTo(Collection) */
  @NonNull public final Set<TenderType> tenderTypes;

  /** @see Builder#note(String) */
  @Nullable public final String note;

  /** @see Builder#autoReturn(long, TimeUnit) */
  public final long autoReturnMillis;

  /** @see Builder#enforceBusinessLocation(String) */
  @Nullable public final String locationId;

  /** @see Builder#requestMetadata */
  @Nullable public final String requestMetadata;

  /** @see Builder#customerId(String) */
  @Nullable public final String customerId;

  ChargeRequest(Builder builder) {
    this.tenderTypes = unmodifiableSet(
        builder.tenderTypes.isEmpty() ? EnumSet.noneOf(TenderType.class)
            : EnumSet.copyOf(builder.tenderTypes));
    this.totalAmount = builder.totalAmount;
    this.currencyCode = builder.currencyCode;
    this.note = builder.note;
    this.autoReturnMillis = builder.autoReturnMillis;
    this.locationId = builder.locationId;
    this.requestMetadata = builder.requestMetadata;
    this.customerId = builder.customerId;
  }

  /** Creates a new {@link Builder} copied from {@link this} transaction. */
  public @NonNull Builder newBuilder() {
    return newBuilder(totalAmount, currencyCode);
  }

  /**
   * Creates a new {@link Builder} copied from {@link this} transaction, with a different amount.
   */
  public @NonNull Builder newBuilder(int totalAmount, CurrencyCode currencyCode) {
    return new Builder(totalAmount, currencyCode).restrictTendersTo(tenderTypes)
        .note(note)
        .autoReturn(autoReturnMillis, TimeUnit.MILLISECONDS)
        .enforceBusinessLocation(locationId)
        .requestMetadata(requestMetadata)
        .customerId(customerId);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChargeRequest)) {
      return false;
    }
    ChargeRequest that = (ChargeRequest) o;
    if (totalAmount != that.totalAmount) {
      return false;
    }
    if (autoReturnMillis != that.autoReturnMillis) {
      return false;
    }
    if (!tenderTypes.equals(that.tenderTypes)) {
      return false;
    }
    if (currencyCode != that.currencyCode) {
      return false;
    }
    if (note != null ? !note.equals(that.note) : that.note != null) {
      return false;
    }
    if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) {
      return false;
    }
    if (requestMetadata != null ? !requestMetadata.equals(that.requestMetadata)
        : that.requestMetadata != null) {
      return false;
    }
    if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) {
      return false;
    }
    return true;
  }

  @Override public int hashCode() {
    int result = tenderTypes.hashCode();
    result = 31 * result + totalAmount;
    result = 31 * result + currencyCode.hashCode();
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (int) (autoReturnMillis ^ (autoReturnMillis >>> 32));
    result = 31 * result + (locationId != null ? locationId.hashCode() : 0);
    result = 31 * result + (requestMetadata != null ? requestMetadata.hashCode() : 0);
    result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
    return result;
  }

  /** A flexible builder to create a {@link ChargeRequest}. */
  public static final class Builder {

    final Set<TenderType> tenderTypes;
    final int totalAmount;
    @NonNull final CurrencyCode currencyCode;
    @Nullable String note;
    long autoReturnMillis;
    @Nullable String locationId;
    @Nullable String requestMetadata;
    @Nullable String customerId;

    /**
     * @param totalAmount Amount to charge. Point of Sale might add taxes and / or a tip on top,
     * depending on the user account configuration. Smallest divisible unit of currency for a given
     * locale, scaled by the default number of decimal places for the currency.
     * For example, totalAmount = 100 in USD means $1.00.
     * @param currencyCode {@link CurrencyCode} representing ISO-4217 currency codes. Square
     * Point of Sale will ensure that the passed in currency code matches the currency of the user
     * logged in to Point of Sale.
     * @throws IllegalArgumentException if totalAmount is negative.
     * @throws NullPointerException if currencyCode is null.
     */
    public Builder(int totalAmount, @NonNull CurrencyCode currencyCode) {
      if (totalAmount < 0) {
        throw new IllegalArgumentException("totalAmount must be non-negative");
      }
      this.totalAmount = totalAmount;
      this.currencyCode = nonNull(currencyCode, "currencyCode");
      tenderTypes = EnumSet.allOf(TenderType.class);
      autoReturnMillis = PosApi.AUTO_RETURN_NO_TIMEOUT;
    }

    /**
     * <p>Restricts the payment methods the merchant can accept for the transaction.
     *
     * <p>If you don't provide this value, all supported payment methods are allowed.
     *
     * @param tenderTypes The payment methods that the merchant can accept.
     * @return This builder to allow chaining of builder method calls.
     * @throws NullPointerException if tenderTypes is null
     * @throws IllegalArgumentException is tenderTypes is empty
     */
    public @NonNull ChargeRequest.Builder restrictTendersTo(
        @NonNull Collection<TenderType> tenderTypes) {
      nonNull(tenderTypes, "tenderTypes");
      if (tenderTypes.isEmpty()) {
        throw new IllegalArgumentException("Please restrict to at least one TenderType.");
      }
      this.tenderTypes.clear();
      this.tenderTypes.addAll(tenderTypes);
      return this;
    }

    /** @see #restrictTendersTo(Collection) */
    public @NonNull ChargeRequest.Builder restrictTendersTo(@NonNull TenderType... tenderTypes) {
      nonNull(tenderTypes, "tenderTypes");
      if (tenderTypes.length == 0) {
        throw new IllegalArgumentException("Please restrict to at least one TenderType.");
      }
      this.tenderTypes.clear();
      Collections.addAll(this.tenderTypes, tenderTypes);
      return this;
    }

    /**
     * <p>Specifies a note to associate with a processed transaction.
     *
     * <p>This note is included in the {@code itemizations} field of {@code Payment} objects
     * returned
     * by the List Payments and Retrieve Payment endpoints of the Square Connect API. It's also
     * included on all paper tickets and receipts associated with the transaction.
     *
     * @param note The note to associate with the transaction.
     * @return This builder to allow chaining of builder method calls.
     * @throws IllegalArgumentException if the note is longer than 500 characters.
     */
    public @NonNull ChargeRequest.Builder note(@Nullable String note) {
      if (note != null && note.length() > PosApi.NOTE_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "note character length must be less than " + PosApi.NOTE_MAX_LENGTH);
      }
      this.note = note;
      return this;
    }

    /**
     * <p>After a transaction completes, Square Point of Sale automatically returns
     * to your app after the timeout you provide to this method.
     *
     * @param timeout The timeout to set, in the provided unit, or {@link
     * PosApi#AUTO_RETURN_NO_TIMEOUT}. If you specify a timeout, it must be
     * between 3.2 seconds and 10 seconds.
     * @param unit the {@link TimeUnit} for the passed in timeout value. May be null if timeout is
     * {@link PosApi#AUTO_RETURN_NO_TIMEOUT}
     * @return This builder to allow chaining of builder method calls.
     * @throws IllegalArgumentException if timeout is not between {@link
     * PosApi#AUTO_RETURN_TIMEOUT_MIN_MILLIS} and
     * {@link PosApi#AUTO_RETURN_TIMEOUT_MAX_MILLIS}.
     */
    public @NonNull ChargeRequest.Builder autoReturn(long timeout, TimeUnit unit) {
      long autoReturnMillis;
      if (timeout != PosApi.AUTO_RETURN_NO_TIMEOUT) {
        autoReturnMillis = unit.toMillis(timeout);
        nonNull(unit, "unit");
        if (autoReturnMillis < PosApi.AUTO_RETURN_TIMEOUT_MIN_MILLIS) {
          throw new IllegalArgumentException(
              "timeout should be at least " + PosApi.AUTO_RETURN_TIMEOUT_MIN_MILLIS);
        }
        if (autoReturnMillis > PosApi.AUTO_RETURN_TIMEOUT_MAX_MILLIS) {
          throw new IllegalArgumentException(
              "timeout should be less than " + PosApi.AUTO_RETURN_TIMEOUT_MAX_MILLIS);
        }
      } else {
        autoReturnMillis = PosApi.AUTO_RETURN_NO_TIMEOUT;
      }
      this.autoReturnMillis = autoReturnMillis;
      return this;
    }

    /**
     * Requires a transaction to be processed by a particular location of a
     * merchant's business.
     *
     * @param locationId If provided, this ID must correspond to whichever location
     * is currently logged in to Square Point of Sale. Otherwise, Square Point of Sale will
     * respond with {@link ErrorCode#ILLEGAL_LOCATION_ID}. If you don't provide
     * this value, the payment will be processed by whichever location is logged
     * in to Square Point of Sale.
     * @return This builder to allow chaining of builder method calls.
     */
    public @NonNull ChargeRequest.Builder enforceBusinessLocation(@Nullable String locationId) {
      this.locationId = locationId;
      return this;
    }

    /**
     * Optional request metadata that Square Point of Sale will return in its response, as
     * {@link Success#requestMetadata} or {@link Error#requestMetadata}. This metadata is currently
     * not sent to Square servers.
     *
     * @param requestMetadata The request metadata, or null.
     * @return This builder to allow chaining of builder method calls.
     */
    public @NonNull ChargeRequest.Builder requestMetadata(@Nullable String requestMetadata) {
      this.requestMetadata = requestMetadata;
      return this;
    }

    /**
     * Optional customer id to associate the sale to a specific customer.
     *
     * @param customerId the customer id, see the online documentation on <a
     * href="https://docs.connect.squareup.com/articles/saving-customer-information/">saving
     * customer information</a>.
     * @return This builder to allow chaining of builder method calls.
     */
    public @NonNull ChargeRequest.Builder customerId(@Nullable String customerId) {
      this.customerId = customerId;
      return this;
    }

    /**
     * Constructs a {@link ChargeRequest} from the current state of this builder.
     */
    public @NonNull ChargeRequest build() {
      return new ChargeRequest(this);
    }
  }

  /**
   * Contains values returned by Square Point of Sale after a successfully processed
   * transaction.
   */
  public static class Success {

    /**
     * The device-generated ID of the transaction.
     *
     * Transaction objects returned by the Connect API's ListTransactions endpoint
     * include this value in the {@code client_id} field when applicable. You
     * cannot currently filter results by the value of this field, however you
     * can match against it to obtain a transaction's full details when
     * {@link #serverTransactionId} is null.
     */
    @NonNull public final String clientTransactionId;

    /**
     * The server-generated ID of the transaction, if available. This value is
     * {@code null} if the created transaction had not yet been assigned an ID
     * by Square servers before Square Point of Sale returned to your app. This happens
     * most commonly for cash payments and payments processed in Square Point of Sale's
     * offline mode.
     */
    @Nullable public final String serverTransactionId;

    /**
     * This value matches the value you provided to the {@link Builder#requestMetadata(String)}
     * method when constructing the {@link ChargeRequest}, if any.
     */
    @Nullable public final String requestMetadata;

    public Success(@NonNull String clientTransactionId, @Nullable String serverTransactionId,
        @Nullable String requestMetadata) {
      this.clientTransactionId = clientTransactionId;
      this.serverTransactionId = serverTransactionId;
      this.requestMetadata = requestMetadata;
    }
  }

  /**
   * Contains values returned by Square Point of Sale after failing to process a
   * transaction.
   */
  public static class Error {

    /**
     * Indicates the type of error that occurred.
     *
     * @see ErrorCode
     */
    @NonNull public final ErrorCode code;

    /**
     * A debug string that describes the error that occurred.
     *
     * This string is provided for debugging purposes only. Do not rely on its
     * exact value, because it might change for future instances of the same error.
     */
    @NonNull public final String debugDescription;

    /**
     * This value matches the value you provided to the {@link Builder#requestMetadata(String)}
     * method when constructing the {@link ChargeRequest}, if any.
     */
    @Nullable public final String requestMetadata;

    public Error(@NonNull ErrorCode code, @NonNull String debugDescription,
        @Nullable String requestMetadata) {
      this.code = code;
      this.debugDescription = debugDescription;
      this.requestMetadata = requestMetadata;
    }
  }

  public enum ErrorCode {

    /** The Point of Sale API is not currently available. */
    DISABLED(PosApi.ERROR_DISABLED),

    /** The merchant account does not support Customer Management. */
    CUSTOMER_MANAGEMENT_NOT_SUPPORTED(PosApi.ERROR_CUSTOMER_MANAGEMENT_NOT_SUPPORTED),

    /**
     * The Customer Id is invalid. This could happen if the account logged in to Square Point of
     * Sale is different from the account from which the customer information was downloaded.
     */
    ERROR_INVALID_CUSTOMER_ID(PosApi.ERROR_INVALID_CUSTOMER_ID),

    /** @deprecated Starting with SDK 1.1, Square Point of Sale supports Square Prepaid Gift Cards. */
    @Deprecated GIFT_CARDS_NOT_SUPPORTED(PosApi.ERROR_GIFT_CARDS_NOT_SUPPORTED),

    /**
     * The provided location ID does not correspond to the location currently logged in to Square
     * Point of Sale.
     */
    ILLEGAL_LOCATION_ID(PosApi.ERROR_ILLEGAL_LOCATION_ID),

    /**
     * @deprecated Starting with SDK 1.1, Square Point of Sale supports split tender transactions,
     * so
     * a transaction can be completed as a split tender if a card has insufficient balance.
     */
    @Deprecated INSUFFICIENT_CARD_BALANCE(PosApi.ERROR_INSUFFICIENT_CARD_BALANCE),

    /**
     * The information provided in the transaction request was invalid (a required field might have
     * been missing or malformed).
     *
     * {@link Error#debugDescription} provides additional details.
     */
    INVALID_REQUEST(PosApi.ERROR_INVALID_REQUEST),

    /** Employee management is enabled but no employee is logged in to Square Point of Sale. */
    NO_EMPLOYEE_LOGGED_IN(PosApi.ERROR_NO_EMPLOYEE_LOGGED_IN),

    /**
     * Square Point of Sale was unable to validate the Point of Sale API request because the Android
     * device did not have an active network connection.
     */
    NO_NETWORK(PosApi.ERROR_NO_NETWORK),

    /**
     * Square Point of Sale did not return a transaction result. In only this case, any value that
     * you provided in {@link Builder#requestMetadata(String)} will not be returned.
     */
    NO_RESULT(PosApi.ERROR_NO_RESULT),

    /**
     * Another Square Point of Sale transaction is already in progress. The merchant should open
     * Square Point of Sale to complete or cancel the current transaction before attempting to
     * initiate a new one.
     */
    TRANSACTION_ALREADY_IN_PROGRESS(PosApi.ERROR_TRANSACTION_ALREADY_IN_PROGRESS),

    /** The merchant canceled the transaction. */
    TRANSACTION_CANCELED(PosApi.ERROR_TRANSACTION_CANCELED),

    /**
     * @deprecated Starting with SDK 1.2, the OAuth authorization flow is no longer required for
     * Point of Sale API, and this error will never be returned.
     */
    @Deprecated UNAUTHORIZED_CLIENT_ID(PosApi.ERROR_UNAUTHORIZED_CLIENT_ID),

    /**
     * An unexpected error occurred. Please contact <a href="mailto:developers@squareup.com">developers@squareup.com</a>
     * and include any code snippets and descriptions of your use case that might help diagnose the
     * issue.
     */
    UNEXPECTED(PosApi.ERROR_UNEXPECTED),

    /**
     * The installed version of Square Point of Sale doesn't support this version of the Point of
     * Sale SDK.
     * This is probably because the installed version of Square Point of Sale is out of date.
     */
    UNSUPPORTED_API_VERSION(PosApi.ERROR_UNSUPPORTED_API_VERSION),

    /**
     * The merchant tried to process the transaction with a credit card, but the merchant's Square
     * account has not yet been activated for card processing.
     */
    USER_NOT_ACTIVATED(PosApi.ERROR_USER_NOT_ACTIVATED),

    /** No user is currently logged in to Square Point of Sale. */
    USER_NOT_LOGGED_IN(PosApi.ERROR_USER_NOT_LOGGED_IN);

    private static final Map<String, ErrorCode> errorCodeByApiString = new LinkedHashMap<>();

    private final String apiCode;

    ErrorCode(String apiCode) {
      this.apiCode = apiCode;
    }

    static {
      for (ErrorCode errorCode : ErrorCode.values()) {
        errorCodeByApiString.put(errorCode.apiCode, errorCode);
      }
    }

    static ErrorCode parse(String apiErrorCode) {
      return errorCodeByApiString.get(apiErrorCode);
    }
  }

  /**
   * Possible forms of payment that a merchant can accept for a Point of Sale API transaction.
   *
   * @see Builder#restrictTendersTo(Collection)
   */
  public enum TenderType {

    /**
     * Allow Magstripe cards, Chip Cards, Keyed-In Cards, Contactless (NFC) Payments, Square
     * Prepaid Gift Cards.
     */
    CARD(EXTRA_TENDER_CARD),

    /** Allow Card On File transactions. */
    CARD_ON_FILE(EXTRA_TENDER_CARD_ON_FILE),

    /** Allow Cash transactions. Useful to keep all payment records in one place. */
    CASH(EXTRA_TENDER_CASH),

    /**
     * Allow Check, Third-Party Gift Cards, and Other Tender transactions. Useful to keep all
     * payment records in one place.
     */
    OTHER(EXTRA_TENDER_OTHER);

    String apiExtraName;

    TenderType(String apiExtraName) {
      this.apiExtraName = apiExtraName;
    }
  }
}
