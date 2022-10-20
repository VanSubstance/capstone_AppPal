package com.capstone.apppal.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.apppal.AppSettings;
import com.capstone.apppal.R;

public class SelectedOption extends ConstraintLayout {
  private ImageView mThicknessSelection;
  private ImageView mColorSelection;
  private ImageView mToolSelection;
  private final static int TOGGLE_TOOL = 0;
  private final static int TOGGLE_COLOR = 1;
  private final static int TOGGLE_WIDTH = 2;

  private Handler toggleHandler = new Handler() {
    public void handleMessage(Message message) {
      if (message.arg1 == TOGGLE_TOOL) {
        setSelection((AppSettings.ToolType) message.obj);
      } else if (message.arg1 == TOGGLE_COLOR) {
        setSelection((AppSettings.ColorType) message.obj);
      } else if (message.arg1 == TOGGLE_WIDTH) {
        setSelection((AppSettings.LineWidth) message.obj);
      }
    }
  };

  public SelectedOption(Context context) {
    super(context);
    init();
  }

  public SelectedOption(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SelectedOption(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.view_selected_option, this);
    mThicknessSelection = findViewById(R.id.brush_selection_img);
    mColorSelection = findViewById(R.id.color_selection_img);
    mToolSelection = findViewById(R.id.tool_selection_img);
  }

  private void setSelection(AppSettings.ToolType toolType) {
    switch (toolType) {
      case ERASE:
        mToolSelection.setImageResource(R.drawable.ic_clear);
        break;
      case STRAIGHT_LINE:
        mToolSelection.setImageResource(R.drawable.ic_selection_straight_line);
        break;
      case CUBE:
        mToolSelection.setImageResource(R.drawable.ic_selection_cube);
        break;
      case RECT:
        mToolSelection.setImageResource(R.drawable.ic_selection_rect);
        break;
      default:
      case NORMAL_PEN:
        mToolSelection.setImageResource(R.drawable.ic_brush_size_option);
        break;
    }
  }

  private void setSelection(AppSettings.ColorType colorType) {
    switch (colorType) {
      case BLACK:
        mColorSelection.setImageResource(R.drawable.ic_color_black);
        break;
      case RED:
        mColorSelection.setImageResource(R.drawable.ic_color_red);
        break;
      case GREEN:
        mColorSelection.setImageResource(R.drawable.ic_color_green);
        break;
      case BLUE:
        mColorSelection.setImageResource(R.drawable.ic_color_blue);
        break;
      default:
      case WHITE:
        mColorSelection.setImageResource(R.drawable.ic_brush_size_option);
        break;
    }
  }

  private void setSelection(AppSettings.LineWidth lineWidth) {
    switch (lineWidth) {
      case SMALL:
        mThicknessSelection.setImageResource(R.drawable.ic_brush_size_small);
        break;
      case MEDIUM:
        mThicknessSelection.setImageResource(R.drawable.ic_brush_size_medium);
        break;
      default:
      case LARGE:
        mThicknessSelection.setImageResource(R.drawable.ic_brush_size_option);
        break;
    }
  }

  public void setSelectionByHandler(AppSettings.ToolType var) {
    Message message = toggleHandler.obtainMessage();
    message.arg1 = TOGGLE_TOOL;
    message.obj = var;
    toggleHandler.sendMessage(message);
  }

  public void setSelectionByHandler(AppSettings.ColorType var) {
    Message message = toggleHandler.obtainMessage();
    message.arg1 = TOGGLE_COLOR;
    message.obj = var;
    toggleHandler.sendMessage(message);
  }

  public void setSelectionByHandler(AppSettings.LineWidth var) {
    Message message = toggleHandler.obtainMessage();
    message.arg1 = TOGGLE_WIDTH;
    message.obj = var;
    toggleHandler.sendMessage(message);
  }
}
