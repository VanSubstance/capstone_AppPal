package com.example.apppal.Handler;

import android.util.Log;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.VO.GestureType;

import java.util.ArrayList;

public class ModeGestureHandler {
    private static ArrayList<GestureType> targetList;
    private static GestureType prevGesture = null;
    private final static int STANDARD_FREQ = 7;
    private static int freq;

    public static boolean detectGesture() {
        targetList = GlobalState.listGesture;
        int res = 0;
        int cnt = 1;
        for (int i = 1; i < 10; i++) {
            if (targetList.get(i) == targetList.get(res)) {
                cnt++;
            } else {
                cnt--;
            }
        }

        GestureType currGesture = targetList.get(res);
        Log.e("Detecting mode", "?? " + targetList.get(res));
        if (currGesture == GestureType.NONE) return true;
        if (prevGesture == null || prevGesture != currGesture) {
            prevGesture = currGesture;
            freq = 0;
        } else {
            freq += 1;
        }

        if (freq == STANDARD_FREQ) {
            GlobalState.currentGesture = prevGesture;
            Log.e("Detecting mode", "Stack detected:: " + prevGesture);
            return false;
        }
        return true;
    }
}
