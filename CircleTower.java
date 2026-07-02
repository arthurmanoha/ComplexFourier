package complexfourier;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import static java.lang.Math.PI;
import java.util.ArrayList;

/**
 *
 * @author arthu
 */
public class CircleTower {

    ArrayList<Double> xTab;
    ArrayList<Double> yTab;

    // Frequencies from negative to positive, with the constant in the middle
    ArrayList<Complex> complexValues;
    int initSize = 1;

    private double time;

    private ArrayList<Point> reconstructedPoints;

    private ArrayList<CustomListener> listeners;

    public CircleTower() {
        xTab = new ArrayList<>(initSize);
        yTab = new ArrayList<>(initSize);

        initializeCircles();
        time = 0;
        reconstructedPoints = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void updateCoefficient(int circleIndex, boolean isRealPart, double value) {
        if (isRealPart) {
            xTab.set(circleIndex, value);
        } else {
            yTab.set(circleIndex, value);
        }

        double x = xTab.get(circleIndex);
        double y = yTab.get(circleIndex);
        double newRadius = Math.sqrt(x * x + y * y);
        double newAngle = Math.atan2(y, x);

        complexValues.set(circleIndex, new Complex(newRadius, newAngle));
    }

    public void paintHistory(Graphics g, int x0, int y0, double zoom) {
    }

    public void paint(Graphics g, int x0, int y0, double zoom) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(3));

        double xPointPrev = x0;
        double yPointPrev = y0;
        double xPoint = 0;
        double yPoint = 0;

        int nbSteps = complexValues.size();
        for (int step = 0; step <= nbSteps / 2; step++) {
            try {

                int rank = nbSteps / 2 + (step % 2 != 0 ? ((step + 1) / 2) : (-(step + 1) / 2));
                int frequency = (step + 1) / 2 * (step % 2 == 0 ? -1 : 1);

                Complex number = complexValues.get(rank);
                double radiusApp = number.getRadius() * zoom;
                double startAngle = number.getArgument();

                if (frequency > 0) {
                    g.setColor(Color.red);
                } else if (frequency < 0) {
                    g.setColor(Color.blue);
                } else {
                    g.setColor(Color.white);
                }

//            // Draw the full circle
//            int xAppCircle = (int) (xAppPrev - radius);
//            int yAppCircle = (int) (y0 - yAppPrev - radius);
//            g.drawOval(xAppCircle, yAppCircle, (int) radius * 2, (int) radius * 2);
                // Draw a radius of the circle
                double currentAngle = startAngle + frequency * time * 2 * Math.PI;
                xPoint = xPointPrev + (radiusApp * Math.cos(currentAngle));
                yPoint = yPointPrev - (radiusApp * Math.sin(currentAngle));
                if (radiusApp > 0.01) {
                }
                g.drawLine((int) xPointPrev, (int) yPointPrev, (int) xPoint, (int) yPoint);
                g.drawString("f=" + frequency, (int) (xPointPrev + xPoint) / 2, (int) (yPointPrev + yPoint) / 2);

                g.setColor(Color.black);
                int radius = 5;
                g.fillOval((int) (xPoint - radius), (int) (yPoint - radius), 2 * radius, 2 * radius);

                xPointPrev = xPoint;
                yPointPrev = yPoint;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("                                Not enough points for step = " + step);
            }
        }
        reconstructedPoints.add(new Point((int) xPoint, (int) yPoint));

        // Draw the full curve
        g.setColor(Color.yellow);
        g2.setStroke(new BasicStroke(1));
        Point prev = null;
        for (Point p : reconstructedPoints) {
            if (prev != null) {
                g.drawLine(prev.x, prev.y, p.x, p.y);
            }
            prev = p;
        }
    }

    public void setTime(double value) {
        this.time = value;
    }

    public void wipe() {
        reconstructedPoints.clear();
    }

    public void computeCoefficients(ArrayList<Point2D> userDefinedPoints) {

        initSize = userDefinedPoints.size();

        int lowerBound = -initSize + 1;
        int upperBound = initSize - 1;

//        System.out.println("initSize: " + initSize);
//        System.out.println("Bounds: " + lowerBound + ", " + upperBound);
        complexValues.clear();
        for (int n = lowerBound; n <= upperBound; n++) { // n in [-n/2; n/2]
//            System.out.println("    n=" + n);
            Complex cn = computeCoef(n, userDefinedPoints);
//            complexValues.set(n - lowerBound, cn);
            complexValues.add(n - lowerBound, cn);
        }
        updateListeners();
        wipe();
    }

    private Complex computeCoef(int n, ArrayList<Point2D> userDefinedPoints) {

        int N = userDefinedPoints.size();
        Complex cn = new Complex();

        for (int t = 0; t < N; t++) {

            // f(t)
            Point2D userPoint = userDefinedPoints.get(t);
            Complex fOfT = new Complex();
            fOfT.setCartesianValue(userPoint.getX(), userPoint.getY());

            // e(-n.2pi.i.t)
//            System.out.println("        argument: " + (-n * 2.0 * t / N) + " * PI");
            Complex exponent = new Complex(1, -n * 2.0 * PI * t / N);
//            System.out.println("        (" + n + ", " + t + ") exponent: " + exponent);

            Complex increment = fOfT.mult(exponent);

            cn = cn.add(increment);
//            System.out.println("        cn: " + cn);
        }
        cn = cn.mult(1 / (double) N);
        System.out.println((n >= 0 ? " " : "") + "    C" + n + ": " + cn);

        return cn;
    }

    public void addListener(CustomListener l) {
        listeners.add(l);
    }

    private void updateListeners() {
        for (CustomListener l : listeners) {
            l.update();
        }
    }

    public double getRadius(int rank) {
        return complexValues.get(rank).getRadius();
    }

    public double getArgument(int rank) {
        return complexValues.get(rank).getArgument();
    }

    public int getSize() {
        return complexValues.size();
    }

    public void setNbCircles(int nbPoints) {
        initSize = 2 * nbPoints - 1;
        initializeCircles();
    }

    private void initializeCircles() {
        if (initSize > 0) {
            complexValues = new ArrayList<>(initSize);
            for (int i = 0; i < initSize; i++) {
                xTab.add(new Double(0));
                yTab.add(new Double(0));
                complexValues.add(new Complex(0));
            }
        }
    }

    /**
     * Translate the reconstructed points
     *
     * @param dx
     * @param dy
     */
    protected void drag(int dx, int dy) {
        for (Point p : reconstructedPoints) {
            p.x += dx;
            p.y += dy;
        }
    }
}
