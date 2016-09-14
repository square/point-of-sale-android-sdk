package com.example.owensbikes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.util.Locale;

/**
 * View for the boxes on top of the bike image  on the main screen that display the first
 * five modifier type items and their selected options.
 */
public class BikeModifierView extends LinearLayout {

  private TextView modifierTitle;
  // Text switcher allows for easy animation when a modifier is updated.
  private TextSwitcher textSwitcher;

  public BikeModifierView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    modifierTitle = (TextView) findViewById(R.id.bike_image_modifier_title);
    textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
    textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
      @Override public View makeView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.modifier_value_textview, null);
      }
    });
  }

  public void setModifier(final BikeItem modifier, Option option) {
    modifierTitle.setText(modifier.name.toUpperCase(Locale.getDefault()));
    textSwitcher.setText(option.name);
  }

  public void updateSelectedOption(BikeItem m, Option option) {
    textSwitcher.setText(option.name);
  }
}
