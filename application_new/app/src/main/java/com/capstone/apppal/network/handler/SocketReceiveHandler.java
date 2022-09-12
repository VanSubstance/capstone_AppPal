package com.capstone.apppal.network.handler;

import android.util.Log;

import com.capstone.apppal.DrawARActivity;
import com.capstone.apppal.R;
import com.capstone.apppal.VO.FunctionType;
import com.capstone.apppal.VO.GestureType;
import com.capstone.apppal.utils.GlobalState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;

public class SocketReceiveHandler {

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
                // 메뉴 열기
                GlobalState.currentFunction = FunctionType.MAIN_MENU;
                DrawARActivity.mMenuSelector.onClick(R.id.menu_button);
                break;
              default:
                break;
            }
            break;
          case MAIN_MENU:
            switch (nowGesture) {
              case ZERO:
                // 아무 변화 없이 메뉴 닫기
                GlobalState.currentFunction = FunctionType.DRAWING;
                DrawARActivity.mMenuSelector.closeChildren(null);
                break;
              case ONE:
                GlobalState.currentFunction = FunctionType.THICKNESS_MENU;
                DrawARActivity.mMenuSelector.onClick(R.id.brush_button);
                break;
              case TWO:
                GlobalState.currentFunction = FunctionType.COLOR_MENU;
                DrawARActivity.mMenuSelector.onClick(R.id.color_button);
                break;
              case THREE:
                GlobalState.currentFunction = FunctionType.TOOL_MENU;
                DrawARActivity.mMenuSelector.onClick(R.id.tool_button);
                break;
              default:
                break;
            }
            break;
          case TOOL_MENU:
            GlobalState.currentFunction = FunctionType.DRAWING;
            switch (nowGesture) {
              case ZERO:
                // 아무 변화 없이 메뉴 닫기
                DrawARActivity.mMenuSelector.closeChildren(null);
                break;
              case ONE:
                DrawARActivity.mMenuSelector.getToolSelector().onClick(R.id.tool_selection_erase);
                break;
              case TWO:
                DrawARActivity.mMenuSelector.getToolSelector().onClick(R.id.tool_selection_rect);
                break;
              case THREE:
                DrawARActivity.mMenuSelector.getToolSelector().onClick(R.id.tool_selection_cube);
                break;
              case FOUR:
                DrawARActivity.mMenuSelector.getToolSelector().onClick(R.id.tool_selection_line);
                break;
              case FIVE:
                DrawARActivity.mMenuSelector.getToolSelector().onClick(R.id.tool_selection_pen);
                break;
              default:
                break;
            }
            break;
          case COLOR_MENU:
            GlobalState.currentFunction = FunctionType.DRAWING;
            switch (nowGesture) {
              case ZERO:
                // 아무 변화 없이 메뉴 닫기
                DrawARActivity.mMenuSelector.closeChildren(null);
                break;
              case ONE:
                DrawARActivity.mMenuSelector.getColorSelector().onClick(R.id.color_selection_blue);
                break;
              case TWO:
                DrawARActivity.mMenuSelector.getColorSelector().onClick(R.id.color_selection_green);
                break;
              case THREE:
                DrawARActivity.mMenuSelector.getColorSelector().onClick(R.id.color_selection_red);
                break;
              case FOUR:
                DrawARActivity.mMenuSelector.getColorSelector().onClick(R.id.color_selection_black);
                break;
              case FIVE:
                DrawARActivity.mMenuSelector.getColorSelector().onClick(R.id.color_selection_white);
                break;
              default:
                break;
            }
            break;
          case THICKNESS_MENU:
            GlobalState.currentFunction = FunctionType.DRAWING;
            switch (nowGesture) {
              case ZERO:
                // 아무 변화 없이 메뉴 닫기
                DrawARActivity.mMenuSelector.closeChildren(null);
                break;
              case ONE:
                DrawARActivity.mMenuSelector.getBrushSelector().onClick(R.id.brush_selection_small);
                break;
              case TWO:
                DrawARActivity.mMenuSelector.getBrushSelector().onClick(R.id.brush_selection_medium);
                break;
              case THREE:
                DrawARActivity.mMenuSelector.getBrushSelector().onClick(R.id.brush_selection_large);
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
