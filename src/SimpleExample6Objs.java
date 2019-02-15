import com.jogamp.opengl.*;
import com.jogamp.opengl.math.Matrix4;

import java.awt.event.*;

public class SimpleExample6Objs extends SimpleExampleBase{
  Object3D[] objs;
  float t=0;
  Vec3 lightpos;
  Vec3 lightcolor;
  EnvironmentParmaeter environmentParmaeter;

  public SimpleExample6Objs(){
    super("Samples", 1024, 1024);
    objs = new Object3D[6];
    objs[0] = new Cylinder2(18,0.07f,0.8f,true);
    objs[1] = new Cylinder2(4,0.5f,0.8f,false);
    objs[2] = new Cylinder2(18,0.3f,0.04f,true);
    objs[3] = new Cylinder2(4,0.5f,0.01f,false);
    objs[4] = new Cylinder2(8,0.3f,0.8f,false);
    objs[5] = new Cylinder2(4,0.3f,0.5f,false);
    addKeyListener(new simpleExampleKeyListener());
    addMouseMotionListener(new simpleExampleMouseMotionListener());
    addMouseListener(new simpleExampleMouseListener());
  }

  public void init(GLAutoDrawable drawable){
    drawable.setGL(new DebugGL4(drawable.getGL().getGL4()));
    final GL4 gl = drawable.getGL().getGL4();
    gl.glViewport(0, 0, SCREENW, SCREENH);

    // Clear color buffer with black
    gl.glClearColor(0.7f, 0.7f, 0.5f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT,1);
    gl.glFrontFace(GL.GL_CCW);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glCullFace(GL.GL_BACK);
    objs[0].init(gl, Material.fromVertexFragmentShaders("resource/shader/cooktorrance.vert","resource/shader/cooktorrance.frag",
            new DefaultMaterialParameter("resource/image/flatwhite.png",null,null,null,new Vec4(3f,3f,3f,1),0.5f,0f)));
    objs[1].init(gl, Material.fromVertexFragmentShaders("resource/shader/cooktorrance.vert","resource/shader/cooktorrance.frag",
            new DefaultMaterialParameter("resource/image/Brick.png","resource/image/BrickNormalMap.png",null,null,new Vec4(124f/255f,194f/255f,152f/255f,1.0f),1,0)));
    objs[2].init(gl, Material.fromVertexFragmentShaders("resource/shader/cooktorrance.vert","resource/shader/cooktorrance.frag",
            new DefaultMaterialParameter("resource/image/coin.png","resource/image/coin_normal.png",null,null,new Vec4(0.67f, 0.43f, 0.22f, 1.00f),0.3f,0.2f)));
    objs[3].init(gl, Material.fromVertexFragmentShaders("resource/shader/cooktorrance.vert","resource/shader/cooktorrance.frag",
            new DefaultMaterialParameter("resource/image/noise.png","resource/image/NormalMap.png",null,null,new Vec4(1.0f,1.0f,1.0f,1.0f),0.1f,0.99f)));
    objs[4].init(gl, Material.fromVertexFragmentShaders("resource/shader/cooktorrance.vert","resource/shader/cooktorrance.frag",
            new DefaultMaterialParameter("resource/image/Wood_Floor_007_COLOR.jpg","resource/image/Wood_Floor_007_NORM.jpg","resource/image/Wood_Floor_007_ROUGH.jpg","resource/image/Wood_Floor_007_OCC.jpg",new Vec4(1.0f,1.0f,1.0f,1.0f),0.8f,0f)));
    objs[5].init(gl, Material.fromVertexFragmentShaders("resource/shader/cooktorrance.vert","resource/shader/cooktorrance.frag",
            new DefaultMaterialParameter("resource/image/Marble_Gray_001_COLOR.jpg","resource/image/Marble_Gray_001_NORM.jpg","resource/image/Marble_Gray_001_ROUGH.jpg","resource/image/Marble_Gray_001_OCC.jpg",new Vec4(1.0f,1.0f,1.0f,1.0f),0.3f,0.8f)));
    //objs.init(gl, mats, null);
    gl.glUseProgram(0);
    lightpos = new Vec3(0.0f, 0.0f, 30f);
    lightcolor = new Vec3(1f, 1f, 1f);
    Matrix4 viewMat = new Matrix4();
    viewMat.loadIdentity();
    viewMat.translate(0,0,5);
    viewMat.invert();
    Matrix4 projMat = new Matrix4();
    projMat.loadIdentity();
    projMat.makeFrustum(-0.5f, 0.5f, -0.5f, 0.5f, 1f, 100f);
    environmentParmaeter = new DefaultEnvironmentParameter(viewMat,projMat);
  }

  public void display(GLAutoDrawable drawable){
    final GL4 gl = drawable.getGL().getGL4();
    if(t<360){
      t = t+0.3f;
    }else{
      t = 0f;
    }
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    for(int i=0; i<6; i++){
      float degToRad = (float) (Math.PI/180f);
      Matrix4 modelMat = new Matrix4();
      modelMat.loadIdentity();
      modelMat.translate((i%3)-1f,0.7f-(i/3)*1.4f,0f);
      modelMat.rotate(t*degToRad,0.3f,0.3f,0);
      modelMat.rotate(90*degToRad,1f,0f,0f);
      modelMat.rotate(45*degToRad,0f,0f,1f);
      objs[i].display(modelMat, environmentParmaeter);
    }
    gl.glFlush();
  }

  public static void main(String[] args){
    new SimpleExample6Objs().start();
  }
  
  class simpleExampleKeyListener implements KeyListener{
    public void keyPressed(KeyEvent e){
      int keycode = e.getKeyCode();
      System.out.print(keycode);
      if(java.awt.event.KeyEvent.VK_LEFT == keycode){
        System.out.print("a");
      }
    }
    public void keyReleased(KeyEvent e){
    }
    public void keyTyped(KeyEvent e){
    }
  }

  class simpleExampleMouseMotionListener implements MouseMotionListener{
    public void mouseDragged(MouseEvent e){
      System.out.println("dragged:"+e.getX()+" "+e.getY());
    }
    public void mouseMoved(MouseEvent e){
      System.out.println("moved:"+e.getX()+" "+e.getY());
    }
  }

  class simpleExampleMouseListener implements MouseListener{
    public void mouseClicked(MouseEvent e){
    }
    public void mouseEntered(MouseEvent e){
    }
    public void mouseExited(MouseEvent e){
    }
    public void mousePressed(MouseEvent e){
      System.out.println("pressed:"+e.getX()+" "+e.getY());
    }
    public void mouseReleased(MouseEvent e){
    }
  }
}
