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

package com.squareup.sdk.register;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.content.pm.PackageManager.GET_SIGNATURES;
import static com.squareup.sdk.register.RegisterSdkHelper.nonNull;

final class RealRegisterClient implements RegisterClient {

  private static final String SDK_VERSION =
      "register-sdk-" + BuildConfig.LIBRARY_VERSION + "-" + BuildConfig.GIT_SHA;
  private static final String API_VERSION = "v1.2";
  private static final String REGISTER_PACKAGE_NAME = "com.squareup";
  private static final Uri PLAY_STORE_WEB_URL =
      Uri.parse("https://play.google.com/store/apps/details?id=" + REGISTER_PACKAGE_NAME);
  private static final String REGISTER_FINGERPRINT =
      "EA:54:A3:62:C8:5B:F4:34:F2:9F:B6:B0:42:D8:3E:5C:7D:C3:8A:D3";

  private final Context context;
  private final String clientId;
  private final PackageManager packageManager;

  RealRegisterClient(Context context, String clientId) {
    this.context = context;
    this.clientId = clientId;
    packageManager = context.getPackageManager();
  }

  @NonNull @Override public Intent createChargeIntent(@NonNull ChargeRequest chargeRequest) {
    nonNull(chargeRequest, "chargeRequest");
    List<ResolveInfo> activities = queryChargeActivities();
    PackageInfo registerPackage = findRegisterWithHighestVersion(activities);
    return createPinnedChargeIntent(chargeRequest, registerPackage);
  }

  @Override public boolean isRegisterInstalled() {
    List<ResolveInfo> activities = queryChargeActivities();
    for (ResolveInfo activity : activities) {
      String packageName = activity.activityInfo.packageName;
      if (isRegister(packageName)) {
        return true;
      }
    }
    return false;
  }

  @Override public void launchRegister() {
    List<ResolveInfo> activities = queryChargeActivities();
    PackageInfo registerPackage = findRegisterWithHighestVersion(activities);
    Intent registerIntent = packageManager.getLaunchIntentForPackage(registerPackage.packageName);
    context.startActivity(registerIntent);
  }

  @Override public void openRegisterPlayStoreListing() {
    Uri uri = PLAY_STORE_WEB_URL;
    Intent playStoreIntent = new Intent(ACTION_VIEW, uri);
    //noinspection deprecation
    playStoreIntent.addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    context.startActivity(playStoreIntent);
  }

  @NonNull @Override public ChargeRequest.Success parseChargeSuccess(@NonNull Intent data) {
    nonNull(data, "data");
    return new ChargeRequest.Success(data.getStringExtra(RegisterApi.RESULT_CLIENT_TRANSACTION_ID),
        data.getStringExtra(RegisterApi.RESULT_SERVER_TRANSACTION_ID),
        data.getStringExtra(RegisterApi.RESULT_REQUEST_METADATA));
  }

  @NonNull @Override public ChargeRequest.Error parseChargeError(@NonNull Intent data) {
    nonNull(data, "data");
    return new ChargeRequest.Error(
        ChargeRequest.ErrorCode.parse(data.getStringExtra(RegisterApi.RESULT_ERROR_CODE)),
        data.getStringExtra(RegisterApi.RESULT_ERROR_DESCRIPTION),
        data.getStringExtra(RegisterApi.RESULT_REQUEST_METADATA));
  }

  private List<ResolveInfo> queryChargeActivities() {
    Intent intent = new Intent(RegisterApi.INTENT_ACTION_CHARGE);
    return packageManager.queryIntentActivities(intent, 0);
  }

  private PackageInfo findRegisterWithHighestVersion(List<ResolveInfo> activities) {
    PackageInfo registerPackage = null;
    for (ResolveInfo activity : activities) {
      String packageName = activity.activityInfo.packageName;
      if (!isRegister(packageName)) {
        continue;
      }
      PackageInfo packageInfo;
      try {
        packageInfo = packageManager.getPackageInfo(packageName, 0);
      } catch (PackageManager.NameNotFoundException e) {
        // Package was uninstalled in between list and getting package info.
        continue;
      }
      if (registerPackage == null || packageInfo.versionCode > registerPackage.versionCode) {
        registerPackage = packageInfo;
      }
    }
    if (registerPackage == null) {
      throw new ActivityNotFoundException("Square Register is not installed on this device.");
    }
    return registerPackage;
  }

