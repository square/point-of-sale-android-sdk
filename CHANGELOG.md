Change Log
==========

## Version 1.3

_2016-12-01

* Supported in **Register 4.54 and above.**
* Adds support for Web API

## Version 1.2

_2016-11-2_

* Supported in **Register 4.52 and above.**
  * Prior to v4.52, Square Register will return an `UNSUPPORTED_API_VERSION` error.
  * Prior to v4.48, Square Register will return an `INVALID_REQUEST` error
* Deprecated the `UNAUTHORIZED_CLIENT_ID` error type. OAuth is no longer required for Register API for Android.

## Version 1.1

_2016-09-21_

* Supported in **Register 4.48 and above.**
  * Prior to v4.48, Square Register will return an `INVALID_REQUEST` error.
* Support for split tender transactions: transactions completed using multiple tender instruments.
  * Deprecated the `INSUFFICIENT_CARD_BALANCE` error type, a transaction can be completed as a
split tender if a card has insufficient balance.
* Support for Square Prepaid Gift Cards transactions, as part of the `CARD` tender type.
  * Deprecated the `GIFT_CARDS_NOT_SUPPORTED` error type.
* New `CARD_ON_FILE` tender type to allow charging a previously-stored card instrument.
* Support for linking customers to transactions
  * See `ChargeRequest.Builder.customerId()`.
  * New `CUSTOMER_MANAGEMENT_NOT_SUPPORTED` error type returned if the merchant account does not
support Customer Management.
  * New `ERROR_INVALID_CUSTOMER_ID` error type.
* For merchants using Employee Management:
  * New `NO_EMPLOYEE_LOGGED_IN` error type.
  * v1.0 returns `NO_USER_LOGGED_IN`.

## Version 1.0

_2016-05-25_

* Initial launch
