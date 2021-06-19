package Lab6_Primitives.Volume;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javafx.geometry.Point3D;

import java.util.ArrayList;

public class PolyTrapeze extends VolumeFigure{
    private Point3D top = new Point3D(0, 1, 0);
    private Point3D bottom = new Point3D(0, -1, 0);
    private int sidesCnt;
    private float topRadius, bottomRadius;

    public PolyTrapeze(int sidesCnt, float topRadius, float bottomRadius){
        this.sidesCnt = sidesCnt;
        this.topRadius = topRadius;
        this.bottomRadius = bottomRadius;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( -0.5f, 0.0f, -6.0f );
        gl.glRotatef(rotateX,0,1,0);
        gl.glRotatef(rotateY,1,0,0);
        gl.glColor3f(0, 1, 0);

        ArrayList<Point3D> bottomVertices = calcVertices(new Point3D(0, 0, -1), bottomRadius);

        drawPolygon(gl, bottomVertices);

        ArrayList<Point3D> topVertices = calcVertices(new Point3D(0, 0, 1), topRadius);

        drawPolygon(gl, topVertices);

        for (int i = 0; i < sidesCnt; i++){
            gl.glColor3f(i % 2, i % 3, i % 5);
            Point3D currentVertice = bottomVertices.get(i);
            Point3D nextVertice = bottomVertices.get((i + 1) % sidesCnt);
            Point3D topCurrentVertice = topVertices.get(i);
            Point3D topNextVertice = topVertices.get((i + 1) % sidesCnt);
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3f(
                    (float) nextVertice.getX(),
                    (float) nextVertice.getY(),
                    (float) nextVertice.getZ()
            );
            gl.glVertex3f(
                    (float) topNextVertice.getX(),
                    (float) topNextVertice.getY(),
                    (float) topNextVertice.getZ()
            );
            gl.glVertex3f(
                    (float) topCurrentVertice.getX(),
                    (float) topCurrentVertice.getY(),
                    (float) topCurrentVertice.getZ()
            );
            gl.glVertex3f(
                    (float) currentVertice.getX(),
                    (float) currentVertice.getY(),
                    (float) currentVertice.getZ()
            );
            gl.glEnd();
        }
        gl.glFlush();
    }

    private void drawPolygon(GL2 gl, ArrayList<Point3D> vertices) {
        gl.glBegin(GL2.GL_POLYGON);
        for (Point3D vertice : vertices) {
            gl.glVertex3f(
                    (float) vertice.getX(),
                    (float) vertice.getY(),
                    (float) vertice.getZ()
            );
        }
        gl.glEnd();
    }

    private ArrayList<Point3D> calcVertices(Point3D center, float radius)
    {
        ArrayList<Point3D> vertices = new ArrayList<>();

        double singleAngle = 360.0 / sidesCnt;
        double sumAngle = 0;
        for (int i = 0; i < sidesCnt; i++)
        {
            double X = center.getX() + Math.cos(sumAngle / 180 * Math.PI) * radius;
            double Y = center.getY() - Math.sin(sumAngle / 180 * Math.PI) * radius;
            sumAngle = sumAngle + singleAngle;
            vertices.add(new Point3D(X, Y, center.getZ()));
        }

        return vertices;
    }
}

