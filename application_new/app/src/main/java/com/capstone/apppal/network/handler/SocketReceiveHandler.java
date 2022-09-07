package com.capstone.apppal.network.handler;

import android.util.Log;

import com.capstone.apppal.VO.GestureType;
import com.capstone.apppal.utils.GlobalState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;

public class SocketReceiveHandler {
  private static boolean isDecidingMenuStage = false;

  public SocketReceiveHandler() {
    receiveHandler();
  }

  private void receiveHandler() {
    while (true) {
      String res = null;
      try {
        StringBuffer sb = new StringBuffer();
        int temp = GlobalState.is.read();
        while (temp > 0 && temp != 125) {
          sb.append((char) temp);
          temp = GlobalState.is.read();
        }
        sb.append((char) 125);
        res = sb.toString();
        JSONObject resData = new JSONObject(res);
        switch (resData.get("function").toString()) {
          case "gesture":
            switch (resData.get("data").toString()) {
              case "pen":
                stackGesture(GestureType.PEN);
                break;
              case "hold":
                stackGesture(GestureType.HOLD);
                break;
              case "mask":
                stackGesture(GestureType.MASK);
                break;
            }
            break;
          default:
            break;
        }
      } catch (IOException | JSONException e) {
        e.printStackTrace();
      }
    }
  }

  private static void stackGesture(GestureType nowGesture) {
    GlobalState.listGesture.add(nowGesture);
    if (GlobalState.listGesture.size() > 6) {
      GlobalState.listGesture.remove(0);
//            if (isDecidingMenuStage) {
//                isDecidingMenuStage = ModeGestureHandler.detectGesture();
//            } else {
//                if (Collections.frequency(GlobalState.listGesture, GlobalState.MENU_GESTURE) >= 7) {
//                    isDecidingMenuStage = true;
//                }
//            }
      if (Collections.frequency(GlobalState.listGesture, nowGesture) >= 5) {
        GlobalState.currentGesture = nowGesture;
        GlobalState.listGesture.clear();
        Log.e("제스쳐 결정", "결정된 제스쳐:: " + GlobalState.currentGesture);
      }
    }
  }
}
