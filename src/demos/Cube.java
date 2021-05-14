package demos;

import javax.swing.JFrame;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Cube extends JFrame implements GLEventListener, MouseMotionListener {

    public static DisplayMode dm, dm_old;
    private int lastX, lastY;
    private static float rotateX, rotateY;
    private GLU glu = new GLU();
    //private float rquad = 0.0f;

    public void dp(GL2 gl,
                            float c1, float c2, float c3,
                            float n11, float n12, float n13,
                            float v11, float v12, float v13,
                            float n21, float n22, float n23,
                            float v21, float v22, float v23,
                            float n31, float n32, float n33,
                            float v31, float v32, float v33){
        gl.glBegin(9);
        gl.glColor3f(c1, c2, c3);
        gl.glNormal3f(n11, n12, n13);
        gl.glVertex3f(v11, v12, v13);
        gl.glNormal3f(n21, n22, n23);
        gl.glVertex3f(v21, v22, v23);
        gl.glNormal3f(n31, n32, n33);
        gl.glVertex3f(v31, v32, v33);
        gl.glEnd();
    }
    @Override
    public void display( GLAutoDrawable drawable ) {

        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( 0f, 0f, -5.0f );

        // Rotate The demos.Cube On X, Y & Z
        //gl.glRotatef(rquad, 1.0f, 1.0f, 1.0f);
        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);

        //giving different colors to different sides
        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f(0.2714140402485682f, 0.27467868908991977f, 0.7761735379584942f);
        gl.glNormal3f(0f, 0f, 1f);
        gl.glVertex3f(-0.325f, 0.05000025f, 2.7499998f);
        gl.glNormal3f(0f, 0f, 1f);
        gl.glVertex3f(-0.325f, 2.5E-07f, 2.7499998f);
        gl.glNormal3f(0f, 0f, 1f);
        gl.glVertex3f(0.325f, 2.5E-07f, 2.7499998f);
        gl.glNormal3f(0f, 0f, 1f);
        gl.glVertex3f(0.325f, 0.05000025f, 2.7499998f);
        gl.glEnd();

        gl.glFlush();
        //rquad -= 0.15f;
    }

    @Override
    public void dispose( GLAutoDrawable drawable ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void init( GLAutoDrawable drawable ) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel( GL2.GL_SMOOTH );
        gl.glClearColor( 0f, 0f, 0f, 0f );
        gl.glClearDepth( 1.0f );
        gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glDepthFunc( GL2.GL_LEQUAL );
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {

        // TODO Auto-generated method stub
        final GL2 gl = drawable.getGL().getGL2();

        final float h = ( float ) width / ( float ) height;
        gl.glViewport( 0, 0, width, height );
        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();

        glu.gluPerspective( 45.0f, h, 1.0, 20.0 );
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
    }

    public static void main(String[] args) {

        final GLProfile profile = GLProfile.get( GLProfile.GL2 );
        GLCapabilities capabilities = new GLCapabilities( profile );

        // The canvas
        final GLCanvas glcanvas = new GLCanvas( capabilities );
        Cube cube = new Cube();

        glcanvas.addGLEventListener( cube );
        glcanvas.setSize( 600, 600 );

        final JFrame frame = new JFrame ( " Multicolored cube" );
        frame.getContentPane().add( glcanvas );
        frame.setSize( frame.getContentPane().getPreferredSize() );
        frame.setVisible( true );
        rotateX = 0f; rotateY = 0f;
        glcanvas.addMouseMotionListener(new Cube());
        final FPSAnimator animator = new FPSAnimator(glcanvas, 300,true);

        animator.start();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        rotateX += e.getX() - lastX;
        rotateY += e.getY() - lastY;
        lastX = e.getX();
        lastY = e.getY();
    }
}