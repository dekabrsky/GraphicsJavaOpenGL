package Lab6.Volume;

import javax.swing.JFrame;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.util.Arrays;
import java.util.List;

public class VolumeMain extends JFrame {

    public static void main(String[] args) {

        final GLProfile profile = GLProfile.get( GLProfile.GL2 );
        GLCapabilities capabilities = new GLCapabilities( profile );

        List<VolumeFigure> figures = Arrays.asList(
                new Cube(),
                new PolyPyramid(4),
                new PolyPyramid(7),
                new PolyPyramid(100),
                new PolyTrapeze(4, 0.5f, 1.5f),
                new PolyTrapeze(100, 0.5f, 1.5f),
                new Sphere()
        );
        for (VolumeFigure figure: figures) {
            final GLCanvas glcanvas = new GLCanvas( capabilities );

            glcanvas.addGLEventListener(figure);
            glcanvas.setSize(600, 600);

            final JFrame frame = new JFrame(figure.getClass().getSimpleName());
            frame.getContentPane().add(glcanvas);
            frame.setSize(frame.getContentPane().getPreferredSize());
            frame.setVisible(true);
            glcanvas.addMouseMotionListener(figure);

            FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
            animator.start();
        }
    }


}