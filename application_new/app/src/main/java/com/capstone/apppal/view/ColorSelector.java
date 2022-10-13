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
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.apppal.AppSettings;
import com.capstone.apppal.DrawARActivity;
import com.capstone.apppal.R;
import com.capstone.apppal.VO.GestureType;

/**
 * Created by Kat on 11/13/17.
 * Custom view for selecting brush size
 */

public class ColorSelector extends ConstraintLayout {

  private static final String TAG = "ColorSelector";

  private static final int WHITE = 0;
  private static final int BLACK = 1;
  private static final int RED = 2;
  private static final int GREEN = 4;
  private static final int BLUE = 3;

  private static final Pair<Integer, AppSettings.ColorType> defaultTool = new Pair<>(WHITE,
    AppSettings.ColorType.WHITE);

  private View mBackground;

  private View mWhiteButton,
    mBlackButton,
    mRedButton,
    mGreenButton,
    mBlueButton;

  private int mSelectedColor = defaultTool.first;

  private AppSettings.ColorType mSelectedColorType = defaultTool.second;

  private boolean mIsOpen = true;

  //the locations of the buttons
  private int mWhiteButtonLoc[] = new int[2];
  private int mBlackButtonLoc[] = new int[2];
  private int mRedButtonLoc[] = new int[2];
  private int mGreenButtonLoc[] = new int[2];
  private int mBlueButtonLoc[] = new int[2];

  public ColorSelector(Context context) {
    super(context);
    init();
  }

