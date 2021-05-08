import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Shablon extends JFrame implements GLEventListener, MouseMotionListener {

    public static DisplayMode dm, dm_old;
    private int lastX, lastY;
    private static float rotateX, rotateY;
    private GLU glu = new GLU();
    //private float rquad = 0.0f;

    @Override
    public void display( GLAutoDrawable drawable ) {

        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( 0f, 0f, -5.0f );

        // Rotate The Cube On X, Y & Z
        //gl.glRotatef(rquad, 1.0f, 1.0f, 1.0f);
        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);

        //вставляй сюда

        gl.glFlush();
        //rquad -= 0.15f;
    }

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

    public void dp(GL2 gl,
                   float c1, float c2, float c3,
                   float n11, float n12, float n13,
                   float v11, float v12, float v13,
                   float n21, float n22, float n23,
                   float v21, float v22, float v23,
                   float n31, float n32, float n33,
                   float v31, float v32, float v33,
                    float n41, float n42, float n43,
                    float v41, float v42, float v43){
        gl.glBegin(9);
        gl.glColor3f(c1, c2, c3);
        gl.glNormal3f(n11, n12, n13);
        gl.glVertex3f(v11, v12, v13);
        gl.glNormal3f(n21, n22, n23);
        gl.glVertex3f(v21, v22, v23);
        gl.glNormal3f(n31, n32, n33);
        gl.glVertex3f(v31, v32, v33);
        gl.glNormal3f(n41, n42, n43);
        gl.glVertex3f(v41, v42, v43);
        gl.glEnd();
    }

    public void dp(GL2 gl,
                    float c1, float c2, float c3,
                    float n11, float n12, float n13,
                    float v11, float v12, float v13,
                    float n21, float n22, float n23,
                    float v21, float v22, float v23,
                    float n31, float n32, float n33,
                    float v31, float v32, float v33,
                    float n41, float n42, float n43,
                    float v41, float v42, float v43,
                   float n11_, float n12_, float n13_,
                   float v11_, float v12_, float v13_,
                   float n21_, float n22_, float n23_,
                   float v21_, float v22_, float v23_,
                   float n31_, float n32_, float n33_,
                   float v31_, float v32_, float v33_,
                   float n41_, float n42_, float n43_,
                   float v41_, float v42_, float v43_){
        gl.glBegin(9);
        gl.glColor3f(c1, c2, c3);
        gl.glNormal3f(n11, n12, n13);
        gl.glVertex3f(v11, v12, v13);
        gl.glNormal3f(n21, n22, n23);
        gl.glVertex3f(v21, v22, v23);
        gl.glNormal3f(n31, n32, n33);
        gl.glVertex3f(v31, v32, v33);
        gl.glNormal3f(n41, n42, n43);
        gl.glVertex3f(v41, v42, v43);
        gl.glNormal3f(n11_, n12_, n13_);
        gl.glVertex3f(v11_, v12_, v13_);
        gl.glNormal3f(n21_, n22_, n23_);
        gl.glVertex3f(v21_, v22_, v23_);
        gl.glNormal3f(n31_, n32_, n33_);
        gl.glVertex3f(v31_, v32_, v33_);
        gl.glNormal3f(n41_, n42_, n43_);
        gl.glVertex3f(v41_, v42_, v43_);
        gl.glEnd();
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
        Shablon cube = new Shablon();

        glcanvas.addGLEventListener( cube );
        glcanvas.setSize( 600, 600 );

        final JFrame frame = new JFrame ( " Multicolored cube" );
        frame.getContentPane().add( glcanvas );
        frame.setSize( frame.getContentPane().getPreferredSize() );
        frame.setVisible( true );
        rotateX = 0f; rotateY = 0f;
        glcanvas.addMouseMotionListener(new Shablon());
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
