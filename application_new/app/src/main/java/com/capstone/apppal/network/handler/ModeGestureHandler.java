package com.capstone.apppal.network.handler;

import android.util.Log;

import com.capstone.apppal.VO.GestureType;
import com.capstone.apppal.utils.GlobalState;

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
        if (prevGesture == null || prevGesture != currGesture) {
            prevGesture = currGesture;
            freq = 0;
        } else {
            freq += 1;
        }

        Log.e("Detecting mode", "?? " + prevGesture);
        if (freq == STANDARD_FREQ) {
            GlobalState.currentGesture = prevGesture;
            Log.e("Detecting mode", "Stack detected:: " + prevGesture);
            return false;
        }
        return true;
    }
}
