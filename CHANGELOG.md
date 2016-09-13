Change Log
==========

## Version 1.1

_Not released yet._

* Supported in **Register 4.48 and above.**
  * Prior to v4.48, Square Register will return an `INVALID_REQUEST` error.
* Support for split tender transactions: transactions completed using multiple tender instruments.
  * Deprecated the `INSUFFICIENT_CARD_BALANCE` error type, a transaction can be completed as a
split tender if a card has insufficient balance.
* Support for Square Prepaid Gift Cards transactions, as part of the `CARD` tender type.
  * Deprecated the `GIFT_CARDS_NOT_SUPPORTED` error type.
* New `CARD_ON_FILE` tender type to allow charging a previously-stored card instrument.
* Support for linking customers to transactions
  * see `ChargeRequest.Builder.customerId()`.
  * New `CUSTOMER_MANAGEMENT_NOT_SUPPORTED` error type returned if the merchant account does not
support Customer Management.
  * New `ERROR_INVALID_CUSTOMER_ID` error type.
* For merchants using Employee Management:
  * New `NO_EMPLOYEE_LOGGED_IN` error type.
  * v1.0 returns `NO_USER_LOGGED_IN`.

## Version 1.0

_2016-05-25_

* Initial launch
