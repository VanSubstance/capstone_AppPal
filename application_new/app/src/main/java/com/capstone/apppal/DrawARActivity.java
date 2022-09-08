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

package com.capstone.apppal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.icu.util.Calendar;
import android.media.Image;
import android.nfc.Tag;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.capstone.apppal.VO.CoordinateInfo;
import com.capstone.apppal.VO.TestCase;
import com.capstone.apppal.analytics.AnalyticsEvents;
import com.capstone.apppal.analytics.Fa;
import com.capstone.apppal.model.Stroke;
import com.capstone.apppal.rendering.AnchorRenderer;
import com.capstone.apppal.rendering.BackgroundRenderer;
import com.capstone.apppal.rendering.LineShaderRenderer;
import com.capstone.apppal.rendering.LineUtils;
import com.capstone.apppal.rendering.PointCloudRenderer;
import com.capstone.apppal.utils.GlobalState;
import com.capstone.apppal.view.BrushSelector;
import com.capstone.apppal.view.ClearDrawingDialog;
import com.capstone.apppal.view.ColorSelector;
import com.capstone.apppal.view.ErrorDialog;
import com.capstone.apppal.view.LeaveRoomDialog;
import com.capstone.apppal.view.MenuSelector;
import com.capstone.apppal.view.PairButton;
import com.capstone.apppal.view.PairButtonToolTip;
import com.capstone.apppal.view.PairView;
import com.capstone.apppal.view.PlaybackView;
import com.capstone.apppal.view.ToolSelector;
import com.capstone.apppal.view.TrackingIndicator;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotTrackingException;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.uncorkedstudios.android.view.recordablesurfaceview.RecordableSurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;


/**
 * This is a complex example that shows how to create an augmented reality (AR) application using
 * the ARCore API.
 */

