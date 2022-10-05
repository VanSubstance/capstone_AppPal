package com.capstone.apppal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.capstone.apppal.VO.RoomsInfo;
import com.capstone.apppal.utils.GlobalState;
import com.capstone.apppal.view.fragments.ListFragment;
import com.capstone.apppal.view.fragments.LoginFragment;

import java.util.HashMap;

public class OnBoardingActivity extends AppCompatActivity {
  private final static String TAG = "OnBoardingActivity";

  private ProgressBar mOnBoardingProgressBar;
  private final static int LOADING_INIT = 0;
  private final static int LOADING_DONE = 1;
  /**
   * 화면 이동용 변수들
   */
  private FragmentManager fragmentManager;
  private LoginFragment loginFragment;
  private ListFragment listFragment;

  public static RoomHandler roomHandler;

  /**
   * 로딩 컴포넌트를 on/off 해줄 핸들러
   */
  private Handler loadingHandler = new Handler() {
    public void handleMessage(Message message) {
      if (message.arg1 == LOADING_INIT) {
        mOnBoardingProgressBar.setVisibility(View.VISIBLE);
      } else {
        mOnBoardingProgressBar.setVisibility(View.GONE);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_onboarding);
    mOnBoardingProgressBar = findViewById(R.id.progress_onboard);
    loginFragment = new LoginFragment();

    if (savedInstanceState == null) {
      fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().add(R.id.fragment_frame, loginFragment).commit();
    }

    roomHandler = new RoomHandler();
  }

  public void goToListFragment(int optionMode) {
    // 로그인 직후일 경우
    if (optionMode == ListFragment.CREATE_OPTION_MODE) {
      listFragment = new ListFragment(optionMode);
      fragmentManager.beginTransaction().replace(R.id.fragment_frame, listFragment).commit();
    } else {
      listFragment = new ListFragment(optionMode);
      fragmentManager.beginTransaction().replace(R.id.fragment_frame, listFragment).addToBackStack("list::" + optionMode).commit();
    }
  }

  public void goToListFragment(int optionMode, HashMap<String, Object>[] dataSet) {
    // 로그인 직후일 경우
    if (optionMode == ListFragment.CREATE_OPTION_MODE) {
      listFragment = new ListFragment(optionMode, dataSet);
      fragmentManager.beginTransaction().replace(R.id.fragment_frame, listFragment).commit();
    } else {
      listFragment = new ListFragment(optionMode, dataSet);
      fragmentManager.beginTransaction().replace(R.id.fragment_frame, listFragment).addToBackStack("list::" + optionMode).commit();
    }
    finishLoading();
  }

  public void enterDrawingRoom(RoomsInfo roomInfo) {
    GlobalState.currentRoomInfo = roomInfo;
    roomHandler.getStrokeInfo(roomInfo.getRoomCode(), data -> {
      Intent drawingIntent = new Intent(this, DrawARActivity.class);
      startActivity(drawingIntent);
      finish();
    });
  }

  public void initLoading() {
    Message message = loadingHandler.obtainMessage();
    message.arg1 = LOADING_INIT;
    loadingHandler.sendMessage(message);
  }

  public void finishLoading() {
    Message message = loadingHandler.obtainMessage();
    message.arg1 = LOADING_DONE;
    loadingHandler.sendMessage(message);
  }
}
