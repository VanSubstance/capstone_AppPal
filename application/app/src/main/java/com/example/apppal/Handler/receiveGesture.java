package com.example.apppal.Handler;

import static com.example.apppal.Storage.GlobalState.is;

import android.util.Log;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.VO.GestureType;

import java.io.IOException;

public class receiveGesture {
    public void receiveHandler() {
        try {
            while (true) {
                Object rcvdData = is.readObject();
                Log.d("socket data", "receiveHandler: " + rcvdData);
                switch (rcvdData.toString()) {
                    case "pen":
                        GlobalState.currentGesture = GestureType.PEN;
                        break;
                    case "twoFinger":
                        GlobalState.currentGesture = GestureType.TWO_FINGER;
                        break;
                    case "mask":
                        GlobalState.currentGesture = GestureType.MASK;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
