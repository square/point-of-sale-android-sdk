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
import com.squareup.sdk.pos.transaction.Transaction;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CARD_FROM_READER;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CARD_ON_FILE;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_CASH;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_GIFT_CARD;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_KEYED_IN_CARD;
import static com.squareup.sdk.pos.PosApi.EXTRA_TENDER_OTHER;
import static com.squareup.sdk.pos.internal.PosSdkHelper.nonNull;
import static java.util.Collections.unmodifiableSet;

/**
 * Represents the details of a transaction to initiate with the Point of Sale API.
 * After building a {@code TransactionRequest} instance with
 * {@link Builder#Builder(int)}, pass it to the
 * {@link PosClient#createTransactionIntent(TransactionRequest)} method to initiate the transaction.
 *
 * @see PosSdk code sample
 */
public final class TransactionRequest {

  /** @see Builder#Builder(int) */
  public final int totalAmount;

  /** @see Builder#restrictTendersTo(Collection) */
  @NonNull public final Set<TenderType> tenderTypes;

  /** @see Builder#note(String) */
  @Nullable public final String note;

  /** @see Builder#autoReturn(boolean) */
  public final boolean autoReturn;

  /** @see Builder#enforceBusinessLocation(String) */
  @Nullable public final String locationId;

  /** @see Builder#state */
  @Nullable public final String state;

  /** @see Builder#customerId(String) */
  @Nullable public final String customerId;

  /** @see Builder#skipReceipt(boolean) */
  public final boolean skipReceipt;

  /** @see Builder#allowSplitTender(boolean) */
  public final boolean allowSplitTender;

  TransactionRequest(Builder builder) {
    this.tenderTypes = unmodifiableSet(
        builder.tenderTypes.isEmpty() ? EnumSet.noneOf(TenderType.class)
            : EnumSet.copyOf(builder.tenderTypes));
    this.totalAmount = builder.totalAmount;
    this.note = builder.note;
    this.autoReturn = builder.autoReturn;
    this.locationId = builder.locationId;
    this.state = builder.state;
    this.customerId = builder.customerId;
    this.skipReceipt = builder.skipReceipt;
    this.allowSplitTender = builder.allowSplitTender;
  }

  /** Creates a new {@link Builder} copied from {@link this} transaction. */
  public @NonNull Builder newBuilder() {
    return newBuilder(totalAmount);
  }