public class DrawARActivity extends BaseActivity
  implements RecordableSurfaceView.RendererCallbacks, View.OnClickListener,
  ClearDrawingDialog.Listener, PlaybackView.Listener,
  ErrorDialog.Listener, RoomManager.StrokeUpdateListener, PairView.Listener,
  LeaveRoomDialog.Listener, PairSessionManager.AnchorStateListener,
  PairButtonToolTip.Listener, PairSessionManager.PartnerUpdateListener {

  private static final String TAG = "DrawARActivity";

  private static final boolean JOIN_GLOBAL_ROOM = BuildConfig.GLOBAL;

  private static final int TOUCH_QUEUE_SIZE = 10;

  private Fa mAnalytics;

  enum Mode {
    TOOL, PAIR_PARTNER_DISCOVERY, PAIR_ANCHOR_RESOLVING, PAIR_ERROR, PAIR_SUCCESS
  }

  private Mode mMode = Mode.TOOL;

  private View mDrawUiContainer;

  // Set to true ensures requestInstall() triggers installation if necessary.
  private boolean mUserRequestedARCoreInstall = true;

  private RecordableSurfaceView mSurfaceView;

  private Session mSession;

  private BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();

  private LineShaderRenderer mLineShaderRenderer = new LineShaderRenderer();
//    private DebugMeshShaderRenderer mLineShaderRenderer = new DebugMeshShaderRenderer();

  private final PointCloudRenderer pointCloud = new PointCloudRenderer();

  private AnchorRenderer zeroAnchorRenderer;

  private AnchorRenderer cloudAnchorRenderer;

  private Frame mFrame;

  private float[] projmtx = new float[16];

  private float[] viewmtx = new float[16];

  private float[] viewPos = new float[3];

  private float[] mZeroMatrix = new float[16];

  private float mScreenWidth = 0;

  private float mScreenHeight = 0;

  private Vector3f mLastTouch;

  private AtomicInteger touchQueueSize;

  private AtomicReferenceArray<Vector3f> touchQueue;

  private float mLineWidthMax = 0.03f;

  private Vector3f mSelectedColor = new Vector3f(0f, 0f, 0f);

  private float[] mLastFramePosition;

  private Boolean isDrawing = false;

  private AtomicBoolean bHasTracked = new AtomicBoolean(false);

  private AtomicBoolean bClearDrawing = new AtomicBoolean(false);

  private AtomicBoolean bUndo = new AtomicBoolean(false);

  private AtomicBoolean bNewTrack = new AtomicBoolean(false);

  private List<Stroke> mStrokes;

  // Test Case
  private ArrayList<TestCase> test = new ArrayList<>();

  private File mOutputFile;

  private MenuSelector mMenuSelector;

  private View mUndoButton;

  private TrackingIndicator mTrackingIndicator;

  private View mClearDrawingButton;

  private Handler mHandler = new Handler(Looper.getMainLooper());

  /*
   * Track number frames where we lose ARCore tracking. If we lose tracking for less than
   * a given number then continue painting.
   */
  private static final int MAX_UNTRACKED_FRAMES = 5;

  private int mFramesNotTracked = 0;

  private PlaybackView mPlaybackView;

  private boolean mDebugEnabled = false;

  private long mRenderDuration;

  /*
   * Session sharing
   */

  private Anchor mAnchor;

  private Map<String, Stroke> mSharedStrokes = new HashMap<>();

  private PairButton mPairButton;

  private TextView mPairActiveView;

  private PairButtonToolTip mPairButtonToolTip;

  private PairView mPairView;

  private PairSessionManager mPairSessionManager;

  /**
   * 미디어파이프용 변수들
   */

  private HandTracking handTracking;


  /**
   * Setup the app when main activity is created
   */
  @SuppressLint("ApplySharedPref")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindowManager().getDefaultDisplay().getMetrics(GlobalState.displayMetrics);

    handTracking = new HandTracking();

    mAnalytics = Fa.get();

    mTrackingIndicator = findViewById(R.id.finding_surfaces_view);

    mSurfaceView = findViewById(R.id.surfaceview);
    mSurfaceView.setRendererCallbacks(this);

    mClearDrawingButton = findViewById(R.id.menu_item_clear);
    mClearDrawingButton.setOnClickListener(this);

//        findViewById(R.id.menu_item_crash).setOnClickListener(this);
//        findViewById(R.id.menu_item_hide_ui).setOnClickListener(this);

    mPairButton = findViewById(R.id.button_pair);
    mPairButton.setOnClickListener(this);
    mPairButtonToolTip = findViewById(R.id.tooltip_button_pair);
    mPairButtonToolTip.setListener(this);
    mPairActiveView = findViewById(R.id.pair_active);

    mUndoButton = findViewById(R.id.undo_button);

    // set up draw settting selector
    mMenuSelector = findViewById(R.id.menu_selector);

    // Reset the zero matrix
    Matrix.setIdentityM(mZeroMatrix, 0);

    mStrokes = new ArrayList<>();
    touchQueueSize = new AtomicInteger(0);
    touchQueue = new AtomicReferenceArray<>(TOUCH_QUEUE_SIZE);

    mPlaybackView = findViewById(R.id.playback);

    mDrawUiContainer = findViewById(R.id.draw_container);

    mPairView = findViewById(R.id.view_join);
    mPairView.setListener(this);

    if (JOIN_GLOBAL_ROOM) {
      mPairSessionManager = new GlobalPairSessionManager(this);

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Pick global session")
        .setItems(R.array.sessions_array, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            GlobalRoomManager.setGlobalRoomName(String.valueOf(which));
          }
        });
      AlertDialog dialog = builder.create();
      dialog.show();
    } else {
      mPairSessionManager = new PairSessionManager(this);
    }
    mPairSessionManager.setPairingStateChangeListener(mPairView);
    mPairSessionManager.addPartnerUpdateListener(mPairButton);
    mPairSessionManager.addPartnerUpdateListener(this);
    mPairSessionManager.setAnchorStateListener(this);
    handTracking.setupLiveDemoUiComponents(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mPairSessionManager.login(this);
    if (mPairSessionManager.isPaired()) {
      mPairSessionManager.resumeListeners(this);
    }
  }

  @Override
  protected void onStop() {
    mPairSessionManager.pauseListeners();

    super.onStop();
  }

  /**
   * onResume part of the Android Activity Lifecycle
   */
  @Override
  protected void onResume() {
    super.onResume();

    // ARCore requires camera permissions to operate. If we did not yet obtain runtime
    // permission on Android M and above, now is a good time to ask the user for it.
    if (PermissionHelper.hasRequiredPermissions(this)) {

      // Check if ARCore is installed/up-to-date
      int message = -1;
      Exception exception = null;
      try {
        if (mSession == null) {
          switch (ArCoreApk.getInstance()
            .requestInstall(this, mUserRequestedARCoreInstall)) {
            case INSTALLED:
              mSession = new Session(this);

              break;
            case INSTALL_REQUESTED:
              // Ensures next invocation of requestInstall() will either return
              // INSTALLED or throw an exception.
              mUserRequestedARCoreInstall = false;
              // at this point, the activity is paused and user will go through
              // installation process
              return;
          }
        }
      } catch (Exception e) {
        exception = e;
        message = getARCoreInstallErrorMessage(e);
      }

      // display possible ARCore error to user
      if (message >= 0) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Exception creating session", exception);
        finish();
        return;
      }

      // Create default config and check if supported.
      Config config = new Config(mSession);
      config.setLightEstimationMode(Config.LightEstimationMode.DISABLED);
      config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
      if (!mSession.isSupported(config)) {
        Toast.makeText(getApplicationContext(), R.string.ar_not_supported,
          Toast.LENGTH_LONG).show();
        finish();
        return;
      }
      mSession.configure(config);

      // Note that order of session/surface resume matters - session must be resumed
      // before the surface view is resumed or the surface may call back on a session that is
      // not ready.
      try {
        mSession.resume();
      } catch (CameraNotAvailableException e) {
        ErrorDialog.newInstance(R.string.error_camera_not_available, true)
          .show(this);
      } catch (Exception e) {
        ErrorDialog.newInstance(R.string.error_resuming_session, true).show(this);
      }

      mSurfaceView.resume();
    } else {
      // take user to permissions activity
      startActivity(new Intent(this, PermissionsActivity.class));
      finish();
    }

    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    mScreenHeight = displayMetrics.heightPixels;
    mScreenWidth = displayMetrics.widthPixels;

    mPlaybackView.setListener(this);

    mPlaybackView.resume();

    if (mPairSessionManager.isPaired()) {

      if (!SessionHelper.shouldContinuePairedSession(this)) {
        mPairSessionManager.checkForPartners(new RoomManager.PartnerDetectionListener() {
          @Override
          public void onPartnersDetected() {
            // Stay in room
          }

          @Override
          public void onNoPartnersDetected() {
            mPairSessionManager.leaveRoom(false);
          }
        });
      } // time limit has not elapsed, force rejoin room (listeners restarted in onStart)

    } else if (!SessionHelper.shouldContinueSession(this)) {
      // if user has left activity for too long, clear the strokes from the previous session
      bClearDrawing.set(true);
      showStrokeDependentUI();
    }

    mPairSessionManager.setSession(mSession);

    mPairView.setListener(this);

    // TODO: Only used id hidden by "Hide UI menu"
    findViewById(R.id.draw_container).setVisibility(View.VISIBLE);
  }

  /**
   * onPause part of the Android Activity Lifecycle
   */
  @Override
  public void onPause() {

    // Note that the order matters - SurfaceView is paused first so that it does not try
    // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
    // still call mSession.update() and get a SessionPausedException.
    mSurfaceView.pause();
    if (mSession != null) {
      mSession.pause();
    }

    mTrackingIndicator.resetTrackingTimeout();

    if (mPlaybackView != null) {
      mPlaybackView.pause();
      mPlaybackView.setListener(null);
    }

    SessionHelper.setSessionEnd(this);

    mPairView.setListener(null);

    super.onPause();
  }


  /**
   * addStroke adds a new stroke to the scene
   */
  private void trackStroke() {
    mLineWidthMax = mMenuSelector.getBrushSelector().getSelectedLineWidth().getWidth();
    mSelectedColor = mMenuSelector.getColorSelector().getSelectedColorType().getColor();
    mLineShaderRenderer.setColor(mSelectedColor);

    Stroke stroke;
    switch (mMenuSelector.getToolSelector().getSelectedToolType()) {
      case RECT:
        for (int i = 0; i < 4; i++) {
          stroke = new Stroke();
          stroke.localLine = true;
          stroke.setLineWidth(mLineWidthMax);
          stroke.setColor(mSelectedColor);
          mStrokes.add(stroke);
        }
        break;
      case CUBE:
        for (int i = 0; i < 12; i++) {
          stroke = new Stroke();
          stroke.localLine = true;
          stroke.setLineWidth(mLineWidthMax);
          stroke.setColor(mSelectedColor);
          mStrokes.add(stroke);
        }
        break;
      default:
        stroke = new Stroke();
        stroke.localLine = true;
        stroke.setLineWidth(mLineWidthMax);
        stroke.setColor(mSelectedColor);
        mStrokes.add(stroke);
        break;
    }

    // update firebase
    int index = mStrokes.size() - 1;
//        mPairSessionManager.updateStroke(index, mStrokes.get(index));
    switch (mMenuSelector.getToolSelector().getSelectedToolType()) {
      case RECT:
        for (int i = 3; i >= 0; i--) {
          mPairSessionManager.addStroke(mStrokes.get(index - i));
        }
        break;
      case CUBE:
        for (int i = 11; i >= 0; i--) {
          mPairSessionManager.addStroke(mStrokes.get(index - i));
        }
        break;
      default:
        mPairSessionManager.addStroke(mStrokes.get(index));
        break;
    }

    showStrokeDependentUI();

    mAnalytics.setUserProperty(AnalyticsEvents.USER_PROPERTY_HAS_DRAWN,
      AnalyticsEvents.VALUE_TRUE);

    mTrackingIndicator.setDrawnInSession();
  }

  /**
   * trackPointFromScreeen adds a point to the current stroke
   *
   * @param touchPoint a 2D point in screen space and is projected into 3D world space
   */
  private void trackPointFromScreeen(Vector3f... touchPoint) {
    Vector3f[] newPoints = new Vector3f[touchPoint.length];
    for (int i = 0; i < touchPoint.length; i++) {
      newPoints[i] = LineUtils
        .GetWorldCoords(touchPoint[i], mScreenWidth, mScreenHeight, projmtx, viewmtx, viewPos);
    }
    trackPoint3f(newPoints);
  }

  /**
   * trackPoint3f adds a point to the current stroke
   *
   * @param newPoint a 3D point in world space
   */
  private void trackPoint3f(Vector3f... newPoint) {
    Vector3f point;
    Vector3f targetPoint;
    int index = mStrokes.size() - 1;

    if (index < 0)
      return;
//    Log.e(TAG, "trackPoint3f: 포인트 포인트:ㅖ: " + newPoint[newPoint.length - 1]);

    switch (mMenuSelector.getToolSelector().getSelectedToolType()) {
      case ERASE:
        /**
         * 지우개 모드
         * */
        targetPoint = newPoint[newPoint.length - 1];
        if (mAnchor != null && mAnchor.getTrackingState() == TrackingState.TRACKING) {
          point = LineUtils.TransformPointToPose(targetPoint, mAnchor.getPose());
          for (int j = 0; j < mStrokes.size(); j++) {
            Stroke stroke = mStrokes.get(j);
            boolean isPassed = false;
            int targetIndex = 0;
            List<Vector3f> pointList = stroke.getPoints();
            for (int i = 0; i < pointList.size(); i++) {
              Vector3f pointToErase = pointList.get(i);
              if (Math.abs(point.getX() - pointToErase.getX()) < 0.00001f
                || Math.abs(point.getY() - pointToErase.getY()) < 0.00001f
                || Math.abs(point.getZ() - pointToErase.getZ()) < 0.00001f) {
                isPassed = true;
                targetIndex = i;
                break;
              }
            }
            if (isPassed) {
              if (targetIndex < 3) {
                mStrokes.get(j).truncatePoints(targetIndex, stroke.size());
              } else if (stroke.size() - 3 < targetIndex) {
                mStrokes.get(j).truncatePoints(0, targetIndex);
              } else {
                Stroke backStroke = new Stroke();
                backStroke.localLine = true;
                backStroke.setLineWidth(mLineWidthMax);
                backStroke.updateStrokeData(stroke);
                backStroke.setColor(mSelectedColor);
                backStroke.truncatePoints(targetIndex + 1, stroke.size());
                mStrokes.get(j).truncatePoints(0, targetIndex - 1);
                mStrokes.add(backStroke);
                mPairSessionManager.addStroke(mStrokes.get(index + 1));
              }
              mLineShaderRenderer.bNeedsUpdate.set(true);
            }
          }
          /**
           * 기존 한 획 통째로 삭제 기능
           * 추후 추가를 위해 코드 보존
           */
//          for (Stroke stroke : mStrokes) {
//            boolean isPassed = false;
//            for (Vector3f pointToErase : stroke.getPoints()) {
//              if (Math.abs(point.getX() - pointToErase.getX()) < 0.00001f
//                || Math.abs(point.getY() - pointToErase.getY()) < 0.00001f
//                || Math.abs(point.getZ() - pointToErase.getZ()) < 0.00001f) {
//                isPassed = true;
//                break;
//              }
//            }
//            if (isPassed) {
//              mPairSessionManager.undoStroke(stroke);
//              mStrokes.remove(stroke);
//              if (mStrokes.isEmpty()) {
//                showStrokeDependentUI();
//              }
//              mLineShaderRenderer.bNeedsUpdate.set(true);
//            }
//          }
        } else {
          for (int j = 0; j < mStrokes.size(); j++) {
            Stroke stroke = mStrokes.get(j);
            boolean isPassed = false;
            int targetIndex = 0;
            List<Vector3f> pointList = stroke.getPoints();
            for (int i = 0; i < pointList.size(); i++) {
              Vector3f pointToErase = pointList.get(i);
              if (Math.abs(targetPoint.getX() - pointToErase.getX()) < 0.00001f
                || Math.abs(targetPoint.getY() - pointToErase.getY()) < 0.00001f
                || Math.abs(targetPoint.getZ() - pointToErase.getZ()) < 0.00001f) {
                isPassed = true;
                targetIndex = i;
                break;
              }
            }
            if (isPassed) {
              if (targetIndex < 3) {
                mStrokes.get(j).truncatePoints(targetIndex, stroke.size());
              } else if (stroke.size() - 3 < targetIndex) {
                mStrokes.get(j).truncatePoints(0, targetIndex);
              } else {
                Stroke backStroke = new Stroke();
                backStroke.localLine = true;
                backStroke.setLineWidth(mLineWidthMax);
                backStroke.setColor(mSelectedColor);
                backStroke.updateStrokeData(stroke);
                backStroke.truncatePoints(targetIndex + 1, stroke.size());
                mStrokes.get(j).truncatePoints(0, targetIndex - 1);
                mStrokes.add(backStroke);
                mPairSessionManager.addStroke(mStrokes.get(index + 1));
              }
              mLineShaderRenderer.bNeedsUpdate.set(true);
            }
          }
          /**
           * 기존 한 획 통째로 삭제 기능
           * 추후 추가를 위해 코드 보존
           */
//          for (Stroke stroke : mStrokes) {
//            boolean isPassed = false;
//            for (Vector3f pointToErase : stroke.getPoints()) {
//              if (Math.abs(targetPoint.getX() - pointToErase.getX()) < 0.00001f
//                || Math.abs(targetPoint.getY() - pointToErase.getY()) < 0.00001f
//                || Math.abs(targetPoint.getZ() - pointToErase.getZ()) < 0.00001f) {
//                isPassed = true;
//                break;
//              }
//            }
//            if (isPassed) {
//              mPairSessionManager.undoStroke(stroke);
//              mStrokes.remove(stroke);
//              if (mStrokes.isEmpty()) {
//                showStrokeDependentUI();
//              }
//              mLineShaderRenderer.bNeedsUpdate.set(true);
//            }
//          }
        }
        mPairSessionManager.updateStroke(mStrokes.get(index));
        break;
      case RECT:
        /**
         * 직사각형 모드
         */
        if (newPoint.length > 0) {
          targetPoint = newPoint[newPoint.length - 1];
          if (mAnchor != null && mAnchor.getTrackingState() == TrackingState.TRACKING) {
            point = LineUtils.TransformPointToPose(targetPoint, mAnchor.getPose());
            if (mStrokes.get(index - 3).size() == 0) {
              for (int i = 3; i >= 0; i--) {
                drawStraightLine(index - i, point);
              }
            } else {
              Vector3f startCoor = mStrokes.get(index - 3).get(0);
              float xs = startCoor.getX();
              float ys = startCoor.getY();
              float zs = startCoor.getZ();
              float xe = point.getX();
              float ye = point.getY();
              float ze = point.getZ();
              ArrayList<Vector3f> coorList = new ArrayList<>();
              coorList.add(new Vector3f(xs, ys, zs));
              coorList.add(new Vector3f(xe, ys, zs));
              coorList.add(new Vector3f(xe, ye, ze));
              coorList.add(new Vector3f(xs, ye, ze));
              drawStraightLine(index - 3, coorList.get(1));
              drawStraightLine(index - 2, coorList.get(1), coorList.get(2));
              drawStraightLine(index - 1, coorList.get(2), coorList.get(3));
              drawStraightLine(index - 0, coorList.get(3));
            }
          } else {
            if (mStrokes.get(index - 3).size() == 0) {
              for (int i = 3; i >= 0; i--) {
                drawStraightLine(index - i, targetPoint);
              }
            } else {
              Vector3f startCoor = mStrokes.get(index - 3).get(0);
              float xs = startCoor.getX();
              float ys = startCoor.getY();
              float zs = startCoor.getZ();
              float xe = targetPoint.getX();
              float ye = targetPoint.getY();
              float ze = targetPoint.getZ();
              ArrayList<Vector3f> coorList = new ArrayList<>();
              coorList.add(new Vector3f(xs, ys, zs));
              coorList.add(new Vector3f(xe, ys, zs));
              coorList.add(new Vector3f(xe, ye, ze));
              coorList.add(new Vector3f(xs, ye, ze));
              drawStraightLine(index - 3, coorList.get(1));
              drawStraightLine(index - 2, coorList.get(1), coorList.get(2));
              drawStraightLine(index - 1, coorList.get(2), coorList.get(3));
              drawStraightLine(index - 0, coorList.get(3));
            }
          }
        }
        break;
      case STRAIGHT_LINE:
        /**
         * 직선 모드
         * */
        if (newPoint.length > 0) {
          targetPoint = newPoint[newPoint.length - 1];
          if (mAnchor != null && mAnchor.getTrackingState() == TrackingState.TRACKING) {
            point = LineUtils.TransformPointToPose(targetPoint, mAnchor.getPose());
            drawStraightLine(index, point);
          } else {
            drawStraightLine(index, targetPoint);
          }
        }
        break;
      case CUBE:
        /**
         * 직육면체 모드
         */
        if (newPoint.length > 0) {
          targetPoint = newPoint[newPoint.length - 1];
          if (mAnchor != null && mAnchor.getTrackingState() == TrackingState.TRACKING) {
            point = LineUtils.TransformPointToPose(targetPoint, mAnchor.getPose());
            if (mStrokes.get(index - 11).size() == 0) {
              for (int i = 11; i >= 0; i--) {
                drawStraightLine(index - i, point);
              }
            } else {
              Vector3f startCoor = mStrokes.get(index - 11).get(0);
              float xs = startCoor.getX();
              float ys = startCoor.getY();
              float zs = startCoor.getZ();
              float xe = point.getX();
              float ye = point.getY();
              float ze = point.getZ();
              ArrayList<Vector3f> coorList = new ArrayList<>();
              coorList.add(new Vector3f(xs, ys, zs));
              coorList.add(new Vector3f(xe, ys, zs));
              coorList.add(new Vector3f(xe, ye, zs));
              coorList.add(new Vector3f(xs, ye, zs));
              coorList.add(new Vector3f(xs, ys, ze));
              coorList.add(new Vector3f(xe, ys, ze));
              coorList.add(new Vector3f(xe, ye, ze));
              coorList.add(new Vector3f(xs, ye, ze));
              drawStraightLine(index - 11, coorList.get(1));
              drawStraightLine(index - 10, coorList.get(1), coorList.get(2));
              drawStraightLine(index - 9, coorList.get(2), coorList.get(3));
              drawStraightLine(index - 8, coorList.get(3));
              drawStraightLine(index - 7, coorList.get(4));
              drawStraightLine(index - 6, coorList.get(1), coorList.get(5));
              drawStraightLine(index - 5, coorList.get(2), coorList.get(6));
              drawStraightLine(index - 4, coorList.get(3), coorList.get(7));
              drawStraightLine(index - 3, coorList.get(4), coorList.get(5));
              drawStraightLine(index - 2, coorList.get(5), coorList.get(6));
              drawStraightLine(index - 1, coorList.get(6), coorList.get(7));
              drawStraightLine(index - 0, coorList.get(7), coorList.get(4));
            }
          } else {
            if (mStrokes.get(index - 11).size() == 0) {
              for (int i = 11; i >= 0; i--) {
                drawStraightLine(index - i, targetPoint);
              }
            } else {
              Vector3f startCoor = mStrokes.get(index - 11).get(0);
              float xs = startCoor.getX();
              float ys = startCoor.getY();
              float zs = startCoor.getZ();
              float xe = targetPoint.getX();
              float ye = targetPoint.getY();
              float ze = targetPoint.getZ();
              ArrayList<Vector3f> coorList = new ArrayList<>();
              coorList.add(new Vector3f(xs, ys, zs));
              coorList.add(new Vector3f(xe, ys, zs));
              coorList.add(new Vector3f(xe, ye, zs));
              coorList.add(new Vector3f(xs, ye, zs));
              coorList.add(new Vector3f(xs, ys, ze));
              coorList.add(new Vector3f(xe, ys, ze));
              coorList.add(new Vector3f(xe, ye, ze));
              coorList.add(new Vector3f(xs, ye, ze));
              drawStraightLine(index - 11, coorList.get(1));
              drawStraightLine(index - 10, coorList.get(1), coorList.get(2));
              drawStraightLine(index - 9, coorList.get(2), coorList.get(3));
              drawStraightLine(index - 8, coorList.get(3));
              drawStraightLine(index - 7, coorList.get(4));
              drawStraightLine(index - 6, coorList.get(1), coorList.get(5));
              drawStraightLine(index - 5, coorList.get(2), coorList.get(6));
              drawStraightLine(index - 4, coorList.get(3), coorList.get(7));
              drawStraightLine(index - 3, coorList.get(4), coorList.get(5));
              drawStraightLine(index - 2, coorList.get(5), coorList.get(6));
              drawStraightLine(index - 1, coorList.get(6), coorList.get(7));
              drawStraightLine(index - 0, coorList.get(7), coorList.get(4));
            }
          }
        }
        break;
      case NORMAL_PEN:
      default:
        /**
         * 일반 펜 모드
         * */
        // test
        for (int i = 0; i < newPoint.length; i++) {
          if (mAnchor != null && mAnchor.getTrackingState() == TrackingState.TRACKING) {
            point = LineUtils.TransformPointToPose(newPoint[i], mAnchor.getPose());
            mStrokes.get(index).add(point, false);
          } else {
            mStrokes.get(index).add(newPoint[i], false);
//            ArrayList<Vector3f> test_line = new ArrayList<>();
//            for(int t =0; t<test.size(); t++)
//            {
//              test_line.add(new Vector3f(test.get(t).getX(),test.get(t).getY(),test.get(t).getZ()));
//            }
//            mStrokes.get(index).add(test_line.get(i), false);
//            Log.e(TAG, "Arcore Standard Check: " + newPoint[i]);
          }
        }
        mPairSessionManager.updateStroke(mStrokes.get(index));
        break;
    }

    // update firebase database
    isDrawing = true;
  }

  /**
   * 선의 기본 단위:: x 기준 0.00001f
   * 1. 시작점과 현재 점의 x 축 거리를 0.00001f 단위로 나누어
   * 2. 좌표를 추가해준다
   */
  public void drawStraightLine(int targetStrokeIndex, Vector3f targetPoint) {
    if (mStrokes.get(targetStrokeIndex).size() >= 2) {
      Vector3f startPoint = mStrokes.get(targetStrokeIndex).get(0);
      float xs = startPoint.getX();
      float ys = startPoint.getY();
      float zs = startPoint.getZ();
      float xe = targetPoint.getX();
      float ye = targetPoint.getY();
      float ze = targetPoint.getZ();

      Vector3f temp = new Vector3f();
      temp.sub(startPoint, targetPoint);
      int cnt = (int) Math.floor(temp.length() / 0.01);

      float xL = (xe - xs) / cnt;
      float yL = (ye - ys) / cnt;
      float zL = (ze - zs) / cnt;
      ArrayList<Vector3f> pointList = new ArrayList<>();
      if (cnt != 0) {
        for (int i = 0; i <= cnt; i++) {
          pointList.add(new Vector3f(xs + (xL * i), ys + (yL * i), zs + (zL * i)));
        }
      } else {
        pointList.add(startPoint);
        pointList.add(targetPoint);
      }
      mStrokes.get(targetStrokeIndex).replaceAll(pointList);
    } else {
      mStrokes.get(targetStrokeIndex).add(targetPoint, false);
    }
    mPairSessionManager.updateStroke(mStrokes.get(targetStrokeIndex));
  }

  public void drawStraightLine(int targetStrokeIndex, Vector3f startPoint, Vector3f endPoint) {
    float xs = startPoint.getX();
    float ys = startPoint.getY();
    float zs = startPoint.getZ();
    float xe = endPoint.getX();
    float ye = endPoint.getY();
    float ze = endPoint.getZ();

    Vector3f temp = new Vector3f();
    temp.sub(startPoint, endPoint);
    int cnt = (int) Math.floor(temp.length() / 0.01);

    float xL = (xe - xs) / cnt;
    float yL = (ye - ys) / cnt;
    float zL = (ze - zs) / cnt;
    ArrayList<Vector3f> pointList = new ArrayList<>();
    if (cnt != 0) {
      for (int i = 0; i <= cnt; i++) {
        pointList.add(new Vector3f(xs + (xL * i), ys + (yL * i), zs + (zL * i)));
      }
    } else {
      pointList.add(startPoint);
      pointList.add(endPoint);
    }
    mStrokes.get(targetStrokeIndex).replaceAll(pointList);
    mPairSessionManager.updateStroke(mStrokes.get(targetStrokeIndex));
  }

  /**
   * update() is executed on the GL Thread.
   * The method handles all operations that need to take place before drawing to the screen.
   * The method :
   * extracts the current projection matrix and view matrix from the AR Pose
   * handles adding stroke and points to the data collections
   * updates the ZeroMatrix and performs the matrix multiplication needed to re-center the drawing
   * updates the Line Renderer with the current strokes, color, distance scale, line width etc
   */
  private void update() {
    try {
      final long updateStartTime = System.currentTimeMillis();

      // Update ARCore frame
      mFrame = mSession.update();

      // Notify the hostManager of all the anchor updates.
      Collection<Anchor> updatedAnchors = mFrame.getUpdatedAnchors();
      mPairSessionManager.onUpdate(updatedAnchors);

      // Update tracking states
      mTrackingIndicator.setTrackingStates(mFrame, mAnchor);
      if (mTrackingIndicator.trackingState == TrackingState.TRACKING && !bHasTracked.get()) {
        bHasTracked.set(true);
        mAnalytics
          .setUserProperty(AnalyticsEvents.USER_PROPERTY_TRACKING_ESTABLISHED,
            AnalyticsEvents.VALUE_TRUE);
      }

      // Get projection matrix.
      mFrame.getCamera().getProjectionMatrix(projmtx, 0, AppSettings.getNearClip(),
        AppSettings.getFarClip());
      mFrame.getCamera().getViewMatrix(viewmtx, 0);

      Pose temp = mFrame.getCamera().getPose();
      viewPos = new float[]{temp.tx(), temp.ty(), temp.tz()};

      float[] position = new float[3];

      mFrame.getCamera().getPose().getTranslation(position, 0);

      //HandTracking Part
      handTracking.getImageFrame(mFrame);


      // Multiply the zero matrix
      Matrix.multiplyMM(viewmtx, 0, viewmtx, 0, mZeroMatrix, 0);

      // Check if camera has moved much, if thats the case, stop touchDown events
      // (stop drawing lines abruptly through the air)
      if (mLastFramePosition != null) {
        Vector3f distance = new Vector3f(position[0], position[1], position[2]);
        distance.sub(new Vector3f(mLastFramePosition[0], mLastFramePosition[1],
          mLastFramePosition[2]));
      }

      mLastFramePosition = position;

      // Add points to strokes from touch queue
      int numPoints = touchQueueSize.get();
      if (numPoints > TOUCH_QUEUE_SIZE) {
        numPoints = TOUCH_QUEUE_SIZE;
      }

      if (numPoints > 0) {
        if (bNewTrack.get()) {
          bNewTrack.set(false);
          trackStroke();
        }

        Vector3f[] points = new Vector3f[numPoints];
        for (int i = 0; i < numPoints; i++) {
          points[i] = touchQueue.get(i);
          mLastTouch = new Vector3f(points[i].x, points[i].y, points[i].z);
        }
        trackPointFromScreeen(points);
      }

      // If no new points have been added and add last point again
      if (numPoints == 0 && GlobalState.isDrawable && GlobalState.currentCursor != null) {
        trackPointFromScreeen(mLastTouch);
        mLineShaderRenderer.bNeedsUpdate.set(true);
      }

      if (numPoints > 0) {
        touchQueueSize.set(0);
        mLineShaderRenderer.bNeedsUpdate.set(true);
      }

      if (bClearDrawing.get()) {
        bClearDrawing.set(false);
        clearDrawing();
        mLineShaderRenderer.bNeedsUpdate.set(true);
      }

      // Check if we are still drawing, otherwise finish line
      if (isDrawing) {
        isDrawing = false;
        if (!mStrokes.isEmpty()) {
          mStrokes.get(mStrokes.size() - 1).finishStroke();
        }
      }

      // Update line animation
//            for (int i = 0; i < mStrokes.size(); i++) {
//                mStrokes.get(i).update();
//            }
      boolean renderNeedsUpdate = false;
      for (Stroke stroke : mSharedStrokes.values()) {
        if (stroke.update()) {
          renderNeedsUpdate = true;
        }
      }
      if (renderNeedsUpdate) {
        mLineShaderRenderer.bNeedsUpdate.set(true);
      }

      if (bUndo.get()) {
        bUndo.set(false);
        if (mStrokes.size() > 0) {
          int index = mStrokes.size() - 1;
          mPairSessionManager.undoStroke(mStrokes.get(index));
          mStrokes.remove(index);
          if (mStrokes.isEmpty()) {
            showStrokeDependentUI();
          }
          mLineShaderRenderer.bNeedsUpdate.set(true);
        }
      }
      if (mLineShaderRenderer.bNeedsUpdate.get()) {
        mLineShaderRenderer.mDrawDistance = AppSettings.getStrokeDrawDistance();
        float distanceScale = 0.0f;
        mLineShaderRenderer.setDistanceScale(distanceScale);
        mLineShaderRenderer.setLineWidth(mLineWidthMax);
        mLineShaderRenderer.clear();
        mLineShaderRenderer.updateStrokes(mStrokes, mSharedStrokes);
        mLineShaderRenderer.upload();
      }

    } catch (Exception e) {
      Log.e(TAG, "update: ", e);
    }
  }

  /**
   * renderScene() clears the Color Buffer and Depth Buffer, draws the current texture from the
   * camera
   * and draws the Line Renderer if ARCore is tracking the world around it
   */
  private void renderScene() {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    if (mFrame != null) {
      mBackgroundRenderer.draw(mFrame);
    }

    // Draw debug anchors
    if (BuildConfig.DEBUG) {
      if (mFrame.getCamera().getTrackingState() == TrackingState.TRACKING) {
        zeroAnchorRenderer.draw(viewmtx, projmtx, false);
      }
    }

    // Draw background.
    if (mFrame != null) {

      // Draw Lines
      if (mTrackingIndicator.isTracking() || (
        // keep painting through 5 frames where we're not tracking
        (bHasTracked.get() && mFramesNotTracked < MAX_UNTRACKED_FRAMES))) {

        if (!mTrackingIndicator.isTracking()) {
          mFramesNotTracked++;
        } else {
          mFramesNotTracked = 0;
        }

        // If the anchor is set, set the modelMatrix of the line renderer to offset to the anchor
        if (mAnchor != null && mAnchor.getTrackingState() == TrackingState.TRACKING) {
          mAnchor.getPose().toMatrix(mLineShaderRenderer.mModelMatrix, 0);

          if (BuildConfig.DEBUG) {
            mAnchor.getPose().toMatrix(cloudAnchorRenderer.mModelMatrix, 0);
            cloudAnchorRenderer.draw(viewmtx, projmtx, true);
          }
        }

        // Render the lines
        mLineShaderRenderer
          .draw(viewmtx, projmtx, mScreenWidth, mScreenHeight,
            AppSettings.getNearClip(),
            AppSettings.getFarClip());
      }

    }

    if (mMode == Mode.PAIR_PARTNER_DISCOVERY || mMode == Mode.PAIR_ANCHOR_RESOLVING) {
      if (mFrame != null) {
        PointCloud pointCloud = mFrame.acquirePointCloud();
        this.pointCloud.update(pointCloud);
        this.pointCloud.draw(viewmtx, projmtx);

        // Application is responsible for releasing the point cloud resources after
        // using it.
        pointCloud.release();
      }
    }

  }

  /**
   * Clears the Datacollection of Strokes and sets the Line Renderer to clear and update itself
   * Designed to be executed on the GL Thread
   */
  private void clearDrawing() {
    mStrokes.clear();
    mLineShaderRenderer.clear();
    mPairSessionManager.clearStrokes();
    showStrokeDependentUI();
  }


  /**
   * onClickUndo handles the touch input on the GUI and sets the AtomicBoolean bUndo to be true
   * the actual undo functionality is executed in the GL Thread
   */
  public void onClickUndo(View button) {

    bUndo.set(true);

    mAnalytics.setUserProperty(AnalyticsEvents.USER_PROPERTY_TAPPED_UNDO,
      AnalyticsEvents.VALUE_TRUE);
  }

  /**
   * onClickClear handle showing an AlertDialog to clear the drawing
   */
  private void onClickClear() {
    ClearDrawingDialog.newInstance(mPairSessionManager.isPaired()).show(this);
    mAnalytics.setUserProperty(AnalyticsEvents.USER_PROPERTY_TAPPED_CLEAR,
      AnalyticsEvents.VALUE_TRUE);
  }

  // ------- Touch events

  /**
   * onTouchEvent handles saving the lastTouch screen position and setting bTouchDown and
   * bNewTrack
   * AtomicBooleans to trigger trackPoint3f and addStroke on the GL Thread to be called
   */
  @Override
  public boolean onTouchEvent(MotionEvent tap) {
    int action = tap.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
      closeViewsOutsideTapTarget(tap);
    }

    // do not accept touch events through the playback view
    // or when we are not tracking
    if (mPlaybackView.isOpen() || !mTrackingIndicator.isTracking()) {
      return false;
    }
    return false;
  }

  private void closeViewsOutsideTapTarget(MotionEvent tap) {
    if (isOutsideViewBounds(mMenuSelector.getBrushSelector(), (int) tap.getRawX(), (int) tap.getRawY())
      && mMenuSelector.getBrushSelector().isOpen()) {
      mMenuSelector.getBrushSelector().close();
    }
    if (isOutsideViewBounds(mMenuSelector.getToolSelector(), (int) tap.getRawX(), (int) tap.getRawY())
      && mMenuSelector.getToolSelector().isOpen()) {
      mMenuSelector.getToolSelector().close();
    }
    if (isOutsideViewBounds(mPairButtonToolTip, (int) tap.getRawX(), (int) tap.getRawY())
      && mPairButtonToolTip.getVisibility() == View.VISIBLE) {
      mPairButtonToolTip.hide();
    }
  }

  private boolean isOutsideViewBounds(View view, int x, int y) {
    Rect outRect = new Rect();
    int[] location = new int[2];
    view.getDrawingRect(outRect);
    view.getLocationOnScreen(location);
    outRect.offset(location[0], location[1]);
    return !outRect.contains(x, y);
  }

  private File createVideoOutputFile() {

    File tempFile;

    File dir = new File(getCacheDir(), "captures");

    if (!dir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      dir.mkdirs();
    }

    Calendar c = Calendar.getInstance();

    String filename = "JustALine_" +
      c.get(Calendar.YEAR) + "-" +
      (c.get(Calendar.MONTH) + 1) + "-" +
      c.get(Calendar.DAY_OF_MONTH)
      + "_" +
      c.get(Calendar.HOUR_OF_DAY) +
      c.get(Calendar.MINUTE) +
      c.get(Calendar.SECOND);

    tempFile = new File(dir, filename + ".mp4");

    return tempFile;

  }

  @Override
  public void onSurfaceDestroyed() {
    mBackgroundRenderer.clearGL();
    mLineShaderRenderer.clearGL();
  }

  @Override
  public void onSurfaceCreated() {
    prepareForRecording();

    zeroAnchorRenderer = new AnchorRenderer();
    cloudAnchorRenderer = new AnchorRenderer();
    pointCloud.createOnGlThread(/*context=*/ this);
  }

  private void prepareForRecording() {
    Log.d(TAG, "prepareForRecording: ");
    try {
      mOutputFile = createVideoOutputFile();
      android.graphics.Point size = new android.graphics.Point();
      getWindowManager().getDefaultDisplay().getRealSize(size);
      mSurfaceView.initRecorder(mOutputFile, size.x, size.y, null, null);

    } catch (IOException ioex) {
      Log.e(TAG, "Couldn't setup recording", ioex);
      Fa.get().exception(ioex, "Error setting up recording");
    }


  }

  @Override
  public void onSurfaceChanged(int width, int height) {
    int rotation = Surface.ROTATION_0;
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      rotation = Surface.ROTATION_90;
    }
    mSession.setDisplayGeometry(rotation, width, height);
  }


  @Override
  public void onContextCreated() {
    mBackgroundRenderer.createOnGlThread(this);
    mSession.setCameraTextureName(mBackgroundRenderer.getTextureId());
    try {
      mLineShaderRenderer.createOnGlThread(DrawARActivity.this);
    } catch (IOException e) {
      e.printStackTrace();
    }
    mLineShaderRenderer.bNeedsUpdate.set(true);
  }

  @Override
  public void onPreDrawFrame() {
    if (mMode == Mode.TOOL) {
      if (GlobalState.currentCursor.size() == 1) {
        touchQueue.set(0, GlobalState.currentCursor.get(0));
        bNewTrack.set(true);
        touchQueueSize.set(1);

      } else if (GlobalState.currentCursor.size() > 1) {
        int numTouches = touchQueueSize.addAndGet(1);
        if (numTouches <= TOUCH_QUEUE_SIZE) {
          touchQueue.set(numTouches - 1, GlobalState.currentCursor.get(1));
        }
      }
    }
    update();
  }

  @Override
  public void onDrawFrame() {
    long renderStartTime = System.currentTimeMillis();

    renderScene();

    mRenderDuration = System.currentTimeMillis() - renderStartTime;
  }

  private void showStrokeDependentUI() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mUndoButton.setVisibility(mStrokes.size() > 0 ? View.VISIBLE : View.GONE);
        mClearDrawingButton.setVisibility(
          (mStrokes.size() > 0 || mSharedStrokes.size() > 0) ? View.VISIBLE
            : View.GONE);
        mTrackingIndicator.setHasStrokes(mStrokes.size() > 0);
      }
    });
  }

  @Override
  public void onClearDrawingConfirmed() {
    bClearDrawing.set(true);
    showStrokeDependentUI();
  }

  private void shareApp() {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message));
    startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
  }


  @Override
  public void onClick(View v) {
    boolean hidePairToolTip = true;
    switch (v.getId()) {
      case R.id.menu_item_clear:
        onClickClear();
        break;
      case R.id.button_pair:
        if (mPairSessionManager.isInRoom()) {
          LeaveRoomDialog.newInstance().show(this);
        } else if (mPairButtonToolTip.getVisibility() == View.GONE) {
          mPairButtonToolTip.show();
          mPairButton.setContentDescription(getString(R.string.content_description_close_join_friend_menu));
          mPairButton.setAccessibilityTraversalBefore(R.id.pair_tooltip_title);
          hidePairToolTip = false;
        }
        break;
    }
    mMenuSelector.getBrushSelector().close();
    mMenuSelector.getToolSelector().close();
    if (hidePairToolTip) {
      mPairButtonToolTip.hide();
      if (!mPairSessionManager.isPaired())
        mPairButton.setContentDescription(getString(R.string.content_description_join_friend));
    }
  }

  @Override
  public void onBackPressed() {
    if (mPlaybackView.isOpen()) {
      mPlaybackView.close();
    } else if (mMode == Mode.PAIR_PARTNER_DISCOVERY || mMode == Mode.PAIR_ANCHOR_RESOLVING) {
      mPairView.hide();
      setMode(Mode.TOOL);
      mPairSessionManager.leaveRoom(false);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    if (mPlaybackView == null || !mPlaybackView.isOpen()) {
      super.onWindowFocusChanged(hasFocus);
    }
  }

  @Override
  public void onPlaybackClosed() {
    showView(mDrawUiContainer);
    showView(mTrackingIndicator);

    setupImmersive();
    prepareForRecording();
  }

  @Override
  public void requestStoragePermission() {
    PermissionHelper.requestStoragePermission(this, false);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {

    if (requestCode == PermissionHelper.REQUEST_CODE_STORAGE_PERMISSIONS) {
      // send storage permissions result to playback view
      mPlaybackView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public void onPairPressed() {
    mPairSessionManager.startPairingSession(this);

    mPairButton.setContentDescription(getString(R.string.content_description_disconnect_from_friend));

    Fa.get().send(AnalyticsEvents.EVENT_TAPPED_START_PAIR);
    Fa.get().setUserProperty(AnalyticsEvents.USER_PROPERTY_HAS_TAPPED_PAIR,
      AnalyticsEvents.VALUE_TRUE);
  }

  @Override
  public void onJoinRoomPressed() {
    try {
      ((GlobalPairSessionManager) mPairSessionManager).joinGlobalRoom(this);
    } catch (ClassCastException e) {
      Fa.get().exception(e, "Join Room pressed in production app");
    }
  }

  /**
   * Update views for the given mode
   */
  private void setMode(Mode mode) {
    if (mMode != mode) {
      mMode = mode;

      switch (mMode) {
        case TOOL:
          showView(mDrawUiContainer);
          showView(mTrackingIndicator);
          mTrackingIndicator.setDrawPromptEnabled(true);
          mTrackingIndicator.removeListener(mPairView);
          mPairView.hide();
          break;
        case PAIR_ANCHOR_RESOLVING:
          hideView(mDrawUiContainer);
          mTrackingIndicator.setDrawPromptEnabled(false);
          showView(mTrackingIndicator);
          mTrackingIndicator.addListener(mPairView);
          break;
        case PAIR_PARTNER_DISCOVERY:
        case PAIR_ERROR:
        case PAIR_SUCCESS:
          hideView(mDrawUiContainer);
          hideView(mTrackingIndicator);
          mTrackingIndicator.setDrawPromptEnabled(false);
          mTrackingIndicator.removeListener(mPairView);
          mPairView.show();
          mPairView.onErrorRemoved();
          break;
      }
    }
  }

  @Override
  public void setAnchor(Anchor anchor) {
    mAnchor = anchor;

    for (Stroke stroke : mStrokes) {
      Log.d(TAG, "setAnchor: pushing line");
      stroke.offsetToPose(mAnchor.getPose());
      mPairSessionManager.addStroke(stroke);
    }

    mLineShaderRenderer.bNeedsUpdate.set(true);
  }

  @Override
  public void onModeChanged(Mode mode) {
    setMode(mode);
  }

  private void showView(View toShow) {
    toShow.setVisibility(View.VISIBLE);
    toShow.animate().alpha(1).start();
  }

  private void hideView(final View toHide) {
    toHide.animate().alpha(0).withEndAction(new Runnable() {
      @Override
      public void run() {
        toHide.setVisibility(View.GONE);
      }
    }).start();
  }

  public void enableView(View toEnable) {
    toEnable.setEnabled(true);
    toEnable.animate().alpha(1f);
  }

  public void disableView(View toDisable) {
    toDisable.setEnabled(false);
    toDisable.animate().alpha(.5f);
  }

  @Override
  public void onPairCanceled() {
    mPairView.hide();

    setMode(Mode.TOOL);

    mPairSessionManager.leaveRoom(false);
  }

  @Override
  public void onPairViewClosed() {
    setMode(Mode.TOOL);
  }

  @Override
  public void onReadyToSetAnchor() {
    mPairSessionManager.readyToSetAnchor();
    Fa.get().send(AnalyticsEvents.EVENT_TAPPED_READY_TO_SET_ANCHOR);
  }

  public void createAnchor() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Pose pose = mFrame.getCamera().getPose();

        try {
          mAnchor = mSession.createAnchor(pose);
        } catch (NotTrackingException e) {
          Log.e(TAG, "Cannot create anchor when not tracking", e);
          mTrackingIndicator.addListener(new TrackingIndicator.DisplayListener() {
            @Override
            public void onErrorDisplaying() {
              // Do nothing, can't set anchor
            }

            @Override
            public void onErrorRemoved() {
              mTrackingIndicator.removeListener(this);
              createAnchor();
            }
          });
          return;
        }

        mPairSessionManager.onAnchorCreated();
        if (mStrokes.size() > 0) {
          for (int i = 0; i < mStrokes.size(); i++) {
            mStrokes.get(i).offsetToPose(pose);
            if (mStrokes.get(i).hasFirebaseReference())
              mPairSessionManager.updateStroke(mStrokes.get(i));
            else
              mPairSessionManager.addStroke(mStrokes.get(i));
          }
          mLineShaderRenderer.bNeedsUpdate.set(true);
        }

        mPairSessionManager.setAnchor(mAnchor);
      }
    });
  }

  @Override
  public void clearLines() {
    mSharedStrokes.clear();
    mStrokes.clear();
    mLineShaderRenderer.bNeedsUpdate.set(true);
  }

  @Override
  public void onAnchorChangedLeftRoom() {
    ErrorDialog.newInstance(R.string.drawing_session_ended, false)
      .show(this);
  }

  @Override
  public void onConnectivityLostLeftRoom() {
    ErrorDialog.newInstance(R.string.pair_no_data_connection_title,
        R.string.pair_no_data_connection_body, false)
      .show(this);
  }

  @Override
  public void clearAnchor(Anchor anchor) {
    if (anchor != null && anchor.equals(mAnchor)) {
      for (Stroke stroke : mStrokes) {
        stroke.offsetFromPose(mAnchor.getPose());
      }
      mAnchor = null;
      Matrix.setIdentityM(mLineShaderRenderer.mModelMatrix, 0);
    }
  }

  @Override
  public void setRoomNumber(String roomKey) {
  }

  @Override
  public void onReadyResolveAnchor() {
    mPairSessionManager.resolveAnchorFromAnchorId();
  }

  @Override
  public void setAnchorResolvingMode() {
    setMode(Mode.PAIR_ANCHOR_RESOLVING);
  }

  @Override
  public void setPairErrorMode() {
    setMode(Mode.PAIR_ERROR);
  }

  @Override
  public void setPairSuccessMode() {
    setMode(Mode.PAIR_SUCCESS);
  }

  @Override
  public void attemptPartnerDiscovery() {
    mPairSessionManager.startPairingSession(this);
  }

  @Override
  public void onPartnerCountChanged(int partnerCount) {
    if (partnerCount < 2) {
      mPairActiveView.setText(R.string.partner_lost);
      mPairActiveView.setBackgroundResource(R.drawable.bg_pair_state_partner_lost);
    } else {
      mPairActiveView.setText(R.string.partner_paired);
      mPairActiveView.setBackgroundResource(R.drawable.bg_pair_state_paired);
    }
  }

  @Override
  public void onConnectedToSession() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mPairSessionManager.setStrokeListener(DrawARActivity.this);
        mPairActiveView.setText(R.string.partner_paired);
        showView(mPairActiveView);

        mTrackingIndicator.setAnchorTrackingMessageEnabled(true);
        mTrackingIndicator.setShowPairedSessionDrawPrompt(true);
      }
    });
  }

  @Override
  public void onDisconnectedFromSession() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        hideView(mPairActiveView);

        mPairButton.setContentDescription(getString(R.string.content_description_join_friend));

        mTrackingIndicator.setAnchorTrackingMessageEnabled(false);
        mTrackingIndicator.setShowPairedSessionDrawPrompt(false);
      }
    });
  }

  @Override
  public void onLineAdded(String uid, Stroke value) {
    value.localLine = false;
    value.calculateTotalLength();
    mSharedStrokes.put(uid, value);
    showStrokeDependentUI();
    mLineShaderRenderer.bNeedsUpdate.set(true);
  }

  @Override
  public void onLineRemoved(String uid) {
    if (mSharedStrokes.containsKey(uid)) {
      mSharedStrokes.remove(uid);
      mLineShaderRenderer.bNeedsUpdate.set(true);
    } else {
      for (Stroke stroke : mStrokes) {
        if (uid.equals(stroke.getFirebaseKey())) {
          mStrokes.remove(stroke);
          if (!stroke.finished) {
          }
          mLineShaderRenderer.bNeedsUpdate.set(true);
          break;
        }
      }
    }

    showStrokeDependentUI();
  }

  @Override
  public void onLineUpdated(String uid, Stroke value) {
    Stroke stroke = mSharedStrokes.get(uid);
    if (stroke == null) {
      return;
    }
    stroke.updateStrokeData(value);
    mLineShaderRenderer.bNeedsUpdate.set(true);
  }

  @Override
  public void exitApp() {
    finish();
  }

  @Override
  public void onExitRoomSelected() {
    mPairSessionManager.leaveRoom(true);
    Fa.get().send(AnalyticsEvents.EVENT_TAPPED_DISCONNECT_PAIRED_SESSION);
  }
}
