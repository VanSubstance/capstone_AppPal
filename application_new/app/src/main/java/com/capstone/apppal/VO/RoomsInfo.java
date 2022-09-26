package com.capstone.apppal.VO;

import com.capstone.apppal.model.Anchor;

public class RoomsInfo {
  private String uid;
  private String passsword;
  private Long timestamp;
  private String title;

  public RoomsInfo() {
  }

  public RoomsInfo(String uid, String passsword, Long timestamp, String title) {
    this.uid = uid;
    this.passsword = passsword;
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
    return passsword;
  }

  public void setPasssword(String passsword) {
    this.passsword = passsword;
  }


  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

}
