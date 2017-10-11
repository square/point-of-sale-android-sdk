Change Log
==========
## Version 3.0

* Added new Transaction object to a ChargeRequest.Success containing details about
  a successful transaction.
* Renamed all Charge -> Transaction
* Auto return is now a boolean parameter. If enabled, POS will auto-return to the application
  after 2.0 seconds.
* Renamed the "requestMetadata" parameter to "state".

## Version 2.0

_2017-05-30_

* Supported in **Point of Sale 4.64 and above.**
  * Renamed "Register" to "Point of Sale".
  * Point of Sale API v2.0 will only work with Point of Sale v4.64 and above.
  * Point of Sale API v1.2 will continue to work with Point of Sale v4.52 and above (including 4.64).
  * [Medium blog post](https://medium.com/square-corner-blog/squares-register-api-is-now-point-of-sale-api-a9956032c32a) announcement
* Renamed the package from `com.squareup.sdk.register` to `com.squareup.sdk.pos`.
* Changed class names of the form `Register*` to `Pos*`.
* Changed method names of the form `*Register*` to `*PointofSale*`.

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
