package eu32k.rocketScience;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;

import eu32k.rocketScience.entity.Entity;
import eu32k.rocketScience.entity.Ground;
import eu32k.rocketScience.entity.Movable;
import eu32k.rocketScience.entity.Rocket;

public class RocketScience implements ApplicationListener {

   private float aspectRatio;
   private float targetZoom = 20.0f;
   private float zoom = targetZoom;

   private Rocket rocket;
   private List<Entity> entities;

   private World world;
   private Camera camera;

   private Menu menu;
   private ShaderProgram shader;

   private SpriteBatch spriteBatch;
   private ParticleEffect explosion;
   private ParticleEffect fire;

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
            + "  gl_FragColor = texture2D(u_texture, v_texCoords);\n" //
            + "}";
      shader = new ShaderProgram(vertexShader, fragmentShader);

      entities = new ArrayList<Entity>();

      rocket = new Rocket(gf, shader);
      entities.add(rocket);

      entities.add(new Movable(gf, "base1.png", shader));
      entities.add(new Movable(gf, "base2.png", shader));

      entities.add(new Entity(gf, "ramp.png", shader));

      for (int i = 1; i < 40; i++) {
         entities.add(new Ground(gf, "ground.png", shader, 8.0f * i, -6.0f));
         entities.add(new Ground(gf, "ground.png", shader, -8.0f * i, -6.0f));
      }

      menu = Menu.getInstance();

      spriteBatch = new SpriteBatch();
      explosion = new ParticleEffect();
      explosion.load(Gdx.files.internal("data/effects/explosion2"), Gdx.files.internal("data/effects"));

      fire = new ParticleEffect();
      fire.load(Gdx.files.internal("data/effects/fire"), Gdx.files.internal("data/effects"));
      fire.start();
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
      Gdx.graphics.setVSync(true);

      Gdx.gl.glClearColor(1f / 255f * 90f, 1f / 255f * 115f, 0.5f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      Body b = rocket.getBody();
      Transform t = b.getTransform();

      if (Gdx.input.isKeyPressed(Input.Keys.UP) && b.isActive()) {
         Vector2 base = new Vector2(0.0f, -2.8f);
         t.mul(base);
         float strength = 200.0f;
         float alpha = b.getAngle() + MathUtils.PI / 2;
         Vector2 force = new Vector2(MathUtils.cos(alpha) * strength, MathUtils.sin(alpha) * strength);
         b.applyForce(force, base);

         spriteBatch.setProjectionMatrix(camera.combined.cpy().translate(base.x, base.y, 0).scl(0.04f).rotate(0, 0, 1, b.getAngle() * 180.0f / MathUtils.PI - 90.0f));
         spriteBatch.begin();
         fire.draw(spriteBatch, Gdx.graphics.getDeltaTime());
         spriteBatch.end();
      }

      if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && b.isActive()) {
         Vector2 base = new Vector2(-0.8f, -1.1f);
         t.mul(base);
         float strength = 50.0f;
         float alpha = b.getAngle() + MathUtils.PI / 2 - 0.6f;
         Vector2 force = new Vector2(MathUtils.cos(alpha) * strength, MathUtils.sin(alpha) * strength);
         b.applyForce(force, base);

         spriteBatch.setProjectionMatrix(camera.combined.cpy().translate(base.x, base.y, 0).scl(0.02f).rotate(0, 0, 1, (b.getAngle() - 0.6f) * 180.0f / MathUtils.PI - 90.0f));
         spriteBatch.begin();
         fire.draw(spriteBatch, Gdx.graphics.getDeltaTime());
         spriteBatch.end();
      }
      if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && b.isActive()) {
         Vector2 base = new Vector2(0.8f, -1.1f);
         t.mul(base);
         float strength = 50.0f;
         float alpha = b.getAngle() + MathUtils.PI / 2 + 0.6f;
         Vector2 force = new Vector2(MathUtils.cos(alpha) * strength, MathUtils.sin(alpha) * strength);
         b.applyForce(force, base);

         spriteBatch.setProjectionMatrix(camera.combined.cpy().translate(base.x, base.y, 0).scl(0.02f).rotate(0, 0, 1, (b.getAngle() + 0.6f) * 180.0f / MathUtils.PI - 90.0f));
         spriteBatch.begin();
         fire.draw(spriteBatch, Gdx.graphics.getDeltaTime());
         spriteBatch.end();
      }

      Fixture head = rocket.head;
      for (Contact contact : world.getContactList()) {
         if (contact.isTouching() && (contact.getFixtureA() == head || contact.getFixtureB() == head)) {
            b.setActive(false);
            explosion.start();
         }
      }

      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
      for (Entity entity : entities) {
         entity.draw(camera.combined);
      }

      if (!explosion.isComplete()) {
         spriteBatch.setProjectionMatrix(camera.combined.cpy().translate(camera.position.x, camera.position.y, 0).scl(0.04f));
         spriteBatch.begin();
         explosion.draw(spriteBatch, Gdx.graphics.getDeltaTime());
         spriteBatch.end();
      }

      // if (!fire.isComplete() && thrusting) {
      //
      // Vector2 base = new Vector2(0.0f, -2.8f);
      // t.mul(base);
      //
      // spriteBatch.setProjectionMatrix(camera.combined.cpy().translate(base.x,
      // base.y, 0).scl(0.04f).rotate(0, 0, 1, b.getAngle() * 180.0f /
      // MathUtils.PI - 90.0f));
      // spriteBatch.begin();
      // fire.draw(spriteBatch, Gdx.graphics.getDeltaTime());
      // spriteBatch.end();
      // }
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
