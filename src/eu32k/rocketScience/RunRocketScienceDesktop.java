package eu32k.rocketScience;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class RunRocketScienceDesktop {
   public static void main(String[] args) {
      new JoglApplication(new RocketScience(), "Rocket Science", 1280, 720, false);
   }
}
