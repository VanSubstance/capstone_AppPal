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

import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.apppal.AppSettings;
import com.capstone.apppal.R;

/**
 * Created by Kat on 11/13/17.
 * Custom view for selecting brush size
 */

public class ToolSelector extends ConstraintLayout implements View.OnClickListener {

  private static final String TAG = "BrushSelector";

  private static final int NORMAL_PEN = 0;

  private static final int ERASE = 1;

  private static final Pair<Integer, AppSettings.ToolType> defaultTool = new Pair<>(NORMAL_PEN,
      AppSettings.ToolType.NORMAL_PEN);

  private View mToolButton;

  private View mNormalPenButton, mEraseButton;

  private View mSelectedToolIndicator;

  private int mSelectedTool = defaultTool.first;

  private AppSettings.ToolType mSelectedToolType = defaultTool.second;

  private boolean mIsOpen = true;

  //the locations of the buttons
  private int mNormalButtonLoc[] = new int[2];

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

    mToolButton = findViewById(R.id.tool_button);
    mToolButton.setOnClickListener(this);

    mSelectedToolIndicator = findViewById(R.id.selected_tool_indicator);

    mNormalPenButton = findViewById(R.id.tool_selection_pen);
    mEraseButton = findViewById(R.id.tool_selection_erase);

    mNormalPenButton.setOnClickListener(this);
    mEraseButton.setOnClickListener(this);

    mToolButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          performClick();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && mIsOpen) {
          //get the point where we let go
          float yloc = event.getRawY();

          AppSettings.ToolType toolType;

          //determine which button was released over
          if (mNormalButtonLoc[1] < yloc && yloc < (mNormalButtonLoc[1] + mNormalPenButton
              .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedTool != NORMAL_PEN) {
              toolType = AppSettings.ToolType.NORMAL_PEN;
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
          if (mNormalButtonLoc[1] < yloc && yloc < (mNormalButtonLoc[1] + mNormalPenButton
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
        mNormalPenButton.getLocationInWindow(mNormalButtonLoc);
        mEraseButton.getLocationInWindow(mEraseButtonLoc);
      }
    });

    onToolSelected(defaultTool.second);
    toggleToolSelectorVisibility();
  }

  @Override
  public void onClick(View view) {

    AppSettings.ToolType toolType = null;
    switch (view.getId()) {
      case R.id.tool_button:
        toggleToolSelectorVisibility();
        return;
      case R.id.tool_selection_pen:
        toolType = AppSettings.ToolType.NORMAL_PEN;
        break;
      case R.id.tool_selection_erase:
        toolType = AppSettings.ToolType.ERASE;
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

  private void onToolSelected(AppSettings.ToolType toolType) {
    mSelectedToolType = toolType;

    TypedValue outValue = new TypedValue();

    switch (toolType) {
      case ERASE:
        getResources().getValue(R.drawable.ic_clear, outValue, true);
        mSelectedTool = ERASE;
        break;
      default:
      case NORMAL_PEN:
        getResources().getValue(R.drawable.brush_option_bg, outValue, true);
        mSelectedTool = NORMAL_PEN;
        break;
    }

    float scale = outValue.getFloat();

    mSelectedToolIndicator.animate().scaleX(scale).scaleY(scale);
  }

  private void toggleToolSelectorVisibility() {
    if (mIsOpen) {
      float y = mSelectedToolIndicator.getY();
      Animator.AnimatorListener hideListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mNormalPenButton.setVisibility(GONE);
          mEraseButton.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mEraseButton.animate().alpha(0).setListener(hideListener).translationY(y);
      mNormalPenButton.animate().alpha(0).translationY(y);
      mEraseButton.setEnabled(false);
      mNormalPenButton.setEnabled(false);

    } else {
      Animator.AnimatorListener showListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mNormalPenButton.setVisibility(VISIBLE);
          mEraseButton.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mEraseButton.animate().alpha(1).setListener(showListener).translationY(0);
      mNormalPenButton.animate().alpha(1).translationY(0);
      mEraseButton.setEnabled(true);
      mNormalPenButton.setEnabled(true);

      mToolButton.setAccessibilityTraversalBefore(R.id.tool_selection_erase);
      mEraseButton.setAccessibilityTraversalBefore(R.id.tool_selection_pen);
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

}
