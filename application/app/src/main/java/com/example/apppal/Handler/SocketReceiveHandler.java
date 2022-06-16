package com.example.apppal.Handler;

import static com.example.apppal.Storage.GlobalState.is;
import static com.example.apppal.Storage.GlobalState.listGesture;

import android.os.Message;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.Utils;
import com.example.apppal.VO.GestureType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class SocketReceiveHandler {
    private static boolean isDecidingMenuStage = false;
    private static int loadingTimer = 0;

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

    private static Timer timer = new Timer();
    private static int secondLeft = 0;

    private static void stackGesture(GestureType nowGesture) {
        listGesture.add(nowGesture);
        if (listGesture.size() > 10) {
            listGesture.remove(0);
            if (isDecidingMenuStage) {
                if (secondLeft == 0)
                    isDecidingMenuStage = ModeGestureHandler.detectGesture();
            } else {
                if (Collections.frequency(listGesture, Utils.MENU_GESTURE) >= 7) {
                    isDecidingMenuStage = true;
                    secondLeft = 3;
                    TimerTask stopWatch = new TimerTask() {
                        @Override
                        public void run() {
                            Message handler = GlobalState.announceHandler.obtainMessage();
                            handler.what = Utils.TEXT_ANNOUNCE;
                            if (secondLeft != 0) {
                                secondLeft--;
                                handler.obj = "detecting initialized in " + secondLeft + "...";
                            } else {
                                handler.obj = "detecting gesture...";
                                this.cancel();
                            }
                            handler.sendToTarget();
                        }
                    };
                    timer.schedule(stopWatch, 0, 1000);
                }
            }
        }
    }
}
