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

import java.util.Locale;

/** Internal helper class. */
class RegisterSdkHelper {

  static <T> T nonNull(T t, String name) {
    if (t == null) {
      throw new NullPointerException(name + " must not be null");
    }
    return t;
  }

  static String bytesToHexString(byte[] bytes) {
    // Capacity should be (bytes.length * 3 - 1), but this avoids negative case.
    StringBuilder hex = new StringBuilder(bytes.length * 3);
    int len = bytes.length;
    for (int i = 0; i < len; i++) {
      int lo = bytes[i] & 0xff;
      if (lo < 0x10) {
        hex.append('0');
      }
      hex.append(Integer.toHexString(lo).toUpperCase(Locale.US));
      if (i != len - 1) {
        hex.append(':');
      }
    }
    return hex.toString();
  }

  private RegisterSdkHelper() {
    throw new AssertionError();
  }
}
