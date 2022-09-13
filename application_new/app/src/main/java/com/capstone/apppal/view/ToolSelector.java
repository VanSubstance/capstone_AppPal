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

public class ToolSelector extends ConstraintLayout {

  private static final String TAG = "ToolSelector";

  private static final int NORMAL_PEN = 0;
  private static final int STRAIGHT_LINE = 1;
  private static final int CUBE = 2;
  private static final int RECT = 4;
  private static final int ERASE = 3;

  private static final Pair<Integer, AppSettings.ToolType> defaultTool = new Pair<>(NORMAL_PEN,
    AppSettings.ToolType.NORMAL_PEN);

  private View mBackground;

  private View mNormalPenButton,
    mStraightLineButton,
    mCubeButton,
    mRectButton,
    mEraseButton;

  private int mSelectedTool = defaultTool.first;

  private AppSettings.ToolType mSelectedToolType = defaultTool.second;

  private boolean mIsOpen = true;

  //the locations of the buttons
  private int mNormalPenButtonLoc[] = new int[2];
  private int mStraightLineButtonLoc[] = new int[2];
  private int mCubeButtonLoc[] = new int[2];
  private int mRectButtonLoc[] = new int[2];
  private int mEraseButtonLoc[] = new int[2];

  public ToolSelector(Context context) {
    super(context);
    init();
  }

