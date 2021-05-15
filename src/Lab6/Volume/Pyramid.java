package Lab6.Volume;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Pyramid extends VolumeFigure {
        @Override
    public void display( GLAutoDrawable drawable ) {

        final GL2 gl = drawable.getGL().getGL2();

        // Clear The Screen And The Depth Buffer
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity(); // Reset The View
        gl.glTranslatef( -0.5f, 0.0f, -6.0f ); // Move the triangle
        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);
        gl.glBegin( GL2.GL_TRIANGLES );

        //drawing triangle in all dimensions
        // Front
        gl.glColor3f( 1f, 0.0f, 0.0f ); // Red
        gl.glVertex3f( 0f, 2.0f, 0f );  // Top Of Triangle (Front)
        gl.glVertex3f( -1.0f, -1.0f, 1.0f ); // Left Of Triangle (Front)
        gl.glVertex3f( 1.0f, -1.0f, 1.0f ); // Right Of Triangle (Front)

        // Right
        gl.glColor3f( 0.0f, 1f, 0.0f ); // Green
        gl.glVertex3f( 0f, 2.0f, 0f );  // Top Of Triangle (Right)
        gl.glVertex3f( 1.0f, -1.0f, 1.0f ); // Left Of Triangle (Right)
        gl.glVertex3f( 1.0f, -1.0f, -1.0f ); // Right Of Triangle (Right)

        // Left
        gl.glColor3f( 0.0f, 0.0f, 1f ); // Blue
        gl.glVertex3f( 0f, 2.0f, 0f );  // Top Of Triangle (Back)
        gl.glVertex3f( 1.0f, -1.0f, -1.0f ); // Left Of Triangle (Back)
        gl.glVertex3f( -1.0f, -1.0f, -1.0f ); // Right Of Triangle (Back)

        //left
        gl.glColor3f( 1f, 1f, 0.0f ); // Yellow
        gl.glVertex3f( 0f, 2.0f, 0f );  // Top Of Triangle (Left)
        gl.glVertex3f( -1.0f, -1.0f, -1.0f ); // Left Of Triangle (Left)
        gl.glVertex3f( -1.0f, -1.0f, 1.0f ); // Right Of Triangle (Left)

        gl.glEnd(); // Done Drawing 3d triangle (Pyramid)

        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f( 1f, 0f, 1.0f ); // Violet
        gl.glVertex3f( 1f, -1f, 1f );  // Top Of Triangle (Left)
        gl.glVertex3f( 1f, -1f, -1f ); // Left Of Triangle (Left)
        gl.glVertex3f( -1.0f, -1f, -1f ); // Right Of Triangle (Left)
        gl.glVertex3f( -1.0f, -1f, 1f ); // Right Of Triangle (Left)
        gl.glEnd();

        gl.glFlush();
    }
}
