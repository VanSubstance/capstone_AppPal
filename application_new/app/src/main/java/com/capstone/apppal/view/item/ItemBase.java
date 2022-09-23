package com.capstone.apppal.view.item;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.apppal.R;

import java.util.HashMap;

public class ItemBase extends ConstraintLayout {
  private TextView mTitleText;
  private TextView mDescText;
  private HashMap<String, Object> data;

  public ItemBase(Context context) {
    super(context);
    init();
  }

  public ItemBase(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ItemBase(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.item_base, this);

    mTitleText = findViewById(R.id.text_title);
    mDescText = findViewById(R.id.text_desc);
  }

  public void setTitleText(String text) {
    Log.e("TAG", "setData: 메인::: " + text);
    mTitleText.setText(text);
  }

  public void setDescText(String text) {
    Log.e("TAG", "setData: 보조::: " + text);
    mDescText.setText(text);
  }

  public Object getData() {
    return data;
  }

  public void setData(HashMap<String, Object> data) {
    this.data = data;
    setTitleText((String) data.get("title"));
    setDescText((String) data.get("desc"));
  }
}
