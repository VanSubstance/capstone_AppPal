package com.capstone.apppal.VO;

import com.google.ar.core.Point;

public class LinesInfo {
  private String creater;
  private Point points;
  private String linecolor;
  private String linewidth;

  public String getCreater() {
    return creater;
  }

  public void setCreater(String creater) {
    this.creater = creater;
  }

  public Point getPoints() {
    return points;
  }

  public void setPoints(Point points) {
    this.points = points;
  }

  public String getLinecolor() {
    return linecolor;
  }

  public void setLinecolor(String linecolor) {
    this.linecolor = linecolor;
  }

  public String getLinewidth() {
    return linewidth;
  }

  public void setLinewidth(String linewidth) {
    this.linewidth = linewidth;
  }
}

