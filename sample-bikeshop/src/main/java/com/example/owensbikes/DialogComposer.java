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

package com.example.owensbikes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import com.squareup.sdk.pos.PosClient;

/**
 * Class for displaying user-facing dialogs.
 */
public class DialogComposer {

  private static final String POINT_OF_SALE_PACKAGE = "com.squareup";

  private final MainActivity activity;
  private final PosClient posClient;

  DialogComposer(MainActivity activity, PosClient posClient) {
    this.activity = activity;
    this.posClient = posClient;
  }

  public void showErrorDialog(int titleResId, int messageResId) {
    AlertDialog.Builder errorAlertBuilder = new AlertDialog.Builder(activity).setTitle(titleResId)
        .setMessage(messageResId)
        .setPositiveButton(activity.getString(R.string.ok), null);
    errorAlertBuilder.show();
  }

  public void showErrorDialogWithRetry(int titleResId, int messageResId) {
    new AlertDialog.Builder(activity).setTitle(titleResId)
        .setMessage(messageResId)
        .setNegativeButton(activity.getString(R.string.ok), null)
        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            activity.checkout();
          }
        })
        .show();
  }

  public void showPointOfSaleUninstalledDialog() {
    new AlertDialog.Builder(activity).setTitle(R.string.error_install_point_of_sale)
        .setMessage(activity.getString(R.string.error_install_point_of_sale_message))
        .setPositiveButton(activity.getString(R.string.install_point_of_sale_confirm),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                posClient.openPointOfSalePlayStoreListing();
              }
            })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  public void showTransactionInProgressDialog() {
    new AlertDialog.Builder(activity).setTitle(R.string.error_transaction_in_progress)
        .setMessage(R.string.error_transaction_in_progress_message)
        .setPositiveButton(R.string.open_point_of_sale, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            // Start Point of Sale as if opened from the Android launcher,
            // which should open to the transaction in progress.
            PackageManager packageManager = activity.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(POINT_OF_SALE_PACKAGE);
            activity.startActivity(intent);
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  public void showUpdatePointOfSaleDialog() {
    new AlertDialog.Builder(activity).setTitle(R.string.update_point_of_sale_title)
        .setMessage(R.string.update_point_of_sale_message)
        .setPositiveButton(activity.getString(R.string.install_point_of_sale_confirm),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                posClient.openPointOfSalePlayStoreListing();
              }
            })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }
}
