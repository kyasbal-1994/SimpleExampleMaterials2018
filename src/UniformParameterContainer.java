import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;

import java.util.HashMap;

/**
 * UniformParameterを含むコンテナクラスの基底クラス
 */
public class UniformParameterContainer extends GL4Resource {
    protected int programID;

    public boolean initialized = false;

    private HashMap<String,Integer> uniformNameMap = new HashMap<>();

    public void init(GL4 gl, int programID) {
        if(this.initialized){
            return;
        }
        super.init(gl);
        this.programID = programID;
        initialized = true;
    }

    /**
     * Uniform変数の場所を取ってくる
     * @param name
     * @return
     */
    protected int getLocation(String name){
        if(uniformNameMap.containsKey(name)){
            return uniformNameMap.get(name);
        }else{
            int location = gl.glGetUniformLocation(this.programID,name);
            uniformNameMap.put(name,location);
            return location;
        }
    }
}
