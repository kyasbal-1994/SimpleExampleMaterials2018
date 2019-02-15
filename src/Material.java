import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.PMVMatrix;

public class Material extends GL4Resource {
    /**
     * 頂点シェーダとフラグメントシェーダからマテリアルを作成する
     * @param vertexShader
     * @param fragmentShader
     * @param parameter
     * @return
     */
    public static Material fromVertexFragmentShaders(String vertexShader,String fragmentShader,MaterialParameter parameter){
        Shader shader = new Shader(vertexShader,fragmentShader);
        return new Material(shader,parameter);
    }

    public Shader shader;
    public MaterialParameter parameter;
    public Material(Shader shader,MaterialParameter parameter) {
        this.shader = shader;
        this.parameter = parameter;
    }

    public void init(GL4 gl){
        super.init(gl);
        this.shader.init(gl);
        this.parameter.init(gl,this.shader.programID);
    }

    public void configure(Matrix4 matrix, EnvironmentParmaeter environmentParmaeter){
        environmentParmaeter.init(gl,shader.programID);
        this.gl.glUseProgram(this.shader.programID);
        this.parameter.configure(matrix);
        environmentParmaeter.configure();
    }

}
