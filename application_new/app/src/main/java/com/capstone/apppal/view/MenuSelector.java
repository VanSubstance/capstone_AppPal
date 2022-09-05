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
import com.capstone.apppal.R;

/**
 * Created by Kat on 11/13/17.
 * Custom view for selecting brush size
 */

public class MenuSelector extends ConstraintLayout implements View.OnClickListener {

  private static final String TAG = "ToolSelector";

  private static final int TOOL = 0;
  private static final int COLOR = 1;
  private static final int THICKNESS = 2;

  private static final Pair<Integer, AppSettings.MenuType> defaultMenu = new Pair<>(TOOL,
    AppSettings.MenuType.TOOL);

  private View mBackground;

  private View mMenuButton;

  private View mToolButton,
    mColorButton,
    mThicknessButton;

  private int mSelectedMenu = defaultMenu.first;

  private AppSettings.MenuType mSelectedMenuType = defaultMenu.second;

  private boolean mIsOpen = true;

  //the locations of the buttons
  private int mToolButtonLoc[] = new int[2];
  private int mColorButtonLoc[] = new int[2];
  private int mThicknessButtonLoc[] = new int[2];

  private ToolSelector mToolSelector;
  private ColorSelector mColorSelector;
  private BrushSelector mBrushSelector;

  public MenuSelector(Context context) {
    super(context);
    init();
  }

