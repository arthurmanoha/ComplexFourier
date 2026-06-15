package complexfourier;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author arthu
 */
public class CircleTower {

    ArrayList<Double> xTab;
    ArrayList<Double> yTab;

    ArrayList<Double> radii;
    ArrayList<Double> angles;

    private double time;

    private ArrayList<Point> results;

    public CircleTower() {
        int initSize = 10;
        xTab = new ArrayList<>(initSize);
        yTab = new ArrayList<>(initSize);
        radii = new ArrayList<>(initSize);
        angles = new ArrayList<>(initSize);
        for (int i = 0; i < initSize; i++) {
            xTab.add(new Double(0));
            yTab.add(new Double(0));
            radii.add(new Double(0));
            angles.add(new Double(0));
        }
        time = 0;
        results = new ArrayList<>();
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

        radii.set(circleIndex, newRadius);
        angles.set(circleIndex, newAngle);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(3));

        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        int xAppPrev = width / 2;
        int yAppPrev = height / 2;
        int xApp = 0;
        int yApp = 0;

        for (int frequency = 0; frequency < radii.size(); frequency++) {
            double radius = radii.get(frequency);
            double startAngle = angles.get(frequency);

            if (frequency % 2 == 0) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.blue);
            }

            // Draw the full circle
            int xAppCircle = (int) (xAppPrev - radius);
            int yAppCircle = (int) (yAppPrev - radius);
            g.drawOval(xAppCircle, yAppCircle, (int) radius * 2, (int) radius * 2);

            // Draw a radius of the circle
            double currentAngle = startAngle + frequency * time * 2 * Math.PI;
            xApp = (int) (xAppPrev + radius * Math.cos(currentAngle));
            yApp = (int) (yAppPrev + radius * Math.sin(currentAngle));
            g.drawLine(xAppPrev, yAppPrev, xApp, yApp);

            xAppPrev = xApp;
            yAppPrev = yApp;
        }
        results.add(new Point(xApp, yApp));

        // Draw the full curve
        g.setColor(Color.blue);
        Point prev = null;
        for (Point p : results) {
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
        results.clear();
    }

}
