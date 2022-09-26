package com.capstone.apppal;

import com.capstone.apppal.VO.RoomsInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RoomHandler {
  private FirebaseDatabase database = FirebaseDatabase.getInstance();

  private DatabaseReference databaseReference = database.getReference();
  protected boolean isPairing = false;
  private static final String ROOT_FIREBASE_ROOMS = "rooms";
  private String roomcode;

  public void singleRoomCreate(RoomsInfo roomsInfo){
    if(isPairing == false) {
      makeRoomCode();
      DatabaseReference roomsListRef = databaseReference.child(ROOT_FIREBASE_ROOMS);
      roomsListRef.setValue(roomcode);
      Long timestamp = System.currentTimeMillis();
      roomsInfo.setTimestamp(timestamp);
      roomsListRef.child(roomcode).setValue(roomsInfo);
    }
  }
  public void makeRoomCode(){
    roomcode = "";
    ArrayList<Integer> numcode = new ArrayList();
    ArrayList<Character> engcode = new ArrayList();
    for(int i = 0; i < 3; i++){
      numcode.add((int)Math.random() * 10);
    }
    for(int i = 0; i < 3; i++){
      engcode.add((char)((Math.random() * 26) +65));
    }
    for(int i = 0; i <3; i++){
      roomcode = roomcode.concat(String.valueOf(numcode.get(i)));
      roomcode = roomcode.concat(String.valueOf(engcode.get(i)));
    }
  }
}
