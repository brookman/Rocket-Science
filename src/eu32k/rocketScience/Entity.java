package eu32k.rocketScience;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;

public class Entity {

   // private Texture texture;
   // private PolygonShape poly;

   private Body body;

   private Mesh[] meshes;
   private float[][] meshesData;

   private static final float[] colors = new float[] { Color.toFloatBits(0, 0, 0, 255), Color.toFloatBits(255, 0, 0, 255), Color.toFloatBits(0, 255, 0, 255), Color.toFloatBits(0, 0, 255, 255),
         Color.toFloatBits(255, 255, 0, 255), Color.toFloatBits(255, 0, 255, 255), Color.toFloatBits(0, 255, 255, 255), Color.toFloatBits(255, 255, 255, 255) };
   private static int colorCounter = 0;

   private int color = colorCounter;

   public Entity(String textureName, Body body) {
      // texture = Textures.tex(textureName);
      this.body = body;
      // mesh = new Mesh(true, 4, 0, new VertexAttribute(Usage.Position, 3, "a_position"), new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

      meshes = new Mesh[body.getFixtureList().size()];
      meshesData = new float[body.getFixtureList().size()][];
      for (int i = 0; i < meshes.length; i++) {
         Fixture fixture = body.getFixtureList().get(i);
         PolygonShape poly = (PolygonShape) fixture.getShape();
         meshesData[i] = new float[poly.getVertexCount() * 4];
         meshes[i] = new Mesh(true, poly.getVertexCount(), 0, new VertexAttribute(Usage.Position, 3, "a_position"), new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
      }

      colorCounter += body.getFixtureList().size();
   }

   public void draw() {
      if (!body.isActive()) {
         return;
      }

      Transform transform = body.getTransform();

      // chain.getVertex(0, v0);
      // transform.mul(v0);
      // chain.getVertex(1, v1);
      // transform.mul(v1);
      // chain.getVertex(2, v2);
      // transform.mul(v2);
      // chain.getVertex(3, v3);
      // transform.mul(v3);

      // mesh.setVertices(new float[] { -5f, -5f, 0, 0, 1, // bottom left
      // 5f, -5f, 0, 1, 1, // bottom right
      // 5f, 5f, 0, 1, 0, // top right
      // -5f, 5f, 0, 0, 0 }); // top left
      //
      // mesh.setVertices(new float[] { v0.x, v0.y, 0, 0, 1, // bottom left
      // v1.x, v1.y, 0, 1, 1, // bottom right
      // v2.x, v2.y, 0, 1, 0, // top right
      // v3.x, v3.y, 0, 0, 0 }); // top left
      Vector2 temp = new Vector2();
      for (int i = 0; i < meshes.length; i++) {
         Fixture fixture = body.getFixtureList().get(i);
         PolygonShape poly = (PolygonShape) fixture.getShape();

         for (int j = 0; j < meshesData[i].length; j += 4) {
            poly.getVertex(j / 4, temp);
            transform.mul(temp);
            meshesData[i][j] = temp.x;
            meshesData[i][j + 1] = temp.y;
            meshesData[i][j + 2] = 0;
            meshesData[i][j + 3] = colors[(color + i) % colors.length];
         }

         meshes[i].setVertices(meshesData[i]);
         meshes[i].render(GL10.GL_TRIANGLE_FAN);
      }
   }

   public Body getBody() {
      return body;
   }
}
