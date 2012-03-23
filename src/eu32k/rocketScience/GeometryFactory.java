package eu32k.rocketScience;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GeometryFactory {

   private World world;

   public GeometryFactory(World world) {
      this.world = world;
   }

   public Body makeRocket(float x, float y) {
      FixtureDef pipe = makeFixture(new Vector2[] { new Vector2(-0.5f, 2.0f), new Vector2(-0.5f, -2.0f), new Vector2(0.5f, -2.0f), new Vector2(0.5f, 2.0f), new Vector2(0.3f, 2.5f),
            new Vector2(-0.3f, 2.5f) }, 1.0f, 0.4f, 0.1f);
      FixtureDef head = makeFixture(new Vector2[] { new Vector2(-0.3f, 2.5f), new Vector2(0.3f, 2.5f), new Vector2(0.0f, 2.8f) }, 1.0f, 0.4f, 0.1f);
      head.isSensor = true;

      FixtureDef engine = makeFixture(new Vector2[] { new Vector2(-0.25f, -2.3f), new Vector2(0.25f, -2.3f), new Vector2(0.0f, -1.8f) }, 1.0f, 0.4f, 0.1f);

      BodyDef bodyDef = makeBodyDef(BodyType.DynamicBody, false, false);

      Body body = makeBody(bodyDef, new FixtureDef[] { engine, head, pipe }, 4f, new Vector2(x, y), 0.0f, head);
      body.setLinearDamping(0.2f);
      body.setAngularDamping(0.5f);

      return body;
   }

   public Body makeGround(float x, float y) {
      FixtureDef ground = makeFixture(new Vector2[] { new Vector2(-100.0f, 1.0f), new Vector2(-100.0f, -1.0f), new Vector2(100.0f, -1.0f), new Vector2(100.0f, 1.0f) }, 1.0f, 0.4f, 0.1f);
      // FixtureDef head = makeFixture(vertices, 1.0f, 0.4f, 0.1f);

      BodyDef bodyDef = makeBodyDef(BodyType.StaticBody, false, false);

      Body body = makeBody(bodyDef, new FixtureDef[] { ground }, 0f, new Vector2(x, y), 0.0f, null);
      return body;
   }

   public Body makeRamp(float x, float y) {
      FixtureDef ground = makeFixture(new Vector2[] { new Vector2(-100.0f, 1.0f), new Vector2(-100.0f, -1.0f), new Vector2(100.0f, -1.0f), new Vector2(100.0f, 1.0f) }, 1.0f, 0.4f, 0.1f);
      // FixtureDef head = makeFixture(vertices, 1.0f, 0.4f, 0.1f);

      BodyDef bodyDef = makeBodyDef(BodyType.StaticBody, false, false);

      Body body = makeBody(bodyDef, new FixtureDef[] { ground }, 0f, new Vector2(x, y), 0.0f, null);
      return body;
   }

   public Body makeBody(BodyDef bd, FixtureDef[] fds, float mass, Vector2 position, float angle, Object userData) {
      Body body = world.createBody(bd);

      for (FixtureDef fd : fds) {
         body.createFixture(fd);
      }

      MassData massData = body.getMassData();
      massData.mass = mass;
      body.setMassData(massData);
      body.setUserData(userData);
      body.setTransform(position, angle);

      return body;
   }

   public BodyDef makeBodyDef(BodyType type, boolean bullet, boolean fixedRotation) {
      BodyDef bd = new BodyDef();
      bd.type = type;
      bd.bullet = bullet;
      bd.fixedRotation = fixedRotation;
      return bd;
   }

   public FixtureDef makeFixture(Vector2[] vertices, float density, float friction, float restitution) {
      FixtureDef fd = new FixtureDef();
      PolygonShape shape = new PolygonShape();
      shape.set(vertices);
      fd.shape = shape;
      fd.density = density;
      fd.friction = friction;
      fd.restitution = restitution;
      return fd;
   }
}
