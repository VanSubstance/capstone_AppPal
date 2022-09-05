package com.capstone.apppal.VO;

import java.io.Serializable;

import javax.vecmath.Vector3f;

public class TestCase implements Serializable {
    private Vector3f point;

    public TestCase(float x, float y, float z) {
        this.point = new Vector3f(x, y, z);
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

    public String toString() {
        return "\nCoordinate::\n" + "X : " + point.getX() + "\nY : " + point.getY() + "\nZ : " + point.getZ();
    }

    public String toStringForJson() {
        return "[" + point.getX() + "," + point.getY() + "," + point.getZ() + "]";
    }

}