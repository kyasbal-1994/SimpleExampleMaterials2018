import javax.swing.*;
import javax.swing.event.*;
import java.lang.reflect.InvocationTargetException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import java.awt.*;
import java.awt.event.*;

public abstract class SimpleExampleBase implements GLEventListener{
  private JFrame frame;
  private GLCanvas canvas;

  protected final int SCREENW, SCREENH;

  public SimpleExampleBase(){
    this("Simple Example");
  }

  public SimpleExampleBase(String windowname){
    this(windowname,320,320);
  }
  
  public SimpleExampleBase(String windowname, int width, int height){
    SCREENW = width;
    SCREENH = height;
    frame = new JFrame(windowname);
    GLProfile profile = GLProfile.get(GLProfile.GL4);
    GLCapabilities capabilities = new GLCapabilities(profile); 
    capabilities.setSampleBuffers(true); //This is effective if supported
    capabilities.setNumSamples(4);
    canvas = new GLCanvas(capabilities);
    canvas.addGLEventListener(this);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container container = frame.getContentPane();
    //    container.setLayout(new GroupLayout
    frame.add(canvas,BorderLayout.NORTH);
  
    canvas.setPreferredSize(new Dimension(SCREENW, SCREENH));
  }

  public void start(){
    try {
      java.awt.EventQueue.invokeAndWait(new Runnable(){
          public void run(){
            initGLUI();
          }
        }
      );
      java.awt.EventQueue.invokeAndWait(new Runnable(){
          public void run(){
            Animator animator = new Animator(canvas);
            animator.start();
          }
        }
      );
    } catch(InvocationTargetException e){
      System.out.println(e);
      e.printStackTrace();
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  private void initGLUI(){
    canvas.setVisible(true);
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);
  }

  public void reshape(GLAutoDrawable drawable,
                        int x, int y, 
                        int width, int height){
  }

  public void dispose(GLAutoDrawable drawable){
  }

  protected void addKeyListener(KeyListener l){
    canvas.addKeyListener(l);
  }

  protected void addMouseListener(MouseListener l){
    canvas.addMouseListener(l);
  }

  protected void addMouseMotionListener(MouseMotionListener l){
    canvas.addMouseMotionListener(l);
  }

}
