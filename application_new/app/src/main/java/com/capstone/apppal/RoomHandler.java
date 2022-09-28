package com.capstone.apppal;

import com.capstone.apppal.VO.RoomsInfo;
import com.capstone.apppal.network.SimpleCallback;
import com.capstone.apppal.utils.CommonFunctions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomHandler {
  private final static String TAG = "RoomHandler";
  private FirebaseDatabase database = FirebaseDatabase.getInstance();

  private DatabaseReference databaseReference = database.getReference();
  private static final String ROOT_FIREBASE_ROOMS = "rooms";
  private static final String ROOT_FIREBASE_USERS = "Users";
  DatabaseReference roomsListRef = databaseReference.child(ROOT_FIREBASE_ROOMS);
  DatabaseReference usersListRef = databaseReference.child(ROOT_FIREBASE_USERS);

  public void singleRoomCreate(RoomsInfo roomsInfo, SimpleCallback<RoomsInfo> simpleCallback) {
    while (true) {
      roomsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          boolean isDuplicated = false;
          String newCode;
          while (true) {
            newCode = makeRoomCode();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              if (snapshot.getValue().equals(newCode)) {
                isDuplicated = true;
                break;
              } else {

              }
            }
            if (isDuplicated) {
              continue;
            } else {
              Long timestamp = System.currentTimeMillis();
              roomsInfo.setRoomCode(newCode);
              roomsInfo.setPasssword(CommonFunctions.Encrypted(roomsInfo.getPasssword(), newCode));
              roomsInfo.setTimestamp(timestamp);
              roomsListRef.child(newCode).setValue(roomsInfo);
              simpleCallback.callback(roomsInfo);
            }
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
      });
    }
  }

  public String makeRoomCode() {
    String roomcode = "";
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
    return roomcode;
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

  public void getMyRoomList(SimpleCallback<ArrayList<RoomsInfo>> simpleCallback) {
  }
}
