// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.capstone.apppal.rendering;

import android.opengl.Matrix;
import android.util.Log;

import com.capstone.apppal.AppSettings;
import com.capstone.apppal.model.Ray;
import com.google.ar.core.Pose;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;


public class LineUtils {

  public static Vector3f GetWorldCoords(Vector3f touchPoint, float screenWidth,
                                        float screenHeight, float[] projectionMatrix, float[] viewMatrix, float[] viewPos) {

    float[] viewMatrixForCalc = new float[16];
    Matrix.invertM(viewMatrixForCalc, 0, viewMatrix, 0);
    float zToMove = touchPoint.getZ() * 0.0002646f;

//    Log.e("TAG", "GetWorldCoords: 이동할 z 거리:: (픽셀) " + touchPoint.getZ());
//    Log.e("TAG", "GetWorldCoords: 이동할 z 거리:: (미터) " + z);

    // z 좌표만큼 view matrix 평행 이동
    float r = viewMatrixForCalc[2];
    float u = viewMatrixForCalc[6];
    float l = -viewMatrixForCalc[10];
    float k = (float) (zToMove / Math.sqrt((r * r) + (u * u) + (l * l)));
    viewMatrixForCalc[12] = viewMatrixForCalc[12] / viewPos[0] * ((k * r) + viewPos[0]);
    viewMatrixForCalc[13] = viewMatrixForCalc[13] / viewPos[1] * ((k * u) + viewPos[1]);
    viewMatrixForCalc[14] = viewMatrixForCalc[14] / viewPos[2] * ((k * l) + viewPos[2]);
    Matrix.invertM(viewMatrixForCalc, 0, viewMatrixForCalc, 0);


    Ray touchRay = projectRay(new Vector2f(touchPoint.getX(), touchPoint.getY()), screenWidth, screenHeight, projectionMatrix,
      viewMatrixForCalc);
    touchRay.direction.scale(AppSettings.getStrokeDrawDistance());
    touchRay.origin.add(touchRay.direction);
    return touchRay.origin;
  }

  private static Ray screenPointToRay(Vector2f point, Vector2f viewportSize, float[] viewProjMtx) {
    point.y = viewportSize.y - point.y;
    float x = point.x * 2.0F / viewportSize.x - 1.0F;
    float y = point.y * 2.0F / viewportSize.y - 1.0F;
    float[] farScreenPoint = new float[]{x, y, 1.0F, 1.0F};
    float[] nearScreenPoint = new float[]{x, y, -1.0F, 1.0F};
    float[] nearPlanePoint = new float[4];
    float[] farPlanePoint = new float[4];
    float[] invertedProjectionMatrix = new float[16];
    Matrix.setIdentityM(invertedProjectionMatrix, 0);
    Matrix.invertM(invertedProjectionMatrix, 0, viewProjMtx, 0);
    Matrix.multiplyMV(nearPlanePoint, 0, invertedProjectionMatrix, 0, nearScreenPoint, 0);
    Matrix.multiplyMV(farPlanePoint, 0, invertedProjectionMatrix, 0, farScreenPoint, 0);
    Vector3f direction = new Vector3f(farPlanePoint[0] / farPlanePoint[3],
      farPlanePoint[1] / farPlanePoint[3], farPlanePoint[2] / farPlanePoint[3]);
    Vector3f origin = new Vector3f(new Vector3f(nearPlanePoint[0] / nearPlanePoint[3],
      nearPlanePoint[1] / nearPlanePoint[3], nearPlanePoint[2] / nearPlanePoint[3]));
    direction.sub(origin);
    direction.normalize();
    return new Ray(origin, direction);
  }

  private static Ray projectRay(Vector2f touchPoint, float screenWidth, float screenHeight,
                                float[] projectionMatrix, float[] viewMatrix) {
    float[] viewProjMtx = new float[16];
    Matrix.multiplyMM(viewProjMtx, 0, projectionMatrix, 0, viewMatrix, 0);
    return screenPointToRay(new Vector2f(touchPoint.getX(), touchPoint.getY()), new Vector2f(screenWidth, screenHeight),
      viewProjMtx);
  }

  public static boolean distanceCheck(Vector3f newPoint, Vector3f lastPoint) {
    Vector3f temp = new Vector3f();
    temp.sub(newPoint, lastPoint);
    return temp.lengthSquared() > AppSettings.getMinDistance();
  }


  /**
   * Transform a vector3f FROM anchor coordinates TO world coordinates
   */
  public static Vector3f TransformPointFromPose(Vector3f point, Pose anchorPose) {
    float[] position = new float[3];
    position[0] = point.x;
    position[1] = point.y;
    position[2] = point.z;

    position = anchorPose.transformPoint(position);
    return new Vector3f(position[0], position[1], position[2]);
  }

  /**
   * Transform a vector3f TO anchor coordinates FROM world coordinates
   */
  public static Vector3f TransformPointToPose(Vector3f point, Pose anchorPose) {
    // Recenter to anchor
    float[] position = new float[3];
    position[0] = point.x;
    position[1] = point.y;
    position[2] = point.z;

    position = anchorPose.inverse().transformPoint(position);
    return new Vector3f(position[0], position[1], position[2]);
  }

}
