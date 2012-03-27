package eu32k.rocketScience;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;

public class RocketScience implements ApplicationListener {

   private float aspectRatio;
   private float targetZoom = 20.0f;
   private float zoom = targetZoom;

   private Entity rocket;
   private Entity ground;

   private World world;
   private Camera camera;

   private SpriteBatch batch;
   private Menu menu;
   private ShaderProgram shader;

   @Override
   public void create() {
      world = new World(new Vector2(0.0f, -10.0f), true);

      GeometryFactory gf = new GeometryFactory(world);

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
            + "  gl_FragColor = v_color;\n"// * texture2D(u_texture, v_texCoords);\n" //
            + "}";
      shader = new ShaderProgram(vertexShader, fragmentShader);

      rocket = new Entity("pixel.png", gf.makeRocket(0.0f, 0.0f), shader);
      ground = new Entity("pixel.png", gf.makeGround(0.0f, -3.5f), shader);

      batch = new SpriteBatch();
      menu = Menu.getInstance();

   }

   @Override
   public void resize(int width, int height) {
      aspectRatio = (float) width / (float) height;
      updateCam();
   }

   private void updateCam() {
      camera = new OrthographicCamera(2f * aspectRatio * zoom, 2f * zoom);
   }

   @Override
   public void render() {
      world.step(Gdx.graphics.getDeltaTime(), 8, 3);

      camera.position.x = rocket.getBody().getPosition().x;
      camera.position.y = rocket.getBody().getPosition().y;
      camera.update();

      Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
      // Gdx.gl.glEnable(GL20.GL_BLEND);
      // Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

      Gdx.gl.glClearColor(1f / 255f * 180f, 1f / 255f * 230f, 1f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      Body b = rocket.getBody();
      Transform t = b.getTransform();
      Vector2 base = new Vector2(0.0f, -2.0f);
      t.mul(base);
      if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
         float strength = 200.0f;
         float alpha = b.getAngle() + MathUtils.PI / 2;
         if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            alpha += 0.2;
         }
         if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            alpha -= 0.2;
         }
         Vector2 force = new Vector2(MathUtils.cos(alpha) * strength, MathUtils.sin(alpha) * strength);
         b.applyForce(force, base);
      }

      // Fixture head = b.getFixtureList().get(1);
      // for (Contact contact : world.getContactList()) {
      // if (contact.isTouching() && (contact.getFixtureA() == head || contact.getFixtureB() == head)) {
      // System.out.println("boom");
      // b.setActive(false);
      // }
      // }

      // targetZoom = 20.0f + b.getLinearVelocity().len();
      //
      // zoom += (targetZoom - zoom) * 0.01f;
      // updateCam();

      rocket.draw(camera.combined);
      ground.draw(camera.combined);

      // batch.begin();
      // menu.draw(batch, 20, 50, Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 4);
      // batch.end();
   }

   @Override
   public void pause() {
   }

   @Override
   public void resume() {
   }

   @Override
   public void dispose() {
   }
}
