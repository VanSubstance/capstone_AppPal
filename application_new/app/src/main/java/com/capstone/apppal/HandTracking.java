package com.capstone.apppal;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;

import com.capstone.apppal.VO.CoordinateInfo;
import com.capstone.apppal.network.GestureRecognitionSocket;
import com.capstone.apppal.utils.CommonFunctions;
import com.capstone.apppal.utils.GlobalState;
import com.google.ar.core.Frame;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

public class HandTracking {
  private Hands hands;
  private GestureRecognitionSocket gestureSocket;
  private static int gestureRecognitionControll = 0;
  private static final int gestureRecognitionSpeed = 4;

  public HandTracking() {
    super();

    gestureSocket = new GestureRecognitionSocket();
    gestureSocket.start();
  }

  /**
   * 카메라에서 현재 프레임 이미지 추출
   */
  Image cameraImage = null;

  public void getImageFrame(Frame mFrame) {
    try {
      cameraImage =
        mFrame.acquireCameraImage();
      byte[] bytes = imageToByte(cameraImage);
      Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
      hands.send(bitmapImage);
    } catch (
      NotYetAvailableException e) {
      // NotYetAvailableException is an exception that can be expected when the camera is not ready
      // yet. The image may become available on a next frame.
      Log.e(TAG, "update: " + e);
    } catch (RuntimeException e) {
      // A different exception occurred, e.g. DeadlineExceededException, ResourceExhaustedException.
      // Handle this error appropriately.
      Log.e(TAG, "update: " + e);
    } finally {
      if (cameraImage != null) {
        cameraImage.close();
      }
    }
  }

  /**
   * mediapipe 관련 함수들
   */

  public void setupLiveDemoUiComponents(Context context) {
    stopCurrentPipeline();
    setupStreamingModePipeline(context);
  }

  private void setupStreamingModePipeline(Context mcontext) {
    // Initializes a new MediaPipe Hands solution instance in the streaming mode.
    hands =
      new Hands(mcontext,
        HandsOptions.builder()
          .setStaticImageMode(true)
          .setMaxNumHands(1)
          .setRunOnGpu(false)
          .build());
    hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:: " + message));

    // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
    /**
     * 스켈레톤 21개 좌표 취득 부분
     */
    hands.setResultListener(
      handsResult -> {
        handleResult(handsResult);
      });
  }

  private void handleResult(HandsResult result) {
    if (result == null) {
      return;
    }
    int numHands = result.multiHandLandmarks().size();
    if (numHands == 0) {
      GlobalState.isDrawable = false;
      GlobalState.currentCursor.clear();
    }
    for (int i = 0; i < numHands; ++i) {
      gestureRecognitionControll += 1;

      LandmarkProto.NormalizedLandmark pin = result.multiHandLandmarks().get(i).getLandmarkList().get(4);
      CoordinateInfo pin4 = new CoordinateInfo(pin.getX(), pin.getY(), pin.getZ(), pin.getVisibility());
      pin = result.multiHandLandmarks().get(i).getLandmarkList().get(8);
      CoordinateInfo pin8 = new CoordinateInfo(pin.getX(), pin.getY(), pin.getZ(), pin.getVisibility());
      pin = result.multiHandLandmarks().get(i).getLandmarkList().get(2);
      CoordinateInfo pin2 = new CoordinateInfo(pin.getX(), pin.getY(), pin.getZ(), pin.getVisibility());
      pin = result.multiHandLandmarks().get(i).getLandmarkList().get(5);
      CoordinateInfo pin5 = new CoordinateInfo(pin.getX(), pin.getY(), pin.getZ(), pin.getVisibility());
      pin = result.multiHandLandmarks().get(i).getLandmarkList().get(0);
      CoordinateInfo pin0 = new CoordinateInfo(pin.getX(), pin.getY(), pin.getZ(), pin.getVisibility());

      Vector3f temp = new Vector3f();
      temp.sub(pin4.getVector(), pin8.getVector());

      if (temp.lengthSquared() >= GlobalState.MINIMUM_DISTANCE_FOR_DRAWING) {
        GlobalState.isDrawable = false;
        GlobalState.currentCursor.clear();
      } else {
        temp.add(pin4.getVector(), pin8.getVector());

        Vector3f pin4_8 = new Vector3f(temp.getX() / 2.0f, temp.getY() / 2.0f, temp.getZ() / 2.0f);
        float disY = Math.abs(pin0.getY() - pin8.getY());
        if (CommonFunctions.isTriangleAnglesOk(pin4_8, pin2.getVector(), pin5.getVector())) {
          GlobalState.isDrawable = true;

          float x = (1.0f - (temp.getY() - 0.5f)) * (float) GlobalState.displayMetrics.widthPixels;
          float y = (temp.getX() / 2.0f) * (float) GlobalState.displayMetrics.heightPixels;
          float z = ((float) GlobalState.displayMetrics.heightPixels * (0.5f - (1.0f * disY)));
//          x = x * (1 + ((((float) GlobalState.displayMetrics.widthPixels) - x) / (float) GlobalState.displayMetrics.widthPixels));
//          y = y * (1 + ((((float) GlobalState.displayMetrics.heightPixels) - y) / (float) GlobalState.displayMetrics.heightPixels));
          GlobalState.currentCursor.add(new Vector3f(
            x,
            y,
            z));
          if (GlobalState.currentCursor.size() >= 3) {
            GlobalState.currentCursor.remove(0);
          }
        }
      }

      ArrayList<CoordinateInfo> coordinateList = new ArrayList<>();
      for (LandmarkProto.NormalizedLandmark landmark : result.multiHandLandmarks().get(i).getLandmarkList()) {
        // Draws the landmark.
        // Get Coordinate
        // Gather all coordinates information for single hand into one array
        if (gestureRecognitionControll % gestureRecognitionSpeed == 0) {
          CoordinateInfo newCoor = new CoordinateInfo(landmark.getX(), landmark.getY(), landmark.getZ(), landmark.getVisibility());
          coordinateList.add(newCoor);
        }
      }
      if (gestureRecognitionControll % gestureRecognitionSpeed == 0) {
        gestureSocket.sendHandCoordinatesToServer(coordinateList);
      }
    }
  }

