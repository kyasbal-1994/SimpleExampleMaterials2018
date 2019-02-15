import com.jogamp.opengl.*;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.*;

abstract class Object3D extends GL4Resource {
  public static final int VERTEXPOSITION=0;
  public static final int VERTEXCOLOR=1;
  public static final int VERTEXTEXCOORD0=3;
  public static final int VERTEXNORMAL=2;
  public static final int VERTEXTANGENT=4;
  protected Material material;
  void init(GL4 gl, Material material){
    super.init(gl);
    this.material = material;
  }
  abstract void display(Matrix4 modelMat, EnvironmentParmaeter parameter);
}
