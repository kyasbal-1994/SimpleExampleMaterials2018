import com.jogamp.opengl.math.Matrix4;

public class DefaultEnvironmentParameter extends EnvironmentParmaeter {

    public Matrix4 view;

    public Matrix4 projection;

    public DefaultEnvironmentParameter(Matrix4 view,Matrix4 projection){
        this.view = view;
        this.projection = projection;
    }
    @Override
    public void configure() {
        gl.glUniformMatrix4fv(getLocation("view"),1,false,view.getMatrix(),0);
        gl.glUniformMatrix4fv(getLocation("projection"),1,false,projection.getMatrix(),0);
    }
}
