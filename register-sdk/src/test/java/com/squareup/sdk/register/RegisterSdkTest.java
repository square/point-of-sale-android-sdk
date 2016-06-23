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

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) //
@Config(manifest = Config.NONE) //
public class RegisterSdkTest {

  @Test(expected = NullPointerException.class) public void nullContextThrows() {
    //noinspection ConstantConditions
    RegisterSdk.createClient(null, "clientId");
  }

  @Test(expected = NullPointerException.class) public void nullClientIdThrows() {
    //noinspection ConstantConditions
    RegisterSdk.createClient(mock(Context.class), null);
  }

  @Test public void extractsApplicationContext() {
    Context context = mock(Context.class);
    when(context.getApplicationContext()).thenReturn(context);
    RegisterSdk.createClient(context, "clientId");
    verify(context).getApplicationContext();
  }
}
