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

import java.util.Collections;
import java.util.List;

/**
 * A BikeItem represents either a modification to the base bike or an added accessory. An item
 * should have at least one Option. For example, the MODIFIER type named "Frame Size" has an option
 * for each frame size, e.g "30 cm", "50 cm".
 **/
final class BikeItem {
  public enum Category {
    ACCESSORY, MODIFIER
  }

  public final String id;
  public final String name;
  public final Category category;
  public final List<Option> options;

  BikeItem(String id, String name, Category category, List<Option> options) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.options = Collections.unmodifiableList(options);
  }
}