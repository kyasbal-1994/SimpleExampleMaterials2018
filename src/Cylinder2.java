import com.jogamp.opengl.*;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Cylinder2 extends Object3D{
  private float[] VertexData; 
  //example for one vertex
  //{ -1.0f,  1.0f,  0f,  0.0f, 0.0f,-1.0f,     0f,1f } 
  //  position            normal                texcoord
  private float[] VertexData2;
  //example for one vertex
  //{ 0f,0f,1f,1f }
  //  color 
  private float[] VertexData3;
  //example for one vertex
  //{ -1.0f,  1.0f,  0f }
  //  tangent
  private final int NormalOffset = Float.SIZE/8*3;
  private final int ColorOffset = 0;//Float.SIZE/8*0;
  private final int TangentOffset = 0;//Float.SIZE/8*0;
  private final int TexCoordOffset = Float.SIZE/8*6;//Float.SIZE/8*10;
  private final int VertexCount;
  private final int VertexSize;
  private final int VertexSize2;
  private final int VertexSize3;
  private final FloatBuffer FBVertexData;
  private final FloatBuffer FBVertexData2;
  private final FloatBuffer FBVertexData3;

  private int[] ElementData; // { 0,1,2 } example for one polygon

  private final int PolygonCount;
  private final int ElementCount;
  private final int ElementSize;
  private final IntBuffer IBElementData;
  private int ElementBufferName;
  private int ArrayBufferName;
  private int ArrayBufferName2;
  private int ArrayBufferName3;
  private boolean initialized = false;

  private MaterialParameter parameter;

  private void setColor(int offset, Vec4 color){
    VertexData2[offset*4  ] = color.data[0];
    VertexData2[offset*4+1] = color.data[1];
    VertexData2[offset*4+2] = color.data[2];
    VertexData2[offset*4+3] = color.data[3];
  }

  private void setTangent(int offset, Vec3 tangentv){
    VertexData3[offset*3  ] = tangentv.data[0];
    VertexData3[offset*3+1] = tangentv.data[1];
    VertexData3[offset*3+2] = tangentv.data[2];
  }

  public Cylinder2(int num, float radius, float height, boolean smooth){
    int offset=0;// center of bottom
    if(smooth){
      VertexData = new float[8*((num+1)*4+2)];
      VertexData2 = new float[4*((num+1)*4+2)];
      VertexData3 = new float[3*((num+1)*4+2)];
    }else{
      VertexData = new float[8*((num+1)*2+2+num*4)];
      VertexData2 = new float[4*((num+1)*2+2+num*4)];
      VertexData3 = new float[3*((num+1)*2+2+num*4)];
    }
    VertexData[0] = 0.0f;       VertexData[1] = 0.0f;
    VertexData[2] = -height/2f; VertexData[3] = 0.0f;
    VertexData[4] = 0.0f;       VertexData[5] = -1.0f;
    VertexData[6] = 0.5f;       VertexData[7] = 0.5f;
    setColor(0, new Vec4(1,1,1,1));
    setTangent(0, new Vec3(-1f,0,0));
    offset=1;
    for(int i=0;i<num+1;i++){ // bottom
      int j = i+offset;
      VertexData[j*8+0] = (float)(radius*Math.cos(i*2*Math.PI/num));
      VertexData[j*8+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
      VertexData[j*8+2] = -height/2f; 
      VertexData[j*8+3] = 0.0f;
      VertexData[j*8+4] = 0.0f;
      VertexData[j*8+5] = -1.0f;
      VertexData[j*8+6] = (float)(-0.5*Math.cos(i*2*Math.PI/num) +0.5f);
      VertexData[j*8+7] = (float)(0.5*Math.sin(i*2*Math.PI/num) +0.5f);
      setColor(j, new Vec4(1,1,1,1));
      setTangent(j, new Vec3(-1f,0,0));
    }

    offset=(num+1)+1;
    for(int i=0;i<num+1;i++){ //top
      int j = i+offset;
      VertexData[j*8  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
      VertexData[j*8+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
      VertexData[j*8+2] = height/2f; 
      VertexData[j*8+3] = 0.0f;
      VertexData[j*8+4] = 0.0f;
      VertexData[j*8+5] = 1.0f;
      VertexData[j*8+6] = (float)(0.5*Math.cos(i*2*Math.PI/num) +0.5f);
      VertexData[j*8+7] = (float)(0.5*Math.sin(i*2*Math.PI/num) +0.5f);
      setColor(j, new Vec4(1,1,1,1));
      setTangent(j, new Vec3(1f,0,0));
    }

    offset = (num+1)*2+1; // center of top
    int j = offset;
    VertexData[j*8  ] = 0.0f;
    VertexData[j*8+1] = 0.0f;
    VertexData[j*8+2] = height/2f; 
    VertexData[j*8+3] = 0.0f;
    VertexData[j*8+4] = 0.0f;
    VertexData[j*8+5] = 1.0f;
    VertexData[j*8+6] = 0.5f;
    VertexData[j*8+7] = 0.5f;
    setColor(j, new Vec4(1,1,1,1));
    setTangent(j, new Vec3(1f,0,0));

    if(smooth){
      offset = (num+1)*2+2;
      for(int i=0;i<num+1;i++){  // lower side
        j = i+offset;
        VertexData[j*8  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*8+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*8+2] = -height/2f; 
        VertexData[j*8+3] = (float)Math.cos(i*2*Math.PI/num);
        VertexData[j*8+4] = (float)Math.sin(i*2*Math.PI/num);
        VertexData[j*8+5] = 0.0f;
        VertexData[j*8+6] = (i*1.0f/(num-1));
        VertexData[j*8+7] = 1.0f;
        setColor(j, new Vec4(1,1,1,1));
        setTangent(j, new Vec3(VertexData[j*8+1],-VertexData[j*8], 0f));
      }
      offset = (num+1)*3+2;
      for(int i=0;i<num+1;i++){ // upper side
        j = i+offset;
        VertexData[j*8  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*8+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*8+2] = height/2f; 
        VertexData[j*8+3] = (float)Math.cos(i*2*Math.PI/num);
        VertexData[j*8+4] = (float)Math.sin(i*2*Math.PI/num);
        VertexData[j*8+5] = 0.0f;
        VertexData[j*8+6] = (i*1.0f/(num-1));
        VertexData[j*8+7] = 0.0f;
        setColor(j, new Vec4(1,1,1,1));
        setTangent(j, new Vec3(VertexData[j*8+1],-VertexData[j*8], 0f));
      }
    }else{ //non smooth
      offset = (num+1)*2+2;
      for(int i=0;i<num;i++){  // lower side
        j = i+offset;
        VertexData[j*8  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*8+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*8+2] = -height/2f; 
        VertexData[j*8+3] = (float)Math.cos((i+0.5)*2*Math.PI/num);
        VertexData[j*8+4] = (float)Math.sin((i+0.5)*2*Math.PI/num);
        VertexData[j*8+5] = 0.0f;
        VertexData[j*8+6] = (i*1.0f/num);
        VertexData[j*8+7] = 1.0f;
        setColor(j, new Vec4(1,1,1,1));
        setTangent(j, new Vec3(VertexData[j*8+1],-VertexData[j*8], 0f));
      }
      offset = (num+1)*2+2+num;
      for(int i=0;i<num;i++){  // lower side 2
        j = i+offset;
        VertexData[j*8  ] = (float)(radius*Math.cos((i+1)*2*Math.PI/num));
        VertexData[j*8+1] = (float)(radius*Math.sin((i+1)*2*Math.PI/num));
        VertexData[j*8+2] = -height/2f; 
        VertexData[j*8+3] = (float)Math.cos((i+0.5)*2*Math.PI/num);
        VertexData[j*8+4] = (float)Math.sin((i+0.5)*2*Math.PI/num);
        VertexData[j*8+5] = 0.0f;
        VertexData[j*8+6] = ((i+1)*1.0f/num);
        VertexData[j*8+7] = 1.0f;
        setColor(j, new Vec4(1,1,1,1));
        setTangent(j, new Vec3(VertexData[j*8+1],-VertexData[j*8], 0f));
      }
      offset = (num+1)*2+2+num*2;
      for(int i=0;i<num;i++){  // upper side
        j = i+offset;
        VertexData[j*8  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*8+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*8+2] = height/2f; 
        VertexData[j*8+3] = (float)Math.cos((i+0.5)*2*Math.PI/num);
        VertexData[j*8+4] = (float)Math.sin((i+0.5)*2*Math.PI/num);
        VertexData[j*8+5] = 0.0f;
        VertexData[j*8+6] = (i*1.0f/num);
        VertexData[j*8+7] = 0.0f;
        setColor(j, new Vec4(1,1,1,1));
        setTangent(j, new Vec3(VertexData[j*8+1],-VertexData[j*8], 0f));
      }
      offset = (num+1)*2+2+num*3;
      for(int i=0;i<num;i++){  // upper side 2
        j = i+offset;
        VertexData[j*8  ] = (float)(radius*Math.cos((i+1)*2*Math.PI/num));
        VertexData[j*8+1] = (float)(radius*Math.sin((i+1)*2*Math.PI/num));
        VertexData[j*8+2] = height/2f;
        VertexData[j*8+3] = (float)Math.cos((i+0.5)*2*Math.PI/num);
        VertexData[j*8+4] = (float)Math.sin((i+0.5)*2*Math.PI/num);
        VertexData[j*8+5] = 0.0f;
        VertexData[j*8+6] = ((i+1)*1.0f/num);
        VertexData[j*8+7] = 0.0f;
        setColor(j, new Vec4(1,1,1,1));
        setTangent(j, new Vec3(VertexData[j*8+1],-VertexData[j*8], 0f));
      }
    }
    ElementData = new int[num*4*3];
    for(int i=0;i<num;i++){ //bottom
      ElementData[i*3]   = 0;
      ElementData[i*3+1] = i+2;
      ElementData[i*3+2] = i+1;
    }
    offset = num;
    for(int i=0;i<num;i++){ //top
      j = offset+i;
      ElementData[j*3]   = (num+1)*2+1;
      ElementData[j*3+1] = num+2+i;
      ElementData[j*3+2] = num+3+i;
    }
    offset = num*2;
    if(smooth){
      for(int i=0;i<num;i++){ //side
        j = offset+i*2;
        ElementData[j*3]   = (num+1)*2+2+i;
        ElementData[j*3+1] = (num+1)*3+2+i+1;
        ElementData[j*3+2] = (num+1)*3+2+i;
        j = offset+i*2+1;
        ElementData[j*3]   = (num+1)*2+2+i;
        ElementData[j*3+1] = (num+1)*2+2+i+1;
        ElementData[j*3+2] = (num+1)*3+2+i+1;
      }
    }else{
      for(int i=0;i<num;i++){ //side
        j = offset+i*2;
        ElementData[j*3]   = (num+1)*2+2+i;
        ElementData[j*3+1] = (num+1)*2+2+num+i;
        ElementData[j*3+2] = (num+1)*2+2+num*2+i;
        j = offset+i*2+1;
        ElementData[j*3]   = (num+1)*2+2+num+i;
        ElementData[j*3+1] = (num+1)*2+2+num*3+i;
        ElementData[j*3+2] = (num+1)*2+2+num*2+i;
      }
    }
    VertexCount = VertexData.length/8;
    VertexSize = VertexData.length*Float.SIZE/8;
    VertexSize2 = VertexData2.length*Float.SIZE/8;
    VertexSize3 = VertexData3.length*Float.SIZE/8;
    FBVertexData = FloatBuffer.wrap(VertexData);
    FBVertexData2 = FloatBuffer.wrap(VertexData2);
    FBVertexData3 = FloatBuffer.wrap(VertexData3);
    PolygonCount = ElementData.length/3;
    ElementCount = ElementData.length;
    ElementSize = ElementCount*Integer.SIZE/8;
    IBElementData = IntBuffer.wrap(ElementData);
  }

  public void init(GL4 gl,Material material){
    super.init(gl,material);
    int[] tmp = new int[1];
    gl.glGenBuffers(1, tmp, 0);
    ArrayBufferName = tmp[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize, FBVertexData, 
                    GL.GL_STATIC_DRAW);
    gl.glGenBuffers(1, tmp, 0);
    ArrayBufferName2 = tmp[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName2);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize2, FBVertexData2,
                    GL.GL_STATIC_DRAW);
    gl.glGenBuffers(1, tmp, 0);
    ArrayBufferName3 = tmp[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName3);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize3, FBVertexData3,
                    GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

    gl.glGenBuffers(1, tmp, 0);
    ElementBufferName = tmp[0];
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, ElementSize, IBElementData, 
                    GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
    material.init(gl);
    this.initialized = true;
  }

  public void display(Matrix4 modelMat, EnvironmentParmaeter parameter){
    if(!this.initialized){
      return;
    }
    this.material.configure(modelMat,parameter);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glVertexAttribPointer(VERTEXPOSITION, 3, GL.GL_FLOAT, 
                             false, 32, 0);
    gl.glVertexAttribPointer(VERTEXNORMAL, 3, GL.GL_FLOAT, 
			     false, 32, NormalOffset);
    gl.glVertexAttribPointer(VERTEXTEXCOORD0, 2, GL.GL_FLOAT,
                             false, 32, TexCoordOffset);

    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName2);
    gl.glVertexAttribPointer(VERTEXCOLOR, 4, GL.GL_FLOAT,
			     false, 16, ColorOffset);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName3);
    gl.glVertexAttribPointer(VERTEXTANGENT, 3, GL.GL_FLOAT,
			     false, 12, TangentOffset);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);

    gl.glEnableVertexAttribArray(VERTEXPOSITION);
    gl.glEnableVertexAttribArray(VERTEXCOLOR);
    gl.glEnableVertexAttribArray(VERTEXNORMAL);
    gl.glEnableVertexAttribArray(VERTEXTEXCOORD0);
    gl.glEnableVertexAttribArray(VERTEXTANGENT);

    gl.glDrawElements(GL.GL_TRIANGLES, ElementCount, GL.GL_UNSIGNED_INT, 0);
  }
}
