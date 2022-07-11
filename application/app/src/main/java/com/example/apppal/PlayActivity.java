package com.example.apppal;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

// ContentResolver dependency
import com.example.apppal.Storage.GlobalState;
import com.example.apppal.renderer.ArRenderer;
import com.example.apppal.renderer.HandsResultGlRenderer;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.InstantPlacementPoint;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.mediapipe.formats.proto.LandmarkProto.Landmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Main activity of MediaPipe Hands app.
 */
public class PlayActivity extends AppCompatActivity {
    private static final String TAG = "PlayActivity";

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

    // ARCore.
    private boolean mUserRequestedInstall = true;
    private Session mSession;

    // GL Test
    private GLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.play_main);
//        setupLiveDemoUiComponents();
//        initializeConnection();
//        checkAREnable();

        mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setRenderer(new ArRenderer());
        setContentView(mSurfaceView);
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
        if (inputSource == InputSource.CAMERA) {
            return;
        }
        stopCurrentPipeline();
        setupStreamingModePipeline(InputSource.CAMERA);
    }

    /**
     * Sets up core workflow for streaming mode.
     */
    private void setupStreamingModePipeline(InputSource inputSource) {
        this.inputSource = inputSource;
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

        if (inputSource == InputSource.CAMERA) {
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        } else {
        }

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
        if (inputSource == InputSource.CAMERA) {
            glSurfaceView.post(this::startCamera);
        }

        // Updates the preview layout.
        FrameLayout frameLayout = findViewById(R.id.rendering_layout);
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(glSurfaceView);
        glSurfaceView.setVisibility(View.VISIBLE);
        frameLayout.requestLayout();
    }

    private void startCamera() {
        cameraInput.start(
                this,
                hands.getGlContext(),
                CameraInput.CameraFacing.BACK,
                glSurfaceView.getWidth(),
                glSurfaceView.getHeight());
    }

    private void stopCurrentPipeline() {
        if (cameraInput != null) {
            cameraInput.setNewFrameListener(null);
            cameraInput.close();
        }
        if (glSurfaceView != null) {
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {
            hands.close();
        }
    }

    private void logWristLandmark(HandsResult result, boolean showPixelValues) {
        if (result.multiHandLandmarks().isEmpty()) {
            return;
        }
        NormalizedLandmark wristLandmark =
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
        Landmark wristWorldLandmark =
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

    void checkAREnable() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Continue to query availability at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAREnable();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            try {
                if (mSession == null) {
                    switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                        case INSTALLED:
                            // Success: Safe to create the AR session.
                            mSession = new Session(this);
                            break;
                        case INSTALL_REQUESTED:
                            // When this method returns `INSTALL_REQUESTED`:
                            // 1. ARCore pauses this activity.
                            // 2. ARCore prompts the user to install or update Google Play
                            //    Services for AR (market://details?id=com.google.ar.core).
                            // 3. ARCore downloads the latest device profile data.
                            // 4. ARCore resumes this activity. The next invocation of
                            //    requestInstall() will either return `INSTALLED` or throw an
                            //    exception if the installation or update did not succeed.
                            mUserRequestedInstall = false;
                            return;
                    }
                }
            } catch (UnavailableUserDeclinedInstallationException | UnavailableDeviceNotCompatibleException e) {
                // Display an appropriate message to the user and return gracefully.
                Log.e(TAG, "checkAREnable: ", e);
                return;
            } catch (UnavailableSdkTooOldException e) {
                e.printStackTrace();
            } catch (UnavailableArcoreNotInstalledException e) {
                e.printStackTrace();
            } catch (UnavailableApkTooOldException e) {
                e.printStackTrace();
            }
            configureSession();
//            try {
//                // To record a live camera session for later playback, call
//                // `session.startRecording(recordingConfig)` at anytime. To playback a previously recorded AR
//                // session instead of using the live camera feed, call
//                // `session.setPlaybackDatasetUri(Uri)` before calling `session.resume()`. To
//                // learn more about recording and playback, see:
//                // https://developers.google.com/ar/develop/java/recording-and-playback
//                mSession.resume();
//            } catch (CameraNotAvailableException e) {
//                mSession = null;
//                return;
//            }
        } else {
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            Toast.makeText(this, "해당 기기는 ArCore를 지원하지 않습니다!", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }

    private void configureSession() {
        Config config = mSession.getConfig();
        config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
        if (mSession.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        } else {
            config.setDepthMode(Config.DepthMode.DISABLED);
        }
        if (Utils.IS_INSTANT_PLACEMENT) {
            config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP);
        } else {
            config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED);
        }
        mSession.configure(config);
    }

    private boolean placementIsDone = false;

    public void onDrawFrame(GL10 gl) {
        try {
            Frame frame = mSession.update();

            // Place an object on tap.
            if (!placementIsDone) {
                // Use estimated distance from the user's device to the real world, based
                // on expected user interaction and behavior.
                float approximateDistanceMeters = 2.0f;
                // Performs a ray cast given a screen tap position.
                // tapX, tapY: 목표로 하는 위치 (스크린 기준)
                float tapX = 0.5f;
                float tapY = 0.5f;
                List<HitResult> results =
                  frame.hitTestInstantPlacement(tapX, tapY, approximateDistanceMeters);
                if (!results.isEmpty()) {
                    InstantPlacementPoint point = (InstantPlacementPoint) results.get(0).getTrackable();
                    // Create an Anchor from the point's pose.
                    Anchor anchor = point.createAnchor(point.getPose());
                    placementIsDone = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

