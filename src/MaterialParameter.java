import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

/**
 * マテリアルのパラメータのコンテナ
 */
public abstract class MaterialParameter extends UniformParameterContainer{
    public abstract void configure(Matrix4 matrix);
}
