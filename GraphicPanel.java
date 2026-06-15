package complexfourier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class GraphicPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private ArrayList<Point> points;
    int width, height;
    private CircleTower tower;

    private int x0, y0;
    double zoom;
    private boolean isMouseWheelPressed;
    private int xMouse, yMouse;

    public GraphicPanel() {
        super();

        width = 900;
        height = 400;

        setPreferredSize(new Dimension(width, height));
        points = new ArrayList<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        tower = new CircleTower();

        x0 = width / 2;
        y0 = height / 2;
        zoom = .8;
        isMouseWheelPressed = false;
    }

    @Override
    public void paintComponent(Graphics g) {

        // Wipe previous image
        g.setColor(Color.white.darker());
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);

        // Draw axes
        g.setColor(Color.black);
        g.drawLine(0, y0, g.getClipBounds().width, y0);
        g.drawLine(x0, 0, x0, g.getClipBounds().height);

        // Draw user-defined points
        g.setColor(Color.red);
        int radius = 5;
        Point prev = null;
        for (Point p : points) {
            g.setColor(Color.red);
            g.fillOval((int) (x0 + p.x * zoom - radius), (int) (y0 - p.y * zoom - radius), 2 * radius, 2 * radius);
            if (prev != null) {
                g.setColor(Color.black);
                g.drawLine((int) (x0 + p.x * zoom), (int) (y0 - p.y * zoom),
                        (int) (x0 + prev.x * zoom), (int) (y0 - prev.y * zoom));
            }
            prev = p;
        }
        tower.paint(g, x0, y0, zoom);
    }

    public void receiveClick(double xClick, double yClick) {
        points.add(new Point((int) xClick, (int) yClick));
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            // Mouse click
            isMouseWheelPressed = true;
            xMouse = e.getX();
            yMouse = e.getY();
        } else {

            double xClickReal = (e.getX() - x0) / zoom;
            double yClickReal = (y0 - e.getY()) / zoom;

            receiveClick(xClickReal, yClickReal);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            // Mouse click
            isMouseWheelPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isMouseWheelPressed) {
            int dx = e.getX() - xMouse;
            int dy = e.getY() - yMouse;

            x0 += dx;
            y0 += dy;

            xMouse = e.getX();
            yMouse = e.getY();
            tower.wipe();

            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double dZoom = e.getPreciseWheelRotation(); // 1: zoom out, -1: zoom in

        double zF = 1.1;
        if (dZoom > 0) {
            zF = 1 / zF;
        }
        x0 = (int) (zF * (x0 - xMouse) + xMouse);
        y0 = (int) (zF * (y0 - yMouse) + yMouse);
        zoom = zoom * zF;
        repaint();
    }

    // Wipe the values computed by the tower
    public void restart() {
        tower.wipe();
        repaint();
    }

    public void receiveText(String text, int index) {
        int circleIndex = index / 2;
        boolean isRealPart = (index % 2 == 0);
        double value = Double.valueOf(text);
        tower.updateCoefficient(circleIndex, isRealPart, value);
        repaint();
    }

    public void setTime(double value) {
        tower.setTime(value);
        repaint();
    }
}