  /**
   * Creates a new {@link Builder} copied from {@link this} transaction, with a different amount.
   */
  public @NonNull Builder newBuilder(int totalAmount) {
    return new Builder(totalAmount) //
        .restrictTendersTo(tenderTypes)
        .note(note)
        .autoReturn(autoReturn)
        .skipReceipt(skipReceipt)
        .allowSplitTender(allowSplitTender)
        .enforceBusinessLocation(locationId)
        .state(state)
        .customerId(customerId);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TransactionRequest)) {
      return false;
    }
    TransactionRequest that = (TransactionRequest) o;
    if (totalAmount != that.totalAmount) {
      return false;
    }
    if (autoReturn != that.autoReturn) {
      return false;
    }
    if (!tenderTypes.equals(that.tenderTypes)) {
      return false;
    }
    if (note != null ? !note.equals(that.note) : that.note != null) {
      return false;
    }
    if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) {
      return false;
    }
    if (state != null ? !state.equals(that.state) : that.state != null) {
      return false;
    }
    if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) {
      return false;
    }
    if (skipReceipt != that.skipReceipt) {
      return false;
    }
    if (allowSplitTender != that.allowSplitTender) {
      return false;
    }
    return true;
  }

  @Override public int hashCode() {
    int result = tenderTypes.hashCode();
    result = 31 * result + totalAmount;
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (autoReturn ? 1 : 0);
    result = 31 * result + (locationId != null ? locationId.hashCode() : 0);
    result = 31 * result + (state != null ? state.hashCode() : 0);
    result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
    result = 31 * result + (skipReceipt ? 1 : 0);
    result = 31 * result + (allowSplitTender ? 1 : 0);
    return result;
  }

  /** A flexible builder to create a {@link TransactionRequest}. */
  public static final class Builder {

    final Set<TenderType> tenderTypes;
    final int totalAmount;
    @Nullable String note;
    @Nullable String locationId;
    @Nullable String state;
    @Nullable String customerId;
    boolean autoReturn;
    boolean skipReceipt;
    boolean allowSplitTender;

    /**
     * @param totalAmount Amount to charge. Point of Sale might add taxes and / or a tip on top,
     * depending on the user account configuration. Smallest divisible unit of currency for a given
     * locale, scaled by the default number of decimal places for the currency.
     * For example, totalAmount = 100 in USD means $1.00.
     * @throws IllegalArgumentException if totalAmount is negative.
     */
    public Builder(int totalAmount) {
      if (totalAmount < 0) {
        throw new IllegalArgumentException("totalAmount must be non-negative");
      }
      this.totalAmount = totalAmount;
      tenderTypes = EnumSet.allOf(TenderType.class);
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
    public @NonNull TransactionRequest.Builder restrictTendersTo(
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
    public @NonNull TransactionRequest.Builder restrictTendersTo(
        @NonNull TenderType... tenderTypes) {
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
    public @NonNull TransactionRequest.Builder note(@Nullable String note) {
      if (note != null && note.length() > PosApi.NOTE_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "note character length must be less than " + PosApi.NOTE_MAX_LENGTH);
      }
      this.note = note;
      return this;
    }

    /**
     * If the autoReturn parameter is set to true, Square Point of Sale will automatically
     * return to your application after 2.0 seconds on completion of a transaction.
     */
    public @NonNull TransactionRequest.Builder autoReturn(boolean enableAutoReturn) {
      this.autoReturn = enableAutoReturn;
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
    public @NonNull TransactionRequest.Builder enforceBusinessLocation(
        @Nullable String locationId) {
      this.locationId = locationId;
      return this;
    }

    /**
     * Optional state data that Square Point of Sale will return in its response, as
     * {@link Success#state} or {@link Error#state}. This data is not sent to Square
     * servers.
     *
     * @param state The state, or null.
     * @return This builder to allow chaining of builder method calls.
     */
    public @NonNull TransactionRequest.Builder state(@Nullable String state) {
      this.state = state;
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
    public @NonNull TransactionRequest.Builder customerId(@Nullable String customerId) {
      this.customerId = customerId;
      return this;
    }

    /**
     * Do not show the receipt screen to the buyer. If the buyer has previously linked their
     * payment method to their email address, he or she will still receive an email receipt.
     */
    public TransactionRequest.Builder skipReceipt(boolean enabled) {
      this.skipReceipt = enabled;
      return this;
    }

    /**
     * Enable the split tender payment flow, which is disabled by default.
     */
    public TransactionRequest.Builder allowSplitTender(boolean allowSplitTender) {
      this.allowSplitTender = allowSplitTender;
      return this;
    }

    /**
     * Constructs a {@link TransactionRequest} from the current state of this builder.
     */
    public @NonNull TransactionRequest build() {
      return new TransactionRequest(this);
    }
  }

  /**
   * Contains values returned by Square Point of Sale after a successfully processed
   * transaction.
   */
  public static class Success {

    /**
     * Represents the result of a transaction processed using the Square Point of Sale API.
     * See {@link Transaction}
     */
    @NonNull public final Transaction transaction;

    /**
     * This value matches the value you provided to the {@link Builder#state(String)}
     * method when constructing the {@link TransactionRequest}, if any.
     */
    @Nullable public final String state;

    public Success(Transaction transaction, @Nullable String state) {
      this.transaction = transaction;
      this.state = state;
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
     * This value matches the value you provided to the {@link Builder#state(String)}
     * method when constructing the {@link TransactionRequest}, if any.
     */
    @Nullable public final String state;

    public Error(@NonNull ErrorCode code, @NonNull String debugDescription,
        @Nullable String state) {
      this.code = code;
      this.debugDescription = debugDescription;
      this.state = state;
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
     * you provided in {@link Builder#state(String)} will not be returned.
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

    /** Allow Magstripe cards, Chip Cards, and Contactless (NFC) transactions. */
    CARD_FROM_READER(EXTRA_TENDER_CARD_FROM_READER),

    /** Allow Card On File transactions. */
    CARD_ON_FILE(EXTRA_TENDER_CARD_ON_FILE),

    /** Allow Square Gift Card transactions. */
    SQUARE_GIFT_CARD(EXTRA_TENDER_GIFT_CARD),

    /** Allow Card transactions without the card present. */
    KEYED_IN_CARD(EXTRA_TENDER_KEYED_IN_CARD),

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
