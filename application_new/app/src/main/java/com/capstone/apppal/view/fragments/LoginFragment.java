

package com.capstone.apppal.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.capstone.apppal.OnBoardingActivity;
import com.capstone.apppal.R;
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
import com.capstone.apppal.utils.GlobalState;

public class LoginFragment extends Fragment {
  private final static String TAG = "LoginFragment";
  private FirebaseAuth mAuth = null;
  private GoogleSignInClient mGoogleSignInClient;
  private static final int RC_SIGN_IN = 9001;
  private SignInButton signInButton;
  private FirebaseDatabase database;
  private DatabaseReference databaseReference;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_login, container, false);
    init(rootView);
    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Save currently selected layout manager.
    super.onSaveInstanceState(savedInstanceState);
  }

  private void init(View rootView) {
    signInButton = rootView.findViewById(R.id.login_button);
    mAuth = FirebaseAuth.getInstance();

    //Configure Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mGoogleSignInClient = GoogleSignIn.getClient(this.getContext(), gso);

    signInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // 여기에서 작업하면 될듯
        signIn();
      }
    });

  }

  private void signIn() {
    ((OnBoardingActivity) getActivity()).initLoading();

    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = task.getResult(ApiException.class);
        firebaseAuthWithGoogle(account);
      } catch (ApiException e) {
        ((OnBoardingActivity) getActivity()).finishLoading();
      }
    }
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          // Sign in success, update UI with the signed-in user's information
          database = FirebaseDatabase.getInstance();
          databaseReference = database.getReference();
          FirebaseUser user = mAuth.getCurrentUser();
          String uid = user.getUid();
          GlobalState.useruid = uid;
          databaseReference.child("Users").child(uid).child("email").setValue(acct.getEmail());
          databaseReference.child("Users").child(uid).child("id").setValue(acct.getId());
          databaseReference.child("Users").child(uid).child("name").setValue(acct.getDisplayName());
          updateUI(user);
        } else {
          // If sign in fails, display a message to the user.
          updateUI(null);
        }
      }
    });
  }

  private void updateUI(FirebaseUser user) { //update ui code here
    ((OnBoardingActivity) getActivity()).finishLoading();
    if (user != null) {
      ((OnBoardingActivity) getActivity()).goToListFragment(ListFragment.CREATE_OPTION_MODE);
    }
  }
}

