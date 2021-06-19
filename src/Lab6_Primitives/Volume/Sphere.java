package Lab6_Primitives.Volume;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

public class Sphere extends VolumeFigure{
    private GLU glu = new GLU();

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        init(gl);
        light(gl);

        GLUquadric sphere = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
        glu.gluQuadricNormals(sphere, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(sphere, GLU.GLU_OUTSIDE);
        final float radius = 1f;
        final int slices = 16;
        final int stacks = 16;
        glu.gluSphere(sphere, radius, slices, stacks);
        glu.gluDeleteQuadric(sphere);
    }

    private void init(GL2 gl) {
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( -0.5f, 0.0f, -6.0f );
        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);
        gl.glColor3f(0.3f, 0.5f, 1f);
    }

    private void light(GL2 gl){
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {-30, 0, 0, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

        gl.glLightfv(gl.GL_LIGHT1, gl.GL_POSITION, lightPos, 0);
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_SPECULAR, lightColorSpecular, 0);

        gl.glEnable(gl.GL_LIGHT1);
        gl.glEnable(gl.GL_LIGHTING);

        float[] rgba = {0.3f, 0.5f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, gl.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, gl.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, gl.GL_SHININESS, 0.5f);
    }
}
