package com.example.apppal.Storage;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.apppal.VO.GestureType;
import com.example.apppal.VO.CoordinateInfo;
import com.example.apppal.Utils;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GlobalState {
    public static GestureType currentGesture = GestureType.NONE;
    public static ArrayList<GestureType> listGesture = new ArrayList<>();
    public static InputStreamReader is;
    public static OutputStreamWriter os;
    public static TextView textAnnounce;

    public static Handler announceHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Utils.TEXT_ANNOUNCE:
                    textAnnounce.setText(msg.obj.toString());
                    break;
            }
        }
    };

    public static ArrayList<CoordinateInfo> tempCoorList = new ArrayList<>();
}
