package Lab6;

import com.jogamp.opengl.GL2;

public interface FlatFigure {
    void setup(GL2 gl2, int width, int height );
    void render(GL2 gl2, int width, int height);
}