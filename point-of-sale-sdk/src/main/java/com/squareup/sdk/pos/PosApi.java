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

/**
 * This class replicates constants used by Square Point of Sale for parsing Point of Sale API
 * transaction requests. These constants are used directly by the SDK. You do not
 * need to use them in your code.
 */
public final class PosApi {

  private static final String NAMESPACE = "com.squareup.pos.";

  public static final String INTENT_ACTION_CHARGE = NAMESPACE + "action.CHARGE";

  /**
   * API version number String extra. Uses semantic versioning.
   */
  public static final String EXTRA_API_VERSION = NAMESPACE + "API_VERSION";

  public static final String EXTRA_REQUEST_METADATA = NAMESPACE + "REQUEST_METADATA";

  public static final String EXTRA_SDK_VERSION = NAMESPACE + "SDK_VERSION";

  public static final String EXTRA_POINT_OF_SALE_CLIENT_ID = NAMESPACE + "CLIENT_ID";

  public static final String EXTRA_CURRENCY_CODE = NAMESPACE + "CURRENCY_CODE";

  public static final String EXTRA_TOTAL_AMOUNT = NAMESPACE + "TOTAL_AMOUNT";

  public static final String EXTRA_NOTE = NAMESPACE + "NOTE";

  public static final String EXTRA_LOCATION_ID = NAMESPACE + "LOCATION_ID";

  public static final String EXTRA_TENDER_TYPES = NAMESPACE + "TENDER_TYPES";

  public static final String EXTRA_TENDER_CARD = NAMESPACE + "TENDER_CARD";

  public static final String EXTRA_TENDER_CASH = NAMESPACE + "TENDER_CASH";

  public static final String EXTRA_TENDER_OTHER = NAMESPACE + "TENDER_OTHER";

  public static final String EXTRA_TENDER_CARD_ON_FILE = NAMESPACE + "TENDER_CARD_ON_FILE";

  public static final String EXTRA_AUTO_RETURN_TIMEOUT_MS = NAMESPACE + "AUTO_RETURN_TIMEOUT_MS";

  public static final String EXTRA_CUSTOMER_ID = NAMESPACE + "CUSTOMER_ID";

  public static final long AUTO_RETURN_NO_TIMEOUT = 0L;

  public static final long AUTO_RETURN_TIMEOUT_MIN_MILLIS = 3_200L;

  public static final long AUTO_RETURN_TIMEOUT_MAX_MILLIS = 10_000L;

  public static final int NOTE_MAX_LENGTH = 500;

  public static final String RESULT_CLIENT_TRANSACTION_ID = NAMESPACE + "CLIENT_TRANSACTION_ID";

  public static final String RESULT_SERVER_TRANSACTION_ID = NAMESPACE + "SERVER_TRANSACTION_ID";

  public static final String RESULT_REQUEST_METADATA = EXTRA_REQUEST_METADATA;

  public static final String RESULT_ERROR_CODE = NAMESPACE + "ERROR_CODE";

  public static final String RESULT_ERROR_DESCRIPTION = NAMESPACE + "ERROR_DESCRIPTION";

  public static final String ERROR_NO_EMPLOYEE_LOGGED_IN =
      NAMESPACE + "ERROR_NO_EMPLOYEE_LOGGED_IN";

  public static final String ERROR_GIFT_CARDS_NOT_SUPPORTED =
      NAMESPACE + "ERROR_GIFT_CARDS_NOT_SUPPORTED";

  public static final String ERROR_INVALID_REQUEST = NAMESPACE + "ERROR_INVALID_REQUEST";

  public static final String ERROR_DISABLED = NAMESPACE + "ERROR_DISABLED";

  public static final String ERROR_ILLEGAL_LOCATION_ID = NAMESPACE + "ERROR_ILLEGAL_LOCATION_ID";

  public static final String ERROR_INSUFFICIENT_CARD_BALANCE =
      NAMESPACE + "ERROR_INSUFFICIENT_CARD_BALANCE";

  public static final String ERROR_NO_RESULT = NAMESPACE + "ERROR_NO_RESULT";

  public static final String ERROR_NO_NETWORK = NAMESPACE + "ERROR_NO_NETWORK";

  public static final String ERROR_TRANSACTION_CANCELED = NAMESPACE + "ERROR_TRANSACTION_CANCELED";

  public static final String ERROR_TRANSACTION_ALREADY_IN_PROGRESS =
      NAMESPACE + "ERROR_TRANSACTION_ALREADY_IN_PROGRESS";

  public static final String ERROR_UNAUTHORIZED_CLIENT_ID =
      NAMESPACE + "ERROR_UNAUTHORIZED_CLIENT_ID";

  public static final String ERROR_UNEXPECTED = NAMESPACE + "ERROR_UNEXPECTED";

  public static final String ERROR_UNSUPPORTED_API_VERSION = NAMESPACE + "UNSUPPORTED_API_VERSION";

  public static final String ERROR_USER_NOT_LOGGED_IN = NAMESPACE + "ERROR_USER_NOT_LOGGED_IN";

  public static final String ERROR_USER_NOT_ACTIVATED = NAMESPACE + "ERROR_USER_NOT_ACTIVATED";

  public static final String ERROR_CUSTOMER_MANAGEMENT_NOT_SUPPORTED =
      NAMESPACE + "ERROR_CUSTOMER_MANAGEMENT_NOT_SUPPORTED";

  public static final String ERROR_INVALID_CUSTOMER_ID = NAMESPACE + "ERROR_INVALID_CUSTOMER_ID";

  private PosApi() {
    throw new AssertionError();
  }
}
