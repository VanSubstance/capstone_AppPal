package com.example.apppal.Handler;

import android.util.Log;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.VO.CoordinateInfo;

import java.util.ArrayList;


public class DrawingToolHandler {
  public static void trackGesture(ArrayList<CoordinateInfo> handCoors) {
    switch (GlobalState.currentGesture) {
      case PEN:
        if (handCoors.size() > 8) {
          GlobalState.tempCoorList.add(handCoors.get(8));
        }
        break;
      case HOLD:
        break;
      default:
        break;
    }
  }
}
