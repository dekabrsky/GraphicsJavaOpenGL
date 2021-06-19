package Lab6_Primitives.Flat;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class RegularPolygon implements FlatFigure {
    Random generator = new Random();
    int sidesCnt;

    public RegularPolygon (int sidesCnt){
        this.sidesCnt = sidesCnt;
    }

    @Override
    public void setup(GL2 gl2, int width, int height ) {
        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();

        GLU glu = new GLU();
        glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        gl2.glViewport( 0, 0, width, height );
    }

    @Override
    public void render( GL2 gl2, int width, int height ) {
        gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
        ArrayList<Point> vertices = calcVertices(new Point(width/2, height/2), width/2);

        gl2.glLoadIdentity();

        gl2.glBegin( GL2.GL_POLYGON );
        for (Point vertice: vertices){
            gl2.glColor3f(
                    generator.nextFloat(),
                    generator.nextFloat(),
                    generator.nextFloat()
            );
            gl2.glVertex2f(
                    (float) vertice.getX(),
                    (float) vertice.getY()
            );
        }

        gl2.glEnd();
    }

    private ArrayList<Point> calcVertices(Point center, int radius)
    {
        ArrayList<Point> vertices = new ArrayList<>();

        double singleAngle = 360.0 / sidesCnt;
        double sumAngle = 0;
        for (int i = 0; i < sidesCnt; i++)
        {
            int X = (int)(center.getX() + Math.round(Math.cos(sumAngle / 180 * Math.PI) * radius));
            int Y = (int)(center.getY() - Math.round(Math.sin(sumAngle / 180 * Math.PI) * radius));
            sumAngle = sumAngle + singleAngle;
            vertices.add(new Point(X, Y));
        }

        return vertices;
    }
}
