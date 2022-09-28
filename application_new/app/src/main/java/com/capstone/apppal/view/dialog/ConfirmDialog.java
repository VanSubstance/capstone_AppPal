package com.capstone.apppal.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.capstone.apppal.R;

public class ConfirmDialog extends Dialog {
  private TextView mMainText;
  private Button mMainButton;
  private Button mSubButton;

  private DataTransfer dataTransfer;

  public ConfirmDialog(@NonNull Context context) {
    super(context);
  }

  public ConfirmDialog(@NonNull Context context, DataTransfer dataTransfer) {
    super(context);
    this.dataTransfer = dataTransfer;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_confirm);

    mMainText = findViewById(R.id.text_main);
    mMainButton = findViewById(R.id.button_main);
    mSubButton = findViewById(R.id.button_sub);

    Data dataSet = dataTransfer.getData();

    if (dataSet.getTextMain() != null) {
      mMainText.setText(dataSet.getTextMain());
    } else {
      mMainText.setVisibility(View.GONE);
    }
    if (dataSet.getTextMainButton() != null) {
      mMainButton.setText(dataSet.getTextMainButton());
      mMainButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          dataTransfer.onMainButtonClick();
          dismiss();
        }
      });
    } else {
      mMainButton.setVisibility(View.GONE);
    }
    if (dataSet.getTextSubButton() != null) {
      mSubButton.setText(dataSet.getTextSubButton());
      mSubButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          dataTransfer.onSubButtonClick();
          dismiss();
        }
      });
    } else {
      mSubButton.setVisibility(View.GONE);
    }

  }

  public interface DataTransfer {
    Data getData();

    void onMainButtonClick();

    void onSubButtonClick();
  }

  public static class Data {
    private String textMain;
    private String textMainButton;
    private String textSubButton;

    public String getTextMain() {
      return textMain;
    }

    public void setTextMain(String text) {
      this.textMain = text;
    }

    public String getTextMainButton() {
      return textMainButton;
    }

    public void setTextMainButton(String textMainButton) {
      this.textMainButton = textMainButton;
    }

    public String getTextSubButton() {
      return textSubButton;
    }

    public void setTextSubButton(String textSubButton) {
      this.textSubButton = textSubButton;
    }
  }
}
