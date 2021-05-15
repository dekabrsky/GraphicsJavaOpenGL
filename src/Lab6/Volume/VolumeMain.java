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

        final GLCanvas glcanvas = new GLCanvas( capabilities );
        List<VolumeFigure> figures = Arrays.asList(new Cube());
        for (VolumeFigure figure: figures) {

            glcanvas.addGLEventListener(figure);
            glcanvas.setSize(600, 600);

            final JFrame frame = new JFrame(" Multicolored cube");
            frame.getContentPane().add(glcanvas);
            frame.setSize(frame.getContentPane().getPreferredSize());
            frame.setVisible(true);
            glcanvas.addMouseMotionListener(figure);
            final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);

            animator.start();
        }
    }


}