  public ColorSelector(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ColorSelector(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.view_color_selector, this);

    mBackground = findViewById(R.id.color_background_pie);

    mWhiteButton = findViewById(R.id.color_selection_white);
    mBlackButton = findViewById(R.id.color_selection_black);
    mRedButton = findViewById(R.id.color_selection_red);
    mGreenButton = findViewById(R.id.color_selection_green);
    mBlueButton = findViewById(R.id.color_selection_blue);

    mBackground.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          performClick();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && mIsOpen) {
          //get the point where we let go
          float yloc = event.getRawY();

          AppSettings.ColorType colorType;

          //determine which button was released over
          if (mWhiteButtonLoc[1] < yloc && yloc < (mWhiteButtonLoc[1] + mWhiteButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedColor != WHITE) {
              colorType = AppSettings.ColorType.WHITE;
              onColorSelected(colorType);
            }
          } else if (mBlackButtonLoc[1] < yloc && yloc < (mBlackButtonLoc[1] + mBlackButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedColor != BLACK) {
              colorType = AppSettings.ColorType.BLACK;
              onColorSelected(colorType);
            }
          } else if (mRedButtonLoc[1] < yloc && yloc < (mRedButtonLoc[1] + mRedButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedColor != RED) {
              colorType = AppSettings.ColorType.RED;
              onColorSelected(colorType);
            }
          } else if (mGreenButtonLoc[1] < yloc && yloc < (mGreenButtonLoc[1] + mGreenButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedColor != GREEN) {
              colorType = AppSettings.ColorType.GREEN;
              onColorSelected(colorType);
            }
          } else if (mBlueButtonLoc[1] < yloc && yloc < (mBlueButtonLoc[1] + mBlueButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedColor != BLUE) {
              colorType = AppSettings.ColorType.BLUE;
              onColorSelected(colorType);
            }
          }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          //toggle if over a button
          float yloc = event.getRawY();
          if (mWhiteButtonLoc[1] < yloc && yloc < (mWhiteButtonLoc[1] + mWhiteButton
            .getHeight())) {
            toggleColorSelectorVisibility();
          } else if (mBlackButtonLoc[1] < yloc && yloc < (mBlackButtonLoc[1] + mBlackButton
            .getHeight())) {
            toggleColorSelectorVisibility();
          } else if (mRedButtonLoc[1] < yloc && yloc < (mRedButtonLoc[1] + mRedButton
            .getHeight())) {
            toggleColorSelectorVisibility();
          } else if (mGreenButtonLoc[1] < yloc && yloc < (mGreenButtonLoc[1] + mGreenButton
            .getHeight())) {
            toggleColorSelectorVisibility();
          } else if (mBlueButtonLoc[1] < yloc && yloc < (mBlueButtonLoc[1]
            + mBlueButton.getHeight())) {
            toggleColorSelectorVisibility();
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
        mWhiteButton.getLocationInWindow(mWhiteButtonLoc);
        mBlackButton.getLocationInWindow(mBlackButtonLoc);
        mRedButton.getLocationInWindow(mRedButtonLoc);
        mGreenButton.getLocationInWindow(mGreenButtonLoc);
        mBlueButton.getLocationInWindow(mBlueButtonLoc);
      }
    });

    onColorSelected(defaultTool.second);
    toggleColorSelectorVisibility();
  }

  public void handleMenu(GestureType nowGesture) {
    AppSettings.ColorType colorType = null;
    switch (nowGesture) {
      case ONE:
        colorType = AppSettings.ColorType.BLUE;
        break;
      case TWO:
        colorType = AppSettings.ColorType.GREEN;
        break;
      case THREE:
        colorType = AppSettings.ColorType.RED;
        break;
      case FOUR:
        colorType = AppSettings.ColorType.BLACK;
        break;
      case FIVE:
        colorType = AppSettings.ColorType.WHITE;
        break;
      default:
        break;
    }
    onColorSelected(colorType);
    toggleColorSelectorVisibility();
  }

  @Override
  public boolean performClick() {
    toggleColorSelectorVisibility();
    return super.performClick();
  }

  /**
   * 현재 선택한 아이콘으로 변경되어야 함
   */
  private void onColorSelected(AppSettings.ColorType colorType) {
    mSelectedColorType = colorType;

    switch (colorType) {
      case BLACK:
        mSelectedColor = BLACK;
        break;
      case RED:
        mSelectedColor = RED;
        break;
      case GREEN:
        mSelectedColor = GREEN;
        break;
      case BLUE:
        mSelectedColor = BLUE;
        break;
      default:
      case WHITE:
        mSelectedColor = WHITE;
        break;
    }
  }

  private void toggleColorSelectorVisibility() {
    if (mIsOpen) {
      float y = mBackground.getY();
      Animator.AnimatorListener hideListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(GONE);
          mWhiteButton.setVisibility(GONE);
          mBlackButton.setVisibility(GONE);
          mRedButton.setVisibility(GONE);
          mGreenButton.setVisibility(GONE);
          mBlueButton.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(0).setListener(hideListener).translationY(y);
      mBlueButton.animate().alpha(0).translationY(y);
      mGreenButton.animate().alpha(0).translationY(y);
      mRedButton.animate().alpha(0).translationY(y);
      mBlackButton.animate().alpha(0).translationY(y);
      mWhiteButton.animate().alpha(0).translationY(y);

      mBackground.setEnabled(false);
      mGreenButton.setEnabled(false);
      mBlueButton.setEnabled(false);
      mRedButton.setEnabled(false);
      mBlackButton.setEnabled(false);
      mWhiteButton.setEnabled(false);

    } else {
      Animator.AnimatorListener showListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(VISIBLE);
          mWhiteButton.setVisibility(VISIBLE);
          mBlackButton.setVisibility(VISIBLE);
          mRedButton.setVisibility(VISIBLE);
          mGreenButton.setVisibility(VISIBLE);
          mBlueButton.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(1).setListener(showListener).translationY(0);
      mBlueButton.animate().alpha(1).translationY(0);
      mGreenButton.animate().alpha(1).translationY(0);
      mRedButton.animate().alpha(1).translationY(0);
      mBlackButton.animate().alpha(1).translationY(0);
      mWhiteButton.animate().alpha(1).translationY(0);

      mBackground.setEnabled(true);
      mBlueButton.setEnabled(true);
      mGreenButton.setEnabled(true);
      mRedButton.setEnabled(true);
      mBlackButton.setEnabled(true);
      mWhiteButton.setEnabled(true);

      mBackground.setAccessibilityTraversalBefore(R.id.color_selection_blue);
      mBlueButton.setAccessibilityTraversalBefore(R.id.color_selection_green);
      mGreenButton.setAccessibilityTraversalBefore(R.id.color_selection_red);
      mRedButton.setAccessibilityTraversalBefore(R.id.color_selection_black);
      mBlackButton.setAccessibilityTraversalBefore(R.id.color_selection_white);
    }
    mIsOpen = !mIsOpen;
    DrawARActivity.finishLoading();
  }

  public AppSettings.ColorType getSelectedColorType() {
    return mSelectedColorType;
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void close() {
    if (mIsOpen) {
      toggleColorSelectorVisibility();
    }
  }

  public void toggle() {
    toggleColorSelectorVisibility();
  }

}
