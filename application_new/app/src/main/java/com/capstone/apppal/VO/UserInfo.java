package com.capstone.apppal.VO;

public class UserInfo {
  String id;
  String email;
  String Name;

  public UserInfo(String id, String email, String name) {
    this.id = id;
    this.email = email;
    this.Name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }
}
