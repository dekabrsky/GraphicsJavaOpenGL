package Lab6.Volume;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javafx.geometry.Point3D;

import java.util.ArrayList;

public class PolyPyramid extends VolumeFigure{
    private int sidesCnt;
    private Point3D top = new Point3D(0, 1, 0);

    public PolyPyramid(int sidesCnt){
        this.sidesCnt = sidesCnt;
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
        ArrayList<Point3D> vertices = calcVertices(new Point3D(0, 0, -1), 1);

        gl.glBegin(GL2.GL_POLYGON);
        for (Point3D vertice: vertices){
            gl.glVertex3f(
                    (float) vertice.getX(),
                    (float) vertice.getY(),
                    (float) vertice.getZ()
            );
        }
        gl.glEnd();

        for (int i = 0; i < vertices.size(); i++){
            gl.glColor3f(i % 2, i % 3, i % 5);
            Point3D currentVertice = vertices.get(i);
            Point3D nextVertice = vertices.get((i + 1) % vertices.size());
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex3f(
                    (float) nextVertice.getX(),
                    (float) nextVertice.getY(),
                    (float) nextVertice.getZ()
            );
            gl.glVertex3f(
                    (float) top.getX(),
                    (float) top.getY(),
                    (float) top.getZ()
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

    private ArrayList<Point3D> calcVertices(Point3D center, int radius)
    {
        ArrayList<Point3D> vertices = new ArrayList<>();

        double singleAngle = 360.0 / sidesCnt;
        double sumAngle = 0;
        for (int i = 0; i < sidesCnt; i++)
        {
            double X = center.getX() + Math.cos(sumAngle / 180 * Math.PI) * radius;
            double Z = center.getY() - Math.sin(sumAngle / 180 * Math.PI) * radius;
            sumAngle = sumAngle + singleAngle;
            vertices.add(new Point3D(X, -1, Z));
        }

        return vertices;
    }
}
