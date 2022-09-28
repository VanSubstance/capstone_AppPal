package com.capstone.apppal.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.capstone.apppal.R;

public class ChoiceDialog extends Dialog {
  private TextView mMainText;
  private Button mButton1;
  private Button mButton2;
  private Button mButton3;

  private DataTransfer dataTransfer;

  public ChoiceDialog(@NonNull Context context) {
    super(context);
  }

  public ChoiceDialog(@NonNull Context context, DataTransfer dataTransfer) {
    super(context);
    this.dataTransfer = dataTransfer;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_choice);

    mMainText = findViewById(R.id.text_main);
    mButton1 = findViewById(R.id.button_1);
    mButton2 = findViewById(R.id.button_2);
    mButton3 = findViewById(R.id.button_3);

    Data dataSet = dataTransfer.getData();

    if (dataSet.getTextMain() != null) {
      mMainText.setText(dataSet.getTextMain());
    } else {
      mMainText.setVisibility(View.GONE);
    }
    mButton1.setText(dataSet.getTextButton1());
    mButton1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dataTransfer.onButtonClick1();
        dismiss();
      }
    });
    if (dataSet.getTextButton2() != null) {
      mButton2.setText(dataSet.getTextButton2());
      mButton2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          dataTransfer.onButtonClick2();
          dismiss();
        }
      });
    } else {
      mButton2.setVisibility(View.GONE);
    }
    if (dataSet.getTextButton3() != null) {
      mButton3.setText(dataSet.getTextButton3());
      mButton3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          dataTransfer.onButtonClick3();
          dismiss();
        }
      });
    } else {
      mButton3.setVisibility(View.GONE);
    }

  }

  public interface DataTransfer {
    Data getData();

    void onButtonClick1();

    void onButtonClick2();

    void onButtonClick3();
  }

  public static class Data {
    private String textMain;
    private String textButton1;
    private String textButton2;
    private String textButton3;

    public String getTextMain() {
      return textMain;
    }

    public void setTextMain(String text) {
      this.textMain = text;
    }

    public String getTextButton1() {
      return textButton1;
    }

    public void setTextButton1(String textButton1) {
      this.textButton1 = textButton1;
    }

    public String getTextButton2() {
      return textButton2;
    }

    public void setTextButton2(String textButton2) {
      this.textButton2 = textButton2;
    }

    public String getTextButton3() {
      return textButton3;
    }

    public void setTextButton3(String textButton3) {
      this.textButton3 = textButton3;
    }
  }
}
