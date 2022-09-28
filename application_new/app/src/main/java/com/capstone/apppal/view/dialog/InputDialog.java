package com.capstone.apppal.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.capstone.apppal.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class InputDialog extends Dialog {
  private final static String TAG = "InputDialog";
  private TextView mMainText;
  private EditText mEditText;
  private Button mMainButton;
  private Button mSubButton;

  private DataTransfer dataTransfer;

  public InputDialog(@NonNull Context context) {
    super(context);
  }

  public InputDialog(@NonNull Context context, DataTransfer dataTransfer) {
    super(context);
    this.dataTransfer = dataTransfer;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_input);

    mMainText = findViewById(R.id.text_main);
    mEditText = findViewById(R.id.text_input);
    mMainButton = findViewById(R.id.button_main);
    mSubButton = findViewById(R.id.button_sub);

    Data dataSet = dataTransfer.getData();

    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(dataSet.getMaxLength())});
    if (dataSet.getIsEncrypted()) {
      mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }
    if (dataSet.getTextEdit() != null) {
      mEditText.setText(dataSet.getTextEdit());
    }

    if (dataSet.getTextMain() != null) {
      mMainText.setText(dataSet.getTextMain());
    }
    mMainButton.setText(dataSet.getTextMainButton());
    mMainButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mEditText.getText().length() < dataSet.getMinLength()) {
          Toast.makeText(getContext(), "너무 짧습니다!", Toast.LENGTH_SHORT).show();
        } else {
          dataTransfer.onMainButtonClick(String.valueOf(mEditText.getText()));
          dismiss();
        }
      }
    });
    if (dataSet.getTextSubButton() != null) {
      mSubButton.setText(dataSet.getTextSubButton());
      mSubButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          dataTransfer.onSubButtonClick(String.valueOf(mEditText.getText()));
          dismiss();
        }
      });
    } else {
      mSubButton.setVisibility(View.GONE);
    }

  }

  public interface DataTransfer {
    Data getData();

    void onMainButtonClick(String inputText);

    void onSubButtonClick(String inputText);
  }

  public static class Data {
    private String textMain;
    private String textEdit;
    private int maxLength = 6;
    private int minLength = 4;
    private String textMainButton;
    private String textSubButton;
    private boolean isEncrypted = false;

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

    public boolean getIsEncrypted() {
      return isEncrypted;
    }

    public void setIsEncrypted(boolean isEncrypted) {
      this.isEncrypted = isEncrypted;
    }

    public int getMaxLength() {
      return maxLength;
    }

    public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
    }

    public String getTextEdit() {
      return textEdit;
    }

    public void setTextEdit(String textEdit) {
      this.textEdit = textEdit;
    }

    public int getMinLength() {
      return minLength;
    }

    public void setMinLength(int minLength) {
      this.minLength = minLength;
    }
  }
}
