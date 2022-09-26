package com.capstone.apppal.utils;

import android.util.DisplayMetrics;

import com.capstone.apppal.VO.CoordinateInfo;
import com.capstone.apppal.VO.FunctionType;
import com.capstone.apppal.VO.GestureType;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

public class GlobalState {
  public final static String PYTHON_SERVER_URL = "ec2-3-39-238-43.ap-northeast-2.compute.amazonaws.com";
  public final static int GESTURE_SOCKET_PORT = 4000;
  public final static float MINIMUM_DISTANCE_FOR_DRAWING = 0.0025f;
  public final static float MAXIMUM_RADIAN_FOR_DRAWING = 2.0f / 3.0f;
  public final static int GESTURE_HISTORY_SIZE = 5;
  public final static int GESTURE_DECISION_SIZE = 4;

  public static ArrayList<ArrayList<CoordinateInfo>> gestureTrackings = new ArrayList<>();
  public static InputStreamReader is;
  public static OutputStreamWriter os;
  public static ArrayList<GestureType> listGesture = new ArrayList<>();
  public static int gestureDetectionRate = 0;

  public static String inviteCode;
  public static boolean isDrawable = false;
  public static FunctionType currentFunction = FunctionType.DRAWING;
  public static ArrayList<Vector3f> currentCursor = new ArrayList<>();
  public static DisplayMetrics displayMetrics = new DisplayMetrics();
  public static String useruid;
  public static int MAXIMUM_COUNT_FOR_LIST = 60;
}
