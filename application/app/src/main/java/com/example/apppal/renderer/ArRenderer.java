package com.example.apppal.renderer;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.example.apppal.renderer.tools.Framebuffer;
import com.example.apppal.renderer.tools.Mesh;
import com.example.apppal.renderer.tools.Shader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** A SampleRender context. */
public class ArRenderer {
  private static final String TAG = ArRenderer.class.getSimpleName();

  private final AssetManager assetManager;

  private int viewportWidth = 1;
  private int viewportHeight = 1;

  /**
   * Constructs a SampleRender object and instantiates GLSurfaceView parameters.
   *
   * @param glSurfaceView Android GLSurfaceView
   * @param renderer Renderer implementation to receive callbacks
   * @param assetManager AssetManager for loading Android resources
   */
  public ArRenderer(GLSurfaceView glSurfaceView, Renderer renderer, AssetManager assetManager) {
    this.assetManager = assetManager;
    glSurfaceView.setPreserveEGLContextOnPause(true);
    glSurfaceView.setEGLContextClientVersion(3);
    glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
    glSurfaceView.setRenderer(
      new GLSurfaceView.Renderer() {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
          GLES30.glEnable(GLES30.GL_BLEND);
          renderer.onSurfaceCreated(ArRenderer.this);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
          viewportWidth = w;
          viewportHeight = h;
          renderer.onSurfaceChanged(ArRenderer.this, w, h);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
          clear(/*framebuffer=*/ null, 0f, 0f, 0f, 1f);
          renderer.onDrawFrame(ArRenderer.this);
        }
      });
    glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    glSurfaceView.setWillNotDraw(false);
  }

  /** Draw a {@link Mesh} with the specified {@link Shader}. */
  public void draw(Mesh mesh, Shader shader) {
    draw(mesh, shader, /*framebuffer=*/ null);
  }

  /**
   * Draw a {@link Mesh} with the specified {@link Shader} to the given {@link Framebuffer}.
   *
   * <p>The {@code framebuffer} argument may be null, in which case the default framebuffer is used.
   */
  public void draw(Mesh mesh, Shader shader, Framebuffer framebuffer) {
    useFramebuffer(framebuffer);
    shader.lowLevelUse();
    mesh.lowLevelDraw();
  }

  /**
   * Clear the given framebuffer.
   *
   * <p>The {@code framebuffer} argument may be null, in which case the default framebuffer is
   * cleared.
   */
  public void clear(Framebuffer framebuffer, float r, float g, float b, float a) {
    useFramebuffer(framebuffer);
    GLES30.glClearColor(r, g, b, a);
    GLES30.glDepthMask(true);
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
  }

  /** Interface to be implemented for rendering callbacks. */
  public static interface Renderer {
    /**
     * Called by {@link ArRenderer} when the GL render surface is created.
     *
     * <p>See {@link GLSurfaceView.Renderer#onSurfaceCreated}.
     */
    public void onSurfaceCreated(ArRenderer render);

    /**
     * Called by {@link ArRenderer} when the GL render surface dimensions are changed.
     *
     * <p>See {@link GLSurfaceView.Renderer#onSurfaceChanged}.
     */
    public void onSurfaceChanged(ArRenderer render, int width, int height);

    /**
     * Called by {@link ArRenderer} when a GL frame is to be rendered.
     *
     * <p>See {@link GLSurfaceView.Renderer#onDrawFrame}.
     */
    public void onDrawFrame(ArRenderer render);
  }

  /* package-private */
  public AssetManager getAssets() {
    return assetManager;
  }

  private void useFramebuffer(Framebuffer framebuffer) {
    int framebufferId;
    int viewportWidth;
    int viewportHeight;
    if (framebuffer == null) {
      framebufferId = 0;
      viewportWidth = this.viewportWidth;
      viewportHeight = this.viewportHeight;
    } else {
      framebufferId = framebuffer.getFramebufferId();
      viewportWidth = framebuffer.getWidth();
      viewportHeight = framebuffer.getHeight();
    }
    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, framebufferId);
    GLES30.glViewport(0, 0, viewportWidth, viewportHeight);
  }
}
