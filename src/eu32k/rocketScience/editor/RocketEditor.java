package eu32k.rocketScience.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class RocketEditor extends JFrame {

   public RocketEditor() {
      super("Rocket Editor");
      setLayout(new BorderLayout());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel view = new JPanel();
      JPanel menu = new JPanel();

      add(view, BorderLayout.CENTER);
      add(menu, BorderLayout.WEST);

      setSize(800, 600);
      setLocationRelativeTo(null);
      setVisible(true);
   }

   public static void main(String[] args) {
      try {
         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equals("Nimbus")) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (Exception e) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e2) {
            // NOP
         }
      }
      new RocketEditor();
   }
}
