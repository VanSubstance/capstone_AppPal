package com.capstone.apppal.VO;

import java.io.Serializable;

import javax.vecmath.Vector3f;

public class CoordinateInfo implements Serializable {
  private Vector3f point;
  private float visibility;

  public CoordinateInfo() {
    this.point = new Vector3f();
    this.visibility = 0.0f;
  }

  public CoordinateInfo(float x, float y, float z, float visibility) {
    super();
    this.point = new Vector3f(x, y, z);
    this.visibility = visibility;
  }

  public float getVisibility() {
    return visibility;
  }

  public void setVisibility(float visibility) {
    this.visibility = visibility;
  }

  public float getX() {
    return point.getX();
  }

  public void setX(float x) {
    point.setX(x);
  }

  public float getY() {
    return point.getY();
  }

  public void setY(float y) {
    point.setY(y);
  }

  public float getZ() {
    return point.getZ();
  }

  public void setZ(float z) {
    point.setZ(z);
  }

  public Vector3f getVector() {
    return this.point;
  }

  public String toString() {
    return "\nCoordinate::\n" + "X : " + point.getX() + "\nY : " + point.getY() + "\nZ : " + point.getZ() + "\nVISIBILITY : " + visibility + "\n";
  }

  public String toStringForJson() {
    return "[" + point.getX() + "," + point.getY() + "," + point.getZ() + "," + visibility + "]";
  }

}