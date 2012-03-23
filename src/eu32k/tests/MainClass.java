package eu32k.tests;

import java.util.ArrayList;
import java.util.List;

import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.DegeneratedLine2DException;
import math.geom2d.polygon.LinearRing2D;
import math.geom2d.polygon.MultiPolygon2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygon2DUtils;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.SimplePolygon2D;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.jogl.JoglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainClass implements ApplicationListener {

   private boolean running;
   private Stage stage;
   private SpriteBatch batch;

   private Sprite sprite;

   private Music music;
   private Sound sound;

   private int width = 0;
   private int height = 0;

   private Polygon2D landscape;
   private ArrayList<Mesh> meshes = new ArrayList<Mesh>();
   private ArrayList<Mesh> borders = new ArrayList<Mesh>();

   // private ShaderProgram shader;

   @Override
   public void create() {
      stage = new Stage(0, 0, true);
      batch = new SpriteBatch();
      sprite = new Sprite(Textures.tex("tex.png"));

      Textures.init("tex.png");
      music = Gdx.audio.newMusic(Gdx.files.internal("data/music.mp3"));
      sound = Gdx.audio.newSound(Gdx.files.internal("data/sound.ogg"));

      music.setLooping(true);
      music.setVolume(0.5f);
      music.play();

      Spline spline = new Spline();
      for (int i = 0; i < 1280; i += 100 * Math.random() + 20) {
         spline.addVertex(new Point2D(i, 300 + Math.random() * 200));
      }
      spline.addVertex(new Point2D(1280, 300));
      Polyline2D pl = spline.getPolyLine(13);
      pl.addVertex(new Point2D(1280, 720));
      pl.addVertex(new Point2D(0, 720));
      // pl.addVertex(new Point2D(0, 0));
      SimplePolygon2D simple = SimplePolygon2D.create(pl.getVertices());
      landscape = simple;
      // landscapeBackground = simple.clone();

      // landscape.get

      // addMeshes(landscape, Color.toFloatBits(100, 80, 0, 255));

      for (int i = 0; i < 100; i++) {
         Polygon2D explosion = makeCirclePoly(Math.random() * 1280, Math.random() * 720, 20, 20);
         landscape = Polygon2DUtils.difference(landscape, explosion);
      }

      addMeshes(meshes, landscape, Color.toFloatBits(0, 160, 0, 255), true);
      addMeshes(borders, landscape, Color.toFloatBits(0, 0, 0, 255), false);

      System.out.println(meshes.size());

      // ShaderProgram.pedantic = false;
      // shader = SpriteBatch.createDefaultShader();

      running = true;
   }

   private void addMeshes(ArrayList<Mesh> mesheList, Polygon2D polygon, float color, boolean triangulate) {
      EarClippingTriangulator tri = new EarClippingTriangulator();

      for (LinearRing2D ring : polygon.getRings()) {
         try {

            List<Vector2> poly = new ArrayList<Vector2>();

            for (Point2D point : ring.getVertices()) {
               poly.add(new Vector2((float) (point.getX() / 700.0) - 0.9f, (float) ((720 - point.getY()) / 700.0) - 0.9f));
            }

            if (triangulate) {
               poly = tri.computeTriangles(poly);
            }

            float[] pos = new float[poly.size() * 4];
            int i = 0;
            for (Vector2 vec : poly) {
               pos[i++] = vec.x;
               pos[i++] = vec.y;
               pos[i++] = 0;
               pos[i++] = color;
            }

            // Mesh mesh = new Mesh(true, pos.length, pos.length, new VertexAttribute(Usage.Position, 3, "a_position"));
            Mesh mesh = new Mesh(true, pos.length, pos.length, new VertexAttribute(Usage.Position, 3, "a_position"), new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
            mesh.setVertices(pos);
            mesheList.add(mesh);
         } catch (DegeneratedLine2DException e) {
            // nop
         }
      }
   }

   private Polygon2D makeCirclePoly(double x, double y, double r, int resolution) {
      return new MultiPolygon2D((LinearRing2D) new Circle2D(new Point2D(x, y), r).getAsPolyline(resolution));
   }

   @Override
   public void dispose() {
      Textures.dispose();
      stage.dispose();
      music.dispose();
      sound.dispose();
   }

   @Override
   public void pause() {
      running = false;
   }

   @Override
   public void render() {
      if (running) {
         if (Gdx.input.justTouched()) {
            sprite.setPosition(Gdx.input.getX() - sprite.getWidth() / 2, height - Gdx.input.getY() - sprite.getHeight() / 2);
            sound.play();
         }

         // update
         // sprite.setRotation(sprite.getRotation() + Gdx.graphics.getDeltaTime() * 100);

         // // draw
         Gdx.gl.glClearColor(1f / 255f * 180f, 1f / 255f * 230f, 1f, 1);
         // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
         Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

         // batch.begin();
         // sprite.draw(batch);
         //
         // batch.end();

         // shader.begin();
         for (Mesh mesh : meshes) {
            mesh.render(GL10.GL_TRIANGLES, 0, mesh.getNumVertices());
         }

         Gdx.gl.glLineWidth(1.5f);
         for (Mesh mesh : borders) {
            mesh.render(GL10.GL_LINE_LOOP, 0, mesh.getNumVertices());
         }
         // shader.end();
      }
   }

   @Override
   public void resize(int width, int height) {
      this.width = width;
      this.height = height;
   }

   @Override
   public void resume() {
      running = true;
   }

   public static void main(String[] args) {
      new JoglApplication(new MainClass(), "Whatever", 1280, 720, false);
   }
}
