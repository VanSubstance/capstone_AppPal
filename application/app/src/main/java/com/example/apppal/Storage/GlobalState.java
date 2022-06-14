package com.example.apppal.Storage;

import com.example.apppal.VO.GestureType;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GlobalState {
    public static GestureType currentGesture;
    public static ArrayList<GestureType> listGesture = new ArrayList<>();
    public static InputStreamReader is;
    public static OutputStreamWriter os;
}