  public MenuSelector(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MenuSelector(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.view_main_selector, this);

    mBackground = findViewById(R.id.main_background_pie);
    mBackground.setOnClickListener(this);

    mMenuButton = findViewById(R.id.menu_button);
    mMenuButton.setOnClickListener(this);

    mToolButton = findViewById(R.id.tool_button);
    mColorButton = findViewById(R.id.color_button);
    mThicknessButton = findViewById(R.id.brush_button);

    mToolButton.setOnClickListener(this);
    mColorButton.setOnClickListener(this);
    mThicknessButton.setOnClickListener(this);

    mToolSelector = findViewById(R.id.tool_selector);
    mColorSelector = findViewById(R.id.color_selector);
    mBrushSelector = findViewById(R.id.brush_selector);

    mMenuButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          performClick();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && mIsOpen) {
          //get the point where we let go
          float yloc = event.getRawY();

          AppSettings.MenuType menuType = null;

          //determine which button was released over
          if (mToolButtonLoc[1] < yloc && yloc < (mToolButtonLoc[1] + mToolButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedMenu != TOOL) {
              menuType = AppSettings.MenuType.TOOL;
              onMenuSelected(menuType);
            }
          } else if (mColorButtonLoc[1] < yloc && yloc < (mColorButtonLoc[1] + mColorButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedMenu != COLOR) {
              menuType = AppSettings.MenuType.COLOR;
              onMenuSelected(menuType);
            }
          } else if (mThicknessButtonLoc[1] < yloc && yloc < (mThicknessButtonLoc[1] + mThicknessButton
            .getHeight())) {
            //prevent calling an update when not needed
            if (mSelectedMenu != THICKNESS) {
              menuType = AppSettings.MenuType.THICKNESS;
              onMenuSelected(menuType);
            }
          }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          //toggle if over a button
          float yloc = event.getRawY();
          if (mToolButtonLoc[1] < yloc && yloc < (mToolButtonLoc[1] + mToolButton
            .getHeight())) {
            toggleMenuSelectorVisibility();
          } else if (mColorButtonLoc[1] < yloc && yloc < (mColorButtonLoc[1] + mColorButton
            .getHeight())) {
            toggleMenuSelectorVisibility();
          } else if (mThicknessButtonLoc[1] < yloc && yloc < (mThicknessButtonLoc[1] + mThicknessButton
            .getHeight())) {
            toggleMenuSelectorVisibility();
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
        mToolButton.getLocationInWindow(mToolButtonLoc);
        mColorButton.getLocationInWindow(mColorButtonLoc);
        mThicknessButton.getLocationInWindow(mThicknessButtonLoc);
      }
    });

    onMenuSelected(defaultMenu.second);
    closeChildren(null);
  }

  @Override
  public void onClick(View view) {

    AppSettings.MenuType menuType = null;
    switch (view.getId()) {
      case R.id.menu_button:
      case R.id.main_background_pie:
        toggleMenuSelectorVisibility();
        return;
      case R.id.tool_button:
        menuType = AppSettings.MenuType.TOOL;
        break;
      case R.id.color_button:
        menuType = AppSettings.MenuType.COLOR;
        break;
      case R.id.brush_button:
        menuType = AppSettings.MenuType.THICKNESS;
        break;
    }

    onMenuSelected(menuType);

    toggleMenuSelectorVisibility();
  }

  @Override
  public boolean performClick() {
    toggleMenuSelectorVisibility();
    return super.performClick();
  }

  /**
   * 현재 선택한 아이콘으로 변경되어야 함
   * 전체 메뉴 아이콘은 변경되어서는 안됨
   */
  private void onMenuSelected(AppSettings.MenuType menuType) {
    mSelectedMenuType = menuType;
    switch (menuType) {
      default:
      case TOOL:
//        mSelectedMenuIndicator.setImageResource(R.drawable.ic_clear);
        mSelectedMenu = TOOL;
        break;
      case COLOR:
//        mSelectedMenuIndicator.setImageResource(R.drawable.ic_selection_straight_line);
        mSelectedMenu = COLOR;
        break;
      case THICKNESS:
//        mSelectedMenuIndicator.setImageResource(R.drawable.ic_selection_cube);
        mSelectedMenu = THICKNESS;
        break;
    }

    closeChildren(menuType);
  }

  private void toggleMenuSelectorVisibility() {
    if (mIsOpen) {
      float y = mBackground.getY();
      Animator.AnimatorListener hideListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(GONE);
          mToolButton.setVisibility(GONE);
          mColorButton.setVisibility(GONE);
          mThicknessButton.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };
      mBackground.animate().alpha(0).setListener(hideListener).translationY(y);
      mThicknessButton.animate().alpha(0).translationY(y);
      mColorButton.animate().alpha(0).translationY(y);
      mToolButton.animate().alpha(0).translationY(y);

      mBackground.setEnabled(false);
      mThicknessButton.setEnabled(false);
      mColorButton.setEnabled(false);
      mToolButton.setEnabled(false);

    } else {
      Animator.AnimatorListener showListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mBackground.setVisibility(VISIBLE);
          mToolButton.setVisibility(VISIBLE);
          mColorButton.setVisibility(VISIBLE);
          mThicknessButton.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      };

      mBackground.animate().alpha(1).setListener(showListener).translationY(0);
      mThicknessButton.animate().alpha(1).translationY(0);
      mColorButton.animate().alpha(1).translationY(0);
      mToolButton.animate().alpha(1).translationY(0);

      mBackground.setEnabled(true);
      mThicknessButton.setEnabled(true);
      mColorButton.setEnabled(true);
      mToolButton.setEnabled(true);

      mMenuButton.setAccessibilityTraversalBefore(R.id.brush_button);
      mThicknessButton.setAccessibilityTraversalBefore(R.id.color_button);
      mColorButton.setAccessibilityTraversalBefore(R.id.tool_button);
    }
    mIsOpen = !mIsOpen;
  }

  public AppSettings.MenuType getSelectedMenuType() {
    return mSelectedMenuType;
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void close() {
    if (mIsOpen) {
      toggleMenuSelectorVisibility();
    }
  }

  public void closeChildren(AppSettings.MenuType exception) {
    Log.e(TAG, "closeChildren: 열거:: " + exception);
    if (exception == null) {
      close();
      mToolSelector.close();
      mColorSelector.close();
      mBrushSelector.close();
    } else {
      switch (exception) {
        case TOOL:
          mToolSelector.toggle();
          mColorSelector.close();
          mBrushSelector.close();
          break;
        case COLOR:
          mToolSelector.close();
          mColorSelector.toggle();
          mBrushSelector.close();
          break;
        case THICKNESS:
          mToolSelector.close();
          mColorSelector.close();
          mBrushSelector.toggle();
          break;
        default:
          mToolSelector.close();
          mColorSelector.close();
          mBrushSelector.close();
          break;
      }
    }
  }

  public ToolSelector getToolSelector() {
    return mToolSelector;
  }

  public void setToolSelector(ToolSelector mToolSelector) {
    this.mToolSelector = mToolSelector;
  }

  public ColorSelector getColorSelector() {
    return mColorSelector;
  }

  public void setColorSelector(ColorSelector mColorSelector) {
    this.mColorSelector = mColorSelector;
  }

  public BrushSelector getBrushSelector() {
    return mBrushSelector;
  }

  public void setBrushSelector(BrushSelector mBrushSelector) {
    this.mBrushSelector = mBrushSelector;
  }

}