  public ToolSelector(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ToolSelector(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.view_tool_selector, this);

    mBackground = findViewById(R.id.tool_background_pie);

    mNormalPenButton = findViewById(R.id.tool_selection_pen);
    mStraightLineButton = findViewById(R.id.tool_selection_line);
    mCubeButton = findViewById(R.id.tool_selection_cube);
    mRectButton = findViewById(R.id.tool_selection_rect);
    mEraseButton = findViewById(R.id.tool_selection_erase);

    mBackground.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          performClick();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && mIsOpen) {
          //get the point where we let go
          float yloc = event.getRawY();

          AppSettings.ToolType toolType;

          //determine which button was released over
          if (mNormalPenButtonLoc[1] < yloc && yloc < (mNormalPenButtonLoc[1] + mNormalPenButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedTool != NORMAL_PEN) {
              toolType = AppSettings.ToolType.NORMAL_PEN;
              onToolSelected(toolType);
            }
          } else if (mStraightLineButtonLoc[1] < yloc && yloc < (mStraightLineButtonLoc[1] + mStraightLineButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedTool != STRAIGHT_LINE) {
              toolType = AppSettings.ToolType.STRAIGHT_LINE;
              onToolSelected(toolType);
            }
          } else if (mCubeButtonLoc[1] < yloc && yloc < (mCubeButtonLoc[1] + mCubeButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedTool != CUBE) {
              toolType = AppSettings.ToolType.CUBE;
              onToolSelected(toolType);
            }
          } else if (mRectButtonLoc[1] < yloc && yloc < (mRectButtonLoc[1] + mRectButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedTool != RECT) {
              toolType = AppSettings.ToolType.RECT;
              onToolSelected(toolType);
            }
          } else if (mEraseButtonLoc[1] < yloc && yloc < (mEraseButtonLoc[1]
            + mEraseButton.getHeight())) {
            if (mSelectedTool != ERASE) {
              toolType = AppSettings.ToolType.ERASE;
              onToolSelected(toolType);
            }
          }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          //toggle if over a button
          float yloc = event.getRawY();
          if (mNormalPenButtonLoc[1] < yloc && yloc < (mNormalPenButtonLoc[1] + mNormalPenButton
            .getHeight())) {
            toggleToolSelectorVisibility();
          } else if (mStraightLineButtonLoc[1] < yloc && yloc < (mStraightLineButtonLoc[1] + mStraightLineButton
            .getHeight())) {
            toggleToolSelectorVisibility();
          } else if (mCubeButtonLoc[1] < yloc && yloc < (mCubeButtonLoc[1] + mCubeButton
            .getHeight())) {
            toggleToolSelectorVisibility();
          } else if (mRectButtonLoc[1] < yloc && yloc < (mRectButtonLoc[1] + mRectButton
            .getHeight())) {
            toggleToolSelectorVisibility();
          } else if (mEraseButtonLoc[1] < yloc && yloc < (mEraseButtonLoc[1]
            + mEraseButton.getHeight())) {
            toggleToolSelectorVisibility();
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
        mNormalPenButton.getLocationInWindow(mNormalPenButtonLoc);
        mStraightLineButton.getLocationInWindow(mStraightLineButtonLoc);
        mCubeButton.getLocationInWindow(mCubeButtonLoc);
        mRectButton.getLocationInWindow(mRectButtonLoc);
        mEraseButton.getLocationInWindow(mEraseButtonLoc);
      }
    });

    onToolSelected(defaultTool.second);
    toggleToolSelectorVisibility();
  }

  public void handleMenu(GestureType nowGesture) {
    AppSettings.ToolType toolType = null;
    switch (nowGesture) {
      case ONE:
        toolType = AppSettings.ToolType.ERASE;
        break;
      case TWO:
        toolType = AppSettings.ToolType.RECT;
        break;
      case THREE:
        toolType = AppSettings.ToolType.CUBE;
        break;
      case FOUR:
        toolType = AppSettings.ToolType.STRAIGHT_LINE;
        break;
      case FIVE:
        toolType = AppSettings.ToolType.NORMAL_PEN;
        break;
      default:
        break;
    }
    onToolSelected(toolType);
    toggleToolSelectorVisibility();
  }

  @Override
  public boolean performClick() {
    toggleToolSelectorVisibility();
    return super.performClick();
  }

  /**
   * 현재 선택한 아이콘으로 변경되어야 함
   */
  private void onToolSelected(AppSettings.ToolType toolType) {
    mSelectedToolType = toolType;
    switch (toolType) {
      case ERASE:
        mSelectedTool = ERASE;
        break;
      case STRAIGHT_LINE:
        mSelectedTool = STRAIGHT_LINE;
        break;
      case CUBE:
        mSelectedTool = CUBE;
        break;
      case RECT:
        mSelectedTool = RECT;
        break;
      default:
      case NORMAL_PEN:
        mSelectedTool = NORMAL_PEN;
        break;
    }
  }

  private void toggleToolSelectorVisibility() {
    if (mIsOpen) {
//      float y = mSelectedToolIndicator.getY();
      float y = mBackground.getY();
      Animator.AnimatorListener hideListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(GONE);
          mNormalPenButton.setVisibility(GONE);
          mStraightLineButton.setVisibility(GONE);
          mCubeButton.setVisibility(GONE);
          mRectButton.setVisibility(GONE);
          mEraseButton.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(0).setListener(hideListener).translationY(y);
      mEraseButton.animate().alpha(0).translationY(y);
      mRectButton.animate().alpha(0).translationY(y);
      mCubeButton.animate().alpha(0).translationY(y);
      mStraightLineButton.animate().alpha(0).translationY(y);
      mNormalPenButton.animate().alpha(0).translationY(y);

      mBackground.setEnabled(false);
      mEraseButton.setEnabled(false);
      mRectButton.setEnabled(false);
      mCubeButton.setEnabled(false);
      mStraightLineButton.setEnabled(false);
      mNormalPenButton.setEnabled(false);

    } else {
      Animator.AnimatorListener showListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(VISIBLE);
          mNormalPenButton.setVisibility(VISIBLE);
          mStraightLineButton.setVisibility(VISIBLE);
          mCubeButton.setVisibility(VISIBLE);
          mRectButton.setVisibility(VISIBLE);
          mEraseButton.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(1).setListener(showListener).translationY(0);
      mEraseButton.animate().alpha(1).translationY(0);
      mRectButton.animate().alpha(1).translationY(0);
      mCubeButton.animate().alpha(1).translationY(0);
      mStraightLineButton.animate().alpha(1).translationY(0);
      mNormalPenButton.animate().alpha(1).translationY(0);

      mBackground.setEnabled(true);
      mEraseButton.setEnabled(true);
      mRectButton.setEnabled(true);
      mCubeButton.setEnabled(true);
      mStraightLineButton.setEnabled(true);
      mNormalPenButton.setEnabled(true);

      mBackground.setAccessibilityTraversalBefore(R.id.tool_selection_erase);
      mEraseButton.setAccessibilityTraversalBefore(R.id.tool_selection_rect);
      mRectButton.setAccessibilityTraversalBefore(R.id.tool_selection_cube);
      mCubeButton.setAccessibilityTraversalBefore(R.id.tool_selection_pen);
      mStraightLineButton.setAccessibilityTraversalBefore(R.id.tool_selection_line);
    }
    mIsOpen = !mIsOpen;
  }

  public AppSettings.ToolType getSelectedToolType() {
    return mSelectedToolType;
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void close() {
    if (mIsOpen) {
      toggleToolSelectorVisibility();
    }
  }

  public void toggle() {
    toggleToolSelectorVisibility();
  }

}