  private void stopCurrentPipeline() {
    if (hands != null) {
      hands.close();
    }
  }

  private void logWristLandmark(HandsResult result, boolean showPixelValues) {
    if (result.multiHandLandmarks().isEmpty()) {
      return;
    }
    LandmarkProto.NormalizedLandmark wristLandmark =
      result.multiHandLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
    // For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
    if (showPixelValues) {
      int width = result.inputBitmap().getWidth();
      int height = result.inputBitmap().getHeight();
      Log.i(
        TAG,
        String.format(
          "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
          wristLandmark.getX() * width, wristLandmark.getY() * height));
    } else {
      Log.i(
        TAG,
        String.format(
          "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
          wristLandmark.getX(), wristLandmark.getY()));
    }
    if (result.multiHandWorldLandmarks().isEmpty()) {
      return;
    }
    LandmarkProto.Landmark wristWorldLandmark =
      result.multiHandWorldLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
    Log.i(
      TAG,
      String.format(
        "MediaPipe Hand wrist world coordinates (in meters with the origin at the hand's"
          + " approximate geometric center): x=%f m, y=%f m, z=%f m",
        wristWorldLandmark.getX(), wristWorldLandmark.getY(), wristWorldLandmark.getZ()));

    List<LandmarkProto.Landmark> coorList = result.multiHandWorldLandmarks().get(0).getLandmarkList();
    // Land Mark
    ArrayList<CoordinateInfo> temp = new ArrayList<>();
    Log.i(TAG, " length coorlist :" + coorList.size());
    for (int i = 0; i < coorList.size(); i++) {
      LandmarkProto.Landmark coor = coorList.get(i);
      temp.add(new CoordinateInfo(coor.getX(), coor.getY(), coor.getZ(), coor.getVisibility()));
    }
    GlobalState.gestureTrackings.add(temp);
    if (GlobalState.gestureTrackings.size() >= 5)
      GlobalState.gestureTrackings.remove(0);
//    Log.e(TAG, "logWristLandmark: 현재 스켈레톤:: " + GlobalState.gestureTrackings.get(GlobalState.gestureTrackings.size() - 1));
  }

  private static byte[] imageToByte(Image image) {
    byte[] byteArray = null;
    byteArray = NV21toJPEG(YUV420toNV21(image), image.getWidth(), image.getHeight(), 100);
    return byteArray;
  }

  private static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
    yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
    return out.toByteArray();
  }

  private static byte[] YUV420toNV21(Image image) {
    byte[] nv21;
    // Get the three planes.
    ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
    ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
    ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

    int ySize = yBuffer.remaining();
    int uSize = uBuffer.remaining();
    int vSize = vBuffer.remaining();


    nv21 = new byte[ySize + uSize + vSize];

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize);
    vBuffer.get(nv21, ySize, vSize);
    uBuffer.get(nv21, ySize + vSize, uSize);

    return nv21;
  }
}

