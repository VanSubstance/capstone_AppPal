package com.capstone.apppal.VO;

public class RoomsInfo {
  private String roomCode;
  private String uid;
  private String password;
  private Long timestamp;
  private String title;

  public RoomsInfo() {
  }

  public RoomsInfo(String uid, String password, Long timestamp, String title) {
    this.uid = uid;
    this.password = password;
    this.timestamp = timestamp;
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getPasssword() {
    return password;
  }

  public void setPasssword(String password) {
    this.password = password;
  }


  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getRoomCode() {
    return roomCode;
  }

  public void setRoomCode(String roomCode) {
    this.roomCode = roomCode;
  }

  public String toString() {
    return "uid:: " + uid + "\n"
      + "password:: " + password + "\n"
      + "timestamp:: " + timestamp + "\n"
      + "title:: " + title + "\n";
  }
}
