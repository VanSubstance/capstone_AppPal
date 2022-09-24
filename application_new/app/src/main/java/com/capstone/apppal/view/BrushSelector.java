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

package com.capstone.apppal.view;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.apppal.AppSettings;
import com.capstone.apppal.R;
import com.capstone.apppal.VO.GestureType;

/**
 * Created by Kat on 11/13/17.
 * Custom view for selecting brush size
 */

public class BrushSelector extends ConstraintLayout {

  private static final String TAG = "BrushSelector";

  private static final int SMALL_BRUSH = 0;

  private static final int MEDIUM_BRUSH = 1;

  private static final int LARGE_BRUSH = 2;

  private static final Pair<Integer, AppSettings.LineWidth> defaultBrush = new Pair<>(MEDIUM_BRUSH,
    AppSettings.LineWidth.SMALL);

  private View mBackground;

  private View mSmallButton, mMediumButton, mLargeButton;

  private int mSelectedBrush = defaultBrush.first;

  private AppSettings.LineWidth mSelectedLineWidth = defaultBrush.second;

  private boolean mIsOpen = true;

  //the locations of the buttons
  private int mSmallButtonLoc[] = new int[2];

  private int mMediumButtonLoc[] = new int[2];

  private int mLargeButtonLoc[] = new int[2];

  public BrushSelector(Context context) {
    super(context);
    init();
  }

  public BrushSelector(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BrushSelector(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.view_brush_selector, this);

    mBackground = findViewById(R.id.brush_background_pie);

    mSmallButton = findViewById(R.id.brush_selection_small);
    mMediumButton = findViewById(R.id.brush_selection_medium);
    mLargeButton = findViewById(R.id.brush_selection_large);

    mBackground.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          performClick();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && mIsOpen) {
          //get the point where we let go
          float yloc = event.getRawY();

          AppSettings.LineWidth lineWidth;

          //determine which button was released over
          if (mSmallButtonLoc[1] < yloc && yloc < (mSmallButtonLoc[1] + mSmallButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedBrush != SMALL_BRUSH) {
              lineWidth = AppSettings.LineWidth.SMALL;
              onBrushSizeSelected(lineWidth);
            }
          } else if (mMediumButtonLoc[1] < yloc && yloc < (mMediumButtonLoc[1]
            + mMediumButton.getHeight())) {
            if (mSelectedBrush != MEDIUM_BRUSH) {
              lineWidth = AppSettings.LineWidth.MEDIUM;
              onBrushSizeSelected(lineWidth);
            }
          } else if (mLargeButtonLoc[1] < yloc && yloc < (mLargeButtonLoc[1]
            + mLargeButton.getHeight())) {
            if (mSelectedBrush != LARGE_BRUSH) {
              lineWidth = AppSettings.LineWidth.LARGE;
              onBrushSizeSelected(lineWidth);
            }
          }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          //toggle if over a button
          float yloc = event.getRawY();
          if (mSmallButtonLoc[1] < yloc && yloc < (mSmallButtonLoc[1] + mSmallButton
            .getHeight())) {
            toggleBrushSelectorVisibility();
          } else if (mMediumButtonLoc[1] < yloc && yloc < (mMediumButtonLoc[1]
            + mMediumButton.getHeight())) {
            toggleBrushSelectorVisibility();
          } else if (mLargeButtonLoc[1] < yloc && yloc < (mLargeButtonLoc[1]
            + mLargeButton.getHeight())) {
            toggleBrushSelectorVisibility();
          }
        }
        return true;
      }
    });

    this.post(new Runnable() {
      @Override
      public void run() {
        //the navigation bar is visible here at this point and throws off my location capture....
        //have to get the height to fix this
        mSmallButton.getLocationInWindow(mSmallButtonLoc);
        mMediumButton.getLocationInWindow(mMediumButtonLoc);
        mLargeButton.getLocationInWindow(mLargeButtonLoc);
      }
    });

    onBrushSizeSelected(defaultBrush.second);
    toggleBrushSelectorVisibility();
  }

  public void handleMenu(GestureType nowGesture) {
    AppSettings.LineWidth lineWidth = null;
    switch (nowGesture) {
      case ONE:
        lineWidth = AppSettings.LineWidth.SMALL;
        break;
      case TWO:
        lineWidth = AppSettings.LineWidth.MEDIUM;
        break;
      case THREE:
        lineWidth = AppSettings.LineWidth.LARGE;
        break;
      default:
        break;
    }
    onBrushSizeSelected(lineWidth);
    toggleBrushSelectorVisibility();
  }

  @Override
  public boolean performClick() {
    toggleBrushSelectorVisibility();
    return super.performClick();
  }

  private void onBrushSizeSelected(AppSettings.LineWidth lineWidth) {
    mSelectedLineWidth = lineWidth;

    TypedValue outValue = new TypedValue();

    switch (lineWidth) {
      case SMALL:
        getResources().getValue(R.dimen.brush_scale_small, outValue, true);
        mSelectedBrush = SMALL_BRUSH;
        break;
      case MEDIUM:
        getResources().getValue(R.dimen.brush_scale_medium, outValue, true);
        mSelectedBrush = MEDIUM_BRUSH;
        break;
      default:
      case LARGE:
        getResources().getValue(R.dimen.brush_scale_large, outValue, true);
        mSelectedBrush = LARGE_BRUSH;
        break;
    }
  }

  private void toggleBrushSelectorVisibility() {
    if (mIsOpen) {
      float y = mBackground.getY();
      Animator.AnimatorListener hideListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(GONE);
          mSmallButton.setVisibility(GONE);
          mMediumButton.setVisibility(GONE);
          mLargeButton.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(0).setListener(hideListener).translationY(y);
      mSmallButton.animate().alpha(0).translationY(y);
      mMediumButton.animate().alpha(0).translationY(y);
      mLargeButton.animate().alpha(0).translationY(y);

      mBackground.setEnabled(false);
      mSmallButton.setEnabled(false);
      mMediumButton.setEnabled(false);
      mLargeButton.setEnabled(false);

    } else {
      Animator.AnimatorListener showListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(VISIBLE);
          mSmallButton.setVisibility(VISIBLE);
          mMediumButton.setVisibility(VISIBLE);
          mLargeButton.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(1).setListener(showListener).translationY(0);
      mSmallButton.animate().alpha(1).translationY(0);
      mMediumButton.animate().alpha(1).translationY(0);
      mLargeButton.animate().alpha(1).translationY(0);

      mBackground.setEnabled(true);
      mSmallButton.setEnabled(true);
      mMediumButton.setEnabled(true);
      mLargeButton.setEnabled(true);

      mBackground.setAccessibilityTraversalBefore(R.id.brush_selection_small);
      mSmallButton.setAccessibilityTraversalBefore(R.id.brush_selection_medium);
      mMediumButton.setAccessibilityTraversalBefore(R.id.brush_selection_large);
    }
    mIsOpen = !mIsOpen;
  }

  public AppSettings.LineWidth getSelectedLineWidth() {
    return mSelectedLineWidth;
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void close() {
    if (mIsOpen) {
      toggleBrushSelectorVisibility();
    }
  }

  public void toggle() {
    toggleBrushSelectorVisibility();
  }

}
