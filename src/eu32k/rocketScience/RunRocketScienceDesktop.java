package eu32k.rocketScience;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class RunRocketScienceDesktop {
   public static void main(String[] args) {
      RocketScience r = new RocketScience();
      new JoglApplication(r, "Rocket Science", 1280, 720, true);

   }
}
