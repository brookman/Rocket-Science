package eu32k.rocketScience.entity;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import eu32k.rocketScience.GeometryFactory;

public class Movable extends Entity {

   public Movable(GeometryFactory factory, String name, ShaderProgram shader) {
      super(factory, name, shader);
   }

   @Override
   protected Body createBody() {
      return factory.loadModel(name, 0.0f, 0.0f, 8.0f, 0.5f, BodyType.DynamicBody);
   }
}