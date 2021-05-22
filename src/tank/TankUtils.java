package tank;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import javafx.geometry.Point3D;

import java.util.ArrayList;

public class TankUtils {
    public static class Wheel {
        private Point3D top, bottom, center;
        private int sidesCnt = 100;
        private float topRadius, bottomRadius;
        private GL2 gl;
        private Texture texture;

        public Wheel(Point3D center, float radius, float depth, GL2 gl, Texture texture){
            this.center = center;
            this.topRadius = radius;
            this.bottomRadius = radius;
            top = center;
            bottom = new Point3D(top.getX() - depth, top.getY(), top.getZ());
            this.gl = gl;
            this.texture = texture;
        }

        public void draw() {
            ArrayList<Point3D> bottomVertices = calcVertices(bottom, bottomRadius);
            ArrayList<Point3D> miniBottomVertices = calcVertices(bottom, bottomRadius / 5);
            drawPolygon(gl, bottomVertices, true);
            drawPolygon(gl, miniBottomVertices, false);

            ArrayList<Point3D> topVertices = calcVertices(top, topRadius);
            ArrayList<Point3D> miniTopVertices = calcVertices(top, topRadius / 5);
            drawPolygon(gl, topVertices, true);
            drawPolygon(gl, miniTopVertices, false);

            for (int i = 0; i < sidesCnt; i++){
                Point3D currentVertice = bottomVertices.get(i);
                Point3D nextVertice = bottomVertices.get((i + 1) % sidesCnt);
                Point3D topCurrentVertice = topVertices.get(i);
                Point3D topNextVertice = topVertices.get((i + 1) % sidesCnt);
                gl.glBegin(GL2.GL_POLYGON);
                gl.glColor3f(0.55f + 0.05f * (i % 2),0.55f + 0.05f * (i % 3),0.8f + 0.05f * (i % 5));
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

            drawStar(top, topVertices);
            drawStar(bottom, bottomVertices);

            gl.glFlush();
        }

        private void drawPolygon(GL2 gl, ArrayList<Point3D> vertices, boolean state) {
            gl.glBegin(GL2.GL_POLYGON);
            if (state) {
                gl.glColor3f(0.45f, 0.45f, 0.70f);
            } else {
                gl.glColor3f(0.25f, 0.25f, 0.5f);
            }
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
                //double X = center.getX() + Math.cos(sumAngle / 180 * Math.PI) * radius;
                double Y = center.getY() - Math.sin(sumAngle / 180 * Math.PI) * radius;
                double Z = center.getZ() - Math.cos(sumAngle / 180 * Math.PI) * radius;
                sumAngle = sumAngle + singleAngle;
                vertices.add(new Point3D(center.getX(), Y, Z));
            }

            return vertices;
        }

        private void drawStar(Point3D center, ArrayList<Point3D> topVertices){
            for (int i = 0; i < sidesCnt; i+=10){
                Point3D topCurrentVertice = topVertices.get(i);
                Point3D topNextVertice = topVertices.get((i + 1) % sidesCnt);
                gl.glBegin(GL2.GL_POLYGON);
                gl.glColor3f(0.25f,0.25f + 0.05f * (i % 3),0.5f + 0.05f * (i % 5));
                gl.glVertex3f(
                        (float) center.getX(),
                        (float) center.getY(),
                        (float) center.getZ()
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
                        (float) center.getX(),
                        (float) center.getY(),
                        (float) center.getZ()
                );
                gl.glEnd();
            }
        }
    }
}
