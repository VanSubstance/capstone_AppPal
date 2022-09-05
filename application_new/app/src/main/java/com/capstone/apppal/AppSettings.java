// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.capstone.apppal;


import javax.vecmath.Vector3f;

public class AppSettings {

  private static Vector3f mColor = new Vector3f(0f, 0f, 0f);

  private static final float strokeDrawDistance = 0.13f;

  private static final float minDistance = 0.000001f;

  private static final float nearClip = 0.001f;

  private static final float farClip = 100.0f;

  private static final float smoothing = 0.07f;

  private static final int smoothingCount = 1500;

  public enum MenuType {
    TOOL(0),
    COLOR(1),
    THICKNESS(2);

    private final int menu;

    MenuType(int i) {
      this.menu = i;
    }

    public int getMenu() {
      return menu;
    }
  }

  public enum LineWidth {
    SMALL(0.006f),
    MEDIUM(0.011f),
    LARGE(0.020f);

    private final float width;

    LineWidth(float i) {
      this.width = i;
    }

    public float getWidth() {
      return width;
    }
  }

  /**
   * red: rgb(219, 85, 77)
   * green: 64, 221, 115
   * blue: rgb(97, 85, 219)
   */

  public enum ColorType {
    WHITE(256f, 256f, 256f),
    BLACK(0f, 0f, 0f),
    RED(219f, 85f, 77f),
    GREEN(64f, 221f, 115f),
    BLUE(97f, 85f, 219f);

    private final Vector3f color;

    ColorType(float r, float g, float b) {
      this.color = new Vector3f(r / 256f, g / 256f, b / 256f);
    }

    public Vector3f getColor() {
      return color;
    }
  }

  public enum ToolType {
    NORMAL_PEN(0),
    STRAIGHT_LINE(1),
    CUBE(2),
    ERASE(3),
    RECT(4);

    private final int type;

    ToolType(int i) {
      this.type = i;
    }
  }

    public int getType() {
      return type;
    }
  }

  public static float getStrokeDrawDistance() {
    return strokeDrawDistance;
  }

  public static void setColor(float r, float g, float b) {
    mColor = new Vector3f(r, g, b);
  }

  public static Vector3f getColor() {
    return mColor;
  }

  public static float getMinDistance() {
    return minDistance;
  }

  static float getNearClip() {
    return nearClip;
  }

  static float getFarClip() {
    return farClip;
  }

  public static float getSmoothing() {
    return smoothing;
  }

  public static int getSmoothingCount() {
    return smoothingCount;
  }
}
