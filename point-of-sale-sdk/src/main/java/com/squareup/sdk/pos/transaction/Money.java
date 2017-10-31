package com.squareup.sdk.pos.transaction;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Represents an amount of money.
 */
@AutoValue //
public abstract class Money implements Parcelable {
  public static Money create(long amount, CurrencyCode currency) {
    return new AutoValue_Money(amount, currency);
  }

  /**
   * The amount of money, in the smallest denomination of the specified currency. For example, when
   * the currency is USD, the amount is in cents.
   *
   * Amounts are always non-negative.
   */
  public abstract long amount();

  /**
   * The type of currency, in ISO 4217 format.
   *
   * For example, the currency code for US dollars is USD.
   */
  public abstract CurrencyCode currency();

  public enum CurrencyCode {
    AED, AFN, ALL, AMD, ANG, AOA, ARS, AUD, AWG, AZN, BAM, BBD, BDT, BGN, BHD, BIF, BMD, BND, //
    BOB, BOV, BRL, BSD, BTN, BWP, BYR, BZD, CAD, CDF, CHE, CHF, CHW, CLF, CLP, CNY, COP, COU, //
    CRC, CUC, CUP, CVE, CZK, DJF, DKK, DOP, DZD, EGP, ERN, ETB, EUR, FJD, FKP, GBP, GEL, GHS, //
    GIP, GMD, GNF, GTQ, GYD, HKD, HNL, HRK, HTG, HUF, IDR, ILS, INR, IQD, IRR, ISK, JMD, JOD, //
    JPY, KES, KGS, KHR, KMF, KPW, KRW, KWD, KYD, KZT, LAK, LBP, LKR, LRD, LSL, LTL, LVL, LYD, //
    MAD, MDL, MGA, MKD, MMK, MNT, MOP, MRO, MUR, MVR, MWK, MXN, MXV, MYR, MZN, NAD, NGN, NIO, //
    NOK, NPR, NZD, OMR, PAB, PEN, PGK, PHP, PKR, PLN, PYG, QAR, RON, RSD, RUB, RWF, SAR, SBD, //
    SCR, SDG, SEK, SGD, SHP, SLL, SOS, SRD, SSP, STD, SVC, SYP, SZL, THB, TJS, TMT, TND, TOP, //
    TRY, TTD, TWD, TZS, UAH, UGX, USD, USN, USS, UYI, UYU, UZS, VEF, VND, VUV, WST, XAF, XAG, //
    XAU, XBA, XBB, XBC, XBD, XCD, XDR, XOF, XPD, XPF, XPT, XTS, XXX, YER, ZAR, ZMK, ZMW,
  }

  public static TypeAdapter<Money> typeAdapter(Gson gson) {
    return new AutoValue_Money.GsonTypeAdapter(gson);
  }
}