  private Intent createPinnedChargeIntent(ChargeRequest chargeRequest,
      PackageInfo registerPackage) {
    Intent intent = new Intent(RegisterApi.INTENT_ACTION_CHARGE);
    intent.putExtra(RegisterApi.EXTRA_REGISTER_CLIENT_ID, clientId);
    intent.putExtra(RegisterApi.EXTRA_TOTAL_AMOUNT, chargeRequest.totalAmount);
    intent.putExtra(RegisterApi.EXTRA_NOTE, chargeRequest.note);
    intent.putExtra(RegisterApi.EXTRA_API_VERSION, API_VERSION);
    intent.putExtra(RegisterApi.EXTRA_SDK_VERSION, SDK_VERSION);
    intent.putExtra(RegisterApi.EXTRA_CURRENCY_CODE, chargeRequest.currencyCode.name());
    intent.putExtra(RegisterApi.EXTRA_REQUEST_METADATA, chargeRequest.requestMetadata);
    if (chargeRequest.customerId != null && chargeRequest.customerId.length() > 0) {
      intent.putExtra(RegisterApi.EXTRA_CUSTOMER_ID, chargeRequest.customerId);
    }

    ArrayList<String> tenderTypeExtra = new ArrayList<>();
    for (ChargeRequest.TenderType tenderType : chargeRequest.tenderTypes) {
      tenderTypeExtra.add(tenderType.apiExtraName);
    }

    intent.putExtra(RegisterApi.EXTRA_TENDER_TYPES, tenderTypeExtra);

    if (chargeRequest.locationId != null && chargeRequest.locationId.length() > 0) {
      intent.putExtra(RegisterApi.EXTRA_LOCATION_ID, chargeRequest.locationId);
    }
    if (chargeRequest.autoReturnMillis > RegisterApi.AUTO_RETURN_NO_TIMEOUT) {
      intent.putExtra(RegisterApi.EXTRA_AUTO_RETURN_TIMEOUT_MS, chargeRequest.autoReturnMillis);
    }
    intent.setPackage(registerPackage.packageName);
    return intent;
  }

  private boolean isRegister(String packageName) {
    return packageName.startsWith(REGISTER_PACKAGE_NAME) //
        && matchesExpectedFingerprint(packageName, REGISTER_FINGERPRINT);
  }

  @SuppressLint("PackageManagerGetSignatures")
  private boolean matchesExpectedFingerprint(String packageName, String expectedFingerprint) {
    PackageInfo packageInfo;
    try {
      // Potential Multiple Certificate Exploit
      // Improper validation of app signatures could lead to issues where a malicious app submits
      // itself to the Play Store with both its real certificate and a fake certificate and gains
      // access to functionality or information it shouldn't have due to another application only
      // checking for the fake certificate and ignoring the rest. We make sure to validate all
      // signatures returned by this method.
      // https://bluebox.com/technical/android-fake-id-vulnerability/
      packageInfo = packageManager.getPackageInfo(packageName, GET_SIGNATURES);
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }

    Signature[] signatures = packageInfo.signatures;
    if (signatures == null || signatures.length == 0) {
      return false;
    }

    CertificateFactory certificateFactory;
    try {
      certificateFactory = CertificateFactory.getInstance("X509");
    } catch (CertificateException e) {
      return false;
    }
    MessageDigest sha1;
    try {
      sha1 = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      return false;
    }
    for (Signature signature : signatures) {
      byte[] signatureBytes = signature.toByteArray();
      InputStream input = new ByteArrayInputStream(signatureBytes);
      X509Certificate certificate;
      try {
        certificate = (X509Certificate) certificateFactory.generateCertificate(input);
      } catch (CertificateException e) {
        return false;
      }
      byte[] encodedCertificate;
      try {
        encodedCertificate = certificate.getEncoded();
      } catch (CertificateEncodingException e) {
        return false;
      }
      byte[] publicKey = sha1.digest(encodedCertificate);
      String actualFingerprint = RegisterSdkHelper.bytesToHexString(publicKey);

      // If any of the embedded certificates is not on the list of authorized fingerprints for
      // this package, we error out.
      if (!expectedFingerprint.equals(actualFingerprint)) {
        return false;
      }
    }
    return true;
  }
}
