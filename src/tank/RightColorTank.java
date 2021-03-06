package tank;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
import javafx.geometry.Point3D;
import tank.elements.Wheel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RightColorTank extends JFrame implements GLEventListener, MouseMotionListener {

    private int lastX, lastY;
    private static float rotateX, rotateY;
    private final GLU glu = new GLU();
    Texture tex;
    int[] textures = new int[1];
    private GL2 gl;

    @Override
    public void display( GLAutoDrawable drawable ) {
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( 0f, -1.0f, -6.0f ); // делаем танк поглубже и пониже для удобства просмотра

        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);

        greenLight(gl);
        toggleEdgeClipping(true); // по умолчанию отсекаем невидимые грани

        drawCorpus(gl);
        drawCorpusCovers(gl);
        drawRightTrack(gl);
        drawRightAmortisation(gl);
        drawMiniGun(gl);
        drawTower(gl);
        drawTowerDetails(gl);
        drawGun(gl);
        drawLeftTrack(gl);
        drawLeftAmortisation(gl);
        drawRightWheels(gl);
        drawLeftWheels(gl);
        gl.glFlush();
    }

    public void dp(GL2 gl,
                   float c1, float c2, float c3,
                   ArrayList<Float> vertices){
        gl.glBegin(9);
        gl.glColor3f(c1, c2, c3);
        int i = 0;
        TextureCoords texcoords = tex.getImageTexCoords();
        while (i < vertices.size()){
            gl.glNormal3f(vertices.get(i++), vertices.get(i++), vertices.get(i++));
            gl.glVertex3f(vertices.get(i++), vertices.get(i++), vertices.get(i++));
            switch (i) { // т.к. даем список точек, углы попадают на эти итерации
                case (6) :
                    gl.glTexCoord2f(texcoords.right(), texcoords.bottom());
                    break;
                case (12) :
                    gl.glTexCoord2f(texcoords.right(), texcoords.top());
                    break;
                case (18) :
                    gl.glTexCoord2f(texcoords.left(), texcoords.top());
                    break;
                case (24) :
                    gl.glTexCoord2f(texcoords.left(), texcoords.bottom());
                    break;
                default:
            }
        }
        gl.glEnd();
    }

    public void greenLight(GL2 gl){
        float[] ambientLight = { 0.1f, 0.f, 0.f,0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);

        float[] diffuseLight = { 0.5f,1f, 0.5f,0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
    }

    @Override
    public void dispose( GLAutoDrawable drawable ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void init( GLAutoDrawable drawable ) {
        gl = drawable.getGL().getGL2();
        gl.glShadeModel( GL2.GL_SMOOTH ); // режим сглаживания (Гуро)
        gl.glClearColor( 0.1f, 0.3f, 0.2f, 0f ); // зеленый фон
        gl.glClearDepth( 1.0f ); // задает значения для очистки буфера глубины
        gl.glEnable( GL2.GL_DEPTH_TEST ); // включаем тест глубины
        gl.glDepthFunc( GL2.GL_LEQUAL ); // есть рисуется все, что имеет глубину, меньшую или равную текущей
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST ); // указывает качество цвета, интерполяций - установлено лучшее
        gl.glEnable(GL2.GL_LIGHTING); // включаем освещение
        gl.glEnable(GL2.GL_LIGHT0); // включаем нулевой источник света
        gl.glEnable(GL2.GL_NORMALIZE); // поручаем OpenGL приводить нормали к единичной длине
        gl.glEnable(GL2.GL_COLOR_MATERIAL); // включаем назначение цвета материалу
        gl.glEnable(GL2.GL_TEXTURE_2D); // или текстуры

        toggleEdgeClipping(true);

        File trackTexture = new File("src/textures/metal.jpg"); //текстура траков
        try {
            tex = TextureIO.newTexture(trackTexture, true);
            tex.enable(gl);
            tex.bind(gl);
            textures[0] = tex.getTextureObject(gl);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
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

        final GLCanvas glcanvas = new GLCanvas( capabilities );
        RightColorTank tank = new RightColorTank();

        glcanvas.addGLEventListener( tank );
        glcanvas.setSize( 800, 800 );

        final JFrame frame = new JFrame ( "Легкий танк Шерман" );
        frame.getContentPane().add( glcanvas );
        frame.setSize( frame.getContentPane().getPreferredSize() );
        frame.setVisible( true );
        rotateX = 0f; rotateY = 0f;
        glcanvas.addMouseMotionListener(new RightColorTank());
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

    void toggleEdgeClipping(boolean enable){
        if (enable){
            gl.glEnable(GL2.GL_CULL_FACE);
            gl.glCullFace(GL2.GL_FRONT);
            gl.glFrontFace(GL2.GL_CW);
        } else {
            gl.glDisable(GL2.GL_CULL_FACE);
        }
    }

    void drawCorpus(GL2 gl) {
        dp(gl,0.07f,0.64f,0.12f, new ArrayList<>(Arrays.asList(0f,3.89f,-0.95f,0.78f,1.45f,-0.37f, 0f,3.89f,-0.95f,0.85f,1.16f,-1.55f, 0f,3.89f,-0.95f,-0.85f,1.16f,-1.55f, 0f,3.89f,-0.95f,-0.78f,1.45f,-0.37f)));
        dp(gl,0.18f,0.69f,0.1f, new ArrayList<>(Arrays.asList(-9.19f,0.35f,-0.01f,-1f,0.87f,-0.01f, -9.19f,0.35f,-0.01f,-0.92f,1.1f,-0.07f, -9.19f,0.35f,-0.01f,-0.98f,0.93f,-1.67f, -9.19f,0.35f,-0.01f,-0.98f,0.78f,-1.67f)));
        dp(gl,0.1f,0.69f,0.17f, new ArrayList<>(Arrays.asList(9.18f,0.4f,-0.02f,0.92f,1.1f,-0.07f, 9.18f,0.4f,-0.02f,0.85f,1.42f,-0.36f, 9.18f,0.4f,-0.02f,0.86f,1.42f,0.95f, 9.18f,0.4f,-0.02f,0.92f,1.16f,1.41f)));
        dp(gl,0.15f,0.96f,0.16f, new ArrayList<>(Arrays.asList(0f,2.19f,-3.34f,-0.91f,0.93f,-1.7f, 0f,2.19f,-3.34f,-0.85f,1.16f,-1.55f, 0f,2.19f,-3.34f,0.85f,1.16f,-1.55f, 0f,2.19f,-3.34f,0.91f,0.93f,-1.7f)));
        dp(gl,0.03f,0.56f,0.08f, new ArrayList<>(Arrays.asList(9.16f,0.5f,-0.01f,0.98f,0.93f,-1.67f, 9.16f,0.5f,-0.01f,0.9f,1.14f,-1.52f, 9.16f,0.5f,-0.01f,0.85f,1.42f,-0.36f, 9.16f,0.5f,-0.01f,0.92f,1.1f,-0.07f)));
        dp(gl,0.02f,0.75f,0.09f, new ArrayList<>(Arrays.asList(-9.12f,0.62f,-0.03f,-1f,0.96f,1.63f, -9.12f,0.62f,-0.03f,-0.92f,1.16f,1.41f, -9.12f,0.62f,-0.03f,-0.92f,1.1f,-0.07f, -9.12f,0.62f,-0.03f,-1f,0.87f,-0.01f)));
        dp(gl,0.09f,0.6f,0.08f, new ArrayList<>(Arrays.asList(-0f,-4f,0f,-0.51f,0.27f,-1.32f, -0f,-4f,0f,0.51f,0.27f,-1.32f, -0f,-4f,0f,0.51f,0.27f,1.29f, -0f,-4f,0f,-0.51f,0.27f,1.29f)));
        dp(gl,0.13f,0.55f,0.04f, new ArrayList<>(Arrays.asList(-0f,-3.06f,-2.58f,-0.51f,0.53f,-1.62f, -0f,-3.06f,-2.58f,0.51f,0.53f,-1.62f, -0f,-3.06f,-2.58f,0.51f,0.27f,-1.32f, -0f,-3.06f,-2.58f,-0.51f,0.27f,-1.32f)));
        dp(gl,0.17f,0.97f,0.18f, new ArrayList<>(Arrays.asList(-9.23f,0f,-0f,-0.51f,0.62f,1.68f, -9.23f,0f,-0f,-0.51f,0.53f,-1.62f, -9.23f,0f,-0f,-0.51f,0.27f,-1.32f, -9.23f,0f,-0f,-0.51f,0.27f,1.29f)));
        dp(gl,0.11f,0.61f,0.13f, new ArrayList<>(Arrays.asList(0f,-2.99f,2.66f,0.51f,0.62f,1.68f, 0f,-2.99f,2.66f,-0.51f,0.62f,1.68f, 0f,-2.99f,2.66f,-0.51f,0.27f,1.29f, 0f,-2.99f,2.66f,0.51f,0.27f,1.29f)));
        dp(gl,0.18f,0.76f,0.16f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,0.51f,0.53f,-1.62f, 9.23f,0f,0f,0.51f,0.62f,1.68f, 9.23f,0f,0f,0.51f,0.27f,1.29f, 9.23f,0f,0f,0.51f,0.27f,-1.32f)));
        dp(gl,0.06f,0.58f,0.05f, new ArrayList<>(Arrays.asList(0f,0f,-4f,-0.92f,0.78f,-1.7f, 0f,0f,-4f,-0.91f,0.93f,-1.7f, 0f,0f,-4f,0.91f,0.93f,-1.7f, 0f,0f,-4f,0.92f,0.78f,-1.7f)));
        dp(gl,0.05f,0.86f,0.14f, new ArrayList<>(Arrays.asList(-9.18f,0.4f,-0.02f,-0.92f,1.16f,1.41f, -9.18f,0.4f,-0.02f,-0.86f,1.42f,0.95f, -9.18f,0.4f,-0.02f,-0.85f,1.42f,-0.36f, -9.18f,0.4f,-0.02f,-0.92f,1.1f,-0.07f)));
        dp(gl,0.08f,0.91f,0.14f, new ArrayList<>(Arrays.asList(-0f,3.47f,1.99f,0.86f,1.16f,1.45f, -0f,3.47f,1.99f,0.79f,1.45f,0.96f, -0f,3.47f,1.99f,-0.79f,1.45f,0.96f, -0f,3.47f,1.99f,-0.86f,1.16f,1.45f)));
        dp(gl,0.02f,0.67f,0.0f, new ArrayList<>(Arrays.asList(0f,4f,-0f,0.79f,1.45f,0.96f, 0f,4f,-0f,0.78f,1.45f,-0.37f, 0f,4f,-0f,-0.78f,1.45f,-0.37f, 0f,4f,-0f,-0.79f,1.45f,0.96f)));
        dp(gl,0.03f,0.79f,0.15f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,0.51f,0.78f,-1.7f, 9.23f,0f,0f,0.51f,0.97f,1.67f, 9.23f,0f,0f,0.51f,0.62f,1.68f, 9.23f,0f,0f,0.51f,0.53f,-1.62f)));
        dp(gl,0.05f,0.89f,0.17f, new ArrayList<>(Arrays.asList(0f,0.07f,4f,0.51f,0.97f,1.67f, 0f,0.07f,4f,-0.51f,0.97f,1.67f, 0f,0.07f,4f,-0.51f,0.62f,1.68f, 0f,0.07f,4f,0.51f,0.62f,1.68f)));
        dp(gl,0.06f,0.64f,0.1f, new ArrayList<>(Arrays.asList(-9.23f,-0f,-0f,-0.51f,0.97f,1.67f, -9.23f,-0f,-0f,-0.51f,0.78f,-1.7f, -9.23f,-0f,-0f,-0.51f,0.53f,-1.62f, -9.23f,-0f,-0f,-0.51f,0.62f,1.68f)));
        dp(gl,0.15f,0.85f,0.03f, new ArrayList<>(Arrays.asList(-0f,-1.25f,-3.8f,-0.51f,0.78f,-1.7f, -0f,-1.25f,-3.8f,0.51f,0.78f,-1.7f, -0f,-1.25f,-3.8f,0.51f,0.53f,-1.62f, -0f,-1.25f,-3.8f,-0.51f,0.53f,-1.62f)));
        dp(gl,0.11f,0.97f,0.09f, new ArrayList<>(Arrays.asList(9.12f,0.62f,-0.03f,1f,0.87f,-0.01f, 9.12f,0.62f,-0.03f,0.92f,1.1f,-0.07f, 9.12f,0.62f,-0.03f,0.92f,1.16f,1.41f, 9.12f,0.62f,-0.03f,1f,0.96f,1.63f)));
        dp(gl,0.02f,0.99f,0.0f, new ArrayList<>(Arrays.asList(0f,-3.99f,0.22f,-0.98f,0.78f,-1.67f, 0f,-3.99f,0.22f,-0.92f,0.78f,-1.7f, 0f,-3.99f,0.22f,0.92f,0.78f,-1.7f, 0f,-3.99f,0.22f,0.98f,0.78f,-1.67f, 0f,-3.99f,0.22f,1f,0.87f,-0.01f, 0f,-3.99f,0.22f,-1f,0.87f,-0.01f)));
        dp(gl,0.18f,0.56f,0.16f, new ArrayList<>(Arrays.asList(0f,-3.99f,0.22f,-1f,0.87f,-0.01f, 0f,-3.99f,0.22f,1f,0.87f,-0.01f, 0f,-3.99f,0.22f,1f,0.96f,1.63f, 0f,-3.99f,0.22f,0.94f,0.97f,1.67f, 0f,-3.99f,0.22f,-0.94f,0.97f,1.67f, 0f,-3.99f,0.22f,-1f,0.96f,1.63f)));
        dp(gl,0.09f,0.83f,0.12f, new ArrayList<>(Arrays.asList(0f,3f,2.65f,0.94f,0.97f,1.67f, 0f,3f,2.65f,0.86f,1.16f,1.45f, 0f,3f,2.65f,-0.86f,1.16f,1.45f, 0f,3f,2.65f,-0.94f,0.97f,1.67f)));
        dp(gl,0.05f,0.55f,0.19f, new ArrayList<>(Arrays.asList(9.19f,0.35f,-0.01f,0.98f,0.78f,-1.67f, 9.19f,0.35f,-0.01f,0.98f,0.93f,-1.67f, 9.19f,0.35f,-0.01f,0.92f,1.1f,-0.07f, 9.19f,0.35f,-0.01f,1f,0.87f,-0.01f)));
        dp(gl,0.18f,0.54f,0.01f, new ArrayList<>(Arrays.asList(-6.16f,1.94f,-2.26f,-0.98f,0.93f,-1.67f, -6.16f,1.94f,-2.26f,-0.9f,1.14f,-1.52f, -6.16f,1.94f,-2.26f,-0.85f,1.16f,-1.55f, -6.16f,1.94f,-2.26f,-0.91f,0.93f,-1.7f)));
        dp(gl,0.04f,0.87f,0.16f, new ArrayList<>(Arrays.asList(6f,3.04f,-0.01f,0.78f,1.45f,-0.37f, 6f,3.04f,-0.01f,0.79f,1.45f,0.96f, 6f,3.04f,-0.01f,0.86f,1.42f,0.95f, 6f,3.04f,-0.01f,0.85f,1.42f,-0.36f)));
        dp(gl,0.11f,0.8f,0.15f, new ArrayList<>(Arrays.asList(-6.43f,2.8f,-0.62f,-0.78f,1.45f,-0.37f, -6.43f,2.8f,-0.62f,-0.85f,1.16f,-1.55f, -6.43f,2.8f,-0.62f,-0.9f,1.14f,-1.52f, -6.43f,2.8f,-0.62f,-0.85f,1.42f,-0.36f)));
        dp(gl,0.17f,0.73f,0.03f, new ArrayList<>(Arrays.asList(6.2f,0.02f,-2.96f,0.98f,0.93f,-1.67f, 6.2f,0.02f,-2.96f,0.98f,0.78f,-1.67f, 6.2f,0.02f,-2.96f,0.92f,0.78f,-1.7f, 6.2f,0.02f,-2.96f,0.91f,0.93f,-1.7f)));
        dp(gl,0.01f,0.88f,0.05f, new ArrayList<>(Arrays.asList(6.57f,2.31f,1.6f,0.86f,1.16f,1.45f, 6.57f,2.31f,1.6f,0.94f,0.97f,1.67f, 6.57f,2.31f,1.6f,1f,0.96f,1.63f, 6.57f,2.31f,1.6f,0.92f,1.16f,1.41f)));
        dp(gl,0.09f,0.51f,0.13f, new ArrayList<>(Arrays.asList(-6.57f,2.31f,1.6f,-0.92f,1.16f,1.41f, -6.57f,2.31f,1.6f,-1f,0.96f,1.63f, -6.57f,2.31f,1.6f,-0.94f,0.97f,1.67f, -6.57f,2.31f,1.6f,-0.86f,1.16f,1.45f)));
        dp(gl,0.15f,0.93f,0.18f, new ArrayList<>(Arrays.asList(6.43f,2.8f,-0.62f,0.85f,1.16f,-1.55f, 6.43f,2.8f,-0.62f,0.78f,1.45f,-0.37f, 6.43f,2.8f,-0.62f,0.85f,1.42f,-0.36f, 6.43f,2.8f,-0.62f,0.9f,1.14f,-1.52f)));
        dp(gl,0.01f,0.92f,0.16f, new ArrayList<>(Arrays.asList(-6f,3.04f,-0.01f,-0.79f,1.45f,0.96f, -6f,3.04f,-0.01f,-0.78f,1.45f,-0.37f, -6f,3.04f,-0.01f,-0.85f,1.42f,-0.36f, -6f,3.04f,-0.01f,-0.86f,1.42f,0.95f)));
        dp(gl,0.14f,0.94f,0.02f, new ArrayList<>(Arrays.asList(-6.2f,0.02f,-2.96f,-0.98f,0.78f,-1.67f, -6.2f,0.02f,-2.96f,-0.98f,0.93f,-1.67f, -6.2f,0.02f,-2.96f,-0.91f,0.93f,-1.7f, -6.2f,0.02f,-2.96f,-0.92f,0.78f,-1.7f)));
        dp(gl,0.18f,0.79f,0.09f, new ArrayList<>(Arrays.asList(6.16f,1.94f,-2.26f,0.9f,1.14f,-1.52f, 6.16f,1.94f,-2.26f,0.98f,0.93f,-1.67f, 6.16f,1.94f,-2.26f,0.91f,0.93f,-1.7f, 6.16f,1.94f,-2.26f,0.85f,1.16f,-1.55f)));
        dp(gl,0.05f,0.86f,0.09f, new ArrayList<>(Arrays.asList(6.12f,2.67f,1.36f,0.79f,1.45f,0.96f, 6.12f,2.67f,1.36f,0.86f,1.16f,1.45f, 6.12f,2.67f,1.36f,0.92f,1.16f,1.41f, 6.12f,2.67f,1.36f,0.86f,1.42f,0.95f)));
        dp(gl,0.16f,0.64f,0.11f, new ArrayList<>(Arrays.asList(-6.12f,2.67f,1.36f,-0.86f,1.42f,0.95f, -6.12f,2.67f,1.36f,-0.92f,1.16f,1.41f, -6.12f,2.67f,1.36f,-0.86f,1.16f,1.45f, -6.12f,2.67f,1.36f,-0.79f,1.45f,0.96f)));
        dp(gl,0.2f,0.77f,0.02f, new ArrayList<>(Arrays.asList(-9.16f,0.5f,-0.01f,-0.92f,1.1f,-0.07f, -9.16f,0.5f,-0.01f,-0.85f,1.42f,-0.36f, -9.16f,0.5f,-0.01f,-0.9f,1.14f,-1.52f, -9.16f,0.5f,-0.01f,-0.98f,0.93f,-1.67f)));
        dp(gl,0.09f,0.77f,0.12f, new ArrayList<>(Arrays.asList(0f,4f,0f,0.61f,1.38f,1.37f, 0f,4f,0f,0.19f,1.38f,1.37f, 0f,4f,0f,0.22f,1.38f,1.48f, 0f,4f,0f,0.58f,1.38f,1.48f)));
        dp(gl,0.17f,0.72f,0.0f, new ArrayList<>(Arrays.asList(0f,4f,0f,0.58f,1.38f,1.48f, 0f,4f,0f,0.22f,1.38f,1.48f, 0f,4f,0f,0.31f,1.38f,1.55f, 0f,4f,0f,0.49f,1.38f,1.55f)));
        dp(gl,0.19f,0.8f,0.16f, new ArrayList<>(Arrays.asList(-9.15f,0f,0.52f,0.19f,1.38f,1.37f, -9.15f,0f,0.52f,0.19f,1.14f,1.37f, -9.15f,0f,0.52f,0.21f,1.11f,1.43f, -9.15f,0f,0.52f,0.22f,1.07f,1.48f, -9.15f,0f,0.52f,0.22f,1.38f,1.48f)));
        dp(gl,0.01f,0.53f,0.19f, new ArrayList<>(Arrays.asList(-8.04f,0f,1.97f,0.22f,1.38f,1.48f, -8.04f,0f,1.97f,0.22f,1.07f,1.48f, -8.04f,0f,1.97f,0.31f,1.01f,1.55f, -8.04f,0f,1.97f,0.31f,1.38f,1.55f)));
        dp(gl,0.14f,1.0f,0.08f, new ArrayList<>(Arrays.asList(-0f,0f,4f,0.31f,1.01f,1.55f, -0f,0f,4f,0.49f,1.01f,1.55f, -0f,0f,4f,0.49f,1.38f,1.55f, -0f,0f,4f,0.31f,1.38f,1.55f)));
        dp(gl,0.19f,0.73f,0.01f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,0.19f,1.38f,0.97f, -9.23f,0f,0f,0.19f,1.14f,1.37f, -9.23f,0f,0f,0.19f,1.38f,1.37f)));
        dp(gl,0.03f,0.64f,0.1f, new ArrayList<>(Arrays.asList(8.04f,0f,1.97f,0.49f,1.01f,1.55f, 8.04f,0f,1.97f,0.58f,1.07f,1.48f, 8.04f,0f,1.97f,0.58f,1.38f,1.48f, 8.04f,0f,1.97f,0.49f,1.38f,1.55f)));
        dp(gl,0.01f,0.86f,0.06f, new ArrayList<>(Arrays.asList(9.15f,-0f,0.52f,0.58f,1.07f,1.48f, 9.15f,-0f,0.52f,0.6f,1.11f,1.43f, 9.15f,-0f,0.52f,0.61f,1.14f,1.37f, 9.15f,-0f,0.52f,0.61f,1.38f,1.37f, 9.15f,-0f,0.52f,0.58f,1.38f,1.48f)));
        dp(gl,0.04f,0.7f,0.08f, new ArrayList<>(Arrays.asList(0f,4f,0f,0.61f,1.38f,0.97f, 0f,4f,0f,0.19f,1.38f,0.97f, 0f,4f,0f,0.19f,1.38f,1.37f, 0f,4f,0f,0.61f,1.38f,1.37f)));
        dp(gl,0.18f,0.78f,0.05f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,0.61f,1.14f,1.37f, 9.23f,0f,0f,0.61f,1.38f,0.97f, 9.23f,0f,0f,0.61f,1.38f,1.37f)));
        dp(gl,0.14f,0.58f,0.07f, new ArrayList<>(Arrays.asList(-0f,-3.47f,-1.99f,0.6f,1.11f,1.43f, -0f,-3.47f,-1.99f,0.21f,1.11f,1.43f, -0f,-3.47f,-1.99f,0.19f,1.14f,1.37f, -0f,-3.47f,-1.99f,0.19f,1.38f,0.97f, -0f,-3.47f,-1.99f,0.61f,1.38f,0.97f, -0f,-3.47f,-1.99f,0.61f,1.14f,1.37f)));
        dp(gl,0.06f,0.85f,0.17f, new ArrayList<>(Arrays.asList(-0f,-3f,-2.65f,0.21f,1.11f,1.43f, -0f,-3f,-2.65f,0.6f,1.11f,1.43f, -0f,-3f,-2.65f,0.58f,1.07f,1.48f, -0f,-3f,-2.65f,0.49f,1.01f,1.55f, -0f,-3f,-2.65f,0.31f,1.01f,1.55f, -0f,-3f,-2.65f,0.22f,1.07f,1.48f)));
        dp(gl,0.04f,0.66f,0.05f, new ArrayList<>(Arrays.asList(0f,4f,0f,-0.19f,1.38f,1.37f, 0f,4f,0f,-0.61f,1.38f,1.37f, 0f,4f,0f,-0.58f,1.38f,1.48f, 0f,4f,0f,-0.22f,1.38f,1.48f)));
        dp(gl,0.04f,0.52f,0.05f, new ArrayList<>(Arrays.asList(0f,4f,-0f,-0.22f,1.38f,1.48f, 0f,4f,-0f,-0.58f,1.38f,1.48f, 0f,4f,-0f,-0.49f,1.38f,1.55f, 0f,4f,-0f,-0.31f,1.38f,1.55f)));
        dp(gl,0.11f,0.64f,0.09f, new ArrayList<>(Arrays.asList(-9.15f,-0f,0.52f,-0.61f,1.38f,1.37f, -9.15f,-0f,0.52f,-0.61f,1.14f,1.37f, -9.15f,-0f,0.52f,-0.6f,1.11f,1.43f, -9.15f,-0f,0.52f,-0.58f,1.07f,1.48f, -9.15f,-0f,0.52f,-0.58f,1.38f,1.48f)));
        dp(gl,0.1f,0.6f,0.18f, new ArrayList<>(Arrays.asList(-8.04f,0f,1.97f,-0.58f,1.38f,1.48f, -8.04f,0f,1.97f,-0.58f,1.07f,1.48f, -8.04f,0f,1.97f,-0.49f,1.01f,1.55f, -8.04f,0f,1.97f,-0.49f,1.38f,1.55f)));
        dp(gl,0.2f,0.92f,0.09f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,-0.61f,1.38f,0.97f, -9.23f,0f,0f,-0.61f,1.14f,1.37f, -9.23f,0f,0f,-0.61f,1.38f,1.37f)));
        dp(gl,0.07f,0.66f,0.06f, new ArrayList<>(Arrays.asList(0f,4f,0f,-0.19f,1.38f,0.97f, 0f,4f,0f,-0.61f,1.38f,0.97f, 0f,4f,0f,-0.61f,1.38f,1.37f, 0f,4f,0f,-0.19f,1.38f,1.37f)));
        dp(gl,0.09f,0.73f,0.16f, new ArrayList<>(Arrays.asList(8.04f,0f,1.97f,-0.31f,1.01f,1.55f, 8.04f,0f,1.97f,-0.22f,1.07f,1.48f, 8.04f,0f,1.97f,-0.22f,1.38f,1.48f, 8.04f,0f,1.97f,-0.31f,1.38f,1.55f)));
        dp(gl,0.09f,0.82f,0.16f, new ArrayList<>(Arrays.asList(-0f,-3.47f,-1.99f,-0.21f,1.11f,1.43f, -0f,-3.47f,-1.99f,-0.6f,1.11f,1.43f, -0f,-3.47f,-1.99f,-0.61f,1.14f,1.37f, -0f,-3.47f,-1.99f,-0.61f,1.38f,0.97f, -0f,-3.47f,-1.99f,-0.19f,1.38f,0.97f, -0f,-3.47f,-1.99f,-0.19f,1.14f,1.37f)));
        dp(gl,0.01f,0.51f,0.08f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.49f,1.01f,1.55f, 0f,0f,4f,-0.31f,1.01f,1.55f, 0f,0f,4f,-0.31f,1.38f,1.55f, 0f,0f,4f,-0.49f,1.38f,1.55f)));
        dp(gl,0.11f,0.81f,0.01f, new ArrayList<>(Arrays.asList(-0f,-3f,-2.65f,-0.6f,1.11f,1.43f, -0f,-3f,-2.65f,-0.21f,1.11f,1.43f, -0f,-3f,-2.65f,-0.22f,1.07f,1.48f, -0f,-3f,-2.65f,-0.31f,1.01f,1.55f, -0f,-3f,-2.65f,-0.49f,1.01f,1.55f, -0f,-3f,-2.65f,-0.58f,1.07f,1.48f)));
        dp(gl,0.0f,0.67f,0.02f, new ArrayList<>(Arrays.asList(9.15f,0f,0.52f,-0.22f,1.07f,1.48f, 9.15f,0f,0.52f,-0.21f,1.11f,1.43f, 9.15f,0f,0.52f,-0.19f,1.14f,1.37f, 9.15f,0f,0.52f,-0.19f,1.38f,1.37f, 9.15f,0f,0.52f,-0.22f,1.38f,1.48f)));
        dp(gl,0.04f,0.55f,0.0f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,-0.19f,1.14f,1.37f, 9.23f,0f,0f,-0.19f,1.38f,0.97f, 9.23f,0f,0f,-0.19f,1.38f,1.37f)));
        dp(gl,0.14f,0.56f,0.07f, new ArrayList<>(Arrays.asList(6.38f,2.89f,0f,-0.4f,1.31f,1.54f, 6.38f,2.89f,0f,-0.4f,1.31f,1.58f, 6.38f,2.89f,0f,-0.35f,1.29f,1.58f, 6.38f,2.89f,0f,-0.35f,1.29f,1.54f)));
        dp(gl,0.18f,0.87f,0.07f, new ArrayList<>(Arrays.asList(9.08f,0.71f,0f,-0.35f,1.29f,1.54f, 9.08f,0.71f,0f,-0.35f,1.29f,1.58f, 9.08f,0.71f,0f,-0.33f,1.24f,1.58f, 9.08f,0.71f,0f,-0.33f,1.24f,1.54f)));
        dp(gl,0.17f,0.92f,0.17f, new ArrayList<>(Arrays.asList(9.08f,-0.71f,0f,-0.33f,1.24f,1.54f, 9.08f,-0.71f,0f,-0.33f,1.24f,1.58f, 9.08f,-0.71f,0f,-0.35f,1.2f,1.58f, 9.08f,-0.71f,0f,-0.35f,1.2f,1.54f)));
        dp(gl,0.08f,0.54f,0.08f, new ArrayList<>(Arrays.asList(6.38f,-2.89f,0f,-0.35f,1.2f,1.54f, 6.38f,-2.89f,0f,-0.35f,1.2f,1.58f, 6.38f,-2.89f,0f,-0.4f,1.17f,1.58f, 6.38f,-2.89f,0f,-0.4f,1.17f,1.54f)));
        dp(gl,0.18f,0.92f,0.07f, new ArrayList<>(Arrays.asList(-6.38f,-2.89f,0f,-0.4f,1.17f,1.54f, -6.38f,-2.89f,0f,-0.4f,1.17f,1.58f, -6.38f,-2.89f,0f,-0.45f,1.2f,1.58f, -6.38f,-2.89f,0f,-0.45f,1.2f,1.54f)));
        dp(gl,0.12f,0.57f,0.14f, new ArrayList<>(Arrays.asList(-9.08f,-0.71f,0f,-0.45f,1.2f,1.54f, -9.08f,-0.71f,0f,-0.45f,1.2f,1.58f, -9.08f,-0.71f,0f,-0.47f,1.24f,1.58f, -9.08f,-0.71f,0f,-0.47f,1.24f,1.54f)));
        dp(gl,0.06f,0.99f,0.01f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.47f,1.24f,1.58f, 0f,0f,4f,-0.45f,1.2f,1.58f, 0f,0f,4f,-0.44f,1.21f,1.58f, 0f,0f,4f,-0.45f,1.24f,1.58f)));
        dp(gl,0.12f,0.92f,0.19f, new ArrayList<>(Arrays.asList(-9.08f,0.71f,0f,-0.47f,1.24f,1.54f, -9.08f,0.71f,0f,-0.47f,1.24f,1.58f, -9.08f,0.71f,0f,-0.45f,1.29f,1.58f, -9.08f,0.71f,0f,-0.45f,1.29f,1.54f)));
        dp(gl,0.02f,0.74f,0.13f, new ArrayList<>(Arrays.asList(-6.38f,2.89f,0f,-0.45f,1.29f,1.54f, -6.38f,2.89f,0f,-0.45f,1.29f,1.58f, -6.38f,2.89f,0f,-0.4f,1.31f,1.58f, -6.38f,2.89f,0f,-0.4f,1.31f,1.54f)));
        dp(gl,0.15f,0.76f,0.2f, new ArrayList<>(Arrays.asList(0f,0f,-4f,-0.4f,1.31f,1.54f, 0f,0f,-4f,-0.35f,1.29f,1.54f, 0f,0f,-4f,-0.33f,1.24f,1.54f, 0f,0f,-4f,-0.35f,1.2f,1.54f, 0f,0f,-4f,-0.4f,1.17f,1.54f, 0f,0f,-4f,-0.45f,1.2f,1.54f, 0f,0f,-4f,-0.47f,1.24f,1.54f, 0f,0f,-4f,-0.45f,1.29f,1.54f)));
        dp(gl,0.06f,0.8f,0.02f, new ArrayList<>(Arrays.asList(9.08f,0.71f,0f,-0.45f,1.24f,1.58f, 9.08f,0.71f,0f,-0.44f,1.21f,1.58f, 9.08f,0.71f,0f,-0.44f,1.21f,1.57f, 9.08f,0.71f,0f,-0.45f,1.24f,1.57f)));
        dp(gl,0.08f,0.59f,0.14f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.33f,1.24f,1.58f, 0f,0f,4f,-0.35f,1.29f,1.58f, 0f,0f,4f,-0.37f,1.28f,1.58f, 0f,0f,4f,-0.35f,1.24f,1.58f)));
        dp(gl,0.05f,0.88f,0.02f, new ArrayList<>(Arrays.asList(-0f,0f,4f,-0.35f,1.29f,1.58f, -0f,0f,4f,-0.4f,1.31f,1.58f, -0f,0f,4f,-0.4f,1.29f,1.58f, -0f,0f,4f,-0.37f,1.28f,1.58f)));
        dp(gl,0.17f,0.88f,0.01f, new ArrayList<>(Arrays.asList(-0f,0f,4f,-0.45f,1.2f,1.58f, -0f,0f,4f,-0.4f,1.17f,1.58f, -0f,0f,4f,-0.4f,1.19f,1.58f, -0f,0f,4f,-0.44f,1.21f,1.58f)));
        dp(gl,0.15f,0.76f,0.17f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.4f,1.31f,1.58f, 0f,0f,4f,-0.45f,1.29f,1.58f, 0f,0f,4f,-0.44f,1.28f,1.58f, 0f,0f,4f,-0.4f,1.29f,1.58f)));
        dp(gl,0.02f,0.74f,0.01f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.4f,1.17f,1.58f, 0f,0f,4f,-0.35f,1.2f,1.58f, 0f,0f,4f,-0.37f,1.21f,1.58f, 0f,0f,4f,-0.4f,1.19f,1.58f)));
        dp(gl,0.07f,0.54f,0.02f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.45f,1.29f,1.58f, 0f,0f,4f,-0.47f,1.24f,1.58f, 0f,0f,4f,-0.45f,1.24f,1.58f, 0f,0f,4f,-0.44f,1.28f,1.58f)));
        dp(gl,0.09f,0.52f,0.13f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.35f,1.2f,1.58f, 0f,0f,4f,-0.33f,1.24f,1.58f, 0f,0f,4f,-0.35f,1.24f,1.58f, 0f,0f,4f,-0.37f,1.21f,1.58f)));
        dp(gl,0.14f,0.6f,0.08f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.37f,1.28f,1.57f, 0f,0f,4f,-0.4f,1.29f,1.57f, 0f,0f,4f,-0.44f,1.28f,1.57f, 0f,0f,4f,-0.45f,1.24f,1.57f, 0f,0f,4f,-0.44f,1.21f,1.57f, 0f,0f,4f,-0.4f,1.19f,1.57f, 0f,0f,4f,-0.37f,1.21f,1.57f, 0f,0f,4f,-0.35f,1.24f,1.57f)));
        dp(gl,0.02f,0.52f,0.2f, new ArrayList<>(Arrays.asList(-9.08f,-0.71f,0f,-0.35f,1.24f,1.58f, -9.08f,-0.71f,0f,-0.37f,1.28f,1.58f, -9.08f,-0.71f,0f,-0.37f,1.28f,1.57f, -9.08f,-0.71f,0f,-0.35f,1.24f,1.57f)));
        dp(gl,0.02f,0.78f,0.13f, new ArrayList<>(Arrays.asList(9.08f,-0.71f,0f,-0.44f,1.28f,1.58f, 9.08f,-0.71f,0f,-0.45f,1.24f,1.58f, 9.08f,-0.71f,0f,-0.45f,1.24f,1.57f, 9.08f,-0.71f,0f,-0.44f,1.28f,1.57f)));
        dp(gl,0.05f,0.75f,0.07f, new ArrayList<>(Arrays.asList(-9.08f,0.71f,0f,-0.37f,1.21f,1.58f, -9.08f,0.71f,0f,-0.35f,1.24f,1.58f, -9.08f,0.71f,0f,-0.35f,1.24f,1.57f, -9.08f,0.71f,0f,-0.37f,1.21f,1.57f)));
        dp(gl,0.01f,0.73f,0.16f, new ArrayList<>(Arrays.asList(6.38f,-2.89f,0f,-0.4f,1.29f,1.58f, 6.38f,-2.89f,0f,-0.44f,1.28f,1.58f, 6.38f,-2.89f,0f,-0.44f,1.28f,1.57f, 6.38f,-2.89f,0f,-0.4f,1.29f,1.57f)));
        dp(gl,0.09f,0.53f,0.08f, new ArrayList<>(Arrays.asList(-6.38f,2.89f,0f,-0.4f,1.19f,1.58f, -6.38f,2.89f,0f,-0.37f,1.21f,1.58f, -6.38f,2.89f,0f,-0.37f,1.21f,1.57f, -6.38f,2.89f,0f,-0.4f,1.19f,1.57f)));
        dp(gl,0.12f,0.84f,0.02f, new ArrayList<>(Arrays.asList(6.38f,2.89f,0f,-0.44f,1.21f,1.58f, 6.38f,2.89f,0f,-0.4f,1.19f,1.58f, 6.38f,2.89f,0f,-0.4f,1.19f,1.57f, 6.38f,2.89f,0f,-0.44f,1.21f,1.57f)));
        dp(gl,0.18f,1.0f,0.11f, new ArrayList<>(Arrays.asList(-6.38f,-2.89f,0f,-0.37f,1.28f,1.58f, -6.38f,-2.89f,0f,-0.4f,1.29f,1.58f, -6.38f,-2.89f,0f,-0.4f,1.29f,1.57f, -6.38f,-2.89f,0f,-0.37f,1.28f,1.57f)));
    }

    void drawCorpusCovers(GL2 gl){
        dp(gl,0.19f,0.15f,0.18f, new ArrayList<>(Arrays.asList(0f,-3.89f,0.93f,0.14f,1.4f,-0.55f, 0f,-3.89f,0.93f,0.14f,1.2f,-1.39f, 0f,-3.89f,0.93f,0.67f,1.2f,-1.39f, 0f,-3.89f,0.93f,0.67f,1.4f,-0.55f)));
        dp(gl,0.16f,0.11f,0.1f, new ArrayList<>(Arrays.asList(0f,3.89f,-0.93f,0.14f,1.43f,-0.55f, 0f,3.89f,-0.93f,0.67f,1.43f,-0.55f, 0f,3.89f,-0.93f,0.67f,1.22f,-1.39f, 0f,3.89f,-0.93f,0.14f,1.22f,-1.39f)));
        dp(gl,0.17f,0.16f,0.11f, new ArrayList<>(Arrays.asList(0f,0.93f,3.89f,0.14f,1.4f,-0.55f, 0f,0.93f,3.89f,0.67f,1.4f,-0.55f, 0f,0.93f,3.89f,0.67f,1.43f,-0.55f, 0f,0.93f,3.89f,0.14f,1.43f,-0.55f)));
        dp(gl,0.13f,0.1f,0.15f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,0.67f,1.4f,-0.55f, 9.23f,0f,0f,0.67f,1.2f,-1.39f, 9.23f,0f,0f,0.67f,1.22f,-1.39f, 9.23f,0f,0f,0.67f,1.43f,-0.55f)));
        dp(gl,0.14f,0.13f,0.2f, new ArrayList<>(Arrays.asList(0f,-0.93f,-3.89f,0.67f,1.2f,-1.39f, 0f,-0.93f,-3.89f,0.14f,1.2f,-1.39f, 0f,-0.93f,-3.89f,0.14f,1.22f,-1.39f, 0f,-0.93f,-3.89f,0.67f,1.22f,-1.39f)));
        dp(gl,0.2f,0.16f,0.19f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,0.14f,1.2f,-1.39f, -9.23f,0f,0f,0.14f,1.4f,-0.55f, -9.23f,0f,0f,0.14f,1.43f,-0.55f, -9.23f,0f,0f,0.14f,1.22f,-1.39f)));
        dp(gl,0.17f,0.19f,0.17f, new ArrayList<>(Arrays.asList(0f,-3.89f,0.93f,-0.64f,1.4f,-0.55f, 0f,-3.89f,0.93f,-0.64f,1.2f,-1.39f, 0f,-3.89f,0.93f,-0.1f,1.2f,-1.39f, 0f,-3.89f,0.93f,-0.1f,1.4f,-0.55f)));
        dp(gl,0.11f,0.15f,0.1f, new ArrayList<>(Arrays.asList(0f,3.89f,-0.93f,-0.64f,1.43f,-0.55f, 0f,3.89f,-0.93f,-0.1f,1.43f,-0.55f, 0f,3.89f,-0.93f,-0.1f,1.22f,-1.39f, 0f,3.89f,-0.93f,-0.64f,1.22f,-1.39f)));
        dp(gl,0.16f,0.17f,0.12f, new ArrayList<>(Arrays.asList(0f,0.93f,3.89f,-0.64f,1.4f,-0.55f, 0f,0.93f,3.89f,-0.1f,1.4f,-0.55f, 0f,0.93f,3.89f,-0.1f,1.43f,-0.55f, 0f,0.93f,3.89f,-0.64f,1.43f,-0.55f)));
        dp(gl,0.11f,0.13f,0.17f, new ArrayList<>(Arrays.asList(9.23f,-0f,0f,-0.1f,1.4f,-0.55f, 9.23f,-0f,0f,-0.1f,1.2f,-1.39f, 9.23f,-0f,0f,-0.1f,1.22f,-1.39f, 9.23f,-0f,0f,-0.1f,1.43f,-0.55f)));
        dp(gl,0.16f,0.15f,0.12f, new ArrayList<>(Arrays.asList(0f,-0.93f,-3.89f,-0.1f,1.2f,-1.39f, 0f,-0.93f,-3.89f,-0.64f,1.2f,-1.39f, 0f,-0.93f,-3.89f,-0.64f,1.22f,-1.39f, 0f,-0.93f,-3.89f,-0.1f,1.22f,-1.39f)));
        dp(gl,0.15f,0.17f,0.12f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,-0.64f,1.2f,-1.39f, -9.23f,0f,0f,-0.64f,1.4f,-0.55f, -9.23f,0f,0f,-0.64f,1.43f,-0.55f, -9.23f,0f,0f,-0.64f,1.22f,-1.39f)));
        dp(gl,0.18f,0.18f,0.19f, new ArrayList<>(Arrays.asList(8.46f,0f,-1.6f,0.48f,1.38f,1.16f, 8.46f,0f,-1.6f,0.48f,1.41f,1.16f, 8.46f,0f,-1.6f,0.59f,1.41f,1.26f, 8.46f,0f,-1.6f,0.59f,1.38f,1.26f)));
        dp(gl,0.12f,0.12f,0.19f, new ArrayList<>(Arrays.asList(9.23f,0f,-0.01f,0.59f,1.38f,1.26f, 9.23f,0f,-0.01f,0.59f,1.41f,1.26f, 9.23f,0f,-0.01f,0.59f,1.41f,1.41f, 9.23f,0f,-0.01f,0.59f,1.38f,1.41f)));
        dp(gl,0.11f,0.19f,0.16f, new ArrayList<>(Arrays.asList(8.48f,0f,1.58f,0.59f,1.38f,1.41f, 8.48f,0f,1.58f,0.59f,1.41f,1.41f, 8.48f,0f,1.58f,0.48f,1.41f,1.52f, 8.48f,0f,1.58f,0.48f,1.38f,1.52f)));
        dp(gl,0.11f,0.11f,0.14f, new ArrayList<>(Arrays.asList(0.09f,0f,4f,0.48f,1.38f,1.52f, 0.09f,0f,4f,0.48f,1.41f,1.52f, 0.09f,0f,4f,0.33f,1.41f,1.52f, 0.09f,0f,4f,0.33f,1.38f,1.52f)));
        dp(gl,0.12f,0.11f,0.1f, new ArrayList<>(Arrays.asList(-8.46f,0f,1.6f,0.33f,1.38f,1.52f, -8.46f,0f,1.6f,0.33f,1.41f,1.52f, -8.46f,0f,1.6f,0.23f,1.41f,1.41f, -8.46f,0f,1.6f,0.23f,1.38f,1.41f)));
        dp(gl,0.11f,0.19f,0.11f, new ArrayList<>(Arrays.asList(-9.23f,0f,0.01f,0.23f,1.38f,1.41f, -9.23f,0f,0.01f,0.23f,1.41f,1.41f, -9.23f,0f,0.01f,0.23f,1.41f,1.26f, -9.23f,0f,0.01f,0.23f,1.38f,1.26f)));
        dp(gl,0.11f,0.15f,0.12f, new ArrayList<>(Arrays.asList(0f,4f,0f,0.59f,1.41f,1.26f, 0f,4f,0f,0.48f,1.41f,1.16f, 0f,4f,0f,0.33f,1.41f,1.16f, 0f,4f,0f,0.23f,1.41f,1.26f, 0f,4f,0f,0.23f,1.41f,1.41f, 0f,4f,0f,0.33f,1.41f,1.52f, 0f,4f,0f,0.48f,1.41f,1.52f, 0f,4f,0f,0.59f,1.41f,1.41f)));
        dp(gl,0.12f,0.19f,0.19f, new ArrayList<>(Arrays.asList(-8.48f,0f,-1.58f,0.23f,1.38f,1.26f, -8.48f,0f,-1.58f,0.23f,1.41f,1.26f, -8.48f,0f,-1.58f,0.33f,1.41f,1.16f, -8.48f,0f,-1.58f,0.33f,1.38f,1.16f)));
        dp(gl,0.13f,0.11f,0.15f, new ArrayList<>(Arrays.asList(-0.09f,0f,-4f,0.33f,1.38f,1.16f, -0.09f,0f,-4f,0.33f,1.41f,1.16f, -0.09f,0f,-4f,0.48f,1.41f,1.16f, -0.09f,0f,-4f,0.48f,1.38f,1.16f)));
        dp(gl,0.16f,0.1f,0.19f, new ArrayList<>(Arrays.asList(0f,-4f,0f,0.48f,1.38f,1.16f, 0f,-4f,0f,0.59f,1.38f,1.26f, 0f,-4f,0f,0.59f,1.38f,1.41f, 0f,-4f,0f,0.48f,1.38f,1.52f, 0f,-4f,0f,0.33f,1.38f,1.52f, 0f,-4f,0f,0.23f,1.38f,1.41f, 0f,-4f,0f,0.23f,1.38f,1.26f, 0f,-4f,0f,0.33f,1.38f,1.16f)));
        dp(gl,0.1f,0.17f,0.15f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,0.33f,1.4f,1.48f, -9.23f,0f,0f,0.33f,1.46f,1.48f, -9.23f,0f,0f,0.33f,1.46f,1.46f, -9.23f,0f,0f,0.33f,1.4f,1.43f)));
        dp(gl,0.13f,0.15f,0.15f, new ArrayList<>(Arrays.asList(0f,1.75f,-3.6f,0.33f,1.4f,1.43f, 0f,1.75f,-3.6f,0.33f,1.46f,1.46f, 0f,1.75f,-3.6f,0.49f,1.46f,1.46f, 0f,1.75f,-3.6f,0.49f,1.4f,1.43f)));
        dp(gl,0.13f,0.14f,0.16f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,0.49f,1.4f,1.43f, 9.23f,0f,0f,0.49f,1.46f,1.46f, 9.23f,0f,0f,0.49f,1.46f,1.48f, 9.23f,0f,0f,0.49f,1.4f,1.48f)));
        dp(gl,0.15f,0.14f,0.18f, new ArrayList<>(Arrays.asList(0f,0f,4f,0.49f,1.46f,1.48f, 0f,0f,4f,0.33f,1.46f,1.48f, 0f,0f,4f,0.34f,1.45f,1.48f, 0f,0f,4f,0.47f,1.45f,1.48f)));
        dp(gl,0.14f,0.11f,0.19f, new ArrayList<>(Arrays.asList(0f,-4f,0f,0.33f,1.4f,1.43f, 0f,-4f,0f,0.49f,1.4f,1.43f, 0f,-4f,0f,0.49f,1.4f,1.48f, 0f,-4f,0f,0.33f,1.4f,1.48f)));
        dp(gl,0.15f,0.14f,0.16f, new ArrayList<>(Arrays.asList(0f,4f,0f,0.49f,1.46f,1.46f, 0f,4f,0f,0.33f,1.46f,1.46f, 0f,4f,0f,0.33f,1.46f,1.48f, 0f,4f,0f,0.49f,1.46f,1.48f)));
        dp(gl,0.16f,0.14f,0.13f, new ArrayList<>(Arrays.asList(0f,-4f,0f,0.47f,1.45f,1.48f, 0f,-4f,0f,0.34f,1.45f,1.48f, 0f,-4f,0f,0.34f,1.45f,1.48f, 0f,-4f,0f,0.47f,1.45f,1.48f)));
        dp(gl,0.13f,0.14f,0.17f, new ArrayList<>(Arrays.asList(0f,0f,4f,0.49f,1.4f,1.48f, 0f,0f,4f,0.49f,1.46f,1.48f, 0f,0f,4f,0.47f,1.45f,1.48f, 0f,0f,4f,0.47f,1.41f,1.48f)));
        dp(gl,0.2f,0.17f,0.14f, new ArrayList<>(Arrays.asList(-0f,0f,4f,0.33f,1.46f,1.48f, -0f,0f,4f,0.33f,1.4f,1.48f, -0f,0f,4f,0.34f,1.41f,1.48f, -0f,0f,4f,0.34f,1.45f,1.48f)));
        dp(gl,0.16f,0.19f,0.1f, new ArrayList<>(Arrays.asList(0f,0f,4f,0.33f,1.4f,1.48f, 0f,0f,4f,0.49f,1.4f,1.48f, 0f,0f,4f,0.47f,1.41f,1.48f, 0f,0f,4f,0.34f,1.41f,1.48f)));
        dp(gl,0.15f,0.14f,0.17f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,0.34f,1.45f,1.48f, 9.23f,0f,0f,0.34f,1.41f,1.48f, 9.23f,0f,0f,0.34f,1.41f,1.48f, 9.23f,0f,0f,0.34f,1.45f,1.48f)));
        dp(gl,0.14f,0.14f,0.12f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,0.47f,1.41f,1.48f, -9.23f,0f,0f,0.47f,1.45f,1.48f, -9.23f,0f,0f,0.47f,1.45f,1.48f, -9.23f,0f,0f,0.47f,1.41f,1.48f)));
        dp(gl,0.2f,0.13f,0.1f, new ArrayList<>(Arrays.asList(0f,4f,0f,0.34f,1.41f,1.48f, 0f,4f,0f,0.47f,1.41f,1.48f, 0f,4f,0f,0.47f,1.41f,1.48f, 0f,4f,0f,0.34f,1.41f,1.48f)));
        dp(gl,0.15f,0.18f,0.14f, new ArrayList<>(Arrays.asList(8.46f,0f,-1.6f,-0.33f,1.38f,1.16f, 8.46f,0f,-1.6f,-0.33f,1.41f,1.16f, 8.46f,0f,-1.6f,-0.22f,1.41f,1.26f, 8.46f,0f,-1.6f,-0.22f,1.38f,1.26f)));
        dp(gl,0.17f,0.14f,0.17f, new ArrayList<>(Arrays.asList(9.23f,0f,-0.01f,-0.22f,1.38f,1.26f, 9.23f,0f,-0.01f,-0.22f,1.41f,1.26f, 9.23f,0f,-0.01f,-0.22f,1.41f,1.41f, 9.23f,0f,-0.01f,-0.22f,1.38f,1.41f)));
        dp(gl,0.15f,0.15f,0.16f, new ArrayList<>(Arrays.asList(8.48f,0f,1.58f,-0.22f,1.38f,1.41f, 8.48f,0f,1.58f,-0.22f,1.41f,1.41f, 8.48f,0f,1.58f,-0.33f,1.41f,1.52f, 8.48f,0f,1.58f,-0.33f,1.38f,1.52f)));
        dp(gl,0.16f,0.14f,0.1f, new ArrayList<>(Arrays.asList(0.09f,0f,4f,-0.33f,1.38f,1.52f, 0.09f,0f,4f,-0.33f,1.41f,1.52f, 0.09f,0f,4f,-0.48f,1.41f,1.52f, 0.09f,0f,4f,-0.48f,1.38f,1.52f)));
        dp(gl,0.18f,0.12f,0.19f, new ArrayList<>(Arrays.asList(-8.46f,0f,1.6f,-0.48f,1.38f,1.52f, -8.46f,0f,1.6f,-0.48f,1.41f,1.52f, -8.46f,0f,1.6f,-0.58f,1.41f,1.41f, -8.46f,0f,1.6f,-0.58f,1.38f,1.41f)));
        dp(gl,0.18f,0.19f,0.13f, new ArrayList<>(Arrays.asList(-9.23f,0f,0.01f,-0.58f,1.38f,1.41f, -9.23f,0f,0.01f,-0.58f,1.41f,1.41f, -9.23f,0f,0.01f,-0.58f,1.41f,1.26f, -9.23f,0f,0.01f,-0.58f,1.38f,1.26f)));
        dp(gl,0.13f,0.15f,0.15f, new ArrayList<>(Arrays.asList(0f,4f,0f,-0.22f,1.41f,1.26f, 0f,4f,0f,-0.33f,1.41f,1.16f, 0f,4f,0f,-0.48f,1.41f,1.16f, 0f,4f,0f,-0.58f,1.41f,1.26f, 0f,4f,0f,-0.58f,1.41f,1.41f, 0f,4f,0f,-0.48f,1.41f,1.52f, 0f,4f,0f,-0.33f,1.41f,1.52f, 0f,4f,0f,-0.22f,1.41f,1.41f)));
        dp(gl,0.17f,0.11f,0.15f, new ArrayList<>(Arrays.asList(-8.48f,0f,-1.58f,-0.58f,1.38f,1.26f, -8.48f,0f,-1.58f,-0.58f,1.41f,1.26f, -8.48f,0f,-1.58f,-0.48f,1.41f,1.16f, -8.48f,0f,-1.58f,-0.48f,1.38f,1.16f)));
        dp(gl,0.17f,0.18f,0.13f, new ArrayList<>(Arrays.asList(-0.09f,0f,-4f,-0.48f,1.38f,1.16f, -0.09f,0f,-4f,-0.48f,1.41f,1.16f, -0.09f,0f,-4f,-0.33f,1.41f,1.16f, -0.09f,0f,-4f,-0.33f,1.38f,1.16f)));
        dp(gl,0.12f,0.11f,0.13f, new ArrayList<>(Arrays.asList(0f,-4f,0f,-0.33f,1.38f,1.16f, 0f,-4f,0f,-0.22f,1.38f,1.26f, 0f,-4f,0f,-0.22f,1.38f,1.41f, 0f,-4f,0f,-0.33f,1.38f,1.52f, 0f,-4f,0f,-0.48f,1.38f,1.52f, 0f,-4f,0f,-0.58f,1.38f,1.41f, 0f,-4f,0f,-0.58f,1.38f,1.26f, 0f,-4f,0f,-0.48f,1.38f,1.16f)));
        dp(gl,0.11f,0.11f,0.2f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,-0.48f,1.4f,1.48f, -9.23f,0f,0f,-0.48f,1.46f,1.48f, -9.23f,0f,0f,-0.48f,1.46f,1.46f, -9.23f,0f,0f,-0.48f,1.4f,1.43f)));
        dp(gl,0.19f,0.15f,0.17f, new ArrayList<>(Arrays.asList(0f,1.75f,-3.6f,-0.48f,1.4f,1.43f, 0f,1.75f,-3.6f,-0.48f,1.46f,1.46f, 0f,1.75f,-3.6f,-0.32f,1.46f,1.46f, 0f,1.75f,-3.6f,-0.32f,1.4f,1.43f)));
        dp(gl,0.16f,0.1f,0.15f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,-0.32f,1.4f,1.43f, 9.23f,0f,0f,-0.32f,1.46f,1.46f, 9.23f,0f,0f,-0.32f,1.46f,1.48f, 9.23f,0f,0f,-0.32f,1.4f,1.48f)));
        dp(gl,0.19f,0.18f,0.1f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.32f,1.46f,1.48f, 0f,0f,4f,-0.48f,1.46f,1.48f, 0f,0f,4f,-0.47f,1.45f,1.48f, 0f,0f,4f,-0.34f,1.45f,1.48f)));
        dp(gl,0.13f,0.17f,0.12f, new ArrayList<>(Arrays.asList(0f,-4f,0f,-0.48f,1.4f,1.43f, 0f,-4f,0f,-0.32f,1.4f,1.43f, 0f,-4f,0f,-0.32f,1.4f,1.48f, 0f,-4f,0f,-0.48f,1.4f,1.48f)));
        dp(gl,0.19f,0.19f,0.13f, new ArrayList<>(Arrays.asList(0f,4f,0f,-0.32f,1.46f,1.46f, 0f,4f,0f,-0.48f,1.46f,1.46f, 0f,4f,0f,-0.48f,1.46f,1.48f, 0f,4f,0f,-0.32f,1.46f,1.48f)));
        dp(gl,0.13f,0.13f,0.2f, new ArrayList<>(Arrays.asList(0f,-4f,0f,-0.34f,1.45f,1.48f, 0f,-4f,0f,-0.47f,1.45f,1.48f, 0f,-4f,0f,-0.47f,1.45f,1.48f, 0f,-4f,0f,-0.34f,1.45f,1.48f)));
        dp(gl,0.19f,0.2f,0.14f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.32f,1.4f,1.48f, 0f,0f,4f,-0.32f,1.46f,1.48f, 0f,0f,4f,-0.34f,1.45f,1.48f, 0f,0f,4f,-0.34f,1.41f,1.48f)));
        dp(gl,0.17f,0.13f,0.14f, new ArrayList<>(Arrays.asList(-0f,0f,4f,-0.48f,1.46f,1.48f, -0f,0f,4f,-0.48f,1.4f,1.48f, -0f,0f,4f,-0.47f,1.41f,1.48f, -0f,0f,4f,-0.47f,1.45f,1.48f)));
        dp(gl,0.19f,0.12f,0.2f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.48f,1.4f,1.48f, 0f,0f,4f,-0.32f,1.4f,1.48f, 0f,0f,4f,-0.34f,1.41f,1.48f, 0f,0f,4f,-0.47f,1.41f,1.48f)));
        dp(gl,0.16f,0.18f,0.1f, new ArrayList<>(Arrays.asList(9.23f,0f,0f,-0.47f,1.45f,1.48f, 9.23f,0f,0f,-0.47f,1.41f,1.48f, 9.23f,0f,0f,-0.47f,1.41f,1.48f, 9.23f,0f,0f,-0.47f,1.45f,1.48f)));
        dp(gl,0.12f,0.11f,0.17f, new ArrayList<>(Arrays.asList(-9.23f,0f,0f,-0.34f,1.41f,1.48f, -9.23f,0f,0f,-0.34f,1.45f,1.48f, -9.23f,0f,0f,-0.34f,1.45f,1.48f, -9.23f,0f,0f,-0.34f,1.41f,1.48f)));
        dp(gl,0.19f,0.12f,0.19f, new ArrayList<>(Arrays.asList(0f,4f,0f,-0.47f,1.41f,1.48f, 0f,4f,0f,-0.34f,1.41f,1.48f, 0f,4f,0f,-0.34f,1.41f,1.48f, 0f,4f,0f,-0.47f,1.41f,1.48f)));
    }

    void drawRightTrack(GL2 gl){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
        toggleEdgeClipping(false);
        dp(gl,0.64f,0.63f,0.87f, new ArrayList<>(Arrays.asList(0f,0f,4f,0.47f,1.41f,1.48f, 0f,0f,4f,0.47f,1.45f,1.48f, 0f,0f,4f,0.34f,1.45f,1.48f, 0f,0f,4f,0.34f,1.41f,1.48f)));
        dp(gl,0.64f,0.62f,0.87f, new ArrayList<>(Arrays.asList(0f,0f,4f,-0.34f,1.41f,1.48f, 0f,0f,4f,-0.34f,1.45f,1.48f, 0f,0f,4f,-0.47f,1.45f,1.48f, 0f,0f,4f,-0.47f,1.41f,1.48f)));
        dp(gl,0.62f,0.63f,0.8f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.2f,-0.53f,0.89f,0.9f, -0f,4.12f,-0.2f,-1.09f,0.89f,0.9f, -0f,4.12f,-0.2f,-1.09f,0.87f,0.61f, -0f,4.12f,-0.2f,-0.53f,0.87f,0.61f)));
        dp(gl,0.64f,0.64f,0.89f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.22f,-0.53f,0.87f,0.61f, 0f,4.12f,-0.22f,-1.09f,0.87f,0.61f, 0f,4.12f,-0.22f,-1.09f,0.86f,0.32f, 0f,4.12f,-0.22f,-0.53f,0.86f,0.32f)));
        dp(gl,0.62f,0.63f,0.89f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.23f,-0.53f,0.86f,0.32f, 0f,4.12f,-0.23f,-1.09f,0.86f,0.32f, 0f,4.12f,-0.23f,-1.09f,0.84f,0.03f, 0f,4.12f,-0.23f,-0.53f,0.84f,0.03f)));
        dp(gl,0.64f,0.63f,0.89f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.23f,-0.53f,0.84f,0.03f, 0f,4.12f,-0.23f,-1.09f,0.84f,0.03f, 0f,4.12f,-0.23f,-1.09f,0.83f,-0.27f, 0f,4.12f,-0.23f,-0.53f,0.83f,-0.27f)));
        dp(gl,0.62f,0.61f,0.81f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.23f,-0.53f,0.83f,-0.27f, 0f,4.12f,-0.23f,-1.09f,0.83f,-0.27f, 0f,4.12f,-0.23f,-1.09f,0.81f,-0.56f, 0f,4.12f,-0.23f,-0.53f,0.81f,-0.56f)));
        dp(gl,0.65f,0.62f,0.83f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.22f,-0.53f,0.81f,-0.56f, 0f,4.12f,-0.22f,-1.09f,0.81f,-0.56f, 0f,4.12f,-0.22f,-1.09f,0.79f,-0.86f, 0f,4.12f,-0.22f,-0.53f,0.79f,-0.86f)));
        dp(gl,0.62f,0.62f,0.82f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.2f,-0.53f,0.79f,-0.86f, 0f,4.12f,-0.2f,-1.09f,0.79f,-0.86f, 0f,4.12f,-0.2f,-1.09f,0.78f,-1.16f, 0f,4.12f,-0.2f,-0.53f,0.78f,-1.16f)));
        dp(gl,0.64f,0.61f,0.87f, new ArrayList<>(Arrays.asList(0f,4.07f,-0.65f,-0.53f,0.78f,-1.16f, 0f,4.07f,-0.65f,-1.09f,0.78f,-1.16f, 0f,4.07f,-0.65f,-1.09f,0.73f,-1.45f, 0f,4.07f,-0.65f,-0.53f,0.73f,-1.45f)));
        dp(gl,0.65f,0.64f,0.86f, new ArrayList<>(Arrays.asList(0f,1.71f,-3.75f,-0.53f,0.73f,-1.45f, 0f,1.71f,-3.75f,-1.09f,0.73f,-1.45f, 0f,1.71f,-3.75f,-1.09f,0.48f,-1.56f, 0f,1.71f,-3.75f,-0.53f,0.48f,-1.56f)));
        dp(gl,0.6f,0.61f,0.87f, new ArrayList<>(Arrays.asList(-0f,-2.35f,-3.39f,-0.53f,0.48f,-1.56f, -0f,-2.35f,-3.39f,-1.09f,0.48f,-1.56f, -0f,-2.35f,-3.39f,-1.09f,0.24f,-1.4f, -0f,-2.35f,-3.39f,-0.53f,0.24f,-1.4f)));
        dp(gl,0.63f,0.63f,0.8f, new ArrayList<>(Arrays.asList(0f,-3.58f,-2.06f,-0.53f,0.24f,-1.4f, 0f,-3.58f,-2.06f,-1.09f,0.24f,-1.4f, 0f,-3.58f,-2.06f,-1.09f,0.1f,-1.14f, 0f,-3.58f,-2.06f,-0.53f,0.1f,-1.14f)));
        dp(gl,0.65f,0.61f,0.87f, new ArrayList<>(Arrays.asList(0f,-3.94f,-1.22f,-0.53f,0.1f,-1.14f, 0f,-3.94f,-1.22f,-1.09f,0.1f,-1.14f, 0f,-3.94f,-1.22f,-1.09f,0.01f,-0.87f, 0f,-3.94f,-1.22f,-0.53f,0.01f,-0.87f)));
        dp(gl,0.65f,0.62f,0.89f, new ArrayList<>(Arrays.asList(-0f,-4.13f,-0.07f,-0.53f,0.01f,-0.87f, -0f,-4.13f,-0.07f,-1.09f,0.01f,-0.87f, -0f,-4.13f,-0.07f,-1.09f,0f,-0.57f, -0f,-4.13f,-0.07f,-0.53f,0f,-0.57f)));
        dp(gl,0.62f,0.62f,0.83f, new ArrayList<>(Arrays.asList(-0f,-4.13f,-0.01f,-0.53f,0f,-0.57f, -0f,-4.13f,-0.01f,-1.09f,0f,-0.57f, -0f,-4.13f,-0.01f,-1.09f,0f,-0.27f, -0f,-4.13f,-0.01f,-0.53f,0f,-0.27f)));
        dp(gl,0.63f,0.64f,0.88f, new ArrayList<>(Arrays.asList(-0f,-4.13f,-0f,-0.53f,0f,-0.27f, -0f,-4.13f,-0f,-1.09f,0f,-0.27f, -0f,-4.13f,-0f,-1.09f,0f,0.02f, -0f,-4.13f,-0f,-0.53f,0f,0.02f)));
        dp(gl,0.64f,0.65f,0.84f, new ArrayList<>(Arrays.asList(0f,-4.13f,0.01f,-0.53f,0f,0.02f, 0f,-4.13f,0.01f,-1.09f,0f,0.02f, 0f,-4.13f,0.01f,-1.09f,0f,0.32f, 0f,-4.13f,0.01f,-0.53f,0f,0.32f)));
        dp(gl,0.6f,0.62f,0.89f, new ArrayList<>(Arrays.asList(-0f,-4.13f,0.02f,-0.53f,0f,0.32f, -0f,-4.13f,0.02f,-1.09f,0f,0.32f, -0f,-4.13f,0.02f,-1.09f,0.01f,0.62f, -0f,-4.13f,0.02f,-0.53f,0.01f,0.62f)));
        dp(gl,0.63f,0.63f,0.89f, new ArrayList<>(Arrays.asList(-0f,-4.13f,0.06f,-0.53f,0.01f,0.62f, -0f,-4.13f,0.06f,-1.09f,0.01f,0.62f, -0f,-4.13f,0.06f,-1.09f,0.01f,0.91f, -0f,-4.13f,0.06f,-0.53f,0.01f,0.91f)));
        dp(gl,0.6f,0.61f,0.81f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.26f,-0.53f,0.01f,0.91f, -0f,-4.12f,0.26f,-1.09f,0.01f,0.91f, -0f,-4.12f,0.26f,-1.09f,0.03f,1.21f, -0f,-4.12f,0.26f,-0.53f,0.03f,1.21f)));
        dp(gl,0.61f,0.63f,0.86f, new ArrayList<>(Arrays.asList(0f,-3.5f,2.18f,-0.53f,0.03f,1.21f, 0f,-3.5f,2.18f,-1.09f,0.03f,1.21f, 0f,-3.5f,2.18f,-1.09f,0.18f,1.46f, 0f,-3.5f,2.18f,-0.53f,0.18f,1.46f)));
        dp(gl,0.64f,0.63f,0.9f, new ArrayList<>(Arrays.asList(0f,-2.81f,3.02f,-0.53f,0.18f,1.46f, 0f,-2.81f,3.02f,-1.09f,0.18f,1.46f, 0f,-2.81f,3.02f,-1.09f,0.4f,1.66f, 0f,-2.81f,3.02f,-0.53f,0.4f,1.66f)));
        dp(gl,0.64f,0.63f,0.89f, new ArrayList<>(Arrays.asList(0f,-0.58f,4.08f,-0.53f,0.4f,1.66f, 0f,-0.58f,4.08f,-1.09f,0.4f,1.66f, 0f,-0.58f,4.08f,-1.09f,0.69f,1.7f, 0f,-0.58f,4.08f,-0.53f,0.69f,1.7f)));
        dp(gl,0.63f,0.62f,0.85f, new ArrayList<>(Arrays.asList(-0.03f,2.78f,3.05f,-0.53f,0.69f,1.7f, -0.03f,2.78f,3.05f,-1.09f,0.69f,1.7f, -0.03f,2.78f,3.05f,-1.09f,0.89f,1.51f, -0.03f,2.78f,3.05f,-0.53f,0.89f,1.52f)));
        dp(gl,0.6f,0.61f,0.8f, new ArrayList<>(Arrays.asList(0f,4.12f,0.14f,-0.53f,0.89f,1.52f, 0f,4.12f,0.14f,-1.09f,0.89f,1.51f, 0f,4.12f,0.14f,-1.09f,0.9f,1.2f, 0f,4.12f,0.14f,-0.53f,0.9f,1.23f)));
        dp(gl,0.61f,0.63f,0.82f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.14f,-0.53f,0.9f,1.23f, 0f,4.12f,-0.14f,-1.09f,0.9f,1.2f, 0f,4.12f,-0.14f,-1.09f,0.89f,0.9f, 0f,4.12f,-0.14f,-0.53f,0.89f,0.9f)));
        dp(gl,0.61f,0.64f,0.87f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.2f,-0.53f,0.85f,0.9f, 0f,-4.12f,0.2f,-0.53f,0.84f,0.61f, 0f,-4.12f,0.2f,-1.09f,0.84f,0.61f, 0f,-4.12f,0.2f,-1.09f,0.85f,0.9f)));
        dp(gl,0.64f,0.62f,0.86f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.22f,-0.53f,0.84f,0.61f, -0f,-4.12f,0.22f,-0.53f,0.82f,0.33f, -0f,-4.12f,0.22f,-1.09f,0.82f,0.33f, -0f,-4.12f,0.22f,-1.09f,0.84f,0.61f)));
        dp(gl,0.65f,0.62f,0.89f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.23f,-0.53f,0.82f,0.33f, -0f,-4.12f,0.23f,-0.53f,0.81f,0.03f, -0f,-4.12f,0.23f,-1.09f,0.81f,0.03f, -0f,-4.12f,0.23f,-1.09f,0.82f,0.33f)));
        dp(gl,0.64f,0.63f,0.87f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.23f,-0.53f,0.81f,0.03f, -0f,-4.12f,0.23f,-0.53f,0.79f,-0.27f, -0f,-4.12f,0.23f,-1.09f,0.79f,-0.27f, -0f,-4.12f,0.23f,-1.09f,0.81f,0.03f)));
        dp(gl,0.63f,0.62f,0.8f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.23f,-0.53f,0.79f,-0.27f, -0f,-4.12f,0.23f,-0.53f,0.77f,-0.56f, -0f,-4.12f,0.23f,-1.09f,0.77f,-0.56f, -0f,-4.12f,0.23f,-1.09f,0.79f,-0.27f)));
        dp(gl,0.63f,0.64f,0.84f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.22f,-0.53f,0.77f,-0.56f, -0f,-4.12f,0.22f,-0.53f,0.76f,-0.86f, -0f,-4.12f,0.22f,-1.09f,0.76f,-0.86f, -0f,-4.12f,0.22f,-1.09f,0.77f,-0.56f)));
        dp(gl,0.61f,0.61f,0.81f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.2f,-0.53f,0.76f,-0.86f, -0f,-4.12f,0.2f,-0.53f,0.74f,-1.15f, -0f,-4.12f,0.2f,-1.09f,0.74f,-1.15f, -0f,-4.12f,0.2f,-1.09f,0.76f,-0.86f)));
        dp(gl,0.61f,0.6f,0.85f, new ArrayList<>(Arrays.asList(-0f,-4.08f,0.59f,-0.53f,0.74f,-1.15f, -0f,-4.08f,0.59f,-0.53f,0.7f,-1.42f, -0f,-4.08f,0.59f,-1.09f,0.7f,-1.42f, -0f,-4.08f,0.59f,-1.09f,0.74f,-1.15f)));
        dp(gl,0.65f,0.61f,0.8f, new ArrayList<>(Arrays.asList(0f,-1.72f,3.75f,-0.53f,0.7f,-1.42f, 0f,-1.72f,3.75f,-0.53f,0.48f,-1.53f, 0f,-1.72f,3.75f,-1.09f,0.48f,-1.53f, 0f,-1.72f,3.75f,-1.09f,0.7f,-1.42f)));
        dp(gl,0.63f,0.64f,0.8f, new ArrayList<>(Arrays.asList(-0f,2.39f,3.36f,-0.53f,0.48f,-1.53f, -0f,2.39f,3.36f,-0.53f,0.27f,-1.38f, -0f,2.39f,3.36f,-1.09f,0.27f,-1.38f, -0f,2.39f,3.36f,-1.09f,0.48f,-1.53f)));
        dp(gl,0.62f,0.64f,0.88f, new ArrayList<>(Arrays.asList(-0f,3.58f,2.05f,-0.53f,0.27f,-1.38f, -0f,3.58f,2.05f,-0.53f,0.13f,-1.13f, -0f,3.58f,2.05f,-1.09f,0.13f,-1.13f, -0f,3.58f,2.05f,-1.09f,0.27f,-1.38f)));
        dp(gl,0.63f,0.65f,0.82f, new ArrayList<>(Arrays.asList(-0f,3.94f,1.22f,-0.53f,0.13f,-1.13f, -0f,3.94f,1.22f,-0.53f,0.04f,-0.86f, -0f,3.94f,1.22f,-1.09f,0.04f,-0.86f, -0f,3.94f,1.22f,-1.09f,0.13f,-1.13f)));
        dp(gl,0.63f,0.6f,0.88f, new ArrayList<>(Arrays.asList(0f,4.13f,0.06f,-0.53f,0.04f,-0.86f, 0f,4.13f,0.06f,-0.53f,0.04f,-0.57f, 0f,4.13f,0.06f,-1.09f,0.04f,-0.57f, 0f,4.13f,0.06f,-1.09f,0.04f,-0.86f)));
        dp(gl,0.61f,0.61f,0.88f, new ArrayList<>(Arrays.asList(0f,4.13f,0.01f,-0.53f,0.04f,-0.57f, 0f,4.13f,0.01f,-0.53f,0.04f,-0.27f, 0f,4.13f,0.01f,-1.09f,0.04f,-0.27f, 0f,4.13f,0.01f,-1.09f,0.04f,-0.57f)));
        dp(gl,0.64f,0.64f,0.89f, new ArrayList<>(Arrays.asList(0f,4.13f,0f,-0.53f,0.04f,-0.27f, 0f,4.13f,0f,-0.53f,0.04f,0.02f, 0f,4.13f,0f,-1.09f,0.04f,0.02f, 0f,4.13f,0f,-1.09f,0.04f,-0.27f)));
        dp(gl,0.62f,0.62f,0.84f, new ArrayList<>(Arrays.asList(0f,4.13f,-0.01f,-0.53f,0.04f,0.02f, 0f,4.13f,-0.01f,-0.53f,0.04f,0.32f, 0f,4.13f,-0.01f,-1.09f,0.04f,0.32f, 0f,4.13f,-0.01f,-1.09f,0.04f,0.02f)));
        dp(gl,0.63f,0.65f,0.83f, new ArrayList<>(Arrays.asList(0f,4.13f,-0.02f,-0.53f,0.04f,0.32f, 0f,4.13f,-0.02f,-0.53f,0.04f,0.62f, 0f,4.13f,-0.02f,-1.09f,0.04f,0.62f, 0f,4.13f,-0.02f,-1.09f,0.04f,0.32f)));
        dp(gl,0.62f,0.64f,0.83f, new ArrayList<>(Arrays.asList(0f,4.13f,-0.06f,-0.53f,0.04f,0.62f, 0f,4.13f,-0.06f,-0.53f,0.05f,0.91f, 0f,4.13f,-0.06f,-1.09f,0.05f,0.91f, 0f,4.13f,-0.06f,-1.09f,0.04f,0.62f)));
        dp(gl,0.61f,0.61f,0.83f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.25f,-0.53f,0.05f,0.91f, 0f,4.12f,-0.25f,-0.53f,0.06f,1.2f, 0f,4.12f,-0.25f,-1.09f,0.06f,1.2f, 0f,4.12f,-0.25f,-1.09f,0.05f,0.91f)));
        dp(gl,0.61f,0.63f,0.86f, new ArrayList<>(Arrays.asList(0f,3.5f,-2.19f,-0.53f,0.06f,1.2f, 0f,3.5f,-2.19f,-0.53f,0.21f,1.43f, 0f,3.5f,-2.19f,-1.09f,0.21f,1.43f, 0f,3.5f,-2.19f,-1.09f,0.06f,1.2f)));
        dp(gl,0.65f,0.63f,0.81f, new ArrayList<>(Arrays.asList(0f,2.82f,-3.01f,-0.53f,0.21f,1.43f, 0f,2.82f,-3.01f,-0.53f,0.41f,1.63f, 0f,2.82f,-3.01f,-1.09f,0.41f,1.63f, 0f,2.82f,-3.01f,-1.09f,0.21f,1.43f)));
        dp(gl,0.65f,0.64f,0.9f, new ArrayList<>(Arrays.asList(0f,0.61f,-4.08f,-0.53f,0.41f,1.63f, 0f,0.61f,-4.08f,-0.53f,0.68f,1.66f, 0f,0.61f,-4.08f,-1.09f,0.68f,1.66f, 0f,0.61f,-4.08f,-1.09f,0.41f,1.63f)));
        dp(gl,0.64f,0.63f,0.82f, new ArrayList<>(Arrays.asList(0.03f,-2.79f,-3.04f,-0.53f,0.68f,1.66f, 0.03f,-2.79f,-3.04f,-0.53f,0.85f,1.51f, 0.03f,-2.79f,-3.04f,-1.09f,0.86f,1.49f, 0.03f,-2.79f,-3.04f,-1.09f,0.68f,1.66f)));
        dp(gl,0.64f,0.61f,0.8f, new ArrayList<>(Arrays.asList(-0f,-4.12f,-0.1f,-0.53f,0.85f,1.51f, -0f,-4.12f,-0.1f,-0.53f,0.86f,1.23f, -0f,-4.12f,-0.1f,-1.09f,0.86f,1.2f, -0f,-4.12f,-0.1f,-1.09f,0.86f,1.49f)));
        dp(gl,0.61f,0.61f,0.81f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.14f,-0.53f,0.86f,1.23f, -0f,-4.12f,0.14f,-0.53f,0.85f,0.9f, -0f,-4.12f,0.14f,-1.09f,0.85f,0.9f, -0f,-4.12f,0.14f,-1.09f,0.86f,1.2f)));
        dp(gl,0.63f,0.62f,0.87f, new ArrayList<>(Arrays.asList(4.13f,-0f,0f,-0.53f,0.89f,0.9f, 4.13f,-0f,0f,-0.53f,0.87f,0.61f, 4.13f,-0f,0f,-0.53f,0.84f,0.61f, 4.13f,-0f,0f,-0.53f,0.85f,0.9f)));
        dp(gl,0.64f,0.63f,0.88f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0f,-1.09f,0.87f,0.61f, -4.13f,0f,-0f,-1.09f,0.89f,0.9f, -4.13f,0f,-0f,-1.09f,0.85f,0.9f, -4.13f,0f,-0f,-1.09f,0.84f,0.61f)));
        dp(gl,0.62f,0.65f,0.82f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.87f,0.61f, 4.13f,0f,0f,-0.53f,0.86f,0.32f, 4.13f,0f,0f,-0.53f,0.82f,0.33f, 4.13f,0f,0f,-0.53f,0.84f,0.61f)));
        dp(gl,0.6f,0.64f,0.83f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.24f,-1.4f, -4.13f,0f,0f,-1.09f,0.48f,-1.56f, -4.13f,0f,0f,-1.09f,0.48f,-1.53f, -4.13f,0f,0f,-1.09f,0.27f,-1.38f)));
        dp(gl,0.64f,0.65f,0.81f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.83f,-0.27f, -4.13f,0f,0f,-1.09f,0.84f,0.03f, -4.13f,0f,0f,-1.09f,0.81f,0.03f, -4.13f,0f,0f,-1.09f,0.79f,-0.27f)));
        dp(gl,0.65f,0.61f,0.81f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,-0.53f,0.83f,-0.27f, 4.13f,0f,-0f,-0.53f,0.81f,-0.56f, 4.13f,0f,-0f,-0.53f,0.77f,-0.56f, 4.13f,0f,-0f,-0.53f,0.79f,-0.27f)));
        dp(gl,0.62f,0.63f,0.88f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0f,-1.09f,0.81f,-0.56f, -4.13f,0f,-0f,-1.09f,0.83f,-0.27f, -4.13f,0f,-0f,-1.09f,0.79f,-0.27f, -4.13f,0f,-0f,-1.09f,0.77f,-0.56f)));
        dp(gl,0.6f,0.61f,0.85f, new ArrayList<>(Arrays.asList(4.13f,-0f,-0f,-0.53f,0.81f,-0.56f, 4.13f,-0f,-0f,-0.53f,0.79f,-0.86f, 4.13f,-0f,-0f,-0.53f,0.76f,-0.86f, 4.13f,-0f,-0f,-0.53f,0.77f,-0.56f)));
        dp(gl,0.63f,0.62f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,-0f,0f,-1.09f,0.79f,-0.86f, -4.13f,-0f,0f,-1.09f,0.81f,-0.56f, -4.13f,-0f,0f,-1.09f,0.77f,-0.56f, -4.13f,-0f,0f,-1.09f,0.76f,-0.86f)));
        dp(gl,0.63f,0.65f,0.84f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.79f,-0.86f, 4.13f,0f,0f,-0.53f,0.78f,-1.16f, 4.13f,0f,0f,-0.53f,0.74f,-1.15f, 4.13f,0f,0f,-0.53f,0.76f,-0.86f)));
        dp(gl,0.62f,0.64f,0.81f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.78f,-1.16f, -4.13f,0f,0f,-1.09f,0.79f,-0.86f, -4.13f,0f,0f,-1.09f,0.76f,-0.86f, -4.13f,0f,0f,-1.09f,0.74f,-1.15f)));
        dp(gl,0.63f,0.61f,0.84f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.78f,-1.16f, 4.13f,0f,0f,-0.53f,0.73f,-1.45f, 4.13f,0f,0f,-0.53f,0.7f,-1.42f, 4.13f,0f,0f,-0.53f,0.74f,-1.15f)));
        dp(gl,0.63f,0.63f,0.87f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.73f,-1.45f, -4.13f,0f,0f,-1.09f,0.78f,-1.16f, -4.13f,0f,0f,-1.09f,0.74f,-1.15f, -4.13f,0f,0f,-1.09f,0.7f,-1.42f)));
        dp(gl,0.64f,0.63f,0.84f, new ArrayList<>(Arrays.asList(4.13f,-0f,-0f,-0.53f,0.73f,-1.45f, 4.13f,-0f,-0f,-0.53f,0.48f,-1.56f, 4.13f,-0f,-0f,-0.53f,0.48f,-1.53f, 4.13f,-0f,-0f,-0.53f,0.7f,-1.42f)));
        dp(gl,0.6f,0.63f,0.81f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.48f,-1.56f, -4.13f,0f,0f,-1.09f,0.73f,-1.45f, -4.13f,0f,0f,-1.09f,0.7f,-1.42f, -4.13f,0f,0f,-1.09f,0.48f,-1.53f)));
        dp(gl,0.62f,0.65f,0.82f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.1f,-1.14f, -4.13f,0f,0f,-1.09f,0.24f,-1.4f, -4.13f,0f,0f,-1.09f,0.27f,-1.38f, -4.13f,0f,0f,-1.09f,0.13f,-1.13f)));
        dp(gl,0.63f,0.64f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,-0f,-0f,-1.09f,0.01f,-0.87f, -4.13f,-0f,-0f,-1.09f,0.1f,-1.14f, -4.13f,-0f,-0f,-1.09f,0.13f,-1.13f, -4.13f,-0f,-0f,-1.09f,0.04f,-0.86f)));
        dp(gl,0.61f,0.63f,0.82f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.48f,-1.56f, 4.13f,0f,0f,-0.53f,0.24f,-1.4f, 4.13f,0f,0f,-0.53f,0.27f,-1.38f, 4.13f,0f,0f,-0.53f,0.48f,-1.53f)));
        dp(gl,0.64f,0.63f,0.81f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0f,-0.57f, -4.13f,0f,0f,-1.09f,0.01f,-0.87f, -4.13f,0f,0f,-1.09f,0.04f,-0.86f, -4.13f,0f,0f,-1.09f,0.04f,-0.57f)));
        dp(gl,0.62f,0.61f,0.82f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.24f,-1.4f, 4.13f,0f,0f,-0.53f,0.1f,-1.14f, 4.13f,0f,0f,-0.53f,0.13f,-1.13f, 4.13f,0f,0f,-0.53f,0.27f,-1.38f)));
        dp(gl,0.63f,0.65f,0.88f, new ArrayList<>(Arrays.asList(-4.13f,-0f,-0f,-1.09f,0.03f,1.21f, -4.13f,-0f,-0f,-1.09f,0.01f,0.91f, -4.13f,-0f,-0f,-1.09f,0.05f,0.91f, -4.13f,-0f,-0f,-1.09f,0.06f,1.2f)));
        dp(gl,0.62f,0.61f,0.8f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,-0.53f,0.1f,-1.14f, 4.13f,0f,-0f,-0.53f,0.01f,-0.87f, 4.13f,0f,-0f,-0.53f,0.04f,-0.86f, 4.13f,0f,-0f,-0.53f,0.13f,-1.13f)));
        dp(gl,0.62f,0.6f,0.8f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0f,-0.57f, 4.13f,0f,0f,-0.53f,0f,-0.27f, 4.13f,0f,0f,-0.53f,0.04f,-0.27f, 4.13f,0f,0f,-0.53f,0.04f,-0.57f)));
        dp(gl,0.62f,0.64f,0.8f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.01f,-0.87f, 4.13f,0f,0f,-0.53f,0f,-0.57f, 4.13f,0f,0f,-0.53f,0.04f,-0.57f, 4.13f,0f,0f,-0.53f,0.04f,-0.86f)));
        dp(gl,0.62f,0.61f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,-0f,-0f,-1.09f,0f,-0.27f, -4.13f,-0f,-0f,-1.09f,0f,-0.57f, -4.13f,-0f,-0f,-1.09f,0.04f,-0.57f, -4.13f,-0f,-0f,-1.09f,0.04f,-0.27f)));
        dp(gl,0.64f,0.61f,0.85f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0f,-0.27f, 4.13f,0f,0f,-0.53f,0f,0.02f, 4.13f,0f,0f,-0.53f,0.04f,0.02f, 4.13f,0f,0f,-0.53f,0.04f,-0.27f)));
        dp(gl,0.63f,0.63f,0.89f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0f,0.02f, -4.13f,0f,0f,-1.09f,0f,-0.27f, -4.13f,0f,0f,-1.09f,0.04f,-0.27f, -4.13f,0f,0f,-1.09f,0.04f,0.02f)));
        dp(gl,0.61f,0.63f,0.85f, new ArrayList<>(Arrays.asList(4.13f,-0f,-0f,-0.53f,0f,0.02f, 4.13f,-0f,-0f,-0.53f,0f,0.32f, 4.13f,-0f,-0f,-0.53f,0.04f,0.32f, 4.13f,-0f,-0f,-0.53f,0.04f,0.02f)));
        dp(gl,0.65f,0.63f,0.87f, new ArrayList<>(Arrays.asList(-4.13f,-0f,-0f,-1.09f,0f,0.32f, -4.13f,-0f,-0f,-1.09f,0f,0.02f, -4.13f,-0f,-0f,-1.09f,0.04f,0.02f, -4.13f,-0f,-0f,-1.09f,0.04f,0.32f)));
        dp(gl,0.61f,0.63f,0.86f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0f,0.32f, 4.13f,0f,0f,-0.53f,0.01f,0.62f, 4.13f,0f,0f,-0.53f,0.04f,0.62f, 4.13f,0f,0f,-0.53f,0.04f,0.32f)));
        dp(gl,0.61f,0.61f,0.86f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.01f,0.62f, -4.13f,0f,0f,-1.09f,0f,0.32f, -4.13f,0f,0f,-1.09f,0.04f,0.32f, -4.13f,0f,0f,-1.09f,0.04f,0.62f)));
        dp(gl,0.63f,0.64f,0.83f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.01f,0.62f, 4.13f,0f,0f,-0.53f,0.01f,0.91f, 4.13f,0f,0f,-0.53f,0.05f,0.91f, 4.13f,0f,0f,-0.53f,0.04f,0.62f)));
        dp(gl,0.63f,0.61f,0.84f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.01f,0.91f, -4.13f,0f,0f,-1.09f,0.01f,0.62f, -4.13f,0f,0f,-1.09f,0.04f,0.62f, -4.13f,0f,0f,-1.09f,0.05f,0.91f)));
        dp(gl,0.61f,0.64f,0.89f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.4f,1.66f, -4.13f,0f,0f,-1.09f,0.18f,1.46f, -4.13f,0f,0f,-1.09f,0.21f,1.43f, -4.13f,0f,0f,-1.09f,0.41f,1.63f)));
        dp(gl,0.61f,0.63f,0.83f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,-0.53f,0.03f,1.21f, 4.13f,0f,-0f,-0.53f,0.18f,1.46f, 4.13f,0f,-0f,-0.53f,0.21f,1.43f, 4.13f,0f,-0f,-0.53f,0.06f,1.2f)));
        dp(gl,0.6f,0.6f,0.84f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.01f,0.91f, 4.13f,0f,0f,-0.53f,0.03f,1.21f, 4.13f,0f,0f,-0.53f,0.06f,1.2f, 4.13f,0f,0f,-0.53f,0.05f,0.91f)));
        dp(gl,0.61f,0.61f,0.86f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0f,-1.09f,0.18f,1.46f, -4.13f,0f,-0f,-1.09f,0.03f,1.21f, -4.13f,0f,-0f,-1.09f,0.06f,1.2f, -4.13f,0f,-0f,-1.09f,0.21f,1.43f)));
        dp(gl,0.64f,0.61f,0.86f, new ArrayList<>(Arrays.asList(-4.13f,-0.01f,-0.01f,-1.09f,0.89f,1.51f, -4.13f,-0.01f,-0.01f,-1.09f,0.69f,1.7f, -4.13f,-0.01f,-0.01f,-1.09f,0.68f,1.66f, -4.13f,-0.01f,-0.01f,-1.09f,0.86f,1.49f)));
        dp(gl,0.63f,0.62f,0.85f, new ArrayList<>(Arrays.asList(4.13f,-0f,0.01f,-0.53f,0.4f,1.66f, 4.13f,-0f,0.01f,-0.53f,0.69f,1.7f, 4.13f,-0f,0.01f,-0.53f,0.68f,1.66f, 4.13f,-0f,0.01f,-0.53f,0.41f,1.63f)));
        dp(gl,0.62f,0.63f,0.81f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.18f,1.46f, 4.13f,0f,0f,-0.53f,0.4f,1.66f, 4.13f,0f,0f,-0.53f,0.41f,1.63f, 4.13f,0f,0f,-0.53f,0.21f,1.43f)));
        dp(gl,0.63f,0.61f,0.81f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0.01f,-1.09f,0.69f,1.7f, -4.13f,0f,-0.01f,-1.09f,0.4f,1.66f, -4.13f,0f,-0.01f,-1.09f,0.41f,1.63f, -4.13f,0f,-0.01f,-1.09f,0.68f,1.66f)));
        dp(gl,0.63f,0.63f,0.86f, new ArrayList<>(Arrays.asList(-4.13f,-0.01f,0f,-1.09f,0.9f,1.2f, -4.13f,-0.01f,0f,-1.09f,0.89f,1.51f, -4.13f,-0.01f,0f,-1.09f,0.86f,1.49f, -4.13f,-0.01f,0f,-1.09f,0.86f,1.2f)));
        dp(gl,0.61f,0.63f,0.82f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.86f,0.32f, -4.13f,0f,0f,-1.09f,0.87f,0.61f, -4.13f,0f,0f,-1.09f,0.84f,0.61f, -4.13f,0f,0f,-1.09f,0.82f,0.33f)));
        dp(gl,0.63f,0.64f,0.86f, new ArrayList<>(Arrays.asList(4.13f,0.01f,0.01f,-0.53f,0.69f,1.7f, 4.13f,0.01f,0.01f,-0.53f,0.89f,1.52f, 4.13f,0.01f,0.01f,-0.53f,0.85f,1.51f, 4.13f,0.01f,0.01f,-0.53f,0.68f,1.66f)));
        dp(gl,0.62f,0.61f,0.88f, new ArrayList<>(Arrays.asList(4.13f,0.01f,-0f,-0.53f,0.89f,1.52f, 4.13f,0.01f,-0f,-0.53f,0.9f,1.23f, 4.13f,0.01f,-0f,-0.53f,0.86f,1.23f, 4.13f,0.01f,-0f,-0.53f,0.85f,1.51f)));
        dp(gl,0.62f,0.64f,0.81f, new ArrayList<>(Arrays.asList(4.13f,-0f,0f,-0.53f,0.9f,1.23f, 4.13f,-0f,0f,-0.53f,0.89f,0.9f, 4.13f,-0f,0f,-0.53f,0.85f,0.9f, 4.13f,-0f,0f,-0.53f,0.86f,1.23f)));
        dp(gl,0.65f,0.62f,0.87f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0f,-1.09f,0.89f,0.9f, -4.13f,0f,-0f,-1.09f,0.9f,1.2f, -4.13f,0f,-0f,-1.09f,0.86f,1.2f, -4.13f,0f,-0f,-1.09f,0.85f,0.9f)));
        dp(gl,0.64f,0.63f,0.85f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.84f,0.03f, 4.13f,0f,0f,-0.53f,0.83f,-0.27f, 4.13f,0f,0f,-0.53f,0.79f,-0.27f, 4.13f,0f,0f,-0.53f,0.81f,0.03f)));
        dp(gl,0.64f,0.61f,0.86f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1.09f,0.84f,0.03f, -4.13f,0f,0f,-1.09f,0.86f,0.32f, -4.13f,0f,0f,-1.09f,0.82f,0.33f, -4.13f,0f,0f,-1.09f,0.81f,0.03f)));
        dp(gl,0.61f,0.62f,0.89f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.53f,0.86f,0.32f, 4.13f,0f,0f,-0.53f,0.84f,0.03f, 4.13f,0f,0f,-0.53f,0.81f,0.03f, 4.13f,0f,0f,-0.53f,0.82f,0.33f)));

        dp(gl,0.8f,0.6f,1f, new ArrayList<>(Arrays.asList(-0.02f,0f,1.18f,-0.19f,2.11f,0.55f, -0.02f,0f,1.18f,-0.19f,2.16f,0.55f, -0.02f,0f,1.18f,-0.34f,2.16f,0.55f, -0.02f,0f,1.18f,-0.34f,2.11f,0.55f)));

        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    void drawRightWheels(GL2 gl){
        toggleEdgeClipping(false); // приходится выключать отсечение граней, т.к., видимо, не везде правильный порядок обхода

        new Wheel(new Point3D(-0.7, 0.6, 1.4), 0.26f, 0.3f, gl).draw();

        for (double z = 1.1; z >= -0.9; z-=0.4)
            new Wheel(new Point3D(-0.7, 0.23, z), 0.17f, 0.3f, gl).draw();

        new Wheel(new Point3D(-0.7, 0.5, -1.25), 0.23f, 0.3f, gl).draw();
    }

    void drawRightAmortisation(GL2 gl){
        toggleEdgeClipping(false);
        dp(gl,0.21f,0.45f,0.24f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.67f,0.53f,-0.55f, 4.13f,0f,0f,-0.67f,0.71f,-0.55f, 4.13f,0f,0f,-0.67f,0.71f,-0.88f, 4.13f,0f,0f,-0.67f,0.53f,-0.88f)));
        dp(gl,0.23f,0.44f,0.21f, new ArrayList<>(Arrays.asList(0f,-0f,-4.13f,-0.67f,0.53f,-0.88f, 0f,-0f,-4.13f,-0.67f,0.71f,-0.88f, 0f,-0f,-4.13f,-1f,0.71f,-0.88f, 0f,-0f,-4.13f,-1f,0.53f,-0.88f)));
        dp(gl,0.21f,0.43f,0.24f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1f,0.53f,-0.88f, -4.13f,0f,0f,-1f,0.71f,-0.88f, -4.13f,0f,0f,-1f,0.71f,-0.55f, -4.13f,0f,0f,-1f,0.53f,-0.55f)));
        dp(gl,0.24f,0.43f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,-1f,0.53f,-0.55f, 0f,0f,4.13f,-1f,0.71f,-0.55f, 0f,0f,4.13f,-0.67f,0.71f,-0.55f, 0f,0f,4.13f,-0.67f,0.53f,-0.55f)));
        dp(gl,0.24f,0.44f,0.25f, new ArrayList<>(Arrays.asList(-3.02f,-2.81f,0f,-1f,0.53f,-0.88f, -3.02f,-2.81f,0f,-1f,0.53f,-0.55f, -3.02f,-2.81f,0f,-0.87f,0.39f,-0.55f, -3.02f,-2.81f,0f,-0.87f,0.39f,-0.88f)));
        dp(gl,0.24f,0.42f,0.24f, new ArrayList<>(Arrays.asList(0f,4.13f,-0f,-1f,0.71f,-0.88f, 0f,4.13f,-0f,-0.67f,0.71f,-0.88f, 0f,4.13f,-0f,-0.67f,0.71f,-0.55f, 0f,4.13f,-0f,-1f,0.71f,-0.55f)));
        dp(gl,0.24f,0.41f,0.21f, new ArrayList<>(Arrays.asList(-0f,-4.13f,0f,-0.67f,0.39f,-0.88f, -0f,-4.13f,0f,-0.87f,0.39f,-0.88f, -0f,-4.13f,0f,-0.87f,0.39f,-0.55f, -0f,-4.13f,0f,-0.67f,0.39f,-0.55f)));
        dp(gl,0.22f,0.44f,0.22f, new ArrayList<>(Arrays.asList(0f,-0f,-4.13f,-0.67f,0.53f,-0.88f, 0f,-0f,-4.13f,-1f,0.53f,-0.88f, 0f,-0f,-4.13f,-0.87f,0.39f,-0.88f, 0f,-0f,-4.13f,-0.67f,0.39f,-0.88f)));
        dp(gl,0.24f,0.44f,0.23f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.67f,0.53f,-0.55f, 4.13f,0f,0f,-0.67f,0.53f,-0.88f, 4.13f,0f,0f,-0.67f,0.39f,-0.88f, 4.13f,0f,0f,-0.67f,0.39f,-0.55f)));
        dp(gl,0.25f,0.45f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,-1f,0.53f,-0.55f, 0f,0f,4.13f,-0.67f,0.53f,-0.55f, 0f,0f,4.13f,-0.67f,0.39f,-0.55f, 0f,0f,4.13f,-0.87f,0.39f,-0.55f)));
        dp(gl,0.21f,0.41f,0.24f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,-0.67f,0.53f,0.26f, 4.13f,0f,-0f,-0.67f,0.71f,0.26f, 4.13f,0f,-0f,-0.67f,0.71f,-0.07f, 4.13f,0f,-0f,-0.67f,0.53f,-0.07f)));
        dp(gl,0.2f,0.44f,0.23f, new ArrayList<>(Arrays.asList(-0f,-0f,-4.13f,-0.67f,0.53f,-0.07f, -0f,-0f,-4.13f,-0.67f,0.71f,-0.07f, -0f,-0f,-4.13f,-1f,0.71f,-0.07f, -0f,-0f,-4.13f,-1f,0.53f,-0.07f)));
        dp(gl,0.22f,0.44f,0.2f, new ArrayList<>(Arrays.asList(-4.13f,-0f,0f,-1f,0.53f,-0.07f, -4.13f,-0f,0f,-1f,0.71f,-0.07f, -4.13f,-0f,0f,-1f,0.71f,0.26f, -4.13f,-0f,0f,-1f,0.53f,0.26f)));
        dp(gl,0.23f,0.43f,0.22f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,-1f,0.53f,0.26f, 0f,0f,4.13f,-1f,0.71f,0.26f, 0f,0f,4.13f,-0.67f,0.71f,0.26f, 0f,0f,4.13f,-0.67f,0.53f,0.26f)));
        dp(gl,0.25f,0.44f,0.22f, new ArrayList<>(Arrays.asList(-3.02f,-2.81f,0f,-1f,0.53f,-0.07f, -3.02f,-2.81f,0f,-1f,0.53f,0.26f, -3.02f,-2.81f,0f,-0.87f,0.39f,0.26f, -3.02f,-2.81f,0f,-0.87f,0.39f,-0.07f)));
        dp(gl,0.23f,0.44f,0.22f, new ArrayList<>(Arrays.asList(0f,4.13f,-0f,-1f,0.71f,-0.07f, 0f,4.13f,-0f,-0.67f,0.71f,-0.07f, 0f,4.13f,-0f,-0.67f,0.71f,0.26f, 0f,4.13f,-0f,-1f,0.71f,0.26f)));
        dp(gl,0.2f,0.42f,0.21f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,-0.67f,0.39f,-0.07f, 0f,-4.13f,0f,-0.87f,0.39f,-0.07f, 0f,-4.13f,0f,-0.87f,0.39f,0.26f, 0f,-4.13f,0f,-0.67f,0.39f,0.26f)));
        dp(gl,0.21f,0.42f,0.24f, new ArrayList<>(Arrays.asList(-0f,-0f,-4.13f,-0.67f,0.53f,-0.07f, -0f,-0f,-4.13f,-1f,0.53f,-0.07f, -0f,-0f,-4.13f,-0.87f,0.39f,-0.07f, -0f,-0f,-4.13f,-0.67f,0.39f,-0.07f)));
        dp(gl,0.22f,0.43f,0.24f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,-0.67f,0.53f,0.26f, 4.13f,0f,-0f,-0.67f,0.53f,-0.07f, 4.13f,0f,-0f,-0.67f,0.39f,-0.07f, 4.13f,0f,-0f,-0.67f,0.39f,0.26f)));
        dp(gl,0.24f,0.45f,0.22f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,-1f,0.53f,0.26f, 0f,0f,4.13f,-0.67f,0.53f,0.26f, 0f,0f,4.13f,-0.67f,0.39f,0.26f, 0f,0f,4.13f,-0.87f,0.39f,0.26f)));
        dp(gl,0.2f,0.44f,0.22f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.67f,0.53f,1.05f, 4.13f,0f,0f,-0.67f,0.71f,1.05f, 4.13f,0f,0f,-0.67f,0.71f,0.72f, 4.13f,0f,0f,-0.67f,0.53f,0.72f)));
        dp(gl,0.2f,0.4f,0.22f, new ArrayList<>(Arrays.asList(0f,-0f,-4.13f,-0.67f,0.53f,0.72f, 0f,-0f,-4.13f,-0.67f,0.71f,0.72f, 0f,-0f,-4.13f,-1f,0.71f,0.72f, 0f,-0f,-4.13f,-1f,0.53f,0.72f)));
        dp(gl,0.24f,0.45f,0.22f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,-1f,0.53f,0.72f, -4.13f,0f,0f,-1f,0.71f,0.72f, -4.13f,0f,0f,-1f,0.71f,1.05f, -4.13f,0f,0f,-1f,0.53f,1.05f)));
        dp(gl,0.24f,0.42f,0.23f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,-1f,0.53f,1.05f, 0f,0f,4.13f,-1f,0.71f,1.05f, 0f,0f,4.13f,-0.67f,0.71f,1.05f, 0f,0f,4.13f,-0.67f,0.53f,1.05f)));
        dp(gl,0.21f,0.43f,0.21f, new ArrayList<>(Arrays.asList(-3.02f,-2.81f,0f,-1f,0.53f,0.72f, -3.02f,-2.81f,0f,-1f,0.53f,1.05f, -3.02f,-2.81f,0f,-0.87f,0.39f,1.05f, -3.02f,-2.81f,0f,-0.87f,0.39f,0.72f)));
        dp(gl,0.23f,0.45f,0.22f, new ArrayList<>(Arrays.asList(0f,4.13f,-0f,-1f,0.71f,0.72f, 0f,4.13f,-0f,-0.67f,0.71f,0.72f, 0f,4.13f,-0f,-0.67f,0.71f,1.05f, 0f,4.13f,-0f,-1f,0.71f,1.05f)));
        dp(gl,0.24f,0.44f,0.21f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,-0.67f,0.39f,0.72f, 0f,-4.13f,0f,-0.87f,0.39f,0.72f, 0f,-4.13f,0f,-0.87f,0.39f,1.05f, 0f,-4.13f,0f,-0.67f,0.39f,1.05f)));
        dp(gl,0.23f,0.41f,0.21f, new ArrayList<>(Arrays.asList(0f,-0f,-4.13f,-0.67f,0.53f,0.72f, 0f,-0f,-4.13f,-1f,0.53f,0.72f, 0f,-0f,-4.13f,-0.87f,0.39f,0.72f, 0f,-0f,-4.13f,-0.67f,0.39f,0.72f)));
        dp(gl,0.23f,0.43f,0.23f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,-0.67f,0.53f,1.05f, 4.13f,0f,0f,-0.67f,0.53f,0.72f, 4.13f,0f,0f,-0.67f,0.39f,0.72f, 4.13f,0f,0f,-0.67f,0.39f,1.05f)));
        dp(gl,0.21f,0.43f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,-1f,0.53f,1.05f, 0f,0f,4.13f,-0.67f,0.53f,1.05f, 0f,0f,4.13f,-0.67f,0.39f,1.05f, 0f,0f,4.13f,-0.87f,0.39f,1.05f)));
        dp(gl,0.21f,0.4f,0.23f, new ArrayList<>(Arrays.asList(-4.12f,0.12f,-0f,-0.69f,0.53f,-0.57f, -4.12f,0.12f,-0f,-0.69f,0.53f,-0.85f, -4.12f,0.12f,-0f,-0.69f,0.69f,-0.86f, -4.12f,0.12f,-0f,-0.69f,0.69f,-0.57f)));
        dp(gl,0.23f,0.41f,0.23f, new ArrayList<>(Arrays.asList(-0.02f,0.09f,4.12f,-0.69f,0.53f,-0.85f, -0.02f,0.09f,4.12f,-0.97f,0.54f,-0.85f, -0.02f,0.09f,4.12f,-0.98f,0.69f,-0.86f, -0.02f,0.09f,4.12f,-0.69f,0.69f,-0.86f)));
        dp(gl,0.23f,0.42f,0.23f, new ArrayList<>(Arrays.asList(4.12f,0.14f,-0f,-0.97f,0.54f,-0.85f, 4.12f,0.14f,-0f,-0.97f,0.54f,-0.57f, 4.12f,0.14f,-0f,-0.98f,0.69f,-0.57f, 4.12f,0.14f,-0f,-0.98f,0.69f,-0.86f)));
        dp(gl,0.23f,0.43f,0.22f, new ArrayList<>(Arrays.asList(-0.02f,0.09f,-4.12f,-0.97f,0.54f,-0.57f, -0.02f,0.09f,-4.12f,-0.69f,0.53f,-0.57f, -0.02f,0.09f,-4.12f,-0.69f,0.69f,-0.57f, -0.02f,0.09f,-4.12f,-0.98f,0.69f,-0.57f)));
        dp(gl,0.24f,0.45f,0.25f, new ArrayList<>(Arrays.asList(3.02f,2.81f,-0f,-0.97f,0.54f,-0.85f, 3.02f,2.81f,-0f,-0.86f,0.41f,-0.85f, 3.02f,2.81f,-0f,-0.86f,0.41f,-0.57f, 3.02f,2.81f,-0f,-0.97f,0.54f,-0.57f)));
        dp(gl,0.25f,0.45f,0.2f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,-0.98f,0.69f,-0.86f, 0f,-4.13f,0f,-0.98f,0.69f,-0.57f, 0f,-4.13f,0f,-0.69f,0.69f,-0.57f, 0f,-4.13f,0f,-0.69f,0.69f,-0.86f)));
        dp(gl,0.23f,0.41f,0.22f, new ArrayList<>(Arrays.asList(0.12f,4.12f,-0f,-0.69f,0.41f,-0.86f, 0.12f,4.12f,-0f,-0.69f,0.41f,-0.57f, 0.12f,4.12f,-0f,-0.86f,0.41f,-0.57f, 0.12f,4.12f,-0f,-0.86f,0.41f,-0.85f)));
        dp(gl,0.24f,0.4f,0.21f, new ArrayList<>(Arrays.asList(-0.01f,-0.09f,4.12f,-0.69f,0.53f,-0.85f, -0.01f,-0.09f,4.12f,-0.69f,0.41f,-0.86f, -0.01f,-0.09f,4.12f,-0.86f,0.41f,-0.85f, -0.01f,-0.09f,4.12f,-0.97f,0.54f,-0.85f)));
        dp(gl,0.24f,0.44f,0.21f, new ArrayList<>(Arrays.asList(-4.12f,-0.16f,0f,-0.69f,0.53f,-0.57f, -4.12f,-0.16f,0f,-0.69f,0.41f,-0.57f, -4.12f,-0.16f,0f,-0.69f,0.41f,-0.86f, -4.12f,-0.16f,0f,-0.69f,0.53f,-0.85f)));
        dp(gl,0.2f,0.43f,0.23f, new ArrayList<>(Arrays.asList(-0.01f,-0.09f,-4.12f,-0.97f,0.54f,-0.57f, -0.01f,-0.09f,-4.12f,-0.86f,0.41f,-0.57f, -0.01f,-0.09f,-4.12f,-0.69f,0.41f,-0.57f, -0.01f,-0.09f,-4.12f,-0.69f,0.53f,-0.57f)));
        dp(gl,0.24f,0.41f,0.21f, new ArrayList<>(Arrays.asList(-4.12f,0.12f,-0f,-0.69f,0.53f,0.23f, -4.12f,0.12f,-0f,-0.69f,0.53f,-0.05f, -4.12f,0.12f,-0f,-0.69f,0.69f,-0.05f, -4.12f,0.12f,-0f,-0.69f,0.69f,0.24f)));
        dp(gl,0.25f,0.44f,0.23f, new ArrayList<>(Arrays.asList(-0.02f,0.09f,4.12f,-0.69f,0.53f,-0.05f, -0.02f,0.09f,4.12f,-0.97f,0.54f,-0.05f, -0.02f,0.09f,4.12f,-0.98f,0.69f,-0.05f, -0.02f,0.09f,4.12f,-0.69f,0.69f,-0.05f)));
        dp(gl,0.21f,0.43f,0.25f, new ArrayList<>(Arrays.asList(4.12f,0.14f,-0f,-0.97f,0.54f,-0.05f, 4.12f,0.14f,-0f,-0.97f,0.54f,0.23f, 4.12f,0.14f,-0f,-0.98f,0.69f,0.24f, 4.12f,0.14f,-0f,-0.98f,0.69f,-0.05f)));
        dp(gl,0.21f,0.43f,0.22f, new ArrayList<>(Arrays.asList(-0.02f,0.09f,-4.12f,-0.97f,0.54f,0.23f, -0.02f,0.09f,-4.12f,-0.69f,0.53f,0.23f, -0.02f,0.09f,-4.12f,-0.69f,0.69f,0.24f, -0.02f,0.09f,-4.12f,-0.98f,0.69f,0.24f)));
        dp(gl,0.24f,0.43f,0.22f, new ArrayList<>(Arrays.asList(3.02f,2.81f,-0f,-0.97f,0.54f,-0.05f, 3.02f,2.81f,-0f,-0.86f,0.41f,-0.05f, 3.02f,2.81f,-0f,-0.86f,0.41f,0.23f, 3.02f,2.81f,-0f,-0.97f,0.54f,0.23f)));
        dp(gl,0.24f,0.44f,0.24f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,-0.98f,0.69f,-0.05f, 0f,-4.13f,0f,-0.98f,0.69f,0.24f, 0f,-4.13f,0f,-0.69f,0.69f,0.24f, 0f,-4.13f,0f,-0.69f,0.69f,-0.05f)));
        dp(gl,0.24f,0.45f,0.23f, new ArrayList<>(Arrays.asList(0.12f,4.12f,-0f,-0.69f,0.41f,-0.05f, 0.12f,4.12f,-0f,-0.69f,0.41f,0.24f, 0.12f,4.12f,-0f,-0.86f,0.41f,0.23f, 0.12f,4.12f,-0f,-0.86f,0.41f,-0.05f)));
        dp(gl,0.23f,0.42f,0.21f, new ArrayList<>(Arrays.asList(-0.01f,-0.09f,4.12f,-0.69f,0.53f,-0.05f, -0.01f,-0.09f,4.12f,-0.69f,0.41f,-0.05f, -0.01f,-0.09f,4.12f,-0.86f,0.41f,-0.05f, -0.01f,-0.09f,4.12f,-0.97f,0.54f,-0.05f)));
        dp(gl,0.24f,0.44f,0.24f, new ArrayList<>(Arrays.asList(-4.12f,-0.16f,0f,-0.69f,0.53f,0.23f, -4.12f,-0.16f,0f,-0.69f,0.41f,0.24f, -4.12f,-0.16f,0f,-0.69f,0.41f,-0.05f, -4.12f,-0.16f,0f,-0.69f,0.53f,-0.05f)));
        dp(gl,0.21f,0.43f,0.24f, new ArrayList<>(Arrays.asList(-0.01f,-0.09f,-4.12f,-0.97f,0.54f,0.23f, -0.01f,-0.09f,-4.12f,-0.86f,0.41f,0.23f, -0.01f,-0.09f,-4.12f,-0.69f,0.41f,0.24f, -0.01f,-0.09f,-4.12f,-0.69f,0.53f,0.23f)));
        dp(gl,0.25f,0.44f,0.24f, new ArrayList<>(Arrays.asList(-4.12f,0.12f,-0f,-0.69f,0.53f,1.03f, -4.12f,0.12f,-0f,-0.69f,0.53f,0.75f, -4.12f,0.12f,-0f,-0.69f,0.69f,0.74f, -4.12f,0.12f,-0f,-0.69f,0.69f,1.03f)));
        dp(gl,0.22f,0.42f,0.25f, new ArrayList<>(Arrays.asList(-0.02f,0.09f,4.12f,-0.69f,0.53f,0.75f, -0.02f,0.09f,4.12f,-0.97f,0.54f,0.74f, -0.02f,0.09f,4.12f,-0.98f,0.69f,0.74f, -0.02f,0.09f,4.12f,-0.69f,0.69f,0.74f)));
        dp(gl,0.24f,0.44f,0.25f, new ArrayList<>(Arrays.asList(4.12f,0.14f,-0f,-0.97f,0.54f,0.74f, 4.12f,0.14f,-0f,-0.97f,0.54f,1.03f, 4.12f,0.14f,-0f,-0.98f,0.69f,1.03f, 4.12f,0.14f,-0f,-0.98f,0.69f,0.74f)));
        dp(gl,0.25f,0.43f,0.22f, new ArrayList<>(Arrays.asList(-0.02f,0.09f,-4.12f,-0.97f,0.54f,1.03f, -0.02f,0.09f,-4.12f,-0.69f,0.53f,1.03f, -0.02f,0.09f,-4.12f,-0.69f,0.69f,1.03f, -0.02f,0.09f,-4.12f,-0.98f,0.69f,1.03f)));
        dp(gl,0.25f,0.41f,0.21f, new ArrayList<>(Arrays.asList(3.02f,2.81f,-0f,-0.97f,0.54f,0.74f, 3.02f,2.81f,-0f,-0.86f,0.41f,0.74f, 3.02f,2.81f,-0f,-0.86f,0.41f,1.03f, 3.02f,2.81f,-0f,-0.97f,0.54f,1.03f)));
        dp(gl,0.21f,0.41f,0.23f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,-0.98f,0.69f,0.74f, 0f,-4.13f,0f,-0.98f,0.69f,1.03f, 0f,-4.13f,0f,-0.69f,0.69f,1.03f, 0f,-4.13f,0f,-0.69f,0.69f,0.74f)));
        dp(gl,0.25f,0.41f,0.22f, new ArrayList<>(Arrays.asList(0.12f,4.12f,-0f,-0.69f,0.41f,0.74f, 0.12f,4.12f,-0f,-0.69f,0.41f,1.03f, 0.12f,4.12f,-0f,-0.86f,0.41f,1.03f, 0.12f,4.12f,-0f,-0.86f,0.41f,0.74f)));
        dp(gl,0.23f,0.44f,0.21f, new ArrayList<>(Arrays.asList(-0.01f,-0.09f,4.12f,-0.69f,0.53f,0.75f, -0.01f,-0.09f,4.12f,-0.69f,0.41f,0.74f, -0.01f,-0.09f,4.12f,-0.86f,0.41f,0.74f, -0.01f,-0.09f,4.12f,-0.97f,0.54f,0.74f)));
        dp(gl,0.24f,0.44f,0.24f, new ArrayList<>(Arrays.asList(-4.12f,-0.16f,0f,-0.69f,0.53f,1.03f, -4.12f,-0.16f,0f,-0.69f,0.41f,1.03f, -4.12f,-0.16f,0f,-0.69f,0.41f,0.74f, -4.12f,-0.16f,0f,-0.69f,0.53f,0.75f)));
        dp(gl,0.22f,0.44f,0.21f, new ArrayList<>(Arrays.asList(-0.01f,-0.09f,-4.12f,-0.97f,0.54f,1.03f, -0.01f,-0.09f,-4.12f,-0.86f,0.41f,1.03f, -0.01f,-0.09f,-4.12f,-0.69f,0.41f,1.03f, -0.01f,-0.09f,-4.12f,-0.69f,0.53f,1.03f)));
    }

    void drawMiniGun(GL2 gl){
        dp(gl,0.44f,0.44f,0.71f, new ArrayList<>(Arrays.asList(0f,-0.22f,-0.03f,-0.4f,1.19f,1.55f, 0f,-0.22f,-0.03f,-0.41f,1.19f,1.53f, 0f,-0.22f,-0.03f,-0.4f,1.19f,1.53f)));
        dp(gl,0.42f,0.43f,0.75f, new ArrayList<>(Arrays.asList(0.02f,0.22f,-0.02f,-0.4f,1.3f,1.55f, 0.02f,0.22f,-0.02f,-0.39f,1.3f,1.54f, 0.02f,0.22f,-0.02f,-0.4f,1.3f,1.53f)));
        dp(gl,0.45f,0.44f,0.86f, new ArrayList<>(Arrays.asList(0f,0.11f,-0.2f,-0.42f,1.26f,1.5f, 0f,0.11f,-0.2f,-0.42f,1.28f,1.51f, 0f,0.11f,-0.2f,-0.39f,1.28f,1.51f, 0f,0.11f,-0.2f,-0.38f,1.26f,1.5f)));
        dp(gl,0.43f,0.41f,0.71f, new ArrayList<>(Arrays.asList(0f,0.22f,-0.03f,-0.41f,1.3f,1.53f, 0f,0.22f,-0.03f,-0.4f,1.3f,1.55f, 0f,0.22f,-0.03f,-0.4f,1.3f,1.53f)));
        dp(gl,0.44f,0.42f,0.84f, new ArrayList<>(Arrays.asList(0f,0f,-0.23f,-0.42f,1.23f,1.5f, 0f,0f,-0.23f,-0.42f,1.26f,1.5f, 0f,0f,-0.23f,-0.38f,1.26f,1.5f, 0f,0f,-0.23f,-0.38f,1.23f,1.5f)));
        dp(gl,0.43f,0.44f,0.75f, new ArrayList<>(Arrays.asList(0.02f,-0.22f,-0.02f,-0.4f,1.19f,1.53f, 0.02f,-0.22f,-0.02f,-0.39f,1.19f,1.54f, 0.02f,-0.22f,-0.02f,-0.4f,1.19f,1.55f)));
        dp(gl,0.43f,0.42f,0.82f, new ArrayList<>(Arrays.asList(-0f,-0.19f,-0.12f,-0.41f,1.19f,1.53f, -0f,-0.19f,-0.12f,-0.42f,1.2f,1.51f, -0f,-0.19f,-0.12f,-0.39f,1.2f,1.51f, -0f,-0.19f,-0.12f,-0.4f,1.19f,1.53f)));
        dp(gl,0.43f,0.41f,0.68f, new ArrayList<>(Arrays.asList(0.14f,-0.11f,-0.14f,-0.35f,1.23f,1.53f, 0.14f,-0.11f,-0.14f,-0.36f,1.2f,1.53f, 0.14f,-0.11f,-0.14f,-0.39f,1.2f,1.51f, 0.14f,-0.11f,-0.14f,-0.38f,1.23f,1.5f)));
        dp(gl,0.44f,0.44f,0.71f, new ArrayList<>(Arrays.asList(0.08f,0.19f,0.08f,-0.4f,1.3f,1.56f, 0.08f,0.19f,0.08f,-0.39f,1.28f,1.58f, 0.08f,0.19f,0.08f,-0.36f,1.28f,1.56f, 0.08f,0.19f,0.08f,-0.39f,1.3f,1.55f)));
        dp(gl,0.4f,0.43f,0.94f, new ArrayList<>(Arrays.asList(0.08f,0.19f,-0.08f,-0.39f,1.3f,1.54f, 0.08f,0.19f,-0.08f,-0.36f,1.28f,1.53f, 0.08f,0.19f,-0.08f,-0.39f,1.28f,1.51f, 0.08f,0.19f,-0.08f,-0.4f,1.3f,1.53f)));
        dp(gl,0.44f,0.43f,0.79f, new ArrayList<>(Arrays.asList(0.16f,0f,-0.16f,-0.35f,1.26f,1.53f, 0.16f,0f,-0.16f,-0.35f,1.23f,1.53f, 0.16f,0f,-0.16f,-0.38f,1.23f,1.5f, 0.16f,0f,-0.16f,-0.38f,1.26f,1.5f)));
        dp(gl,0.43f,0.4f,0.99f, new ArrayList<>(Arrays.asList(0.03f,0.22f,0f,-0.4f,1.3f,1.55f, 0.03f,0.22f,0f,-0.39f,1.3f,1.55f, 0.03f,0.22f,0f,-0.39f,1.3f,1.54f)));
        dp(gl,0.41f,0.42f,0.93f, new ArrayList<>(Arrays.asList(0.23f,0f,0f,-0.35f,1.26f,1.57f, 0.23f,0f,0f,-0.35f,1.23f,1.57f, 0.23f,0f,0f,-0.35f,1.23f,1.53f, 0.23f,0f,0f,-0.35f,1.26f,1.53f)));
        dp(gl,0.43f,0.44f,0.66f, new ArrayList<>(Arrays.asList(0.08f,-0.19f,-0.08f,-0.36f,1.2f,1.53f, 0.08f,-0.19f,-0.08f,-0.39f,1.19f,1.54f, 0.08f,-0.19f,-0.08f,-0.4f,1.19f,1.53f, 0.08f,-0.19f,-0.08f,-0.39f,1.2f,1.51f)));
        dp(gl,0.43f,0.42f,0.88f, new ArrayList<>(Arrays.asList(0.2f,0.11f,0f,-0.36f,1.28f,1.56f, 0.2f,0.11f,0f,-0.35f,1.26f,1.57f, 0.2f,0.11f,0f,-0.35f,1.26f,1.53f, 0.2f,0.11f,0f,-0.36f,1.28f,1.53f)));
        dp(gl,0.42f,0.44f,0.64f, new ArrayList<>(Arrays.asList(0.12f,0.19f,0f,-0.39f,1.3f,1.55f, 0.12f,0.19f,0f,-0.36f,1.28f,1.56f, 0.12f,0.19f,0f,-0.36f,1.28f,1.53f, 0.12f,0.19f,0f,-0.39f,1.3f,1.54f)));
        dp(gl,0.42f,0.41f,0.83f, new ArrayList<>(Arrays.asList(0.02f,0.22f,0.02f,-0.4f,1.3f,1.55f, 0.02f,0.22f,0.02f,-0.4f,1.3f,1.56f, 0.02f,0.22f,0.02f,-0.39f,1.3f,1.55f)));
        dp(gl,0.42f,0.43f,0.96f, new ArrayList<>(Arrays.asList(0.12f,-0.19f,0f,-0.36f,1.2f,1.56f, 0.12f,-0.19f,0f,-0.39f,1.19f,1.55f, 0.12f,-0.19f,0f,-0.39f,1.19f,1.54f, 0.12f,-0.19f,0f,-0.36f,1.2f,1.53f)));
        dp(gl,0.42f,0.4f,0.89f, new ArrayList<>(Arrays.asList(0.2f,-0.11f,0f,-0.35f,1.23f,1.57f, 0.2f,-0.11f,0f,-0.36f,1.2f,1.56f, 0.2f,-0.11f,0f,-0.36f,1.2f,1.53f, 0.2f,-0.11f,0f,-0.35f,1.23f,1.53f)));
        dp(gl,0.42f,0.43f,0.75f, new ArrayList<>(Arrays.asList(0.03f,-0.22f,0f,-0.39f,1.19f,1.54f, 0.03f,-0.22f,0f,-0.39f,1.19f,1.55f, 0.03f,-0.22f,0f,-0.4f,1.19f,1.55f)));
        dp(gl,0.44f,0.41f,0.91f, new ArrayList<>(Arrays.asList(0.16f,0f,0.16f,-0.38f,1.26f,1.6f, 0.16f,0f,0.16f,-0.38f,1.23f,1.6f, 0.16f,0f,0.16f,-0.35f,1.23f,1.57f, 0.16f,0f,0.16f,-0.35f,1.26f,1.57f)));
        dp(gl,0.45f,0.42f,0.64f, new ArrayList<>(Arrays.asList(0.08f,-0.19f,0.08f,-0.39f,1.2f,1.58f, 0.08f,-0.19f,0.08f,-0.4f,1.19f,1.56f, 0.08f,-0.19f,0.08f,-0.39f,1.19f,1.55f, 0.08f,-0.19f,0.08f,-0.36f,1.2f,1.56f)));
        dp(gl,0.44f,0.43f,0.84f, new ArrayList<>(Arrays.asList(0.02f,-0.22f,0.02f,-0.39f,1.19f,1.55f, 0.02f,-0.22f,0.02f,-0.4f,1.19f,1.56f, 0.02f,-0.22f,0.02f,-0.4f,1.19f,1.55f)));
        dp(gl,0.41f,0.42f,0.99f, new ArrayList<>(Arrays.asList(-0f,-0.11f,0.2f,-0.42f,1.23f,1.6f, -0f,-0.11f,0.2f,-0.42f,1.2f,1.58f, -0f,-0.11f,0.2f,-0.39f,1.2f,1.58f, -0f,-0.11f,0.2f,-0.38f,1.23f,1.6f)));
        dp(gl,0.42f,0.42f,0.87f, new ArrayList<>(Arrays.asList(0.14f,-0.11f,0.14f,-0.38f,1.23f,1.6f, 0.14f,-0.11f,0.14f,-0.39f,1.2f,1.58f, 0.14f,-0.11f,0.14f,-0.36f,1.2f,1.56f, 0.14f,-0.11f,0.14f,-0.35f,1.23f,1.57f)));
        dp(gl,0.44f,0.43f,0.68f, new ArrayList<>(Arrays.asList(0f,0.19f,0.12f,-0.41f,1.3f,1.56f, 0f,0.19f,0.12f,-0.42f,1.28f,1.58f, 0f,0.19f,0.12f,-0.39f,1.28f,1.58f, 0f,0.19f,0.12f,-0.4f,1.3f,1.56f)));
        dp(gl,0.45f,0.42f,0.93f, new ArrayList<>(Arrays.asList(0f,0.22f,0.03f,-0.4f,1.3f,1.55f, 0f,0.22f,0.03f,-0.41f,1.3f,1.56f, 0f,0.22f,0.03f,-0.4f,1.3f,1.56f)));
        dp(gl,0.41f,0.44f,0.88f, new ArrayList<>(Arrays.asList(0.14f,0.11f,0.14f,-0.39f,1.28f,1.58f, 0.14f,0.11f,0.14f,-0.38f,1.26f,1.6f, 0.14f,0.11f,0.14f,-0.35f,1.26f,1.57f, 0.14f,0.11f,0.14f,-0.36f,1.28f,1.56f)));
        dp(gl,0.44f,0.4f,0.65f, new ArrayList<>(Arrays.asList(-0f,-0.22f,0.03f,-0.4f,1.19f,1.56f, -0f,-0.22f,0.03f,-0.41f,1.19f,1.56f, -0f,-0.22f,0.03f,-0.4f,1.19f,1.55f)));
        dp(gl,0.44f,0.42f,0.87f, new ArrayList<>(Arrays.asList(-0f,0.11f,0.2f,-0.42f,1.28f,1.58f, -0f,0.11f,0.2f,-0.42f,1.26f,1.6f, -0f,0.11f,0.2f,-0.38f,1.26f,1.6f, -0f,0.11f,0.2f,-0.39f,1.28f,1.58f)));
        dp(gl,0.41f,0.44f,0.62f, new ArrayList<>(Arrays.asList(-0f,-0f,0.23f,-0.42f,1.26f,1.6f, -0f,-0f,0.23f,-0.42f,1.23f,1.6f, -0f,-0f,0.23f,-0.38f,1.23f,1.6f, -0f,-0f,0.23f,-0.38f,1.26f,1.6f)));
        dp(gl,0.43f,0.41f,0.75f, new ArrayList<>(Arrays.asList(-0.16f,-0f,0.16f,-0.45f,1.26f,1.57f, -0.16f,-0f,0.16f,-0.45f,1.23f,1.57f, -0.16f,-0f,0.16f,-0.42f,1.23f,1.6f, -0.16f,-0f,0.16f,-0.42f,1.26f,1.6f)));
        dp(gl,0.43f,0.43f,0.98f, new ArrayList<>(Arrays.asList(-0f,-0.19f,0.12f,-0.42f,1.2f,1.58f, -0f,-0.19f,0.12f,-0.41f,1.19f,1.56f, -0f,-0.19f,0.12f,-0.4f,1.19f,1.56f, -0f,-0.19f,0.12f,-0.39f,1.2f,1.58f)));
        dp(gl,0.41f,0.44f,0.95f, new ArrayList<>(Arrays.asList(-0.14f,0.11f,0.14f,-0.44f,1.28f,1.56f, -0.14f,0.11f,0.14f,-0.45f,1.26f,1.57f, -0.14f,0.11f,0.14f,-0.42f,1.26f,1.6f, -0.14f,0.11f,0.14f,-0.42f,1.28f,1.58f)));
        dp(gl,0.42f,0.42f,0.69f, new ArrayList<>(Arrays.asList(-0.02f,0.22f,0.02f,-0.4f,1.3f,1.55f, -0.02f,0.22f,0.02f,-0.41f,1.3f,1.55f, -0.02f,0.22f,0.02f,-0.41f,1.3f,1.56f)));
        dp(gl,0.43f,0.4f,0.78f, new ArrayList<>(Arrays.asList(-0.03f,0.22f,-0f,-0.4f,1.3f,1.55f, -0.03f,0.22f,-0f,-0.41f,1.3f,1.54f, -0.03f,0.22f,-0f,-0.41f,1.3f,1.55f)));
        dp(gl,0.42f,0.42f,0.8f, new ArrayList<>(Arrays.asList(-0.08f,-0.19f,0.08f,-0.44f,1.2f,1.56f, -0.08f,-0.19f,0.08f,-0.41f,1.19f,1.55f, -0.08f,-0.19f,0.08f,-0.41f,1.19f,1.56f, -0.08f,-0.19f,0.08f,-0.42f,1.2f,1.58f)));
        dp(gl,0.43f,0.42f,0.79f, new ArrayList<>(Arrays.asList(-0.14f,-0.11f,0.14f,-0.45f,1.23f,1.57f, -0.14f,-0.11f,0.14f,-0.44f,1.2f,1.56f, -0.14f,-0.11f,0.14f,-0.42f,1.2f,1.58f, -0.14f,-0.11f,0.14f,-0.42f,1.23f,1.6f)));
        dp(gl,0.44f,0.4f,0.93f, new ArrayList<>(Arrays.asList(-0.02f,-0.22f,0.02f,-0.41f,1.19f,1.56f, -0.02f,-0.22f,0.02f,-0.41f,1.19f,1.55f, -0.02f,-0.22f,0.02f,-0.4f,1.19f,1.55f)));
        dp(gl,0.42f,0.4f,0.76f, new ArrayList<>(Arrays.asList(-0.2f,-0.11f,-0f,-0.45f,1.23f,1.53f, -0.2f,-0.11f,-0f,-0.44f,1.2f,1.53f, -0.2f,-0.11f,-0f,-0.44f,1.2f,1.56f, -0.2f,-0.11f,-0f,-0.45f,1.23f,1.57f)));
        dp(gl,0.43f,0.4f,0.72f, new ArrayList<>(Arrays.asList(-0.2f,0.11f,-0f,-0.44f,1.28f,1.53f, -0.2f,0.11f,-0f,-0.45f,1.26f,1.53f, -0.2f,0.11f,-0f,-0.45f,1.26f,1.57f, -0.2f,0.11f,-0f,-0.44f,1.28f,1.56f)));
        dp(gl,0.42f,0.42f,0.96f, new ArrayList<>(Arrays.asList(-0.08f,0.19f,0.08f,-0.41f,1.3f,1.55f, -0.08f,0.19f,0.08f,-0.44f,1.28f,1.56f, -0.08f,0.19f,0.08f,-0.42f,1.28f,1.58f, -0.08f,0.19f,0.08f,-0.41f,1.3f,1.56f)));
        dp(gl,0.42f,0.43f,0.93f, new ArrayList<>(Arrays.asList(-0.03f,-0.22f,0f,-0.41f,1.19f,1.55f, -0.03f,-0.22f,0f,-0.41f,1.19f,1.54f, -0.03f,-0.22f,0f,-0.4f,1.19f,1.55f)));
        dp(gl,0.44f,0.42f,0.98f, new ArrayList<>(Arrays.asList(0f,0.19f,-0.12f,-0.42f,1.28f,1.51f, 0f,0.19f,-0.12f,-0.41f,1.3f,1.53f, 0f,0.19f,-0.12f,-0.4f,1.3f,1.53f, 0f,0.19f,-0.12f,-0.39f,1.28f,1.51f)));
        dp(gl,0.42f,0.43f,0.84f, new ArrayList<>(Arrays.asList(-0.12f,0.19f,-0f,-0.41f,1.3f,1.54f, -0.12f,0.19f,-0f,-0.44f,1.28f,1.53f, -0.12f,0.19f,-0f,-0.44f,1.28f,1.56f, -0.12f,0.19f,-0f,-0.41f,1.3f,1.55f)));
        dp(gl,0.43f,0.43f,0.68f, new ArrayList<>(Arrays.asList(-0.23f,0f,-0f,-0.45f,1.26f,1.53f, -0.23f,0f,-0f,-0.45f,1.23f,1.53f, -0.23f,0f,-0f,-0.45f,1.23f,1.57f, -0.23f,0f,-0f,-0.45f,1.26f,1.57f)));
        dp(gl,0.4f,0.44f,0.98f, new ArrayList<>(Arrays.asList(-0.02f,0.22f,-0.02f,-0.4f,1.3f,1.55f, -0.02f,0.22f,-0.02f,-0.41f,1.3f,1.53f, -0.02f,0.22f,-0.02f,-0.41f,1.3f,1.54f)));
        dp(gl,0.42f,0.44f,0.73f, new ArrayList<>(Arrays.asList(-0.16f,0f,-0.16f,-0.42f,1.26f,1.5f, -0.16f,0f,-0.16f,-0.42f,1.23f,1.5f, -0.16f,0f,-0.16f,-0.45f,1.23f,1.53f, -0.16f,0f,-0.16f,-0.45f,1.26f,1.53f)));
        dp(gl,0.45f,0.41f,0.98f, new ArrayList<>(Arrays.asList(-0.12f,-0.19f,-0f,-0.44f,1.2f,1.53f, -0.12f,-0.19f,-0f,-0.41f,1.19f,1.54f, -0.12f,-0.19f,-0f,-0.41f,1.19f,1.55f, -0.12f,-0.19f,-0f,-0.44f,1.2f,1.56f)));
        dp(gl,0.41f,0.42f,0.91f, new ArrayList<>(Arrays.asList(-0.08f,0.19f,-0.08f,-0.41f,1.3f,1.53f, -0.08f,0.19f,-0.08f,-0.42f,1.28f,1.51f, -0.08f,0.19f,-0.08f,-0.44f,1.28f,1.53f, -0.08f,0.19f,-0.08f,-0.41f,1.3f,1.54f)));
        dp(gl,0.43f,0.43f,0.72f, new ArrayList<>(Arrays.asList(0.14f,0.11f,-0.14f,-0.36f,1.28f,1.53f, 0.14f,0.11f,-0.14f,-0.35f,1.26f,1.53f, 0.14f,0.11f,-0.14f,-0.38f,1.26f,1.5f, 0.14f,0.11f,-0.14f,-0.39f,1.28f,1.51f)));
        dp(gl,0.42f,0.44f,0.77f, new ArrayList<>(Arrays.asList(-0.08f,-0.19f,-0.08f,-0.42f,1.2f,1.51f, -0.08f,-0.19f,-0.08f,-0.41f,1.19f,1.53f, -0.08f,-0.19f,-0.08f,-0.41f,1.19f,1.54f, -0.08f,-0.19f,-0.08f,-0.44f,1.2f,1.53f)));
        dp(gl,0.42f,0.44f,0.65f, new ArrayList<>(Arrays.asList(-0.14f,-0.11f,-0.14f,-0.42f,1.23f,1.5f, -0.14f,-0.11f,-0.14f,-0.42f,1.2f,1.51f, -0.14f,-0.11f,-0.14f,-0.44f,1.2f,1.53f, -0.14f,-0.11f,-0.14f,-0.45f,1.23f,1.53f)));
        dp(gl,0.4f,0.41f,0.73f, new ArrayList<>(Arrays.asList(-0.02f,-0.22f,-0.02f,-0.41f,1.19f,1.54f, -0.02f,-0.22f,-0.02f,-0.41f,1.19f,1.53f, -0.02f,-0.22f,-0.02f,-0.4f,1.19f,1.55f)));
        dp(gl,0.41f,0.43f,0.93f, new ArrayList<>(Arrays.asList(0f,-0.11f,-0.2f,-0.42f,1.2f,1.51f, 0f,-0.11f,-0.2f,-0.42f,1.23f,1.5f, 0f,-0.11f,-0.2f,-0.38f,1.23f,1.5f, 0f,-0.11f,-0.2f,-0.39f,1.2f,1.51f)));
        dp(gl,0.43f,0.43f,0.83f, new ArrayList<>(Arrays.asList(-0.14f,0.11f,-0.14f,-0.42f,1.28f,1.51f, -0.14f,0.11f,-0.14f,-0.42f,1.26f,1.5f, -0.14f,0.11f,-0.14f,-0.45f,1.26f,1.53f, -0.14f,0.11f,-0.14f,-0.44f,1.28f,1.53f)));
        dp(gl,0.45f,0.42f,0.97f, new ArrayList<>(Arrays.asList(0.09f,0.21f,0.01f,-0.4f,1.27f,1.59f, 0.09f,0.21f,0.01f,-0.4f,1.26f,1.77f, 0.09f,0.21f,0.01f,-0.39f,1.25f,1.77f, 0.09f,0.21f,0.01f,-0.39f,1.26f,1.59f)));
        dp(gl,0.4f,0.43f,0.61f, new ArrayList<>(Arrays.asList(0.21f,0.09f,0.01f,-0.39f,1.26f,1.59f, 0.21f,0.09f,0.01f,-0.39f,1.25f,1.77f, 0.21f,0.09f,0.01f,-0.39f,1.24f,1.77f, 0.21f,0.09f,0.01f,-0.38f,1.24f,1.59f)));
        dp(gl,0.43f,0.44f,0.95f, new ArrayList<>(Arrays.asList(0.21f,-0.09f,0.01f,-0.38f,1.24f,1.59f, 0.21f,-0.09f,0.01f,-0.39f,1.24f,1.77f, 0.21f,-0.09f,0.01f,-0.39f,1.23f,1.77f, 0.21f,-0.09f,0.01f,-0.39f,1.23f,1.59f)));
        dp(gl,0.43f,0.41f,0.65f, new ArrayList<>(Arrays.asList(0.09f,-0.21f,0.01f,-0.39f,1.23f,1.59f, 0.09f,-0.21f,0.01f,-0.39f,1.23f,1.77f, 0.09f,-0.21f,0.01f,-0.4f,1.23f,1.77f, 0.09f,-0.21f,0.01f,-0.4f,1.22f,1.59f)));
        dp(gl,0.41f,0.42f,0.76f, new ArrayList<>(Arrays.asList(-0.09f,-0.21f,0.01f,-0.4f,1.22f,1.59f, -0.09f,-0.21f,0.01f,-0.4f,1.23f,1.77f, -0.09f,-0.21f,0.01f,-0.41f,1.23f,1.77f, -0.09f,-0.21f,0.01f,-0.42f,1.23f,1.59f)));
        dp(gl,0.44f,0.4f,0.81f, new ArrayList<>(Arrays.asList(-0.21f,-0.09f,0.01f,-0.42f,1.23f,1.59f, -0.21f,-0.09f,0.01f,-0.41f,1.23f,1.77f, -0.21f,-0.09f,0.01f,-0.42f,1.24f,1.77f, -0.21f,-0.09f,0.01f,-0.42f,1.24f,1.59f)));
        dp(gl,0.41f,0.44f,0.83f, new ArrayList<>(Arrays.asList(0f,-0f,0.23f,-0.39f,1.25f,1.77f, 0f,-0f,0.23f,-0.4f,1.26f,1.77f, 0f,-0f,0.23f,-0.41f,1.25f,1.77f, 0f,-0f,0.23f,-0.42f,1.24f,1.77f, 0f,-0f,0.23f,-0.41f,1.23f,1.77f, 0f,-0f,0.23f,-0.4f,1.23f,1.77f, 0f,-0f,0.23f,-0.39f,1.23f,1.77f, 0f,-0f,0.23f,-0.39f,1.24f,1.77f)));
        dp(gl,0.4f,0.44f,0.84f, new ArrayList<>(Arrays.asList(-0.21f,0.09f,0.01f,-0.42f,1.24f,1.59f, -0.21f,0.09f,0.01f,-0.42f,1.24f,1.77f, -0.21f,0.09f,0.01f,-0.41f,1.25f,1.77f, -0.21f,0.09f,0.01f,-0.42f,1.26f,1.59f)));
        dp(gl,0.4f,0.42f,0.64f, new ArrayList<>(Arrays.asList(-0.09f,0.21f,0.01f,-0.42f,1.26f,1.59f, -0.09f,0.21f,0.01f,-0.41f,1.25f,1.77f, -0.09f,0.21f,0.01f,-0.4f,1.26f,1.77f, -0.09f,0.21f,0.01f,-0.4f,1.27f,1.59f)));
        dp(gl,0.42f,0.43f,0.99f, new ArrayList<>(Arrays.asList(-0f,0f,-0.23f,-0.4f,1.27f,1.59f, -0f,0f,-0.23f,-0.39f,1.26f,1.59f, -0f,0f,-0.23f,-0.38f,1.24f,1.59f, -0f,0f,-0.23f,-0.39f,1.23f,1.59f, -0f,0f,-0.23f,-0.4f,1.22f,1.59f, -0f,0f,-0.23f,-0.42f,1.23f,1.59f, -0f,0f,-0.23f,-0.42f,1.24f,1.59f, -0f,0f,-0.23f,-0.42f,1.26f,1.59f)));
    }

    void drawTower(GL2 gl){
        dp(gl,0.04f,0.99f,0.08f, new ArrayList<>(Arrays.asList(0f,1.67f,0.88f,-0.23f,1.63f,0.57f, 0f,1.67f,0.88f,-0.23f,1.47f,0.64f, 0f,1.67f,0.88f,0.23f,1.47f,0.64f, 0f,1.67f,0.88f,0.23f,1.63f,0.57f)));
        dp(gl,0.14f,0.92f,0.07f, new ArrayList<>(Arrays.asList(0f,2.47f,0.22f,-0.23f,1.47f,0.64f, 0f,2.47f,0.22f,-0.23f,1.45f,0.69f, 0f,2.47f,0.22f,0.23f,1.45f,0.69f, 0f,2.47f,0.22f,0.23f,1.47f,0.64f)));
        dp(gl,0.13f,0.92f,0.07f, new ArrayList<>(Arrays.asList(-2.38f,-0f,0f,0.23f,1.47f,0.64f, -2.38f,-0f,0f,0.23f,1.45f,0.69f, -2.38f,-0f,0f,0.23f,1.45f,0.9f, -2.38f,-0f,0f,0.23f,1.46f,0.95f, -2.38f,-0f,0f,0.23f,1.85f,0.8f, -2.38f,-0f,0f,0.23f,1.85f,0.8f, -2.38f,-0f,0f,0.23f,1.86f,0.8f, -2.38f,-0f,0f,0.23f,1.79f,0.64f, -2.38f,-0f,0f,0.23f,1.63f,0.57f)));
        dp(gl,0.01f,0.81f,0.09f, new ArrayList<>(Arrays.asList(0f,-2.47f,0.22f,-0.23f,1.86f,0.8f, 0f,-2.47f,0.22f,-0.23f,1.79f,0.64f, 0f,-2.47f,0.22f,0.23f,1.79f,0.64f, 0f,-2.47f,0.22f,0.23f,1.86f,0.8f)));
        dp(gl,0.01f,0.97f,0.09f, new ArrayList<>(Arrays.asList(-0f,-1.67f,0.88f,-0.23f,1.79f,0.64f, -0f,-1.67f,0.88f,-0.23f,1.63f,0.57f, -0f,-1.67f,0.88f,0.23f,1.63f,0.57f, -0f,-1.67f,0.88f,0.23f,1.79f,0.64f)));
        dp(gl,0.04f,0.86f,0.16f, new ArrayList<>(Arrays.asList(2.38f,0f,0f,-0.23f,1.63f,0.57f, 2.38f,0f,0f,-0.23f,1.79f,0.64f, 2.38f,0f,0f,-0.23f,1.86f,0.8f, 2.38f,0f,0f,-0.23f,1.85f,0.8f, 2.38f,0f,0f,-0.23f,1.79f,0.83f, 2.38f,0f,0f,-0.23f,1.58f,0.9f, 2.38f,0f,0f,-0.23f,1.46f,0.95f, 2.38f,0f,0f,-0.23f,1.45f,0.9f, 2.38f,0f,0f,-0.23f,1.45f,0.86f, 2.38f,0f,0f,-0.23f,1.45f,0.69f, 2.38f,0f,0f,-0.23f,1.47f,0.64f)));
        dp(gl,0.14f,0.8f,0.18f, new ArrayList<>(Arrays.asList(2.26f,0.69f,0.17f,0.62f,1.45f,0.52f, 2.26f,0.69f,0.17f,0.46f,1.95f,0.47f, 2.26f,0.69f,0.17f,0.4f,1.95f,0.77f, 2.26f,0.69f,0.17f,0.46f,1.45f,0.95f)));
        dp(gl,0.05f,0.8f,0.18f, new ArrayList<>(Arrays.asList(0f,2.51f,0f,0.31f,2.03f,-0.27f, 0f,2.51f,0f,-0.31f,2.03f,-0.27f, 0f,2.51f,0f,-0.36f,2.03f,0.14f, 0f,2.51f,0f,0.36f,2.03f,0.14f)));
        dp(gl,0.05f,0.86f,0.1f, new ArrayList<>(Arrays.asList(0f,2.51f,-0f,0.38f,2.03f,0.47f, 0f,2.51f,-0f,-0.38f,2.03f,0.47f, 0f,2.51f,-0f,-0.34f,2.03f,0.68f, 0f,2.51f,-0f,0.34f,2.03f,0.68f)));
        dp(gl,0.02f,0.81f,0.14f, new ArrayList<>(Arrays.asList(0f,0.76f,-1.12f,-0.37f,1.95f,-0.35f, 0f,0.76f,-1.12f,0.37f,1.95f,-0.35f, 0f,0.76f,-1.12f,0.27f,1.89f,-0.36f, 0f,0.76f,-1.12f,-0.27f,1.89f,-0.36f)));
        dp(gl,0.06f,0.84f,0.05f, new ArrayList<>(Arrays.asList(-2.27f,0.75f,-0.05f,-0.44f,1.95f,0.14f, -2.27f,0.75f,-0.05f,-0.58f,1.45f,0.07f, -2.27f,0.75f,-0.05f,-0.62f,1.45f,0.52f, -2.27f,0.75f,-0.05f,-0.46f,1.95f,0.47f)));
        dp(gl,0.08f,0.92f,0.06f, new ArrayList<>(Arrays.asList(0.05f,1.06f,-1.07f,-0.4f,1.45f,-0.46f, 0.05f,1.06f,-1.07f,-0.37f,1.95f,-0.35f, 0.05f,1.06f,-1.07f,-0.27f,1.89f,-0.36f, 0.05f,1.06f,-1.07f,-0.29f,1.53f,-0.44f)));
        dp(gl,0.08f,0.81f,0.13f, new ArrayList<>(Arrays.asList(0f,-2.51f,-0f,-0.62f,1.45f,0.52f, 0f,-2.51f,-0f,0.62f,1.45f,0.52f, 0f,-2.51f,-0f,0.46f,1.45f,0.95f, 0f,-2.51f,-0f,0.23f,1.45f,0.9f, 0f,-2.51f,-0f,0.23f,1.45f,0.69f, 0f,-2.51f,-0f,-0.23f,1.45f,0.69f, 0f,-2.51f,-0f,-0.23f,1.45f,0.86f, 0f,-2.51f,-0f,-0.46f,1.45f,0.95f)));
        dp(gl,0.1f,0.83f,0.19f, new ArrayList<>(Arrays.asList(-0.05f,1.06f,-1.07f,0.37f,1.95f,-0.35f, -0.05f,1.06f,-1.07f,0.4f,1.45f,-0.46f, -0.05f,1.06f,-1.07f,0.29f,1.53f,-0.44f, -0.05f,1.06f,-1.07f,0.27f,1.89f,-0.36f)));
        dp(gl,0.12f,0.96f,0.07f, new ArrayList<>(Arrays.asList(0f,-2.51f,0f,-0.4f,1.45f,-0.46f, 0f,-2.51f,0f,0.4f,1.45f,-0.46f, 0f,-2.51f,0f,0.58f,1.45f,0.07f, 0f,-2.51f,0f,-0.58f,1.45f,0.07f)));
        dp(gl,0.06f,0.82f,0.11f, new ArrayList<>(Arrays.asList(2.27f,0.75f,-0.05f,0.46f,1.95f,0.47f, 2.27f,0.75f,-0.05f,0.62f,1.45f,0.52f, 2.27f,0.75f,-0.05f,0.58f,1.45f,0.07f, 2.27f,0.75f,-0.05f,0.44f,1.95f,0.14f)));
        dp(gl,0.04f,0.88f,0.08f, new ArrayList<>(Arrays.asList(-0f,-2.51f,0f,0.62f,1.45f,0.52f, -0f,-2.51f,0f,-0.62f,1.45f,0.52f, -0f,-2.51f,0f,-0.58f,1.45f,0.07f, -0f,-2.51f,0f,0.58f,1.45f,0.07f)));
        dp(gl,0.05f,0.89f,0.12f, new ArrayList<>(Arrays.asList(0f,2.51f,0f,-0.38f,2.03f,0.47f, 0f,2.51f,0f,0.38f,2.03f,0.47f, 0f,2.51f,0f,0.36f,2.03f,0.14f, 0f,2.51f,0f,-0.36f,2.03f,0.14f)));
        dp(gl,0.08f,0.97f,0.06f, new ArrayList<>(Arrays.asList(0f,1.55f,0.92f,0.46f,1.45f,0.95f, 0f,1.55f,0.92f,0.4f,1.95f,0.77f, 0f,1.55f,0.92f,0.23f,1.85f,0.8f, 0f,1.55f,0.92f,0.23f,1.46f,0.95f, 0f,1.55f,0.92f,-0.23f,1.46f,0.95f, 0f,1.55f,0.92f,-0.23f,1.58f,0.9f, 0f,1.55f,0.92f,-0.23f,1.79f,0.83f, 0f,1.55f,0.92f,-0.4f,1.95f,0.77f, 0f,1.55f,0.92f,-0.46f,1.45f,0.95f)));
        dp(gl,0.16f,0.85f,0.09f, new ArrayList<>(Arrays.asList(2.31f,0.54f,-0.13f,0.4f,1.45f,-0.46f, 2.31f,0.54f,-0.13f,0.37f,1.95f,-0.35f, 2.31f,0.54f,-0.13f,0.44f,1.95f,0.14f, 2.31f,0.54f,-0.13f,0.58f,1.45f,0.07f)));
        dp(gl,0.2f,0.89f,0.12f, new ArrayList<>(Arrays.asList(-2.31f,0.54f,-0.13f,-0.58f,1.45f,0.07f, -2.31f,0.54f,-0.13f,-0.44f,1.95f,0.14f, -2.31f,0.54f,-0.13f,-0.37f,1.95f,-0.35f, -2.31f,0.54f,-0.13f,-0.4f,1.45f,-0.46f)));
        dp(gl,0.12f,0.98f,0.05f, new ArrayList<>(Arrays.asList(0f,0.96f,-1.09f,0.4f,1.45f,-0.46f, 0f,0.96f,-1.09f,-0.4f,1.45f,-0.46f, 0f,0.96f,-1.09f,-0.29f,1.53f,-0.44f, 0f,0.96f,-1.09f,0.29f,1.53f,-0.44f)));
        dp(gl,0.03f,0.91f,0.07f, new ArrayList<>(Arrays.asList(0f,2.28f,-0.5f,-0.31f,2.03f,-0.27f, 0f,2.28f,-0.5f,0.31f,2.03f,-0.27f, 0f,2.28f,-0.5f,0.37f,1.95f,-0.35f, 0f,2.28f,-0.5f,-0.37f,1.95f,-0.35f)));
        dp(gl,0.14f,0.84f,0.01f, new ArrayList<>(Arrays.asList(0f,2.3f,0.47f,0.34f,2.03f,0.68f, 0f,2.3f,0.47f,-0.34f,2.03f,0.68f, 0f,2.3f,0.47f,-0.4f,1.95f,0.77f, 0f,2.3f,0.47f,0.4f,1.95f,0.77f)));
        dp(gl,0.03f,0.95f,0.1f, new ArrayList<>(Arrays.asList(-1.63f,1.83f,-0.03f,-0.44f,1.95f,0.14f, -1.63f,1.83f,-0.03f,-0.46f,1.95f,0.47f, -1.63f,1.83f,-0.03f,-0.38f,2.03f,0.47f, -1.63f,1.83f,-0.03f,-0.36f,2.03f,0.14f)));
        dp(gl,0.05f,0.84f,0.16f, new ArrayList<>(Arrays.asList(1.63f,1.83f,-0.03f,0.46f,1.95f,0.47f, 1.63f,1.83f,-0.03f,0.44f,1.95f,0.14f, 1.63f,1.83f,-0.03f,0.36f,2.03f,0.14f, 1.63f,1.83f,-0.03f,0.38f,2.03f,0.47f)));
        dp(gl,0.14f,0.91f,0.14f, new ArrayList<>(Arrays.asList(-1.64f,1.81f,0.08f,-0.34f,2.03f,0.68f, -1.64f,1.81f,0.08f,-0.38f,2.03f,0.47f, -1.64f,1.81f,0.08f,-0.46f,1.95f,0.47f, -1.64f,1.81f,0.08f,-0.4f,1.95f,0.77f)));
        dp(gl,0.01f,0.81f,0.14f, new ArrayList<>(Arrays.asList(-1.67f,1.79f,-0.05f,-0.36f,2.03f,0.14f, -1.67f,1.79f,-0.05f,-0.31f,2.03f,-0.27f, -1.67f,1.79f,-0.05f,-0.37f,1.95f,-0.35f, -1.67f,1.79f,-0.05f,-0.44f,1.95f,0.14f)));
        dp(gl,0.18f,0.81f,0.07f, new ArrayList<>(Arrays.asList(1.67f,1.79f,-0.05f,0.44f,1.95f,0.14f, 1.67f,1.79f,-0.05f,0.37f,1.95f,-0.35f, 1.67f,1.79f,-0.05f,0.31f,2.03f,-0.27f, 1.67f,1.79f,-0.05f,0.36f,2.03f,0.14f)));
        dp(gl,0.12f,0.89f,0.01f, new ArrayList<>(Arrays.asList(1.64f,1.81f,0.08f,0.4f,1.95f,0.77f, 1.64f,1.81f,0.08f,0.46f,1.95f,0.47f, 1.64f,1.81f,0.08f,0.38f,2.03f,0.47f, 1.64f,1.81f,0.08f,0.34f,2.03f,0.68f)));
        dp(gl,0.17f,0.81f,0.03f, new ArrayList<>(Arrays.asList(-2.26f,0.69f,0.17f,-0.46f,1.45f,0.95f, -2.26f,0.69f,0.17f,-0.4f,1.95f,0.77f, -2.26f,0.69f,0.17f,-0.46f,1.95f,0.47f, -2.26f,0.69f,0.17f,-0.62f,1.45f,0.52f)));
        dp(gl,0.13f,0.99f,0.17f, new ArrayList<>(Arrays.asList(-0f,-2.51f,0f,-0.46f,1.45f,0.95f, -0f,-2.51f,0f,-0.23f,1.45f,0.86f, -0f,-2.51f,0f,-0.23f,1.45f,0.9f, -0f,-2.51f,0f,0.23f,1.45f,0.9f, -0f,-2.51f,0f,0.46f,1.45f,0.95f)));
        dp(gl,0.09f,0.86f,0.05f, new ArrayList<>(Arrays.asList(-0f,2.47f,-0.22f,-0.23f,1.45f,0.9f, -0f,2.47f,-0.22f,-0.23f,1.46f,0.95f, -0f,2.47f,-0.22f,0.23f,1.46f,0.95f, -0f,2.47f,-0.22f,0.23f,1.45f,0.9f)));
        dp(gl,0.1f,0.85f,0.02f, new ArrayList<>(Arrays.asList(0f,-2.47f,-0.22f,-0.23f,1.85f,0.8f, 0f,-2.47f,-0.22f,-0.23f,1.86f,0.8f, 0f,-2.47f,-0.22f,0.23f,1.86f,0.8f, 0f,-2.47f,-0.22f,0.23f,1.85f,0.8f)));
        dp(gl,0.17f,0.93f,0.05f, new ArrayList<>(Arrays.asList(0f,1.55f,0.92f,-0.4f,1.95f,0.77f, 0f,1.55f,0.92f,-0.23f,1.79f,0.83f, 0f,1.55f,0.92f,-0.23f,1.85f,0.8f, 0f,1.55f,0.92f,0.23f,1.85f,0.8f, 0f,1.55f,0.92f,0.23f,1.85f,0.8f, 0f,1.55f,0.92f,0.4f,1.95f,0.77f)));
        dp(gl,0.13f,0.82f,0.1f, new ArrayList<>(Arrays.asList(0f,-1.67f,-0.88f,-0.23f,1.63f,0.57f, 0f,-1.67f,-0.88f,-0.11f,1.63f,0.57f, 0f,-1.67f,-0.88f,-0.15f,1.53f,0.62f, 0f,-1.67f,-0.88f,-0.23f,1.53f,0.62f)));
        dp(gl,0.01f,0.99f,0.15f, new ArrayList<>(Arrays.asList(-2.38f,-0f,0f,-0.23f,1.63f,0.57f, -2.38f,-0f,0f,-0.23f,1.53f,0.62f, -2.38f,-0f,0f,-0.23f,1.53f,0.62f, -2.38f,-0f,0f,-0.23f,1.75f,0.62f)));
        dp(gl,0.09f,0.87f,0.04f, new ArrayList<>(Arrays.asList(-0f,-2.51f,0f,-0.38f,2.03f,0.47f, -0f,-2.51f,0f,-0.36f,2.03f,0.15f, -0f,-2.51f,0f,-0.16f,2.03f,0.15f, -0f,-2.51f,0f,-0.02f,2.03f,0.29f, -0f,-2.51f,0f,-0.02f,2.03f,0.47f)));
        dp(gl,0.16f,0.83f,0.14f, new ArrayList<>(Arrays.asList(-0.01f,0f,1.18f,-0.16f,1.53f,0.62f, -0.01f,0f,1.18f,-0.16f,1.75f,0.62f, -0.01f,0f,1.18f,-0.23f,1.75f,0.62f, -0.01f,0f,1.18f,-0.23f,1.53f,0.62f)));
        dp(gl,0.08f,0.89f,0.09f, new ArrayList<>(Arrays.asList(-0f,2.51f,-0f,-0.16f,2.07f,0.15f, -0f,2.51f,-0f,-0.36f,2.07f,0.15f, -0f,2.51f,-0f,-0.5f,2.07f,0.29f, -0f,2.51f,-0f,-0.5f,2.07f,0.48f, -0f,2.51f,-0f,-0.36f,2.07f,0.62f, -0f,2.51f,-0f,-0.16f,2.07f,0.62f, -0f,2.51f,-0f,-0.02f,2.07f,0.48f, -0f,2.51f,-0f,-0.02f,2.07f,0.29f)));
        dp(gl,0.15f,0.94f,0.09f, new ArrayList<>(Arrays.asList(-2.14f,0f,0.52f,-0.5f,1.84f,0.48f, -2.14f,0f,0.52f,-0.45f,1.95f,0.53f, -2.14f,0f,0.52f,-0.36f,2.03f,0.62f, -2.14f,0f,0.52f,-0.36f,2.07f,0.62f, -2.14f,0f,0.52f,-0.5f,2.07f,0.48f)));
        dp(gl,0.14f,0.98f,0.16f, new ArrayList<>(Arrays.asList(2.38f,-0f,0f,-0.02f,2.03f,0.48f, 2.38f,-0f,0f,-0.02f,2.03f,0.47f, 2.38f,-0f,0f,-0.02f,2.03f,0.29f, 2.38f,-0f,0f,-0.02f,2.07f,0.29f, 2.38f,-0f,0f,-0.02f,2.07f,0.48f)));
        dp(gl,0.03f,0.93f,0.02f, new ArrayList<>(Arrays.asList(-2.38f,-0f,-0f,-0.5f,1.78f,0.29f, -2.38f,-0f,-0f,-0.5f,1.84f,0.48f, -2.38f,-0f,-0f,-0.5f,1.84f,0.48f, -2.38f,-0f,-0f,-0.5f,2.07f,0.48f, -2.38f,-0f,-0f,-0.5f,2.07f,0.29f)));
        dp(gl,0.07f,0.99f,0.09f, new ArrayList<>(Arrays.asList(-0f,-2.51f,-0f,-0.35f,2.03f,0.62f, -0f,-2.51f,-0f,-0.38f,2.03f,0.47f, -0f,-2.51f,-0f,-0.02f,2.03f,0.47f, -0f,-2.51f,-0f,-0.02f,2.03f,0.48f, -0f,-2.51f,-0f,-0.16f,2.03f,0.62f)));
        dp(gl,0.04f,0.82f,0.12f, new ArrayList<>(Arrays.asList(2.13f,0f,0.52f,-0.15f,1.53f,0.62f, 2.13f,0f,0.52f,-0.11f,1.63f,0.57f, 2.13f,0f,0.52f,-0.16f,1.75f,0.62f, 2.13f,0f,0.52f,-0.16f,1.53f,0.62f)));
        dp(gl,0.14f,0.99f,0.04f, new ArrayList<>(Arrays.asList(2.13f,0f,0.52f,-0.16f,2.03f,0.62f, 2.13f,0f,0.52f,-0.02f,2.03f,0.48f, 2.13f,0f,0.52f,-0.02f,2.07f,0.48f, 2.13f,0f,0.52f,-0.16f,2.07f,0.62f)));
        dp(gl,0.14f,0.83f,0.17f, new ArrayList<>(Arrays.asList(0.01f,-0f,-1.18f,-0.16f,2.03f,0.15f, 0.01f,-0f,-1.18f,-0.36f,2.03f,0.15f, 0.01f,-0f,-1.18f,-0.36f,2.03f,0.15f, 0.01f,-0f,-1.18f,-0.36f,2.07f,0.15f, 0.01f,-0f,-1.18f,-0.16f,2.07f,0.15f)));
        dp(gl,0.17f,0.85f,0.18f, new ArrayList<>(Arrays.asList(2.27f,-0.74f,0.05f,-0.44f,1.95f,0.23f, 2.27f,-0.74f,0.05f,-0.46f,1.95f,0.47f, 2.27f,-0.74f,0.05f,-0.5f,1.84f,0.48f, 2.27f,-0.74f,0.05f,-0.5f,1.78f,0.29f)));
        dp(gl,0.15f,0.83f,0.19f, new ArrayList<>(Arrays.asList(1.63f,-1.83f,0.03f,-0.36f,2.03f,0.15f, 1.63f,-1.83f,0.03f,-0.38f,2.03f,0.47f, 1.63f,-1.83f,0.03f,-0.46f,1.95f,0.47f, 1.63f,-1.83f,0.03f,-0.44f,1.95f,0.23f, 1.63f,-1.83f,0.03f,-0.36f,2.03f,0.15f)));
        dp(gl,0.02f,0.85f,0.05f, new ArrayList<>(Arrays.asList(2.24f,-0.81f,-0.11f,-0.5f,1.84f,0.48f, 2.24f,-0.81f,-0.11f,-0.46f,1.95f,0.47f, 2.24f,-0.81f,-0.11f,-0.45f,1.95f,0.53f, 2.24f,-0.81f,-0.11f,-0.5f,1.84f,0.48f)));
        dp(gl,0.08f,0.94f,0.04f, new ArrayList<>(Arrays.asList(-2.13f,0f,-0.52f,-0.36f,2.03f,0.15f, -2.13f,0f,-0.52f,-0.44f,1.95f,0.23f, -2.13f,0f,-0.52f,-0.5f,1.78f,0.29f, -2.13f,0f,-0.52f,-0.5f,2.07f,0.29f, -2.13f,0f,-0.52f,-0.36f,2.07f,0.15f)));
        dp(gl,0.07f,0.84f,0.08f, new ArrayList<>(Arrays.asList(0f,-2.51f,-0f,-0.23f,1.53f,0.62f, 0f,-2.51f,-0f,-0.23f,1.53f,0.62f, 0f,-2.51f,-0f,-0.15f,1.53f,0.62f, 0f,-2.51f,-0f,-0.16f,1.53f,0.62f)));
        dp(gl,0.06f,0.85f,0.12f, new ArrayList<>(Arrays.asList(2.14f,0f,-0.52f,-0.02f,2.03f,0.29f, 2.14f,0f,-0.52f,-0.16f,2.03f,0.15f, 2.14f,0f,-0.52f,-0.16f,2.07f,0.15f, 2.14f,0f,-0.52f,-0.02f,2.07f,0.29f)));
        dp(gl,0.12f,0.84f,0.02f, new ArrayList<>(Arrays.asList(1.63f,-1.83f,-0.07f,-0.45f,1.95f,0.53f, 1.63f,-1.83f,-0.07f,-0.46f,1.95f,0.47f, 1.63f,-1.83f,-0.07f,-0.38f,2.03f,0.47f, 1.63f,-1.83f,-0.07f,-0.35f,2.03f,0.62f, 1.63f,-1.83f,-0.07f,-0.36f,2.03f,0.62f)));
        dp(gl,0.03f,0.91f,0.13f, new ArrayList<>(Arrays.asList(-0.01f,0f,1.18f,-0.36f,2.03f,0.62f, -0.01f,0f,1.18f,-0.35f,2.03f,0.62f, -0.01f,0f,1.18f,-0.16f,2.03f,0.62f, -0.01f,0f,1.18f,-0.16f,2.07f,0.62f, -0.01f,0f,1.18f,-0.36f,2.07f,0.62f)));
        dp(gl,0.15f,0.87f,0.0f, new ArrayList<>(Arrays.asList(-0f,1.67f,-0.88f,-0.11f,1.63f,0.57f, -0f,1.67f,-0.88f,-0.23f,1.63f,0.57f, -0f,1.67f,-0.88f,-0.23f,1.75f,0.62f, -0f,1.67f,-0.88f,-0.16f,1.75f,0.62f)));
    }

    void drawTowerDetails(GL2 gl){
        dp(gl,0.15f,0.64f,0.19f, new ArrayList<>(Arrays.asList(2.38f,0.15f,-0f,0.27f,1.89f,-0.36f, 2.38f,0.15f,-0f,0.29f,1.53f,-0.44f, 2.38f,0.15f,-0f,0.29f,1.53f,-0.61f, 2.38f,0.15f,-0f,0.27f,1.89f,-0.61f)));
        dp(gl,0.14f,0.68f,0.2f, new ArrayList<>(Arrays.asList(0f,0f,-1.18f,-0.29f,1.53f,-0.61f, 0f,0f,-1.18f,-0.27f,1.89f,-0.61f, 0f,0f,-1.18f,0.27f,1.89f,-0.61f, 0f,0f,-1.18f,0.29f,1.53f,-0.61f)));
        dp(gl,0.11f,0.63f,0.16f, new ArrayList<>(Arrays.asList(-2.38f,0.15f,-0f,-0.29f,1.53f,-0.44f, -2.38f,0.15f,-0f,-0.27f,1.89f,-0.36f, -2.38f,0.15f,-0f,-0.27f,1.89f,-0.61f, -2.38f,0.15f,-0f,-0.29f,1.53f,-0.61f)));
        dp(gl,0.19f,0.6f,0.16f, new ArrayList<>(Arrays.asList(0f,-2.51f,0f,0.29f,1.53f,-0.44f, 0f,-2.51f,0f,-0.29f,1.53f,-0.44f, 0f,-2.51f,0f,-0.29f,1.53f,-0.61f, 0f,-2.51f,0f,0.29f,1.53f,-0.61f)));
        dp(gl,0.18f,0.67f,0.19f, new ArrayList<>(Arrays.asList(0f,2.51f,-0f,-0.27f,1.89f,-0.36f, 0f,2.51f,-0f,0.27f,1.89f,-0.36f, 0f,2.51f,-0f,0.27f,1.89f,-0.61f, 0f,2.51f,-0f,-0.27f,1.89f,-0.61f)));
        dp(gl,0.17f,0.66f,0.14f, new ArrayList<>(Arrays.asList(2.14f,0f,-0.52f,-0.18f,2.07f,0.18f, 2.14f,0f,-0.52f,-0.18f,2.11f,0.18f, 2.14f,0f,-0.52f,-0.06f,2.11f,0.3f, 2.14f,0f,-0.52f,-0.06f,2.07f,0.3f)));
        dp(gl,0.17f,0.62f,0.13f, new ArrayList<>(Arrays.asList(2.38f,0f,0f,-0.06f,2.07f,0.3f, 2.38f,0f,0f,-0.06f,2.11f,0.3f, 2.38f,0f,0f,-0.06f,2.11f,0.47f, 2.38f,0f,0f,-0.06f,2.07f,0.47f)));
        dp(gl,0.14f,0.7f,0.15f, new ArrayList<>(Arrays.asList(2.13f,0f,0.52f,-0.06f,2.07f,0.47f, 2.13f,0f,0.52f,-0.06f,2.11f,0.47f, 2.13f,0f,0.52f,-0.18f,2.11f,0.59f, 2.13f,0f,0.52f,-0.18f,2.07f,0.59f)));
        dp(gl,0.14f,0.6f,0.14f, new ArrayList<>(Arrays.asList(-0f,0f,1.18f,-0.18f,2.07f,0.59f, -0f,0f,1.18f,-0.18f,2.11f,0.59f, -0f,0f,1.18f,-0.35f,2.11f,0.59f, -0f,0f,1.18f,-0.35f,2.07f,0.59f)));
        dp(gl,0.18f,0.67f,0.14f, new ArrayList<>(Arrays.asList(-2.14f,0f,0.52f,-0.35f,2.07f,0.59f, -2.14f,0f,0.52f,-0.35f,2.11f,0.59f, -2.14f,0f,0.52f,-0.47f,2.11f,0.47f, -2.14f,0f,0.52f,-0.47f,2.07f,0.47f)));
        dp(gl,0.2f,0.61f,0.18f, new ArrayList<>(Arrays.asList(-2.38f,0f,-0f,-0.47f,2.07f,0.47f, -2.38f,0f,-0f,-0.47f,2.11f,0.47f, -2.38f,0f,-0f,-0.47f,2.11f,0.3f, -2.38f,0f,-0f,-0.47f,2.07f,0.3f)));
        dp(gl,0.13f,0.66f,0.14f, new ArrayList<>(Arrays.asList(-0f,2.51f,0f,-0.06f,2.11f,0.3f, -0f,2.51f,0f,-0.18f,2.11f,0.18f, -0f,2.51f,0f,-0.35f,2.11f,0.18f, -0f,2.51f,0f,-0.47f,2.11f,0.3f, -0f,2.51f,0f,-0.47f,2.11f,0.47f, -0f,2.51f,0f,-0.35f,2.11f,0.59f, -0f,2.51f,0f,-0.18f,2.11f,0.59f, -0f,2.51f,0f,-0.06f,2.11f,0.47f)));
        dp(gl,0.11f,0.67f,0.17f, new ArrayList<>(Arrays.asList(-2.13f,0f,-0.52f,-0.47f,2.07f,0.3f, -2.13f,0f,-0.52f,-0.47f,2.11f,0.3f, -2.13f,0f,-0.52f,-0.35f,2.11f,0.18f, -2.13f,0f,-0.52f,-0.35f,2.07f,0.18f)));
        dp(gl,0.2f,0.68f,0.16f, new ArrayList<>(Arrays.asList(0f,0f,-1.18f,-0.35f,2.07f,0.18f, 0f,0f,-1.18f,-0.35f,2.11f,0.18f, 0f,0f,-1.18f,-0.18f,2.11f,0.18f, 0f,0f,-1.18f,-0.18f,2.07f,0.18f)));
        dp(gl,0.2f,0.67f,0.1f, new ArrayList<>(Arrays.asList(0f,-2.51f,-0f,-0.18f,2.07f,0.18f, 0f,-2.51f,-0f,-0.06f,2.07f,0.3f, 0f,-2.51f,-0f,-0.06f,2.07f,0.47f, 0f,-2.51f,-0f,-0.18f,2.07f,0.59f, 0f,-2.51f,-0f,-0.35f,2.07f,0.59f, 0f,-2.51f,-0f,-0.47f,2.07f,0.47f, 0f,-2.51f,-0f,-0.47f,2.07f,0.3f, 0f,-2.51f,-0f,-0.35f,2.07f,0.18f)));
        dp(gl,0.18f,0.65f,0.17f, new ArrayList<>(Arrays.asList(-2.38f,0f,-0f,-0.35f,2.1f,0.55f, -2.38f,0f,-0f,-0.35f,2.17f,0.55f, -2.38f,0f,-0f,-0.35f,2.17f,0.52f, -2.38f,0f,-0f,-0.35f,2.1f,0.49f)));
        dp(gl,0.16f,0.64f,0.14f, new ArrayList<>(Arrays.asList(0.01f,1.81f,-0.81f,-0.35f,2.1f,0.49f, 0.01f,1.81f,-0.81f,-0.35f,2.17f,0.52f, 0.01f,1.81f,-0.81f,-0.17f,2.17f,0.52f, 0.01f,1.81f,-0.81f,-0.17f,2.1f,0.49f)));
        dp(gl,0.1f,0.64f,0.18f, new ArrayList<>(Arrays.asList(2.38f,0f,0f,-0.17f,2.1f,0.49f, 2.38f,0f,0f,-0.17f,2.17f,0.52f, 2.38f,0f,0f,-0.17f,2.17f,0.55f, 2.38f,0f,0f,-0.17f,2.1f,0.55f)));
        dp(gl,0.17f,0.67f,0.11f, new ArrayList<>(Arrays.asList(-0.02f,-0f,1.18f,-0.17f,2.17f,0.55f, -0.02f,-0f,1.18f,-0.35f,2.17f,0.55f, -0.02f,-0f,1.18f,-0.34f,2.16f,0.55f, -0.02f,-0f,1.18f,-0.19f,2.16f,0.55f)));
        dp(gl,0.17f,0.64f,0.14f, new ArrayList<>(Arrays.asList(0f,-2.51f,0f,-0.35f,2.1f,0.49f, 0f,-2.51f,0f,-0.17f,2.1f,0.49f, 0f,-2.51f,0f,-0.17f,2.1f,0.55f, 0f,-2.51f,0f,-0.35f,2.1f,0.55f)));
        dp(gl,0.17f,0.62f,0.13f, new ArrayList<>(Arrays.asList(0f,2.51f,0f,-0.17f,2.17f,0.52f, 0f,2.51f,0f,-0.35f,2.17f,0.52f, 0f,2.51f,0f,-0.35f,2.17f,0.55f, 0f,2.51f,0f,-0.17f,2.17f,0.55f)));
        dp(gl,0.14f,0.68f,0.19f, new ArrayList<>(Arrays.asList(0f,-2.51f,0f,-0.19f,2.16f,0.55f, 0f,-2.51f,0f,-0.34f,2.16f,0.55f, 0f,-2.51f,0f,-0.34f,2.16f,0.55f, 0f,-2.51f,0f,-0.19f,2.16f,0.55f)));
        dp(gl,0.13f,0.67f,0.15f, new ArrayList<>(Arrays.asList(-0.02f,0f,1.18f,-0.17f,2.1f,0.55f, -0.02f,0f,1.18f,-0.17f,2.17f,0.55f, -0.02f,0f,1.18f,-0.19f,2.16f,0.55f, -0.02f,0f,1.18f,-0.19f,2.11f,0.55f)));
        dp(gl,0.16f,0.67f,0.14f, new ArrayList<>(Arrays.asList(-0.02f,0f,1.18f,-0.35f,2.17f,0.55f, -0.02f,0f,1.18f,-0.35f,2.1f,0.55f, -0.02f,0f,1.18f,-0.34f,2.11f,0.55f, -0.02f,0f,1.18f,-0.34f,2.16f,0.55f)));
        dp(gl,0.14f,0.6f,0.13f, new ArrayList<>(Arrays.asList(-0.02f,0f,1.18f,-0.35f,2.1f,0.55f, -0.02f,0f,1.18f,-0.17f,2.1f,0.55f, -0.02f,0f,1.18f,-0.19f,2.11f,0.55f, -0.02f,0f,1.18f,-0.34f,2.11f,0.55f)));
        dp(gl,0.14f,0.61f,0.18f, new ArrayList<>(Arrays.asList(2.38f,0f,0f,-0.34f,2.16f,0.55f, 2.38f,0f,0f,-0.34f,2.11f,0.55f, 2.38f,0f,0f,-0.34f,2.11f,0.55f, 2.38f,0f,0f,-0.34f,2.16f,0.55f)));
        dp(gl,0.19f,0.7f,0.17f, new ArrayList<>(Arrays.asList(-2.38f,0f,-0f,-0.19f,2.11f,0.55f, -2.38f,0f,-0f,-0.19f,2.16f,0.55f, -2.38f,0f,-0f,-0.19f,2.16f,0.55f, -2.38f,0f,-0f,-0.19f,2.11f,0.55f)));
        dp(gl,0.17f,0.64f,0.11f, new ArrayList<>(Arrays.asList(0f,2.51f,-0f,-0.34f,2.11f,0.55f, 0f,2.51f,-0f,-0.19f,2.11f,0.55f, 0f,2.51f,-0f,-0.19f,2.11f,0.55f, 0f,2.51f,-0f,-0.34f,2.11f,0.55f)));
        dp(gl,0.14f,0.6f,0.2f, new ArrayList<>(Arrays.asList(-2.27f,-0.75f,0.05f,0.47f,1.93f,0.45f, -2.27f,-0.75f,0.05f,0.44f,1.93f,0.15f, -2.27f,-0.75f,0.05f,0.57f,1.47f,0.09f, -2.27f,-0.75f,0.05f,0.61f,1.47f,0.5f)));
        dp(gl,0.12f,0.69f,0.13f, new ArrayList<>(Arrays.asList(2.27f,0.75f,-0.05f,0.5f,1.93f,0.44f, 2.27f,0.75f,-0.05f,0.63f,1.49f,0.49f, 2.27f,0.75f,-0.05f,0.6f,1.49f,0.1f, 2.27f,0.75f,-0.05f,0.48f,1.93f,0.16f)));
        dp(gl,0.12f,0.7f,0.13f, new ArrayList<>(Arrays.asList(1.35f,0.86f,0.88f,0.47f,1.93f,0.45f, 1.35f,0.86f,0.88f,0.61f,1.47f,0.5f, 1.35f,0.86f,0.88f,0.63f,1.49f,0.49f, 1.35f,0.86f,0.88f,0.5f,1.93f,0.44f)));
        dp(gl,0.19f,0.65f,0.16f, new ArrayList<>(Arrays.asList(0.82f,0.87f,-1.03f,0.57f,1.47f,0.09f, 0.82f,0.87f,-1.03f,0.44f,1.93f,0.15f, 0.82f,0.87f,-1.03f,0.48f,1.93f,0.16f, 0.82f,0.87f,-1.03f,0.6f,1.49f,0.1f)));
        dp(gl,0.12f,0.68f,0.11f, new ArrayList<>(Arrays.asList(1.49f,-1.96f,-0.03f,0.61f,1.47f,0.5f, 1.49f,-1.96f,-0.03f,0.57f,1.47f,0.09f, 1.49f,-1.96f,-0.03f,0.6f,1.49f,0.1f, 1.49f,-1.96f,-0.03f,0.63f,1.49f,0.49f)));
        dp(gl,0.19f,0.69f,0.13f, new ArrayList<>(Arrays.asList(0.3f,2.49f,-0.01f,0.44f,1.93f,0.15f, 0.3f,2.49f,-0.01f,0.47f,1.93f,0.45f, 0.3f,2.49f,-0.01f,0.5f,1.93f,0.44f, 0.3f,2.49f,-0.01f,0.48f,1.93f,0.16f)));
        dp(gl,0.15f,0.64f,0.19f, new ArrayList<>(Arrays.asList(2.27f,-0.75f,0.05f,-0.45f,1.92f,0.16f, 2.27f,-0.75f,0.05f,-0.47f,1.92f,0.45f, 2.27f,-0.75f,0.05f,-0.61f,1.48f,0.5f, 2.27f,-0.75f,0.05f,-0.57f,1.48f,0.1f)));
        dp(gl,0.18f,0.64f,0.14f, new ArrayList<>(Arrays.asList(-2.27f,0.75f,-0.05f,-0.47f,1.93f,0.16f, -2.27f,0.75f,-0.05f,-0.6f,1.48f,0.1f, -2.27f,0.75f,-0.05f,-0.63f,1.48f,0.49f, -2.27f,0.75f,-0.05f,-0.49f,1.93f,0.45f)));
        dp(gl,0.14f,0.64f,0.18f, new ArrayList<>(Arrays.asList(-0.24f,0.6f,1.13f,-0.61f,1.48f,0.5f, -0.24f,0.6f,1.13f,-0.47f,1.92f,0.45f, -0.24f,0.6f,1.13f,-0.49f,1.93f,0.45f, -0.24f,0.6f,1.13f,-0.63f,1.48f,0.49f)));
        dp(gl,0.18f,0.68f,0.18f, new ArrayList<>(Arrays.asList(0.53f,0.5f,-1.12f,-0.45f,1.92f,0.16f, 0.53f,0.5f,-1.12f,-0.57f,1.48f,0.1f, 0.53f,0.5f,-1.12f,-0.6f,1.48f,0.1f, 0.53f,0.5f,-1.12f,-0.47f,1.93f,0.16f)));
        dp(gl,0.18f,0.65f,0.16f, new ArrayList<>(Arrays.asList(-0.64f,-2.42f,-0.01f,-0.57f,1.48f,0.1f, -0.64f,-2.42f,-0.01f,-0.61f,1.48f,0.5f, -0.64f,-2.42f,-0.01f,-0.63f,1.48f,0.49f, -0.64f,-2.42f,-0.01f,-0.6f,1.48f,0.1f)));
        dp(gl,0.16f,0.67f,0.11f, new ArrayList<>(Arrays.asList(0.64f,2.42f,0.01f,-0.47f,1.92f,0.45f, 0.64f,2.42f,0.01f,-0.45f,1.92f,0.16f, 0.64f,2.42f,0.01f,-0.47f,1.93f,0.16f, 0.64f,2.42f,0.01f,-0.49f,1.93f,0.45f)));
        //# Mesh 'demos.Cube.011' with 1 faces
    }

    void drawGun(GL2 gl){
        dp(gl,0.12f,0.53f,0.22f, new ArrayList<>(Arrays.asList(-0f,-0.31f,-0.75f,-0.2f,1.63f,0.58f, -0f,-0.31f,-0.75f,0.2f,1.63f,0.58f, -0f,-0.31f,-0.75f,0.2f,1.49f,0.64f, -0f,-0.31f,-0.75f,-0.2f,1.49f,0.64f)));
        dp(gl,0.12f,0.52f,0.2f, new ArrayList<>(Arrays.asList(-0f,-0.75f,-0.31f,-0.2f,1.49f,0.64f, -0f,-0.75f,-0.31f,0.2f,1.49f,0.64f, -0f,-0.75f,-0.31f,0.2f,1.43f,0.78f, -0f,-0.75f,-0.31f,-0.2f,1.43f,0.78f)));
        dp(gl,0.14f,0.51f,0.21f, new ArrayList<>(Arrays.asList(0f,-0.75f,0.31f,-0.2f,1.43f,0.78f, 0f,-0.75f,0.31f,0.2f,1.43f,0.78f, 0f,-0.75f,0.31f,0.2f,1.49f,0.93f, 0f,-0.75f,0.31f,-0.2f,1.49f,0.93f)));
        dp(gl,0.18f,0.52f,0.29f, new ArrayList<>(Arrays.asList(0f,-0.31f,0.75f,-0.2f,1.49f,0.93f, 0f,-0.31f,0.75f,0.2f,1.49f,0.93f, 0f,-0.31f,0.75f,0.2f,1.63f,0.99f, 0f,-0.31f,0.75f,-0.2f,1.63f,0.99f)));
        dp(gl,0.18f,0.52f,0.27f, new ArrayList<>(Arrays.asList(0f,0.31f,0.75f,-0.2f,1.63f,0.99f, 0f,0.31f,0.75f,0.2f,1.63f,0.99f, 0f,0.31f,0.75f,0.2f,1.77f,0.93f, 0f,0.31f,0.75f,-0.2f,1.77f,0.93f)));
        dp(gl,0.17f,0.55f,0.23f, new ArrayList<>(Arrays.asList(-0f,0.75f,0.31f,-0.2f,1.77f,0.93f, -0f,0.75f,0.31f,0.2f,1.77f,0.93f, -0f,0.75f,0.31f,0.2f,1.83f,0.78f, -0f,0.75f,0.31f,-0.2f,1.83f,0.78f)));
        dp(gl,0.11f,0.58f,0.21f, new ArrayList<>(Arrays.asList(0.82f,0f,-0f,0.2f,1.49f,0.64f, 0.82f,0f,-0f,0.2f,1.63f,0.58f, 0.82f,0f,-0f,0.2f,1.77f,0.64f, 0.82f,0f,-0f,0.2f,1.83f,0.78f, 0.82f,0f,-0f,0.2f,1.77f,0.93f, 0.82f,0f,-0f,0.2f,1.63f,0.99f, 0.82f,0f,-0f,0.2f,1.49f,0.93f, 0.82f,0f,-0f,0.2f,1.43f,0.78f)));
        dp(gl,0.13f,0.56f,0.24f, new ArrayList<>(Arrays.asList(-0f,0.75f,-0.31f,-0.2f,1.83f,0.78f, -0f,0.75f,-0.31f,0.2f,1.83f,0.78f, -0f,0.75f,-0.31f,0.2f,1.77f,0.64f, -0f,0.75f,-0.31f,-0.2f,1.77f,0.64f)));
        dp(gl,0.2f,0.53f,0.21f, new ArrayList<>(Arrays.asList(-0f,0.31f,-0.75f,-0.2f,1.77f,0.64f, -0f,0.31f,-0.75f,0.2f,1.77f,0.64f, -0f,0.31f,-0.75f,0.2f,1.63f,0.58f, -0f,0.31f,-0.75f,-0.2f,1.63f,0.58f)));
        dp(gl,0.13f,0.5f,0.25f, new ArrayList<>(Arrays.asList(-0.82f,-0f,0f,-0.2f,1.63f,0.58f, -0.82f,-0f,0f,-0.2f,1.49f,0.64f, -0.82f,-0f,0f,-0.2f,1.43f,0.78f, -0.82f,-0f,0f,-0.2f,1.49f,0.93f, -0.82f,-0f,0f,-0.2f,1.63f,0.99f, -0.82f,-0f,0f,-0.2f,1.77f,0.93f, -0.82f,-0f,0f,-0.2f,1.83f,0.78f, -0.82f,-0f,0f,-0.2f,1.77f,0.64f)));
        dp(gl,0.18f,0.51f,0.3f, new ArrayList<>(Arrays.asList(0.31f,0.75f,0.01f,0f,1.7f,1.04f, 0.31f,0.75f,0.01f,0f,1.69f,1.84f, 0.31f,0.75f,0.01f,0.04f,1.67f,1.84f, 0.31f,0.75f,0.01f,0.05f,1.68f,1.04f)));
        dp(gl,0.17f,0.56f,0.22f, new ArrayList<>(Arrays.asList(0.75f,0.31f,0.01f,0.05f,1.68f,1.04f, 0.75f,0.31f,0.01f,0.04f,1.67f,1.84f, 0.75f,0.31f,0.01f,0.06f,1.63f,1.84f, 0.75f,0.31f,0.01f,0.07f,1.63f,1.04f)));
        dp(gl,0.18f,0.56f,0.29f, new ArrayList<>(Arrays.asList(0.75f,-0.31f,0.01f,0.07f,1.63f,1.04f, 0.75f,-0.31f,0.01f,0.06f,1.63f,1.84f, 0.75f,-0.31f,0.01f,0.04f,1.59f,1.84f, 0.75f,-0.31f,0.01f,0.05f,1.58f,1.04f)));
        dp(gl,0.12f,0.55f,0.27f, new ArrayList<>(Arrays.asList(0.31f,-0.75f,0.01f,0.05f,1.58f,1.04f, 0.31f,-0.75f,0.01f,0.04f,1.59f,1.84f, 0.31f,-0.75f,0.01f,0f,1.57f,1.84f, 0.31f,-0.75f,0.01f,0f,1.56f,1.04f)));
        dp(gl,0.16f,0.53f,0.26f, new ArrayList<>(Arrays.asList(-0.31f,-0.75f,0.01f,0f,1.56f,1.04f, -0.31f,-0.75f,0.01f,0f,1.57f,1.84f, -0.31f,-0.75f,0.01f,-0.04f,1.59f,1.84f, -0.31f,-0.75f,0.01f,-0.05f,1.58f,1.04f)));
        dp(gl,0.19f,0.53f,0.25f, new ArrayList<>(Arrays.asList(-0.75f,-0.31f,0.01f,-0.05f,1.58f,1.04f, -0.75f,-0.31f,0.01f,-0.04f,1.59f,1.84f, -0.75f,-0.31f,0.01f,-0.06f,1.63f,1.84f, -0.75f,-0.31f,0.01f,-0.07f,1.63f,1.04f)));
        dp(gl,0.17f,0.59f,0.27f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,0f,1.69f,1.84f, 0f,0f,0.82f,-0.04f,1.67f,1.84f, 0f,0f,0.82f,-0.03f,1.66f,1.84f, 0f,0f,0.82f,0f,1.67f,1.84f)));
        dp(gl,0.17f,0.55f,0.27f, new ArrayList<>(Arrays.asList(-0.75f,0.31f,0.01f,-0.07f,1.63f,1.04f, -0.75f,0.31f,0.01f,-0.06f,1.63f,1.84f, -0.75f,0.31f,0.01f,-0.04f,1.67f,1.84f, -0.75f,0.31f,0.01f,-0.05f,1.68f,1.04f)));
        dp(gl,0.17f,0.51f,0.27f, new ArrayList<>(Arrays.asList(-0.31f,0.75f,0.01f,-0.05f,1.68f,1.04f, -0.31f,0.75f,0.01f,-0.04f,1.67f,1.84f, -0.31f,0.75f,0.01f,0f,1.69f,1.84f, -0.31f,0.75f,0.01f,0f,1.7f,1.04f)));
        dp(gl,0.14f,0.58f,0.22f, new ArrayList<>(Arrays.asList(0f,-0f,-0.82f,0f,1.7f,1.04f, 0f,-0f,-0.82f,0.05f,1.68f,1.04f, 0f,-0f,-0.82f,0.07f,1.63f,1.04f, 0f,-0f,-0.82f,0.05f,1.58f,1.04f, 0f,-0f,-0.82f,0f,1.56f,1.04f, 0f,-0f,-0.82f,-0.05f,1.58f,1.04f, 0f,-0f,-0.82f,-0.07f,1.63f,1.04f, 0f,-0f,-0.82f,-0.05f,1.68f,1.04f)));
        dp(gl,0.11f,0.53f,0.27f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,0.12f,1.55f,1.06f, 0f,0f,0.82f,0.12f,1.71f,1.06f, 0f,0f,0.82f,-0.12f,1.71f,1.06f, 0f,0f,0.82f,-0.12f,1.55f,1.06f)));
        dp(gl,0.18f,0.6f,0.25f, new ArrayList<>(Arrays.asList(-0f,-0f,-0.82f,-0.15f,1.52f,0.84f, -0f,-0f,-0.82f,-0.15f,1.74f,0.84f, -0f,-0f,-0.82f,0.15f,1.74f,0.84f, -0f,-0f,-0.82f,0.15f,1.52f,0.84f)));
        dp(gl,0.14f,0.6f,0.29f, new ArrayList<>(Arrays.asList(-0f,0.82f,-0f,0.15f,1.74f,0.84f, -0f,0.82f,-0f,-0.15f,1.74f,0.84f, -0f,0.82f,-0f,-0.15f,1.74f,1.03f, -0f,0.82f,-0f,0.15f,1.74f,1.03f)));
        dp(gl,0.18f,0.53f,0.26f, new ArrayList<>(Arrays.asList(-0.82f,-0f,0f,-0.15f,1.52f,1.03f, -0.82f,-0f,0f,-0.15f,1.74f,1.03f, -0.82f,-0f,0f,-0.15f,1.74f,0.84f, -0.82f,-0f,0f,-0.15f,1.52f,0.84f)));
        dp(gl,0.19f,0.51f,0.26f, new ArrayList<>(Arrays.asList(0.82f,0f,-0f,0.15f,1.52f,0.84f, 0.82f,0f,-0f,0.15f,1.74f,0.84f, 0.82f,0f,-0f,0.15f,1.74f,1.03f, 0.82f,0f,-0f,0.15f,1.52f,1.03f)));
        dp(gl,0.2f,0.57f,0.28f, new ArrayList<>(Arrays.asList(-0.58f,-0f,0.58f,-0.12f,1.55f,1.06f, -0.58f,-0f,0.58f,-0.12f,1.71f,1.06f, -0.58f,-0f,0.58f,-0.15f,1.74f,1.03f, -0.58f,-0f,0.58f,-0.15f,1.52f,1.03f)));
        dp(gl,0.13f,0.51f,0.29f, new ArrayList<>(Arrays.asList(0.58f,0f,0.58f,0.12f,1.71f,1.06f, 0.58f,0f,0.58f,0.12f,1.55f,1.06f, 0.58f,0f,0.58f,0.15f,1.52f,1.03f, 0.58f,0f,0.58f,0.15f,1.74f,1.03f)));
        dp(gl,0.13f,0.55f,0.21f, new ArrayList<>(Arrays.asList(0f,-0.58f,0.58f,-0.15f,1.52f,1.03f, 0f,-0.58f,0.58f,0.15f,1.52f,1.03f, 0f,-0.58f,0.58f,0.12f,1.55f,1.06f, 0f,-0.58f,0.58f,-0.12f,1.55f,1.06f)));
        dp(gl,0.2f,0.56f,0.29f, new ArrayList<>(Arrays.asList(0f,0.58f,0.58f,0.15f,1.74f,1.03f, 0f,0.58f,0.58f,-0.15f,1.74f,1.03f, 0f,0.58f,0.58f,-0.12f,1.71f,1.06f, 0f,0.58f,0.58f,0.12f,1.71f,1.06f)));
        dp(gl,0.11f,0.54f,0.22f, new ArrayList<>(Arrays.asList(0f,-0.82f,0f,-0.15f,1.52f,0.84f, 0f,-0.82f,0f,0.15f,1.52f,0.84f, 0f,-0.82f,0f,0.15f,1.52f,1.03f, 0f,-0.82f,0f,-0.15f,1.52f,1.03f)));
        dp(gl,0.12f,0.58f,0.22f, new ArrayList<>(Arrays.asList(0.31f,-0.75f,0f,0f,1.67f,1.84f, 0.31f,-0.75f,0f,-0.03f,1.66f,1.84f, 0.31f,-0.75f,0f,-0.03f,1.66f,1.81f, 0.31f,-0.75f,0f,0f,1.67f,1.81f)));
        dp(gl,0.11f,0.53f,0.21f, new ArrayList<>(Arrays.asList(-0f,0f,0.82f,0f,1.57f,1.84f, -0f,0f,0.82f,0.04f,1.59f,1.84f, -0f,0f,0.82f,0.03f,1.6f,1.84f, -0f,0f,0.82f,0f,1.59f,1.84f)));
        dp(gl,0.11f,0.54f,0.29f, new ArrayList<>(Arrays.asList(-0f,0f,0.82f,-0.04f,1.67f,1.84f, -0f,0f,0.82f,-0.06f,1.63f,1.84f, -0f,0f,0.82f,-0.04f,1.63f,1.84f, -0f,0f,0.82f,-0.03f,1.66f,1.84f)));
        dp(gl,0.12f,0.6f,0.22f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,0.04f,1.59f,1.84f, 0f,0f,0.82f,0.06f,1.63f,1.84f, 0f,0f,0.82f,0.04f,1.63f,1.84f, 0f,0f,0.82f,0.03f,1.6f,1.84f)));
        dp(gl,0.15f,0.59f,0.25f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,-0.06f,1.63f,1.84f, 0f,0f,0.82f,-0.04f,1.59f,1.84f, 0f,0f,0.82f,-0.03f,1.6f,1.84f, 0f,0f,0.82f,-0.04f,1.63f,1.84f)));
        dp(gl,0.1f,0.54f,0.3f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,0.06f,1.63f,1.84f, 0f,0f,0.82f,0.04f,1.67f,1.84f, 0f,0f,0.82f,0.03f,1.66f,1.84f, 0f,0f,0.82f,0.04f,1.63f,1.84f)));
        dp(gl,0.2f,0.58f,0.26f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,0.04f,1.67f,1.84f, 0f,0f,0.82f,0f,1.69f,1.84f, 0f,0f,0.82f,0f,1.67f,1.84f, 0f,0f,0.82f,0.03f,1.66f,1.84f)));
        dp(gl,0.12f,0.51f,0.23f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,-0.04f,1.59f,1.84f, 0f,0f,0.82f,0f,1.57f,1.84f, 0f,0f,0.82f,0f,1.59f,1.84f, 0f,0f,0.82f,-0.03f,1.6f,1.84f)));
        dp(gl,0.18f,0.52f,0.22f, new ArrayList<>(Arrays.asList(-0.31f,0.75f,0f,0f,1.59f,1.84f, -0.31f,0.75f,0f,0.03f,1.6f,1.84f, -0.31f,0.75f,0f,0.03f,1.6f,1.81f, -0.31f,0.75f,0f,0f,1.59f,1.81f)));
        dp(gl,0.2f,0.58f,0.25f, new ArrayList<>(Arrays.asList(0.31f,0.75f,0f,-0.03f,1.6f,1.84f, 0.31f,0.75f,0f,0f,1.59f,1.84f, 0.31f,0.75f,0f,0f,1.59f,1.81f, 0.31f,0.75f,0f,-0.03f,1.6f,1.81f)));
        dp(gl,0.12f,0.54f,0.29f, new ArrayList<>(Arrays.asList(-0.31f,-0.75f,0f,0.03f,1.66f,1.84f, -0.31f,-0.75f,0f,0f,1.67f,1.84f, -0.31f,-0.75f,0f,0f,1.67f,1.81f, -0.31f,-0.75f,0f,0.03f,1.66f,1.81f)));
        dp(gl,0.17f,0.57f,0.22f, new ArrayList<>(Arrays.asList(0.75f,0.31f,0f,-0.04f,1.63f,1.84f, 0.75f,0.31f,0f,-0.03f,1.6f,1.84f, 0.75f,0.31f,0f,-0.03f,1.6f,1.81f, 0.75f,0.31f,0f,-0.04f,1.63f,1.81f)));
        dp(gl,0.12f,0.56f,0.24f, new ArrayList<>(Arrays.asList(-0.75f,-0.31f,0f,0.04f,1.63f,1.84f, -0.75f,-0.31f,0f,0.03f,1.66f,1.84f, -0.75f,-0.31f,0f,0.03f,1.66f,1.81f, -0.75f,-0.31f,0f,0.04f,1.63f,1.81f)));
        dp(gl,0.18f,0.57f,0.25f, new ArrayList<>(Arrays.asList(0.75f,-0.31f,0f,-0.03f,1.66f,1.84f, 0.75f,-0.31f,0f,-0.04f,1.63f,1.84f, 0.75f,-0.31f,0f,-0.04f,1.63f,1.81f, 0.75f,-0.31f,0f,-0.03f,1.66f,1.81f)));
        dp(gl,0.17f,0.54f,0.21f, new ArrayList<>(Arrays.asList(-0.75f,0.31f,0f,0.03f,1.6f,1.84f, -0.75f,0.31f,0f,0.04f,1.63f,1.84f, -0.75f,0.31f,0f,0.04f,1.63f,1.81f, -0.75f,0.31f,0f,0.03f,1.6f,1.81f)));
    }

    void drawLeftTrack(GL2 gl){
        toggleEdgeClipping(false);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
        dp(gl,0.61f,0.62f,0.8f, new ArrayList<>(Arrays.asList(0f,0f,0.82f,0.03f,1.66f,1.81f, 0f,0f,0.82f,0f,1.67f,1.81f, 0f,0f,0.82f,-0.03f,1.66f,1.81f, 0f,0f,0.82f,-0.04f,1.63f,1.81f, 0f,0f,0.82f,-0.03f,1.6f,1.81f, 0f,0f,0.82f,0f,1.59f,1.81f, 0f,0f,0.82f,0.03f,1.6f,1.81f, 0f,0f,0.82f,0.04f,1.63f,1.81f)));
        dp(gl,0.62f,0.6f,0.85f, new ArrayList<>(Arrays.asList(0f,4.12f,-0.2f,0.53f,0.89f,0.9f, 0f,4.12f,-0.2f,1.09f,0.89f,0.9f, 0f,4.12f,-0.2f,1.09f,0.87f,0.61f, 0f,4.12f,-0.2f,0.53f,0.87f,0.61f)));
        dp(gl,0.62f,0.64f,0.82f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.22f,0.53f,0.87f,0.61f, -0f,4.12f,-0.22f,1.09f,0.87f,0.61f, -0f,4.12f,-0.22f,1.09f,0.86f,0.32f, -0f,4.12f,-0.22f,0.53f,0.86f,0.32f)));
        dp(gl,0.61f,0.6f,0.86f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.23f,0.53f,0.86f,0.32f, -0f,4.12f,-0.23f,1.09f,0.86f,0.32f, -0f,4.12f,-0.23f,1.09f,0.84f,0.03f, -0f,4.12f,-0.23f,0.53f,0.84f,0.03f)));
        dp(gl,0.61f,0.63f,0.84f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.23f,0.53f,0.84f,0.03f, -0f,4.12f,-0.23f,1.09f,0.84f,0.03f, -0f,4.12f,-0.23f,1.09f,0.83f,-0.27f, -0f,4.12f,-0.23f,0.53f,0.83f,-0.27f)));
        dp(gl,0.63f,0.65f,0.84f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.23f,0.53f,0.83f,-0.27f, -0f,4.12f,-0.23f,1.09f,0.83f,-0.27f, -0f,4.12f,-0.23f,1.09f,0.81f,-0.56f, -0f,4.12f,-0.23f,0.53f,0.81f,-0.56f)));
        dp(gl,0.65f,0.63f,0.83f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.22f,0.53f,0.81f,-0.56f, -0f,4.12f,-0.22f,1.09f,0.81f,-0.56f, -0f,4.12f,-0.22f,1.09f,0.79f,-0.86f, -0f,4.12f,-0.22f,0.53f,0.79f,-0.86f)));
        dp(gl,0.64f,0.62f,0.82f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.2f,0.53f,0.79f,-0.86f, -0f,4.12f,-0.2f,1.09f,0.79f,-0.86f, -0f,4.12f,-0.2f,1.09f,0.78f,-1.16f, -0f,4.12f,-0.2f,0.53f,0.78f,-1.16f)));
        dp(gl,0.64f,0.61f,0.83f, new ArrayList<>(Arrays.asList(-0f,4.07f,-0.65f,0.53f,0.78f,-1.16f, -0f,4.07f,-0.65f,1.09f,0.78f,-1.16f, -0f,4.07f,-0.65f,1.09f,0.73f,-1.45f, -0f,4.07f,-0.65f,0.53f,0.73f,-1.45f)));
        dp(gl,0.6f,0.6f,0.87f, new ArrayList<>(Arrays.asList(0f,1.71f,-3.75f,0.53f,0.73f,-1.45f, 0f,1.71f,-3.75f,1.09f,0.73f,-1.45f, 0f,1.71f,-3.75f,1.09f,0.48f,-1.56f, 0f,1.71f,-3.75f,0.53f,0.48f,-1.56f)));
        dp(gl,0.62f,0.64f,0.87f, new ArrayList<>(Arrays.asList(0f,-2.35f,-3.39f,0.53f,0.48f,-1.56f, 0f,-2.35f,-3.39f,1.09f,0.48f,-1.56f, 0f,-2.35f,-3.39f,1.09f,0.24f,-1.4f, 0f,-2.35f,-3.39f,0.53f,0.24f,-1.4f)));
        dp(gl,0.63f,0.62f,0.87f, new ArrayList<>(Arrays.asList(-0f,-3.58f,-2.06f,0.53f,0.24f,-1.4f, -0f,-3.58f,-2.06f,1.09f,0.24f,-1.4f, -0f,-3.58f,-2.06f,1.09f,0.1f,-1.14f, -0f,-3.58f,-2.06f,0.53f,0.1f,-1.14f)));
        dp(gl,0.65f,0.63f,0.81f, new ArrayList<>(Arrays.asList(-0f,-3.94f,-1.22f,0.53f,0.1f,-1.14f, -0f,-3.94f,-1.22f,1.09f,0.1f,-1.14f, -0f,-3.94f,-1.22f,1.09f,0.01f,-0.87f, -0f,-3.94f,-1.22f,0.53f,0.01f,-0.87f)));
        dp(gl,0.63f,0.6f,0.81f, new ArrayList<>(Arrays.asList(0f,-4.13f,-0.07f,0.53f,0.01f,-0.87f, 0f,-4.13f,-0.07f,1.09f,0.01f,-0.87f, 0f,-4.13f,-0.07f,1.09f,0f,-0.57f, 0f,-4.13f,-0.07f,0.53f,0f,-0.57f)));
        dp(gl,0.62f,0.61f,0.83f, new ArrayList<>(Arrays.asList(0f,-4.13f,-0.01f,0.53f,0f,-0.57f, 0f,-4.13f,-0.01f,1.09f,0f,-0.57f, 0f,-4.13f,-0.01f,1.09f,0f,-0.27f, 0f,-4.13f,-0.01f,0.53f,0f,-0.27f)));
        dp(gl,0.6f,0.62f,0.83f, new ArrayList<>(Arrays.asList(0f,-4.13f,-0f,0.53f,0f,-0.27f, 0f,-4.13f,-0f,1.09f,0f,-0.27f, 0f,-4.13f,-0f,1.09f,0f,0.02f, 0f,-4.13f,-0f,0.53f,0f,0.02f)));
        dp(gl,0.63f,0.62f,0.88f, new ArrayList<>(Arrays.asList(0f,-4.13f,0.01f,0.53f,0f,0.02f, 0f,-4.13f,0.01f,1.09f,0f,0.02f, 0f,-4.13f,0.01f,1.09f,0f,0.32f, 0f,-4.13f,0.01f,0.53f,0f,0.32f)));
        dp(gl,0.61f,0.61f,0.8f, new ArrayList<>(Arrays.asList(0f,-4.13f,0.02f,0.53f,0f,0.32f, 0f,-4.13f,0.02f,1.09f,0f,0.32f, 0f,-4.13f,0.02f,1.09f,0.01f,0.62f, 0f,-4.13f,0.02f,0.53f,0.01f,0.62f)));
        dp(gl,0.64f,0.65f,0.84f, new ArrayList<>(Arrays.asList(0f,-4.13f,0.06f,0.53f,0.01f,0.62f, 0f,-4.13f,0.06f,1.09f,0.01f,0.62f, 0f,-4.13f,0.06f,1.09f,0.01f,0.91f, 0f,-4.13f,0.06f,0.53f,0.01f,0.91f)));
        dp(gl,0.65f,0.61f,0.89f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.26f,0.53f,0.01f,0.91f, 0f,-4.12f,0.26f,1.09f,0.01f,0.91f, 0f,-4.12f,0.26f,1.09f,0.03f,1.21f, 0f,-4.12f,0.26f,0.53f,0.03f,1.21f)));
        dp(gl,0.62f,0.62f,0.89f, new ArrayList<>(Arrays.asList(0f,-3.5f,2.18f,0.53f,0.03f,1.21f, 0f,-3.5f,2.18f,1.09f,0.03f,1.21f, 0f,-3.5f,2.18f,1.09f,0.18f,1.46f, 0f,-3.5f,2.18f,0.53f,0.18f,1.46f)));
        dp(gl,0.64f,0.61f,0.89f, new ArrayList<>(Arrays.asList(0f,-2.81f,3.02f,0.53f,0.18f,1.46f, 0f,-2.81f,3.02f,1.09f,0.18f,1.46f, 0f,-2.81f,3.02f,1.09f,0.4f,1.66f, 0f,-2.81f,3.02f,0.53f,0.4f,1.66f)));
        dp(gl,0.62f,0.65f,0.89f, new ArrayList<>(Arrays.asList(0f,-0.58f,4.08f,0.53f,0.4f,1.66f, 0f,-0.58f,4.08f,1.09f,0.4f,1.66f, 0f,-0.58f,4.08f,1.09f,0.69f,1.7f, 0f,-0.58f,4.08f,0.53f,0.69f,1.7f)));
        dp(gl,0.62f,0.62f,0.85f, new ArrayList<>(Arrays.asList(0.03f,2.78f,3.05f,0.53f,0.69f,1.7f, 0.03f,2.78f,3.05f,1.09f,0.69f,1.7f, 0.03f,2.78f,3.05f,1.09f,0.89f,1.51f, 0.03f,2.78f,3.05f,0.53f,0.89f,1.52f)));
        dp(gl,0.63f,0.65f,0.82f, new ArrayList<>(Arrays.asList(-0f,4.12f,0.14f,0.53f,0.89f,1.52f, -0f,4.12f,0.14f,1.09f,0.89f,1.51f, -0f,4.12f,0.14f,1.09f,0.9f,1.2f, -0f,4.12f,0.14f,0.53f,0.9f,1.23f)));
        dp(gl,0.63f,0.63f,0.87f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.14f,0.53f,0.9f,1.23f, -0f,4.12f,-0.14f,1.09f,0.9f,1.2f, -0f,4.12f,-0.14f,1.09f,0.89f,0.9f, -0f,4.12f,-0.14f,0.53f,0.89f,0.9f)));
        dp(gl,0.62f,0.63f,0.81f, new ArrayList<>(Arrays.asList(-0f,-4.12f,0.2f,0.53f,0.85f,0.9f, -0f,-4.12f,0.2f,0.53f,0.84f,0.61f, -0f,-4.12f,0.2f,1.09f,0.84f,0.61f, -0f,-4.12f,0.2f,1.09f,0.85f,0.9f)));
        dp(gl,0.65f,0.62f,0.86f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.22f,0.53f,0.84f,0.61f, 0f,-4.12f,0.22f,0.53f,0.82f,0.33f, 0f,-4.12f,0.22f,1.09f,0.82f,0.33f, 0f,-4.12f,0.22f,1.09f,0.84f,0.61f)));
        dp(gl,0.62f,0.61f,0.85f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.23f,0.53f,0.82f,0.33f, 0f,-4.12f,0.23f,0.53f,0.81f,0.03f, 0f,-4.12f,0.23f,1.09f,0.81f,0.03f, 0f,-4.12f,0.23f,1.09f,0.82f,0.33f)));
        dp(gl,0.63f,0.62f,0.89f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.23f,0.53f,0.81f,0.03f, 0f,-4.12f,0.23f,0.53f,0.79f,-0.27f, 0f,-4.12f,0.23f,1.09f,0.79f,-0.27f, 0f,-4.12f,0.23f,1.09f,0.81f,0.03f)));
        dp(gl,0.63f,0.64f,0.85f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.23f,0.53f,0.79f,-0.27f, 0f,-4.12f,0.23f,0.53f,0.77f,-0.56f, 0f,-4.12f,0.23f,1.09f,0.77f,-0.56f, 0f,-4.12f,0.23f,1.09f,0.79f,-0.27f)));
        dp(gl,0.61f,0.62f,0.84f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.22f,0.53f,0.77f,-0.56f, 0f,-4.12f,0.22f,0.53f,0.76f,-0.86f, 0f,-4.12f,0.22f,1.09f,0.76f,-0.86f, 0f,-4.12f,0.22f,1.09f,0.77f,-0.56f)));
        dp(gl,0.6f,0.62f,0.85f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.2f,0.53f,0.76f,-0.86f, 0f,-4.12f,0.2f,0.53f,0.74f,-1.15f, 0f,-4.12f,0.2f,1.09f,0.74f,-1.15f, 0f,-4.12f,0.2f,1.09f,0.76f,-0.86f)));
        dp(gl,0.62f,0.61f,0.8f, new ArrayList<>(Arrays.asList(0f,-4.08f,0.59f,0.53f,0.74f,-1.15f, 0f,-4.08f,0.59f,0.53f,0.7f,-1.42f, 0f,-4.08f,0.59f,1.09f,0.7f,-1.42f, 0f,-4.08f,0.59f,1.09f,0.74f,-1.15f)));
        dp(gl,0.64f,0.62f,0.88f, new ArrayList<>(Arrays.asList(-0f,-1.72f,3.75f,0.53f,0.7f,-1.42f, -0f,-1.72f,3.75f,0.53f,0.48f,-1.53f, -0f,-1.72f,3.75f,1.09f,0.48f,-1.53f, -0f,-1.72f,3.75f,1.09f,0.7f,-1.42f)));
        dp(gl,0.64f,0.63f,0.88f, new ArrayList<>(Arrays.asList(0f,2.39f,3.36f,0.53f,0.48f,-1.53f, 0f,2.39f,3.36f,0.53f,0.27f,-1.38f, 0f,2.39f,3.36f,1.09f,0.27f,-1.38f, 0f,2.39f,3.36f,1.09f,0.48f,-1.53f)));
        dp(gl,0.62f,0.63f,0.82f, new ArrayList<>(Arrays.asList(0f,3.58f,2.05f,0.53f,0.27f,-1.38f, 0f,3.58f,2.05f,0.53f,0.13f,-1.13f, 0f,3.58f,2.05f,1.09f,0.13f,-1.13f, 0f,3.58f,2.05f,1.09f,0.27f,-1.38f)));
        dp(gl,0.61f,0.63f,0.88f, new ArrayList<>(Arrays.asList(0f,3.94f,1.22f,0.53f,0.13f,-1.13f, 0f,3.94f,1.22f,0.53f,0.04f,-0.86f, 0f,3.94f,1.22f,1.09f,0.04f,-0.86f, 0f,3.94f,1.22f,1.09f,0.13f,-1.13f)));
        dp(gl,0.61f,0.64f,0.81f, new ArrayList<>(Arrays.asList(-0f,4.13f,0.06f,0.53f,0.04f,-0.86f, -0f,4.13f,0.06f,0.53f,0.04f,-0.57f, -0f,4.13f,0.06f,1.09f,0.04f,-0.57f, -0f,4.13f,0.06f,1.09f,0.04f,-0.86f)));
        dp(gl,0.62f,0.64f,0.88f, new ArrayList<>(Arrays.asList(-0f,4.13f,0.01f,0.53f,0.04f,-0.57f, -0f,4.13f,0.01f,0.53f,0.04f,-0.27f, -0f,4.13f,0.01f,1.09f,0.04f,-0.27f, -0f,4.13f,0.01f,1.09f,0.04f,-0.57f)));
        dp(gl,0.62f,0.6f,0.86f, new ArrayList<>(Arrays.asList(-0f,4.13f,0f,0.53f,0.04f,-0.27f, -0f,4.13f,0f,0.53f,0.04f,0.02f, -0f,4.13f,0f,1.09f,0.04f,0.02f, -0f,4.13f,0f,1.09f,0.04f,-0.27f)));
        dp(gl,0.64f,0.64f,0.8f, new ArrayList<>(Arrays.asList(0f,4.13f,-0.01f,0.53f,0.04f,0.02f, 0f,4.13f,-0.01f,0.53f,0.04f,0.32f, 0f,4.13f,-0.01f,1.09f,0.04f,0.32f, 0f,4.13f,-0.01f,1.09f,0.04f,0.02f)));
        dp(gl,0.63f,0.63f,0.85f, new ArrayList<>(Arrays.asList(-0f,4.13f,-0.02f,0.53f,0.04f,0.32f, -0f,4.13f,-0.02f,0.53f,0.04f,0.62f, -0f,4.13f,-0.02f,1.09f,0.04f,0.62f, -0f,4.13f,-0.02f,1.09f,0.04f,0.32f)));
        dp(gl,0.61f,0.62f,0.85f, new ArrayList<>(Arrays.asList(-0f,4.13f,-0.06f,0.53f,0.04f,0.62f, -0f,4.13f,-0.06f,0.53f,0.05f,0.91f, -0f,4.13f,-0.06f,1.09f,0.05f,0.91f, -0f,4.13f,-0.06f,1.09f,0.04f,0.62f)));
        dp(gl,0.63f,0.62f,0.82f, new ArrayList<>(Arrays.asList(-0f,4.12f,-0.25f,0.53f,0.05f,0.91f, -0f,4.12f,-0.25f,0.53f,0.06f,1.2f, -0f,4.12f,-0.25f,1.09f,0.06f,1.2f, -0f,4.12f,-0.25f,1.09f,0.05f,0.91f)));
        dp(gl,0.6f,0.62f,0.88f, new ArrayList<>(Arrays.asList(0f,3.5f,-2.19f,0.53f,0.06f,1.2f, 0f,3.5f,-2.19f,0.53f,0.21f,1.43f, 0f,3.5f,-2.19f,1.09f,0.21f,1.43f, 0f,3.5f,-2.19f,1.09f,0.06f,1.2f)));
        dp(gl,0.62f,0.64f,0.9f, new ArrayList<>(Arrays.asList(0f,2.82f,-3.01f,0.53f,0.21f,1.43f, 0f,2.82f,-3.01f,0.53f,0.41f,1.63f, 0f,2.82f,-3.01f,1.09f,0.41f,1.63f, 0f,2.82f,-3.01f,1.09f,0.21f,1.43f)));
        dp(gl,0.62f,0.62f,0.82f, new ArrayList<>(Arrays.asList(0f,0.61f,-4.08f,0.53f,0.41f,1.63f, 0f,0.61f,-4.08f,0.53f,0.68f,1.66f, 0f,0.61f,-4.08f,1.09f,0.68f,1.66f, 0f,0.61f,-4.08f,1.09f,0.41f,1.63f)));
        dp(gl,0.63f,0.63f,0.81f, new ArrayList<>(Arrays.asList(-0.03f,-2.79f,-3.04f,0.53f,0.68f,1.66f, -0.03f,-2.79f,-3.04f,0.53f,0.85f,1.51f, -0.03f,-2.79f,-3.04f,1.09f,0.86f,1.49f, -0.03f,-2.79f,-3.04f,1.09f,0.68f,1.66f)));
        dp(gl,0.62f,0.61f,0.82f, new ArrayList<>(Arrays.asList(0f,-4.12f,-0.1f,0.53f,0.85f,1.51f, 0f,-4.12f,-0.1f,0.53f,0.86f,1.23f, 0f,-4.12f,-0.1f,1.09f,0.86f,1.2f, 0f,-4.12f,-0.1f,1.09f,0.86f,1.49f)));
        dp(gl,0.61f,0.61f,0.8f, new ArrayList<>(Arrays.asList(0f,-4.12f,0.14f,0.53f,0.86f,1.23f, 0f,-4.12f,0.14f,0.53f,0.85f,0.9f, 0f,-4.12f,0.14f,1.09f,0.85f,0.9f, 0f,-4.12f,0.14f,1.09f,0.86f,1.2f)));
        dp(gl,0.62f,0.64f,0.88f, new ArrayList<>(Arrays.asList(-4.13f,-0f,0f,0.53f,0.89f,0.9f, -4.13f,-0f,0f,0.53f,0.87f,0.61f, -4.13f,-0f,0f,0.53f,0.84f,0.61f, -4.13f,-0f,0f,0.53f,0.85f,0.9f)));
        dp(gl,0.62f,0.6f,0.86f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0.87f,0.61f, 4.13f,0f,-0f,1.09f,0.89f,0.9f, 4.13f,0f,-0f,1.09f,0.85f,0.9f, 4.13f,0f,-0f,1.09f,0.84f,0.61f)));
        dp(gl,0.61f,0.63f,0.8f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.87f,0.61f, -4.13f,0f,0f,0.53f,0.86f,0.32f, -4.13f,0f,0f,0.53f,0.82f,0.33f, -4.13f,0f,0f,0.53f,0.84f,0.61f)));
        dp(gl,0.65f,0.62f,0.86f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.24f,-1.4f, 4.13f,0f,0f,1.09f,0.48f,-1.56f, 4.13f,0f,0f,1.09f,0.48f,-1.53f, 4.13f,0f,0f,1.09f,0.27f,-1.38f)));
        dp(gl,0.63f,0.61f,0.9f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.83f,-0.27f, 4.13f,0f,0f,1.09f,0.84f,0.03f, 4.13f,0f,0f,1.09f,0.81f,0.03f, 4.13f,0f,0f,1.09f,0.79f,-0.27f)));
        dp(gl,0.65f,0.64f,0.87f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.83f,-0.27f, -4.13f,0f,0f,0.53f,0.81f,-0.56f, -4.13f,0f,0f,0.53f,0.77f,-0.56f, -4.13f,0f,0f,0.53f,0.79f,-0.27f)));
        dp(gl,0.6f,0.62f,0.85f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0.81f,-0.56f, 4.13f,0f,-0f,1.09f,0.83f,-0.27f, 4.13f,0f,-0f,1.09f,0.79f,-0.27f, 4.13f,0f,-0f,1.09f,0.77f,-0.56f)));
        dp(gl,0.63f,0.61f,0.84f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0f,0.53f,0.81f,-0.56f, -4.13f,0f,-0f,0.53f,0.79f,-0.86f, -4.13f,0f,-0f,0.53f,0.76f,-0.86f, -4.13f,0f,-0f,0.53f,0.77f,-0.56f)));
        dp(gl,0.61f,0.64f,0.84f, new ArrayList<>(Arrays.asList(4.13f,-0f,0f,1.09f,0.79f,-0.86f, 4.13f,-0f,0f,1.09f,0.81f,-0.56f, 4.13f,-0f,0f,1.09f,0.77f,-0.56f, 4.13f,-0f,0f,1.09f,0.76f,-0.86f)));
        dp(gl,0.62f,0.64f,0.89f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.79f,-0.86f, -4.13f,0f,0f,0.53f,0.78f,-1.16f, -4.13f,0f,0f,0.53f,0.74f,-1.15f, -4.13f,0f,0f,0.53f,0.76f,-0.86f)));
        dp(gl,0.65f,0.64f,0.82f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.78f,-1.16f, 4.13f,0f,0f,1.09f,0.79f,-0.86f, 4.13f,0f,0f,1.09f,0.76f,-0.86f, 4.13f,0f,0f,1.09f,0.74f,-1.15f)));
        dp(gl,0.64f,0.61f,0.83f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.78f,-1.16f, -4.13f,0f,0f,0.53f,0.73f,-1.45f, -4.13f,0f,0f,0.53f,0.7f,-1.42f, -4.13f,0f,0f,0.53f,0.74f,-1.15f)));
        dp(gl,0.65f,0.61f,0.87f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.73f,-1.45f, 4.13f,0f,0f,1.09f,0.78f,-1.16f, 4.13f,0f,0f,1.09f,0.74f,-1.15f, 4.13f,0f,0f,1.09f,0.7f,-1.42f)));
        dp(gl,0.65f,0.61f,0.88f, new ArrayList<>(Arrays.asList(-4.13f,0f,-0f,0.53f,0.73f,-1.45f, -4.13f,0f,-0f,0.53f,0.48f,-1.56f, -4.13f,0f,-0f,0.53f,0.48f,-1.53f, -4.13f,0f,-0f,0.53f,0.7f,-1.42f)));
        dp(gl,0.63f,0.62f,0.87f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.48f,-1.56f, 4.13f,0f,0f,1.09f,0.73f,-1.45f, 4.13f,0f,0f,1.09f,0.7f,-1.42f, 4.13f,0f,0f,1.09f,0.48f,-1.53f)));
        dp(gl,0.64f,0.63f,0.82f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.1f,-1.14f, 4.13f,0f,0f,1.09f,0.24f,-1.4f, 4.13f,0f,0f,1.09f,0.27f,-1.38f, 4.13f,0f,0f,1.09f,0.13f,-1.13f)));
        dp(gl,0.6f,0.62f,0.86f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0.01f,-0.87f, 4.13f,0f,-0f,1.09f,0.1f,-1.14f, 4.13f,0f,-0f,1.09f,0.13f,-1.13f, 4.13f,0f,-0f,1.09f,0.04f,-0.86f)));
        dp(gl,0.64f,0.63f,0.87f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.48f,-1.56f, -4.13f,0f,0f,0.53f,0.24f,-1.4f, -4.13f,0f,0f,0.53f,0.27f,-1.38f, -4.13f,0f,0f,0.53f,0.48f,-1.53f)));
        dp(gl,0.64f,0.63f,0.81f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0f,-0.57f, 4.13f,0f,0f,1.09f,0.01f,-0.87f, 4.13f,0f,0f,1.09f,0.04f,-0.86f, 4.13f,0f,0f,1.09f,0.04f,-0.57f)));
        dp(gl,0.61f,0.64f,0.89f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.24f,-1.4f, -4.13f,0f,0f,0.53f,0.1f,-1.14f, -4.13f,0f,0f,0.53f,0.13f,-1.13f, -4.13f,0f,0f,0.53f,0.27f,-1.38f)));
        dp(gl,0.65f,0.61f,0.89f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0.03f,1.21f, 4.13f,0f,-0f,1.09f,0.01f,0.91f, 4.13f,0f,-0f,1.09f,0.05f,0.91f, 4.13f,0f,-0f,1.09f,0.06f,1.2f)));
        dp(gl,0.63f,0.61f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.1f,-1.14f, -4.13f,0f,0f,0.53f,0.01f,-0.87f, -4.13f,0f,0f,0.53f,0.04f,-0.86f, -4.13f,0f,0f,0.53f,0.13f,-1.13f)));
        dp(gl,0.62f,0.63f,0.82f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0f,-0.57f, -4.13f,0f,0f,0.53f,0f,-0.27f, -4.13f,0f,0f,0.53f,0.04f,-0.27f, -4.13f,0f,0f,0.53f,0.04f,-0.57f)));
        dp(gl,0.63f,0.62f,0.89f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.01f,-0.87f, -4.13f,0f,0f,0.53f,0f,-0.57f, -4.13f,0f,0f,0.53f,0.04f,-0.57f, -4.13f,0f,0f,0.53f,0.04f,-0.86f)));
        dp(gl,0.62f,0.61f,0.87f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0f,-0.27f, 4.13f,0f,-0f,1.09f,0f,-0.57f, 4.13f,0f,-0f,1.09f,0.04f,-0.57f, 4.13f,0f,-0f,1.09f,0.04f,-0.27f)));
        dp(gl,0.65f,0.62f,0.8f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0f,-0.27f, -4.13f,0f,0f,0.53f,0f,0.02f, -4.13f,0f,0f,0.53f,0.04f,0.02f, -4.13f,0f,0f,0.53f,0.04f,-0.27f)));
        dp(gl,0.61f,0.64f,0.88f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0f,0.02f, 4.13f,0f,0f,1.09f,0f,-0.27f, 4.13f,0f,0f,1.09f,0.04f,-0.27f, 4.13f,0f,0f,1.09f,0.04f,0.02f)));
        dp(gl,0.61f,0.61f,0.86f, new ArrayList<>(Arrays.asList(-4.13f,-0f,-0f,0.53f,0f,0.02f, -4.13f,-0f,-0f,0.53f,0f,0.32f, -4.13f,-0f,-0f,0.53f,0.04f,0.32f, -4.13f,-0f,-0f,0.53f,0.04f,0.02f)));
        dp(gl,0.65f,0.61f,0.84f, new ArrayList<>(Arrays.asList(4.13f,-0f,-0f,1.09f,0f,0.32f, 4.13f,-0f,-0f,1.09f,0f,0.02f, 4.13f,-0f,-0f,1.09f,0.04f,0.02f, 4.13f,-0f,-0f,1.09f,0.04f,0.32f)));
        dp(gl,0.62f,0.62f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0f,0.32f, -4.13f,0f,0f,0.53f,0.01f,0.62f, -4.13f,0f,0f,0.53f,0.04f,0.62f, -4.13f,0f,0f,0.53f,0.04f,0.32f)));
        dp(gl,0.62f,0.63f,0.87f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.01f,0.62f, 4.13f,0f,0f,1.09f,0f,0.32f, 4.13f,0f,0f,1.09f,0.04f,0.32f, 4.13f,0f,0f,1.09f,0.04f,0.62f)));
        dp(gl,0.63f,0.64f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.01f,0.62f, -4.13f,0f,0f,0.53f,0.01f,0.91f, -4.13f,0f,0f,0.53f,0.05f,0.91f, -4.13f,0f,0f,0.53f,0.04f,0.62f)));
        dp(gl,0.64f,0.64f,0.88f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.01f,0.91f, 4.13f,0f,0f,1.09f,0.01f,0.62f, 4.13f,0f,0f,1.09f,0.04f,0.62f, 4.13f,0f,0f,1.09f,0.05f,0.91f)));
        dp(gl,0.63f,0.62f,0.89f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.4f,1.66f, 4.13f,0f,0f,1.09f,0.18f,1.46f, 4.13f,0f,0f,1.09f,0.21f,1.43f, 4.13f,0f,0f,1.09f,0.41f,1.63f)));
        dp(gl,0.63f,0.61f,0.82f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.03f,1.21f, -4.13f,0f,0f,0.53f,0.18f,1.46f, -4.13f,0f,0f,0.53f,0.21f,1.43f, -4.13f,0f,0f,0.53f,0.06f,1.2f)));
        dp(gl,0.6f,0.6f,0.83f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.01f,0.91f, -4.13f,0f,0f,0.53f,0.03f,1.21f, -4.13f,0f,0f,0.53f,0.06f,1.2f, -4.13f,0f,0f,0.53f,0.05f,0.91f)));
        dp(gl,0.64f,0.6f,0.84f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0.18f,1.46f, 4.13f,0f,-0f,1.09f,0.03f,1.21f, 4.13f,0f,-0f,1.09f,0.06f,1.2f, 4.13f,0f,-0f,1.09f,0.21f,1.43f)));
        dp(gl,0.64f,0.63f,0.87f, new ArrayList<>(Arrays.asList(4.13f,-0.01f,-0.01f,1.09f,0.89f,1.51f, 4.13f,-0.01f,-0.01f,1.09f,0.69f,1.7f, 4.13f,-0.01f,-0.01f,1.09f,0.68f,1.66f, 4.13f,-0.01f,-0.01f,1.09f,0.86f,1.49f)));
        dp(gl,0.61f,0.62f,0.89f, new ArrayList<>(Arrays.asList(-4.13f,-0f,0.01f,0.53f,0.4f,1.66f, -4.13f,-0f,0.01f,0.53f,0.69f,1.7f, -4.13f,-0f,0.01f,0.53f,0.68f,1.66f, -4.13f,-0f,0.01f,0.53f,0.41f,1.63f)));
        dp(gl,0.61f,0.63f,0.83f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.18f,1.46f, -4.13f,0f,0f,0.53f,0.4f,1.66f, -4.13f,0f,0f,0.53f,0.41f,1.63f, -4.13f,0f,0f,0.53f,0.21f,1.43f)));
        dp(gl,0.65f,0.6f,0.85f, new ArrayList<>(Arrays.asList(4.13f,0f,-0.01f,1.09f,0.69f,1.7f, 4.13f,0f,-0.01f,1.09f,0.4f,1.66f, 4.13f,0f,-0.01f,1.09f,0.41f,1.63f, 4.13f,0f,-0.01f,1.09f,0.68f,1.66f)));
        dp(gl,0.62f,0.64f,0.83f, new ArrayList<>(Arrays.asList(4.13f,-0.01f,0f,1.09f,0.9f,1.2f, 4.13f,-0.01f,0f,1.09f,0.89f,1.51f, 4.13f,-0.01f,0f,1.09f,0.86f,1.49f, 4.13f,-0.01f,0f,1.09f,0.86f,1.2f)));
        dp(gl,0.61f,0.6f,0.89f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.86f,0.32f, 4.13f,0f,0f,1.09f,0.87f,0.61f, 4.13f,0f,0f,1.09f,0.84f,0.61f, 4.13f,0f,0f,1.09f,0.82f,0.33f)));
        dp(gl,0.61f,0.63f,0.84f, new ArrayList<>(Arrays.asList(-4.13f,0.01f,0.01f,0.53f,0.69f,1.7f, -4.13f,0.01f,0.01f,0.53f,0.89f,1.52f, -4.13f,0.01f,0.01f,0.53f,0.85f,1.51f, -4.13f,0.01f,0.01f,0.53f,0.68f,1.66f)));
        dp(gl,0.65f,0.64f,0.8f, new ArrayList<>(Arrays.asList(-4.13f,0.01f,-0f,0.53f,0.89f,1.52f, -4.13f,0.01f,-0f,0.53f,0.9f,1.23f, -4.13f,0.01f,-0f,0.53f,0.86f,1.23f, -4.13f,0.01f,-0f,0.53f,0.85f,1.51f)));
        dp(gl,0.64f,0.61f,0.87f, new ArrayList<>(Arrays.asList(-4.13f,-0f,0f,0.53f,0.9f,1.23f, -4.13f,-0f,0f,0.53f,0.89f,0.9f, -4.13f,-0f,0f,0.53f,0.85f,0.9f, -4.13f,-0f,0f,0.53f,0.86f,1.23f)));
        dp(gl,0.6f,0.62f,0.88f, new ArrayList<>(Arrays.asList(4.13f,0f,-0f,1.09f,0.89f,0.9f, 4.13f,0f,-0f,1.09f,0.9f,1.2f, 4.13f,0f,-0f,1.09f,0.86f,1.2f, 4.13f,0f,-0f,1.09f,0.85f,0.9f)));
        dp(gl,0.6f,0.65f,0.8f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.84f,0.03f, -4.13f,0f,0f,0.53f,0.83f,-0.27f, -4.13f,0f,0f,0.53f,0.79f,-0.27f, -4.13f,0f,0f,0.53f,0.81f,0.03f)));
        dp(gl,0.61f,0.63f,0.86f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1.09f,0.84f,0.03f, 4.13f,0f,0f,1.09f,0.86f,0.32f, 4.13f,0f,0f,1.09f,0.82f,0.33f, 4.13f,0f,0f,1.09f,0.81f,0.03f)));
        dp(gl,0.62f,0.63f,0.85f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.53f,0.86f,0.32f, -4.13f,0f,0f,0.53f,0.84f,0.03f, -4.13f,0f,0f,0.53f,0.81f,0.03f, -4.13f,0f,0f,0.53f,0.82f,0.33f)));
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    void drawLeftWheels(GL2 gl){
        toggleEdgeClipping(false);

        new Wheel(new Point3D(1, 0.6, 1.4), 0.26f, 0.3f, gl).draw();

        for (double z = 1.1; z >= -0.9; z-=0.4)
            new Wheel(new Point3D(1, 0.23, z), 0.17f, 0.3f, gl).draw();

        new Wheel(new Point3D(1, 0.5, -1.25), 0.23f, 0.3f, gl).draw();
    }

    void drawLeftAmortisation(GL2 gl){
        toggleEdgeClipping(false);
        dp(gl,0.22f,0.4f,0.22f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.67f,0.53f,-0.55f, -4.13f,0f,0f,0.67f,0.71f,-0.55f, -4.13f,0f,0f,0.67f,0.71f,-0.88f, -4.13f,0f,0f,0.67f,0.53f,-0.88f)));
        dp(gl,0.2f,0.4f,0.22f, new ArrayList<>(Arrays.asList(0f,0f,-4.13f,0.67f,0.53f,-0.88f, 0f,0f,-4.13f,0.67f,0.71f,-0.88f, 0f,0f,-4.13f,1f,0.71f,-0.88f, 0f,0f,-4.13f,1f,0.53f,-0.88f)));
        dp(gl,0.2f,0.43f,0.22f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1f,0.53f,-0.88f, 4.13f,0f,0f,1f,0.71f,-0.88f, 4.13f,0f,0f,1f,0.71f,-0.55f, 4.13f,0f,0f,1f,0.53f,-0.55f)));
        dp(gl,0.25f,0.45f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,1f,0.53f,-0.55f, 0f,0f,4.13f,1f,0.71f,-0.55f, 0f,0f,4.13f,0.67f,0.71f,-0.55f, 0f,0f,4.13f,0.67f,0.53f,-0.55f)));
        dp(gl,0.21f,0.45f,0.25f, new ArrayList<>(Arrays.asList(3.02f,-2.81f,-0f,1f,0.53f,-0.88f, 3.02f,-2.81f,-0f,1f,0.53f,-0.55f, 3.02f,-2.81f,-0f,0.87f,0.39f,-0.55f, 3.02f,-2.81f,-0f,0.87f,0.39f,-0.88f)));
        dp(gl,0.24f,0.42f,0.21f, new ArrayList<>(Arrays.asList(0f,4.13f,0f,1f,0.71f,-0.88f, 0f,4.13f,0f,0.67f,0.71f,-0.88f, 0f,4.13f,0f,0.67f,0.71f,-0.55f, 0f,4.13f,0f,1f,0.71f,-0.55f)));
        dp(gl,0.24f,0.44f,0.24f, new ArrayList<>(Arrays.asList(0f,-4.13f,-0f,0.67f,0.39f,-0.88f, 0f,-4.13f,-0f,0.87f,0.39f,-0.88f, 0f,-4.13f,-0f,0.87f,0.39f,-0.55f, 0f,-4.13f,-0f,0.67f,0.39f,-0.55f)));
        dp(gl,0.22f,0.44f,0.23f, new ArrayList<>(Arrays.asList(0f,0f,-4.13f,0.67f,0.53f,-0.88f, 0f,0f,-4.13f,1f,0.53f,-0.88f, 0f,0f,-4.13f,0.87f,0.39f,-0.88f, 0f,0f,-4.13f,0.67f,0.39f,-0.88f)));
        dp(gl,0.25f,0.4f,0.22f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.67f,0.53f,-0.55f, -4.13f,0f,0f,0.67f,0.53f,-0.88f, -4.13f,0f,0f,0.67f,0.39f,-0.88f, -4.13f,0f,0f,0.67f,0.39f,-0.55f)));
        dp(gl,0.22f,0.42f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,1f,0.53f,-0.55f, 0f,0f,4.13f,0.67f,0.53f,-0.55f, 0f,0f,4.13f,0.67f,0.39f,-0.55f, 0f,0f,4.13f,0.87f,0.39f,-0.55f)));
        dp(gl,0.22f,0.44f,0.2f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.67f,0.53f,0.26f, -4.13f,0f,0f,0.67f,0.71f,0.26f, -4.13f,0f,0f,0.67f,0.71f,-0.07f, -4.13f,0f,0f,0.67f,0.53f,-0.07f)));
        dp(gl,0.23f,0.45f,0.21f, new ArrayList<>(Arrays.asList(0f,-0f,-4.13f,0.67f,0.53f,-0.07f, 0f,-0f,-4.13f,0.67f,0.71f,-0.07f, 0f,-0f,-4.13f,1f,0.71f,-0.07f, 0f,-0f,-4.13f,1f,0.53f,-0.07f)));
        dp(gl,0.21f,0.43f,0.22f, new ArrayList<>(Arrays.asList(4.13f,-0f,0f,1f,0.53f,-0.07f, 4.13f,-0f,0f,1f,0.71f,-0.07f, 4.13f,-0f,0f,1f,0.71f,0.26f, 4.13f,-0f,0f,1f,0.53f,0.26f)));
        dp(gl,0.21f,0.45f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,1f,0.53f,0.26f, 0f,0f,4.13f,1f,0.71f,0.26f, 0f,0f,4.13f,0.67f,0.71f,0.26f, 0f,0f,4.13f,0.67f,0.53f,0.26f)));
        dp(gl,0.21f,0.43f,0.22f, new ArrayList<>(Arrays.asList(3.02f,-2.81f,0f,1f,0.53f,-0.07f, 3.02f,-2.81f,0f,1f,0.53f,0.26f, 3.02f,-2.81f,0f,0.87f,0.39f,0.26f, 3.02f,-2.81f,0f,0.87f,0.39f,-0.07f)));
        dp(gl,0.24f,0.4f,0.22f, new ArrayList<>(Arrays.asList(0f,4.13f,0f,1f,0.71f,-0.07f, 0f,4.13f,0f,0.67f,0.71f,-0.07f, 0f,4.13f,0f,0.67f,0.71f,0.26f, 0f,4.13f,0f,1f,0.71f,0.26f)));
        dp(gl,0.23f,0.43f,0.23f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,0.67f,0.39f,-0.07f, 0f,-4.13f,0f,0.87f,0.39f,-0.07f, 0f,-4.13f,0f,0.87f,0.39f,0.26f, 0f,-4.13f,0f,0.67f,0.39f,0.26f)));
        dp(gl,0.2f,0.42f,0.23f, new ArrayList<>(Arrays.asList(0f,-0f,-4.13f,0.67f,0.53f,-0.07f, 0f,-0f,-4.13f,1f,0.53f,-0.07f, 0f,-0f,-4.13f,0.87f,0.39f,-0.07f, 0f,-0f,-4.13f,0.67f,0.39f,-0.07f)));
        dp(gl,0.21f,0.44f,0.25f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.67f,0.53f,0.26f, -4.13f,0f,0f,0.67f,0.53f,-0.07f, -4.13f,0f,0f,0.67f,0.39f,-0.07f, -4.13f,0f,0f,0.67f,0.39f,0.26f)));
        dp(gl,0.23f,0.42f,0.23f, new ArrayList<>(Arrays.asList(0f,-0f,4.13f,1f,0.53f,0.26f, 0f,-0f,4.13f,0.67f,0.53f,0.26f, 0f,-0f,4.13f,0.67f,0.39f,0.26f, 0f,-0f,4.13f,0.87f,0.39f,0.26f)));
        dp(gl,0.22f,0.42f,0.24f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.67f,0.53f,1.05f, -4.13f,0f,0f,0.67f,0.71f,1.05f, -4.13f,0f,0f,0.67f,0.71f,0.72f, -4.13f,0f,0f,0.67f,0.53f,0.72f)));
        dp(gl,0.21f,0.42f,0.24f, new ArrayList<>(Arrays.asList(0f,0f,-4.13f,0.67f,0.53f,0.72f, 0f,0f,-4.13f,0.67f,0.71f,0.72f, 0f,0f,-4.13f,1f,0.71f,0.72f, 0f,0f,-4.13f,1f,0.53f,0.72f)));
        dp(gl,0.24f,0.43f,0.2f, new ArrayList<>(Arrays.asList(4.13f,0f,0f,1f,0.53f,0.72f, 4.13f,0f,0f,1f,0.71f,0.72f, 4.13f,0f,0f,1f,0.71f,1.05f, 4.13f,0f,0f,1f,0.53f,1.05f)));
        dp(gl,0.22f,0.45f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,1f,0.53f,1.05f, 0f,0f,4.13f,1f,0.71f,1.05f, 0f,0f,4.13f,0.67f,0.71f,1.05f, 0f,0f,4.13f,0.67f,0.53f,1.05f)));
        dp(gl,0.22f,0.44f,0.22f, new ArrayList<>(Arrays.asList(3.02f,-2.81f,0f,1f,0.53f,0.72f, 3.02f,-2.81f,0f,1f,0.53f,1.05f, 3.02f,-2.81f,0f,0.87f,0.39f,1.05f, 3.02f,-2.81f,0f,0.87f,0.39f,0.72f)));
        dp(gl,0.23f,0.42f,0.23f, new ArrayList<>(Arrays.asList(0f,4.13f,0f,1f,0.71f,0.72f, 0f,4.13f,0f,0.67f,0.71f,0.72f, 0f,4.13f,0f,0.67f,0.71f,1.05f, 0f,4.13f,0f,1f,0.71f,1.05f)));
        dp(gl,0.2f,0.45f,0.24f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,0.67f,0.39f,0.72f, 0f,-4.13f,0f,0.87f,0.39f,0.72f, 0f,-4.13f,0f,0.87f,0.39f,1.05f, 0f,-4.13f,0f,0.67f,0.39f,1.05f)));
        dp(gl,0.24f,0.44f,0.25f, new ArrayList<>(Arrays.asList(0f,0f,-4.13f,0.67f,0.53f,0.72f, 0f,0f,-4.13f,1f,0.53f,0.72f, 0f,0f,-4.13f,0.87f,0.39f,0.72f, 0f,0f,-4.13f,0.67f,0.39f,0.72f)));
        dp(gl,0.23f,0.4f,0.24f, new ArrayList<>(Arrays.asList(-4.13f,0f,0f,0.67f,0.53f,1.05f, -4.13f,0f,0f,0.67f,0.53f,0.72f, -4.13f,0f,0f,0.67f,0.39f,0.72f, -4.13f,0f,0f,0.67f,0.39f,1.05f)));
        dp(gl,0.24f,0.41f,0.21f, new ArrayList<>(Arrays.asList(0f,0f,4.13f,1f,0.53f,1.05f, 0f,0f,4.13f,0.67f,0.53f,1.05f, 0f,0f,4.13f,0.67f,0.39f,1.05f, 0f,0f,4.13f,0.87f,0.39f,1.05f)));
        dp(gl,0.23f,0.43f,0.23f, new ArrayList<>(Arrays.asList(4.12f,0.12f,0f,0.69f,0.53f,-0.57f, 4.12f,0.12f,0f,0.69f,0.53f,-0.85f, 4.12f,0.12f,0f,0.69f,0.69f,-0.86f, 4.12f,0.12f,0f,0.69f,0.69f,-0.57f)));
        dp(gl,0.22f,0.45f,0.22f, new ArrayList<>(Arrays.asList(0.02f,0.09f,4.12f,0.69f,0.53f,-0.85f, 0.02f,0.09f,4.12f,0.97f,0.54f,-0.85f, 0.02f,0.09f,4.12f,0.98f,0.69f,-0.86f, 0.02f,0.09f,4.12f,0.69f,0.69f,-0.86f)));
        dp(gl,0.23f,0.44f,0.24f, new ArrayList<>(Arrays.asList(-4.12f,0.14f,0f,0.97f,0.54f,-0.85f, -4.12f,0.14f,0f,0.97f,0.54f,-0.57f, -4.12f,0.14f,0f,0.98f,0.69f,-0.57f, -4.12f,0.14f,0f,0.98f,0.69f,-0.86f)));
        dp(gl,0.21f,0.43f,0.21f, new ArrayList<>(Arrays.asList(0.02f,0.09f,-4.12f,0.97f,0.54f,-0.57f, 0.02f,0.09f,-4.12f,0.69f,0.53f,-0.57f, 0.02f,0.09f,-4.12f,0.69f,0.69f,-0.57f, 0.02f,0.09f,-4.12f,0.98f,0.69f,-0.57f)));
        dp(gl,0.23f,0.42f,0.25f, new ArrayList<>(Arrays.asList(-3.02f,2.81f,0f,0.97f,0.54f,-0.85f, -3.02f,2.81f,0f,0.86f,0.41f,-0.85f, -3.02f,2.81f,0f,0.86f,0.41f,-0.57f, -3.02f,2.81f,0f,0.97f,0.54f,-0.57f)));
        dp(gl,0.2f,0.4f,0.21f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,0.98f,0.69f,-0.86f, 0f,-4.13f,0f,0.98f,0.69f,-0.57f, 0f,-4.13f,0f,0.69f,0.69f,-0.57f, 0f,-4.13f,0f,0.69f,0.69f,-0.86f)));
        dp(gl,0.2f,0.43f,0.24f, new ArrayList<>(Arrays.asList(-0.12f,4.12f,0f,0.69f,0.41f,-0.86f, -0.12f,4.12f,0f,0.69f,0.41f,-0.57f, -0.12f,4.12f,0f,0.86f,0.41f,-0.57f, -0.12f,4.12f,0f,0.86f,0.41f,-0.85f)));
        dp(gl,0.21f,0.44f,0.21f, new ArrayList<>(Arrays.asList(0.01f,-0.09f,4.12f,0.69f,0.53f,-0.85f, 0.01f,-0.09f,4.12f,0.69f,0.41f,-0.86f, 0.01f,-0.09f,4.12f,0.86f,0.41f,-0.85f, 0.01f,-0.09f,4.12f,0.97f,0.54f,-0.85f)));
        dp(gl,0.2f,0.42f,0.23f, new ArrayList<>(Arrays.asList(4.12f,-0.16f,-0f,0.69f,0.53f,-0.57f, 4.12f,-0.16f,-0f,0.69f,0.41f,-0.57f, 4.12f,-0.16f,-0f,0.69f,0.41f,-0.86f, 4.12f,-0.16f,-0f,0.69f,0.53f,-0.85f)));
        dp(gl,0.2f,0.42f,0.24f, new ArrayList<>(Arrays.asList(0.01f,-0.09f,-4.12f,0.97f,0.54f,-0.57f, 0.01f,-0.09f,-4.12f,0.86f,0.41f,-0.57f, 0.01f,-0.09f,-4.12f,0.69f,0.41f,-0.57f, 0.01f,-0.09f,-4.12f,0.69f,0.53f,-0.57f)));
        dp(gl,0.25f,0.45f,0.23f, new ArrayList<>(Arrays.asList(4.12f,0.12f,0f,0.69f,0.53f,0.23f, 4.12f,0.12f,0f,0.69f,0.53f,-0.05f, 4.12f,0.12f,0f,0.69f,0.69f,-0.05f, 4.12f,0.12f,0f,0.69f,0.69f,0.24f)));
        dp(gl,0.24f,0.43f,0.24f, new ArrayList<>(Arrays.asList(0.02f,0.09f,4.12f,0.69f,0.53f,-0.05f, 0.02f,0.09f,4.12f,0.97f,0.54f,-0.05f, 0.02f,0.09f,4.12f,0.98f,0.69f,-0.05f, 0.02f,0.09f,4.12f,0.69f,0.69f,-0.05f)));
        dp(gl,0.2f,0.41f,0.22f, new ArrayList<>(Arrays.asList(-4.12f,0.14f,-0f,0.97f,0.54f,-0.05f, -4.12f,0.14f,-0f,0.97f,0.54f,0.23f, -4.12f,0.14f,-0f,0.98f,0.69f,0.24f, -4.12f,0.14f,-0f,0.98f,0.69f,-0.05f)));
        dp(gl,0.24f,0.42f,0.2f, new ArrayList<>(Arrays.asList(0.02f,0.09f,-4.12f,0.97f,0.54f,0.23f, 0.02f,0.09f,-4.12f,0.69f,0.53f,0.23f, 0.02f,0.09f,-4.12f,0.69f,0.69f,0.24f, 0.02f,0.09f,-4.12f,0.98f,0.69f,0.24f)));
        dp(gl,0.2f,0.43f,0.21f, new ArrayList<>(Arrays.asList(-3.02f,2.81f,0f,0.97f,0.54f,-0.05f, -3.02f,2.81f,0f,0.86f,0.41f,-0.05f, -3.02f,2.81f,0f,0.86f,0.41f,0.23f, -3.02f,2.81f,0f,0.97f,0.54f,0.23f)));
        dp(gl,0.23f,0.4f,0.22f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,0.98f,0.69f,-0.05f, 0f,-4.13f,0f,0.98f,0.69f,0.24f, 0f,-4.13f,0f,0.69f,0.69f,0.24f, 0f,-4.13f,0f,0.69f,0.69f,-0.05f)));
        dp(gl,0.21f,0.43f,0.25f, new ArrayList<>(Arrays.asList(-0.12f,4.12f,0f,0.69f,0.41f,-0.05f, -0.12f,4.12f,0f,0.69f,0.41f,0.24f, -0.12f,4.12f,0f,0.86f,0.41f,0.23f, -0.12f,4.12f,0f,0.86f,0.41f,-0.05f)));
        dp(gl,0.22f,0.43f,0.21f, new ArrayList<>(Arrays.asList(0.01f,-0.09f,4.12f,0.69f,0.53f,-0.05f, 0.01f,-0.09f,4.12f,0.69f,0.41f,-0.05f, 0.01f,-0.09f,4.12f,0.86f,0.41f,-0.05f, 0.01f,-0.09f,4.12f,0.97f,0.54f,-0.05f)));
        dp(gl,0.23f,0.43f,0.23f, new ArrayList<>(Arrays.asList(4.12f,-0.16f,0f,0.69f,0.53f,0.23f, 4.12f,-0.16f,0f,0.69f,0.41f,0.24f, 4.12f,-0.16f,0f,0.69f,0.41f,-0.05f, 4.12f,-0.16f,0f,0.69f,0.53f,-0.05f)));
        dp(gl,0.22f,0.43f,0.22f, new ArrayList<>(Arrays.asList(0.01f,-0.09f,-4.12f,0.97f,0.54f,0.23f, 0.01f,-0.09f,-4.12f,0.86f,0.41f,0.23f, 0.01f,-0.09f,-4.12f,0.69f,0.41f,0.24f, 0.01f,-0.09f,-4.12f,0.69f,0.53f,0.23f)));
        dp(gl,0.22f,0.41f,0.2f, new ArrayList<>(Arrays.asList(4.12f,0.12f,0f,0.69f,0.53f,1.03f, 4.12f,0.12f,0f,0.69f,0.53f,0.75f, 4.12f,0.12f,0f,0.69f,0.69f,0.74f, 4.12f,0.12f,0f,0.69f,0.69f,1.03f)));
        dp(gl,0.25f,0.44f,0.22f, new ArrayList<>(Arrays.asList(0.02f,0.09f,4.12f,0.69f,0.53f,0.75f, 0.02f,0.09f,4.12f,0.97f,0.54f,0.74f, 0.02f,0.09f,4.12f,0.98f,0.69f,0.74f, 0.02f,0.09f,4.12f,0.69f,0.69f,0.74f)));
        dp(gl,0.21f,0.44f,0.21f, new ArrayList<>(Arrays.asList(-4.12f,0.14f,0f,0.97f,0.54f,0.74f, -4.12f,0.14f,0f,0.97f,0.54f,1.03f, -4.12f,0.14f,0f,0.98f,0.69f,1.03f, -4.12f,0.14f,0f,0.98f,0.69f,0.74f)));
        dp(gl,0.24f,0.41f,0.22f, new ArrayList<>(Arrays.asList(0.02f,0.09f,-4.12f,0.97f,0.54f,1.03f, 0.02f,0.09f,-4.12f,0.69f,0.53f,1.03f, 0.02f,0.09f,-4.12f,0.69f,0.69f,1.03f, 0.02f,0.09f,-4.12f,0.98f,0.69f,1.03f)));
        dp(gl,0.24f,0.43f,0.23f, new ArrayList<>(Arrays.asList(-3.02f,2.81f,0f,0.97f,0.54f,0.74f, -3.02f,2.81f,0f,0.86f,0.41f,0.74f, -3.02f,2.81f,0f,0.86f,0.41f,1.03f, -3.02f,2.81f,0f,0.97f,0.54f,1.03f)));
        dp(gl,0.23f,0.42f,0.24f, new ArrayList<>(Arrays.asList(0f,-4.13f,0f,0.98f,0.69f,0.74f, 0f,-4.13f,0f,0.98f,0.69f,1.03f, 0f,-4.13f,0f,0.69f,0.69f,1.03f, 0f,-4.13f,0f,0.69f,0.69f,0.74f)));
        dp(gl,0.22f,0.43f,0.2f, new ArrayList<>(Arrays.asList(-0.12f,4.12f,0f,0.69f,0.41f,0.74f, -0.12f,4.12f,0f,0.69f,0.41f,1.03f, -0.12f,4.12f,0f,0.86f,0.41f,1.03f, -0.12f,4.12f,0f,0.86f,0.41f,0.74f)));
        dp(gl,0.23f,0.42f,0.23f, new ArrayList<>(Arrays.asList(0.01f,-0.09f,4.12f,0.69f,0.53f,0.75f, 0.01f,-0.09f,4.12f,0.69f,0.41f,0.74f, 0.01f,-0.09f,4.12f,0.86f,0.41f,0.74f, 0.01f,-0.09f,4.12f,0.97f,0.54f,0.74f)));
        dp(gl,0.21f,0.42f,0.23f, new ArrayList<>(Arrays.asList(4.12f,-0.16f,0f,0.69f,0.53f,1.03f, 4.12f,-0.16f,0f,0.69f,0.41f,1.03f, 4.12f,-0.16f,0f,0.69f,0.41f,0.74f, 4.12f,-0.16f,0f,0.69f,0.53f,0.75f)));
        dp(gl,0.24f,0.43f,0.22f, new ArrayList<>(Arrays.asList(0.01f,-0.09f,-4.12f,0.97f,0.54f,1.03f, 0.01f,-0.09f,-4.12f,0.86f,0.41f,1.03f, 0.01f,-0.09f,-4.12f,0.69f,0.41f,1.03f, 0.01f,-0.09f,-4.12f,0.69f,0.53f,1.03f)));
    }
}

