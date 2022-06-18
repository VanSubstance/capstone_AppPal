package com.example.apppal.Renderer;

import android.content.Context;

import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;

public class BaseGIRenderer extends Hands {

  public BaseGIRenderer(Context context, HandsOptions options) {
    super(context, options);
  }
}
