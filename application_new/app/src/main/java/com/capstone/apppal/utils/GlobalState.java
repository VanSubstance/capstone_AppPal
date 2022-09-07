package com.capstone.apppal.utils;

import android.util.DisplayMetrics;

import com.capstone.apppal.VO.CoordinateInfo;
import com.capstone.apppal.VO.GestureType;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

public class GlobalState {
  public static ArrayList<ArrayList<CoordinateInfo>> gestureTrackings = new ArrayList<>();
  public final static String PYTHON_SERVER_URL = "124.197.210.234";
  public final static int GESTURE_SOCKET_PORT = 4000;
  public final static GestureType MENU_GESTURE = GestureType.MASK;
  public static InputStreamReader is;
  public static OutputStreamWriter os;
  public static ArrayList<GestureType> listGesture = new ArrayList<>();

  public static boolean isDrawable = false;
  public final static float MINIMUM_DISTANCE_FOR_DRAWING = 0.002f;
  public static GestureType currentGesture;
  public static Vector3f currentCursor = new Vector3f();
  public static DisplayMetrics displayMetrics = new DisplayMetrics();
}
