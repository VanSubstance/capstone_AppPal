package com.example.apppal.VO;

import java.io.Serializable;

public class CoordinateInfo implements Serializable {
    private float x;
    private float y;
    private float z;
    private float visibility;

    public CoordinateInfo(float x, float y, float z, float visibility) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.visibility = visibility;
    }

    public float getVisibility() {
        return visibility;
    }

    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String toString() {
        return "\nCoordinate::\n" + "X : " + x + "Y : " + y + "Z : " + z + "VISIBILITY : " + visibility + "\n";
    }

    public String toStringForJson() {
        return "[" + x + "," + y + "," + z + "," + visibility + "]";
    }

}
