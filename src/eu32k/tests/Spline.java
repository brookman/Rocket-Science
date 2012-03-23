package eu32k.tests;

import java.util.ArrayList;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polyline2D;

public class Spline {

   private ArrayList<Point2D> points = new ArrayList<Point2D>();

   private class Cubic {

      private double a, b, c, d;

      public Cubic(double a, double b, double c, double d) {
         this.a = a;
         this.b = b;
         this.c = c;
         this.d = d;
      }

      public double eval(double u) {
         return ((d * u + c) * u + b) * u + a;
      }
   }

   public void addVertex(Point2D p) {
      points.add(p);
   }

   private Cubic[] calcNaturalCubic(double[] x) {
      int n = x.length - 1;
      double[] gamma = new double[x.length];
      double[] delta = new double[x.length];
      double[] d = new double[x.length];
      int i;

      gamma[0] = 1.0f / 2.0f;
      for (i = 1; i < n; i++) {
         gamma[i] = 1 / (4 - gamma[i - 1]);
      }
      gamma[n] = 1 / (2 - gamma[n - 1]);

      delta[0] = 3 * (x[1] - x[0]) * gamma[0];
      for (i = 1; i < n; i++) {
         delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
      }
      delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

      d[n] = delta[n];
      for (i = n - 1; i >= 0; i--) {
         d[i] = delta[i] - gamma[i] * d[i + 1];
      }

      Cubic[] curves = new Cubic[n];
      for (i = 0; i < n; i++) {
         curves[i] = new Cubic(x[i], d[i], 3 * (x[i + 1] - x[i]) - 2 * d[i] - d[i + 1], 2 * (x[i] - x[i + 1]) + d[i] + d[i + 1]);
      }
      return curves;
   }

   public Polyline2D getPolyLine(int steps) {
      Polyline2D line = new Polyline2D();

      double[] xpoints = new double[points.size()];
      double[] ypoints = new double[points.size()];
      for (int i = 0; i < points.size(); i++) {
         xpoints[i] = points.get(i).getX();
         ypoints[i] = points.get(i).getY();
      }
      Cubic[] x = calcNaturalCubic(xpoints);
      Cubic[] y = calcNaturalCubic(ypoints);

      line.addVertex(new Point2D(x[0].eval(0), y[0].eval(0)));

      for (int i = 0; i < x.length; i++) {
         for (int j = 1; j <= steps; j++) {
            double u = j / (double) steps;
            line.addVertex(new Point2D(x[i].eval(u), y[i].eval(u)));
         }
      }
      return line;
   }
}
