package com.example.openglstudy;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by knxy on 11/20/13.
 *
 * as a Fragment to show the opengl example
 */
public class DrawShapeMoveRender implements GLSurfaceView.Renderer {

    private DrawShapeTriangle triangle;
    private DrawShapeSquare square;

    private float angleTriangle = 0.0f;
    private float angleSquare = 0.0f;
    private float speedTriangle = 0.5f;
    private float speedSquare = -0.4f;

    /// option menu controllable variables
    private boolean IsResetGl = true;

    public DrawShapeMoveRender() {
        triangle = new DrawShapeTriangle();
        square = new DrawShapeSquare();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        gl.glShadeModel(GL10.GL_SMOOTH);
        // set color to black, half opaque
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        //really nice perspective calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = 1;
        }

        gl.glViewport(0, 0, width, height);
        // Select the model-view matrix to manipulate objects
        // (Deselect the projection matrix)
        // indicates that any new transformations will affect the modelview matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        //calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // reset current modelview matrix
        gl.glLoadIdentity();

        // move up 1.1 unit to left, 6 unit into z axis
        gl.glTranslatef(-1.0f, 0.0f, -6.0f);
        gl.glRotatef(angleTriangle, 0.0f, 1.0f, 0.0f);
        triangle.draw(gl);

        if ( IsResetGl ){
            gl.glLoadIdentity();
        }
        gl.glTranslatef(1.0f, 0f, -6.0f);
        gl.glRotatef(angleSquare, 1.0f, 0.0f, 0.0f);
        square.draw(gl);

        angleTriangle += speedTriangle;
        angleSquare += speedSquare;
    }

    public void setResetGl(boolean isResetGl) {
        IsResetGl = isResetGl;
    }
}