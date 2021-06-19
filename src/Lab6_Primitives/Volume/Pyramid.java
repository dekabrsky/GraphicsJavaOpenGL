package Lab6_Primitives.Volume;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Pyramid extends VolumeFigure {
        @Override
    public void display( GLAutoDrawable drawable ) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( -0.5f, 0.0f, -6.0f );
        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);
        gl.glBegin( GL2.GL_TRIANGLES );


        gl.glColor3f( 1f, 0.0f, 0.0f );
        gl.glVertex3f( 0f, 2.0f, 0f );
        gl.glVertex3f( -1.0f, -1.0f, 1.0f );
        gl.glVertex3f( 1.0f, -1.0f, 1.0f );

        gl.glColor3f( 0.0f, 1f, 0.0f );
        gl.glVertex3f( 0f, 2.0f, 0f );
        gl.glVertex3f( 1.0f, -1.0f, 1.0f );
        gl.glVertex3f( 1.0f, -1.0f, -1.0f );

        gl.glColor3f( 0.0f, 0.0f, 1f );
        gl.glVertex3f( 0f, 2.0f, 0f );
        gl.glVertex3f( 1.0f, -1.0f, -1.0f );
        gl.glVertex3f( -1.0f, -1.0f, -1.0f );

        gl.glColor3f( 1f, 1f, 0.0f );
        gl.glVertex3f( 0f, 2.0f, 0f );
        gl.glVertex3f( -1.0f, -1.0f, -1.0f );
        gl.glVertex3f( -1.0f, -1.0f, 1.0f );

        gl.glEnd();

        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f( 1f, 0f, 1.0f );
        gl.glVertex3f( 1f, -1f, 1f );
        gl.glVertex3f( 1f, -1f, -1f );
        gl.glVertex3f( -1.0f, -1f, -1f );
        gl.glVertex3f( -1.0f, -1f, 1f );
        gl.glEnd();

        gl.glFlush();
    }
}
