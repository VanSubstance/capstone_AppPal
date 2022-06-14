package com.example.apppal.Handler;

import static com.example.apppal.Storage.GlobalState.is;
import static com.example.apppal.Storage.GlobalState.listGesture;

import android.util.Log;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.Utils;
import com.example.apppal.VO.GestureType;

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
                int temp = is.read();
                while (temp > 0 && temp != 125) {
                    sb.append((char) temp);
                    temp = is.read();
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
        listGesture.add(nowGesture);
        if (listGesture.size() > 10) {
            listGesture.remove(0);
            if (isDecidingMenuStage) {
                isDecidingMenuStage = ModeGestureHandler.detectGesture();
            } else {
                if (Collections.frequency(listGesture, Utils.MENU_GESTURE) >= 7) {
                    isDecidingMenuStage = true;
                }
            }
        }
    }
}
