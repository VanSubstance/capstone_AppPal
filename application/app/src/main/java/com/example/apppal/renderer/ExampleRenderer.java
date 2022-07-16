package com.example.apppal.renderer;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ExampleRenderer implements GLSurfaceView.Renderer {
  private Square square;
  private int zvalue, delta;

  public ExampleRenderer() {
    square = new Square();
    zvalue = -4;
    delta = -1;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
    gl.glClearColor(0, 0, 1, 0.5f);
    gl.glShadeModel(GL10.GL_SMOOTH);
    gl.glClearDepthf(1.0f);
    gl.glEnable(GL10.GL_DEPTH_TEST);
    gl.glDepthFunc(GL10.GL_LEQUAL);
    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    gl.glViewport(0, 0, width, height);
    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glLoadIdentity();
    GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    gl.glLoadIdentity();
    zvalue += delta;
    gl.glTranslatef(0, 0, zvalue);
    if (zvalue < -100 || zvalue > -4) delta *= -1;
    gl.glTranslatef(0, 0, -4);
    square.draw(gl);
  }
}
