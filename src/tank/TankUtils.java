package tank;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import javafx.geometry.Point3D;

import java.util.ArrayList;

public class TankUtils {

    public static class Wheel {
        private Point3D top;
        private Point3D bottom;
        private int sidesCnt = 100;
        private float topRadius, bottomRadius;
        private GL2 gl;
        private Texture texture;

        public Wheel(Point3D center, float radius, float depth, GL2 gl, Texture texture){
            this.topRadius = radius;
            this.bottomRadius = radius;
            top = center;
            bottom = new Point3D(top.getX() - depth, top.getY(), top.getZ());
            this.gl = gl;
            this.texture = texture;
        }

        public void draw() {
            ArrayList<Point3D> bottomVertices = calcVertices(bottom, bottomRadius);

            drawPolygon(gl, bottomVertices);

            ArrayList<Point3D> topVertices = calcVertices(top, topRadius);

            drawPolygon(gl, topVertices);

            for (int i = 0; i < sidesCnt; i++){
                //gl.glColor3f(i % 2, i % 3, i % 5);
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
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 2);
            TextureCoords texcoords = texture.getImageTexCoords();
            gl.glBegin(GL2.GL_POLYGON);
            int i = 0;
            for (Point3D vertice : vertices) {
                gl.glVertex3f(
                        (float) vertice.getX(),
                        (float) vertice.getY(),
                        (float) vertice.getZ()
                );
                switch (i) {
                    case (0) :
                        gl.glTexCoord2f(texcoords.right(), texcoords.bottom());
                        break;
                    case (25) :
                        gl.glTexCoord2f(texcoords.left(), texcoords.bottom());
                        break;
                    case (50) :
                        gl.glTexCoord2f(texcoords.left(), texcoords.top());
                        break;
                    case (75) :
                        gl.glTexCoord2f(texcoords.right(), texcoords.top());
                        break;
                    default:
                }
                i++;
            }
            gl.glEnd();
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        }

        private ArrayList<Point3D> calcVertices(Point3D center, float radius)
        {
            ArrayList<Point3D> vertices = new ArrayList<>();

            double singleAngle = 360.0 / sidesCnt;
            double sumAngle = 0;
            for (int i = 0; i < sidesCnt; i++)
            {
                //double X = center.getX() + Math.cos(sumAngle / 180 * Math.PI) * radius;
                double Y = center.getY() - Math.sin(sumAngle / 180 * Math.PI) * radius;
                double Z = center.getZ() - Math.cos(sumAngle / 180 * Math.PI) * radius;
                sumAngle = sumAngle + singleAngle;
                vertices.add(new Point3D(center.getX(), Y, Z));
            }

            return vertices;
        }
    }

}
