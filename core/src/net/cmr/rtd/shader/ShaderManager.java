package net.cmr.rtd.shader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import net.cmr.rtd.ProjectTetraTD;

/**
 * Manages custom shaders for rendering effects.
 * Custom uniforms:
 * - u_time: Elapsed time in seconds
 * - u_resolution: Screen resolution
 */
public class ShaderManager implements Disposable {
   
    public enum CustomShader {
        GRAYSCALE("grayscale.frag", "default.vert"),
        RAINBOW("rainbow.frag", "default.vert"),
        PLAYER("playerColorShader.frag", "default.vert"),

        ;

        public final String fragmentShaderLocation;
        public final String vertexShaderLocation;

        CustomShader(String fragmentShaderLocation, String vertexShaderLocation) {
            this.fragmentShaderLocation = fragmentShaderLocation;
            this.vertexShaderLocation = vertexShaderLocation;
        }
    }

    private Map<CustomShader, ShaderProgram> shaders = new HashMap<>();
    private static final String SHADER_PATH = "shaders/";
    private static Stack<ShaderProgram> shaderStack = new Stack<>();

    public ShaderManager() {
        loadShaders();
    }

    public void reloadShaders() {
        dispose();
        loadShaders();
        shaderStack.clear();
    }

    public void loadShaders() {
        ShaderProgram.pedantic = false;
        for (CustomShader shader : CustomShader.values()) {
            try {
                String fragmentShader = Gdx.files.internal(SHADER_PATH + shader.fragmentShaderLocation).readString();
                String vertexShader = Gdx.files.internal(SHADER_PATH + shader.vertexShaderLocation).readString();
                ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
                if (!program.isCompiled()) {
                    throw new Exception("Error compiling shader: " + program.getLog());
                }
                shaders.put(shader, program);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    float elapsedTime = 0;

    public void update() {
        if (!shaderStack.isEmpty()) {
            throw new IllegalStateException("Shader stack is not empty. Make sure to call disableShader after rendering is done.");
        }
        elapsedTime += Gdx.graphics.getDeltaTime();
        for (ShaderProgram shader : shaders.values()) {
            shader.bind();
            shader.setUniformf("u_time", elapsedTime);
        }
    }

    /**
     * Enable a shader for the given batch
     * Must call disableShader after rendering is done.
     */
    public void enableShader(Batch batch, CustomShader shader, float... inputs) {
        Objects.requireNonNull(batch);
        ShaderProgram shaderProgram = shaders.get(shader);
        if (shaderProgram == null) {
            return;
        }
        batch.setShader(shaderProgram);
        shaderProgram.bind();
        if (inputs != null) {
            for (int i = 0; i < inputs.length; i++) {
                shaders.get(shader).setUniformf("u_input" + i, inputs[i]);
            }
        }
        /*ShaderProgram program = shaders.get(shader);
        shaderStack.push(batch.getShader());
        program.bind();
        batch.setShader(program);
        if (inputs != null) {
            for (int i = 0; i < inputs.length; i++) {
                program.setUniformf("u_input" + i, inputs[i]);
            }
        }*/
    }
    
    /**
     * Disable the current shader and restore the previous shader
     * Must be called after rendering is done and enableShader was called.
     */
    public void disableShader(Batch batch) {
        Objects.requireNonNull(batch);
        batch.setShader(null);
        /*ShaderProgram program = shaderStack.pop();
        if (program != null) {
            program.bind();
        }
        batch.setShader(program);*/
    }

    public void dispose() {
        for (ShaderProgram shader : shaders.values()) {
            shader.dispose();
        }
    }

}
