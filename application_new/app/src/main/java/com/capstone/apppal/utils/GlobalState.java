package com.capstone.apppal.utils;

import com.capstone.apppal.VO.CoordinateInfo;
import com.capstone.apppal.VO.GestureType;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GlobalState {
  public static ArrayList<ArrayList<CoordinateInfo>> gestureTrackings = new ArrayList<>();
  public final static String PYTHON_SERVER_URL = "124.197.210.234";
  public final static int GESTURE_SOCKET_PORT = 4000;
  public final static GestureType MENU_GESTURE = GestureType.MASK;
  public static InputStreamReader is;
  public static OutputStreamWriter os;
  public static GestureType currentGesture;
  public static ArrayList<GestureType> listGesture = new ArrayList<>();
}
