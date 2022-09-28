package com.capstone.apppal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.capstone.apppal.VO.RoomsInfo;
import com.capstone.apppal.VO.UserInfo;
import com.capstone.apppal.utils.GlobalState;
import com.capstone.apppal.view.fragments.ListFragment;
import com.capstone.apppal.view.fragments.LoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class OnBoardingActivity extends AppCompatActivity {

  /**
   * 화면 이동용 변수들
   */
  private FragmentManager fragmentManager;
  private LoginFragment loginFragment;
  private ListFragment listFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_onboarding);
    loginFragment = new LoginFragment();

    if (savedInstanceState == null) {
      fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().add(R.id.fragment_frame, loginFragment).commit();
    }
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
  }

  /**
   * @param roomInfo
   * @구현 단순히 진입이 아닌 타겟 방 진입:: 신규도 마찬가지임
   */
  public void enterDrawingRoom(RoomsInfo roomInfo) {
    GlobalState.currentRoomInfo = roomInfo;
    Intent drawingIntent = new Intent(this, DrawARActivity.class);
    startActivity(drawingIntent);
    finish();
  }
}
