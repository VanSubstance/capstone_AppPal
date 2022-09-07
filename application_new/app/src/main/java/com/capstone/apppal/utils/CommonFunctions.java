package com.capstone.apppal.utils;

import javax.vecmath.Vector3f;

public class CommonFunctions {
  public static boolean isTriangleAnglesOk(Vector3f a, Vector3f b, Vector3f c) {
    Vector3f calc = new Vector3f();
    calc.sub(a, b);
    float disAB = calc.length();
    calc = new Vector3f();
    calc.sub(b, c);
    float disBC = calc.length();
    calc = new Vector3f();
    calc.sub(c, a);
    float disCA = calc.length();
    float radA = (float) Math.acos((disAB * disAB + disCA * disCA - disBC * disBC) / (2 * disAB * disCA)) / (float) Math.PI;
    if (radA <= GlobalState.MAXIMUM_RADIAN_FOR_DRAWING) {
      float radB = (float) Math.acos((disAB * disAB + disBC * disBC - disCA * disCA) / (2 * disAB * disBC)) / (float) Math.PI;
      if (radB <= GlobalState.MAXIMUM_RADIAN_FOR_DRAWING) {
        float radC = (float) Math.acos((disBC * disBC + disCA * disCA - disAB * disAB) / (2 * disBC * disCA)) / (float) Math.PI;
        if (radC <= GlobalState.MAXIMUM_RADIAN_FOR_DRAWING) {
          return true;
        }
      }
    }
    return false;
  }
}
