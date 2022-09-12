package com.capstone.apppal.network.handler;

import android.util.Log;

import com.capstone.apppal.VO.FunctionType;
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
//        Log.e("데이터", ":: 소켓 -> 앱 :: " + resData);
        switch (resData.get("function").toString()) {
          case "gesture":
            switch (resData.get("data").toString()) {
              case "ONE":
                stackGesture(GestureType.ONE);
                break;
              case "TWO":
                stackGesture(GestureType.TWO);
                break;
              case "THREE":
                stackGesture(GestureType.THREE);
                break;
              case "FOUR":
                stackGesture(GestureType.FOUR);
                break;
              case "FIVE":
                stackGesture(GestureType.FIVE);
                break;
              case "ZERO":
                stackGesture(GestureType.ZERO);
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
    if (GlobalState.listGesture.size() > GlobalState.GESTURE_HISTORY_SIZE) {
      GlobalState.listGesture.remove(0);
      if (Collections.frequency(GlobalState.listGesture, nowGesture) >= GlobalState.GESTURE_DECISION_SIZE) {
        switch (GlobalState.currentFunction) {
          case DRAWING:
            switch (nowGesture) {
              case FIVE:
                GlobalState.currentFunction = FunctionType.MENU;
                break;
              default:
                break;
            }
            break;
          case MENU:
            switch (nowGesture) {
              case ZERO:
                GlobalState.currentFunction = FunctionType.DRAWING;
                break;
              default:
                break;
            }
            break;
          default:
            break;
        }
        GlobalState.listGesture.clear();
//        Log.e("기능 결정", "현재 기능 :: " + GlobalState.currentFunction);
      }
    }
  }
}
