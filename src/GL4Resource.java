import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;

public abstract class GL4Resource {
    protected GL4 gl;
    public void init(GL4 gl){
        this.gl = gl;
    }
}
