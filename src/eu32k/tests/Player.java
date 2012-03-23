package eu32k.tests;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {

   private ShapeRenderer renderer;

   public Player() {
      renderer = new ShapeRenderer();
      width = 10;
      height = 10;
   }

   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      batch.end();

      renderer.setProjectionMatrix(batch.getProjectionMatrix());
      renderer.setTransformMatrix(batch.getTransformMatrix());
      renderer.translate(x, y, 0);

      renderer.begin(ShapeType.Rectangle);
      renderer.rect(0, 0, width, height);
      renderer.end();

      batch.begin();

   }

   @Override
   public Actor hit(float x, float y) {
      System.out.println("hit");
      return x > 0 && x < width && y > 0 && y < height ? this : null;
   }

   @Override
   public boolean touchDown(float x, float y, int pointer) {
      System.out.println("touchDown");
      return hit(x, y) != null;
   }

   @Override
   public void touchDragged(float x, float y, int pointer) {
      this.x = x;
      this.y = y;
   }

   @Override
   public void touchUp(float x, float y, int pointer) {
   }
}
