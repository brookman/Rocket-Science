package eu32k.rocketScience.entity;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import eu32k.rocketScience.GeometryFactory;

public class Rocket extends Entity {

   public Rocket(GeometryFactory factory, ShaderProgram shader) {
      super(factory, "rocket.png", shader);
   }

   @Override
   protected Body createBody(float x, float y) {
      return factory.loadModel(name, 0.0f, 0.0f, 8.0f, 4.0f, BodyType.DynamicBody);
   }
}
