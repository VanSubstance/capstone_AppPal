package com.example.apppal.Handler;

import static com.example.apppal.Storage.GlobalState.is;

import android.util.Log;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.VO.GestureType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SocketReceiveHandler {
    public SocketReceiveHandler() {
        receiveHandler();
    }
    private void receiveHandler() {
        while (true) {
            String res = null;
            try {
                StringBuffer sb = new StringBuffer();
                int temp = is.read();
                while(temp > 0 && temp != 125) {
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
                                Log.e("Gesture Recognition", "Current Gesture:: PEN");
                                GlobalState.currentGesture = GestureType.PEN;
                                break;
                            case "hold":
                                Log.e("Gesture Recognition", "Current Gesture:: HOLD");
                                GlobalState.currentGesture = GestureType.HOLD;
                                break;
                            case "mask":
                                Log.e("Gesture Recognition", "Current Gesture:: MASK");
                                GlobalState.currentGesture = GestureType.MASK;
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
}
