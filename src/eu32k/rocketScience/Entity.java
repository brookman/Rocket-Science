package eu32k.rocketScience;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;

import eu32k.tests.Textures;

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

   private SpriteBatch batch;
   private Texture texture;
   private Texture texture2;

   public Entity(String textureName, Body body) {
      batch = new SpriteBatch();
      texture = Textures.tex("pixel.png");
      texture2 = Textures.tex("pixel2.png");
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

      boolean scale = true;

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

      Vector2 before = new Vector2();
      Vector2 current = new Vector2();
      Vector2 after = new Vector2();
      for (int i = 0; i < meshes.length; i++) {
         Fixture fixture = body.getFixtureList().get(i);
         PolygonShape poly = (PolygonShape) fixture.getShape();

         for (int j = 0; j < meshesData[i].length; j += 4) {
            int polyIndex = j / 4;
            poly.getVertex(polyIndex, current);
            // transform.mul(current);

            if (scale) {
               poly.getVertex(polyIndex == 0 ? poly.getVertexCount() - 1 : polyIndex - 1, before);
               // transform.mul(before);
               poly.getVertex(polyIndex == poly.getVertexCount() - 1 ? 0 : polyIndex + 1, after);
               // transform.mul(after);

               before = before.sub(current);
               after = after.sub(current);

               float len = 0.2f;
               float average = (before.angle() + after.angle()) / 2.0f;
               if (polyIndex < poly.getVertexCount() - 1) {
                  average += 180.0f;
               }

               Vector2 newPoint = new Vector2(MathUtils.cos(average / 180.0f * MathUtils.PI) * len, MathUtils.sin(average / 180.0f * MathUtils.PI) * len);

               newPoint = current.cpy().add(newPoint);
               transform.mul(newPoint);

               transform.mul(current);

               // poly.getVertex(polyIndex, newPoint);
               // newPoint.mul(1.2f);
               // transform.mul(newPoint);

               batch.begin();
               batch.draw(texture, current.x * 20 + 200, current.y * 20 + 200);
               batch.draw(texture2, newPoint.x * 20 + 200, newPoint.y * 20 + 200);
               batch.end();
            }

            meshesData[i][j] = current.x;
            meshesData[i][j + 1] = current.y;
            meshesData[i][j + 2] = 0;
            meshesData[i][j + 3] = colors[(color + i) % colors.length];
         }

         // render
         // meshes[i].setVertices(meshesData[i]);
         // meshes[i].render(GL10.GL_TRIANGLE_FAN);
      }

   }

   public Body getBody() {
      return body;
   }
}
