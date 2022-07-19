package com.example.apppal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppal.Storage.GlobalState;
import com.example.apppal.renderer.HandsResultGlRenderer;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

public class GestureActivity extends AppCompatActivity {
  private static final String TAG = "GestureActivity";
  private Hands hands;
  // Run the pipeline and the model inference on GPU or CPU.
  private static final boolean RUN_ON_GPU = true;
  private enum InputSource {
    UNKNOWN,
    CAMERA,
  }
  private InputSource inputSource = InputSource.UNKNOWN;
  // Live camera demo UI and camera components.
  private CameraInput cameraInput;
  private SolutionGlSurfaceView<HandsResult> glSurfaceView;
  private GestureRecognitionSocket gestureSocket;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gesture_main);
    setupLiveDemoUiComponents();
    initializeConnection();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (inputSource == InputSource.CAMERA) {
      // Restarts the camera and the opengl surface rendering.
      cameraInput = new CameraInput(this);
      cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
      glSurfaceView.post(this::startCamera);
      glSurfaceView.setVisibility(View.VISIBLE);
    } else {
    }
  }
  @Override
  protected void onPause() {
    super.onPause();
    if (inputSource == InputSource.CAMERA) {
      glSurfaceView.setVisibility(View.GONE);
      cameraInput.close();
    } else {
    }
  }
  /**
   * Sets up the UI components for the live demo with camera input.
   */
  private void setupLiveDemoUiComponents() {
    setupStreamingModePipeline();
    initializeConnection();
  }

  /**
   * Sets up core workflow for streaming mode.
   */
  private void setupStreamingModePipeline() {
    // Initializes a new MediaPipe Hands solution instance in the streaming mode.
    hands =
      new Hands(
        this,
        HandsOptions.builder()
          .setStaticImageMode(false)
          .setMaxNumHands(1)
          .setRunOnGpu(RUN_ON_GPU)
          .build());
    hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));
    // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
    glSurfaceView =
      new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
    glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
    glSurfaceView.setRenderInputImage(true);
    hands.setResultListener(
      handsResult -> {
        logWristLandmark(handsResult, /*showPixelValues=*/ false);
        glSurfaceView.setRenderData(handsResult);
        glSurfaceView.requestRender();
      });
    // The runnable to start camera after the gl surface view is attached.
    // For video input source, videoInput.start() will be called when the video uri is available.
    glSurfaceView.post(this::startCamera);
    // Updates the preview layout.
    FrameLayout frameLayout = findViewById(R.id.preview_display_layout);
    frameLayout.removeAllViewsInLayout();
    frameLayout.addView(glSurfaceView);
    glSurfaceView.setVisibility(View.VISIBLE);
    frameLayout.requestLayout();
  }
  private void startCamera() {
    cameraInput = new CameraInput(this);
    cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
    cameraInput.start(
      this,
      hands.getGlContext(),
      CameraInput.CameraFacing.BACK,
      glSurfaceView.getWidth(),
      glSurfaceView.getHeight());
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
  }
  private void initializeConnection() {
    gestureSocket = new GestureRecognitionSocket();
    gestureSocket.start();

    GlobalState.textAnnounce = findViewById(R.id.text_announce);
    Utils.IS_GESTURE_DETECTION = true;
  }
}