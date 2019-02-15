import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.PMVMatrix;

public class DefaultMaterialParameter extends MaterialParameter {

    private String baseColorTexturePath;

    private String normalMapTexturePath;

    private String roughnessMapTexturePath;

    private String occlusionMapTexturePath;

    private int baseColorTexture;

    private int normalMapTexture;

    private int occlusionMapTexture;

    private int roughnessMapTexture;

    private Vec4 baseColor;

    private float roughness;

    private float metallic;

    public DefaultMaterialParameter(String baseColorTexturePath, String normalMapTexturePath, String roughnessMapTexturePath, String occlusionMapTexturePath, Vec4 baseColor, float roughness, float metallic){
        this.baseColorTexturePath = baseColorTexturePath;
        this.normalMapTexturePath = normalMapTexturePath;
        this.roughnessMapTexturePath = roughnessMapTexturePath;
        this.occlusionMapTexturePath = occlusionMapTexturePath;
        this.baseColor = baseColor;
        this.roughness = roughness;
        this.metallic = metallic;
    }

    @Override
    public void init(GL4 gl,int programID) {
        super.init(gl,programID);

        baseColorTexture = this.createTexture();
        if(baseColorTexturePath!=null) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, baseColorTexture);
            defaultTextureParameter();
            pathToGPU(this.baseColorTexturePath);
        }

        normalMapTexture = this.createTexture();
        if(normalMapTexturePath != null) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, normalMapTexture);
            defaultTextureParameter();
            pathToGPU(this.normalMapTexturePath);
        }

        occlusionMapTexture = this.createTexture();
        if(occlusionMapTexturePath != null){
            gl.glBindTexture(GL.GL_TEXTURE_2D, occlusionMapTexture);
            defaultTextureParameter();
            pathToGPU(this.occlusionMapTexturePath);
        }

        roughnessMapTexture = this.createTexture();
        if(roughnessMapTexturePath != null){
            gl.glBindTexture(GL.GL_TEXTURE_2D, roughnessMapTexture);
            defaultTextureParameter();
            pathToGPU(this.roughnessMapTexturePath);
        }

    }

    @Override
    public void configure(Matrix4 matrix) {
        gl.glUniformMatrix4fv(getLocation("model"), 1, false, matrix.getMatrix(),0);
        gl.glUniform4f(getLocation("baseColorFactor"),baseColor.data[0],baseColor.data[1],baseColor.data[2],baseColor.data[3]);
        gl.glUniform4f(getLocation("textureFlags"),toFlag(baseColorTexturePath),toFlag(roughnessMapTexturePath),toFlag(normalMapTexturePath),toFlag(occlusionMapTexturePath));
        gl.glUniform4f(getLocation("metallicRoughnessFactor"),this.metallic,this.roughness,0,0);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D,baseColorTexture);
        gl.glUniform1i(getLocation("baseColorTexture"),0);

        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D,normalMapTexture);
        gl.glUniform1i(getLocation("normalTexture"),1);

        gl.glActiveTexture(GL.GL_TEXTURE2);
        gl.glBindTexture(GL.GL_TEXTURE_2D,roughnessMapTexture);
        gl.glUniform1i(getLocation("roughnessTexture"),2);

        gl.glActiveTexture(GL.GL_TEXTURE3);
        gl.glBindTexture(GL.GL_TEXTURE_2D, occlusionMapTexture);
        gl.glUniform1i(getLocation("occlusionTexture"),3);
    }

    private int createTexture(){
        int[] texs = new int[1];
        this.gl.glGenTextures(1,texs,0);
        return texs[0];
    }

    private void defaultTextureParameter(){
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S,GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T,GL.GL_REPEAT);
    }

    private void pathToGPU(String path){
        ImageLoader img = new ImageLoader(path);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA8, img.getWidth(),
                img.getHeight(), 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE,
                img.getByteBuffer());
    }

    private int toFlag(String path){
        return path == null ? 0:1;
    }
}
