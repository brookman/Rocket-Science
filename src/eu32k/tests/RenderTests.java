package eu32k.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.jogl.JoglApplication;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class RenderTests implements ApplicationListener {

   private Mesh mesh;
   private Texture texture;
   private ShaderProgram shader;

   private Camera camera;

   @Override
   public void create() {
      if (mesh == null) {

         mesh = new Mesh(true, 4, 4, new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
               new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));

         mesh.setVertices(new float[] { -0.5f, -0.5f, 0, Color.toFloatBits(255, 0, 0, 255), 0, 1, 0.5f, -0.5f, 0, Color.toFloatBits(0, 255, 0, 255), 1, 1, 0.5f, 0.5f, 0,
               Color.toFloatBits(0, 0, 255, 255), 1, 0, -0.5f, 0.5f, 0, Color.toFloatBits(0, 0, 255, 255), 0, 0 });

         texture = Textures.tex("tex.png");

         String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
               + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
               + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + ";\n" //
               + "uniform mat4 u_projectionViewMatrix;\n" //
               + "varying vec4 v_color;\n" //
               + "varying vec2 v_texCoords;\n" //
               + "\n" //
               + "void main()\n" //
               + "{\n" //
               + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
               + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + ";\n" //
               + "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
               + "}\n";
         String fragmentShader = "#ifdef GL_ES\n" //
               + "#define LOWP lowp\n" //
               + "precision mediump float;\n" //
               + "#else\n" //
               + "#define LOWP \n" //
               + "#endif\n" //
               + "varying LOWP vec4 v_color;\n" //
               + "varying vec2 v_texCoords;\n" //
               + "uniform sampler2D u_texture;\n" //
               + "void main()\n"//
               + "{\n" //
               + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
               + "}";

         shader = new ShaderProgram(vertexShader, fragmentShader);
      }
   }

   @Override
   public void resize(int width, int height) {
      float aspectRatio = (float) width / (float) height;
      camera = new OrthographicCamera(2f * aspectRatio, 2f);
   }

   @Override
   public void render() {
      Gdx.gl.glClearColor(1f / 255f * 180f, 1f / 255f * 230f, 1f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

      // Gdx.gl.glEnable(GL20.GL_BLEND);
      // Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

      shader.begin();

      shader.setUniformMatrix("u_projectionViewMatrix", camera.combined);

      texture.bind();
      mesh.render(shader, GL20.GL_TRIANGLE_FAN, 0, 4);

      shader.end();
   }

   @Override
   public void pause() {
      // TODO Auto-generated method stub

   }

   @Override
   public void resume() {
      // TODO Auto-generated method stub

   }

   @Override
   public void dispose() {
      // TODO Auto-generated method stub

   }

   public static void main(String[] args) {
      new JoglApplication(new RenderTests(), "Render Tests", 1280, 720, true);
   }

}
