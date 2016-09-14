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
