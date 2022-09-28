package com.capstone.apppal;

import com.capstone.apppal.VO.RoomsInfo;
import com.capstone.apppal.network.SimpleCallback;
import com.capstone.apppal.utils.GlobalState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class RoomHandler implements Observer {
  private final static String TAG = "RoomHandler";
  private FirebaseDatabase database = FirebaseDatabase.getInstance();

  private DatabaseReference databaseReference = database.getReference();
  protected boolean isPairing = false;
  private static final String ROOT_FIREBASE_ROOMS = "rooms";
  private String roomcode;
  DatabaseReference roomsListRef = databaseReference.child(ROOT_FIREBASE_ROOMS);

  public void singleRoomCreate(RoomsInfo roomsInfo) {
    if (isPairing == false) {
      while (true) {
        makeRoomCode();
        if (checkingRoomCode(roomcode)) {
          break;
        }
      }
      GlobalState.inviteCode = makeInviteCode();
      Long timestamp = System.currentTimeMillis();
      roomsInfo.setTimestamp(timestamp);
      roomsListRef.child(roomcode).setValue(roomsInfo);
    }
  }

  public void makeRoomCode() {
    roomcode = "";
    ArrayList<Integer> numcode = new ArrayList();
    ArrayList<Character> engcode = new ArrayList();
    for (int i = 0; i < 3; i++) {
      numcode.add((int) (Math.random() * 10));
    }
    for (int i = 0; i < 3; i++) {
      engcode.add((char) ((Math.random() * 26) + 65));
    }
    for (int i = 0; i < 3; i++) {
      roomcode = roomcode.concat(String.valueOf(numcode.get(i)));
      roomcode = roomcode.concat(String.valueOf(engcode.get(i)));
    }
  }

  public String makeInviteCode() {
    String invite = "";
    ArrayList<Integer> numcode = new ArrayList();
    ArrayList<Character> engcode = new ArrayList();
    for (int i = 0; i < 2; i++) {
      numcode.add((int) (Math.random() * 10));
    }
    for (int i = 0; i < 2; i++) {
      engcode.add((char) ((Math.random() * 26) + 65));
    }
    for (int i = 0; i < 2; i++) {
      invite = invite.concat(String.valueOf(numcode.get(i)));
      invite = invite.concat(String.valueOf(engcode.get(i)));
    }
    return invite;
  }

  public boolean checkingRoomCode(String code) {
    final boolean[] checking = {true};
    roomsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          if (snapshot.getValue().equals(code)) {
            checking[0] = false;
          } else {

          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
    return checking[0];
  }

  public void getRoomInfo(String roomCode, SimpleCallback<RoomsInfo> simpleCallback) {
    roomsListRef.child(roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        RoomsInfo roomInfo = dataSnapshot.getValue(RoomsInfo.class);
        roomInfo.setRoomCode(roomCode);

        simpleCallback.callback(roomInfo);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
  }

  @Override
  public void update(Observable observable, Object o) {

  }
}
