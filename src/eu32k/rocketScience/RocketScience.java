package eu32k.rocketScience;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;

public class RocketScience implements ApplicationListener {

   private float aspectRatio;
   private float zoom = 20.0f;
   private float targetZoom = 20.0f;

   private SpriteBatch batch;

   private Entity rocket;
   private Entity ground;

   private World world;
   private Camera camera;

   @Override
   public void create() {
      world = new World(new Vector2(0.0f, -10.0f), true);
      // BodyBuilder bb = new BodyBuilder(world);
      // bb = bb.fixture(bb.fixtureDefBuilder().friction(0.4f).density(1.0f).boxShape(1f, 4f).restitution(0.1f));
      //
      // bb = bb.position(0f, 0f).mass(4f).type(BodyType.DynamicBody).userData(rocket);
      // Body rocketBody = bb.build();
      //
      // PolygonShape shape = new PolygonShape();
      // shape.set(ne)
      //
      //
      // rocketBody.setLinearDamping(0.2f);
      // rocketBody.setAngularDamping(0.2f);

      GeometryFactory gf = new GeometryFactory(world);

      batch = new SpriteBatch();

      rocket = new Entity("rocket_blurred.png", gf.makeRocket(0.0f, 0.0f));
      ground = new Entity("ground.png", gf.makeGround(0.0f, -3.5f));

      // Body groundBody = bb.fixture(bb.fixtureDefBuilder().boxShape(40f, 1f).friction(0.4f).restitution(0f)).position(-3f, -4.1f).mass(1f).type(BodyType.StaticBody).build();

      // bb.fixture(bb.fixtureDefBuilder().circleShape(0.5f).restitution(0f)).position(0, -2f).mass(1f).type(BodyType.StaticBody).build();

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

      // world.getContactList().get(0).get

      world.step(Gdx.graphics.getDeltaTime(), 8, 3);

      camera.position.x = rocket.getBody().getPosition().x;
      camera.position.y = rocket.getBody().getPosition().y;
      camera.update();
      camera.apply(Gdx.gl10);

      // Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);
      // Gdx.gl.glEnable(GL10.GL_BLEND);
      Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

      Gdx.gl.glClearColor(1f / 255f * 180f, 1f / 255f * 230f, 1f, 1);
      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

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

      Fixture head = b.getFixtureList().get(1);
      for (Contact contact : world.getContactList()) {
         if (contact.isTouching() && (contact.getFixtureA() == head || contact.getFixtureB() == head)) {
            System.out.println("boom");
            b.setActive(false);
         }
      }

      targetZoom = 20.0f + b.getLinearVelocity().len();

      zoom += (targetZoom - zoom) * 0.01f;
      updateCam();

      rocket.draw();
      ground.draw();

      // box2dDebugRenderer.render(world, camera.projection);
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
