package complexfourier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class GraphicPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private ArrayList<Point2D> userDefinedPoints;
    private ArrayList<Boolean> selectionFlags;
    int width, height;
    private CircleTower tower;

    private int x0, y0;
    double zoom;
    private boolean isMouseWheelPressed;
    private double xClickReal, yClickReal;
    private int xMouse, yMouse;

    private String filename = "saved_points.txt";

    public enum GraphicPanelMode {
        DRAW,
        SELECT
    }
    private GraphicPanelMode currentMode;
    private double xClickApp, yClickApp;
    private boolean isSelecting;

    public GraphicPanel() {
        this(null);
    }

    public GraphicPanel(CircleTower newTower) {
        super();

        width = 1000;
        height = 750;

        setPreferredSize(new Dimension(width, height));
        userDefinedPoints = new ArrayList<>();
        selectionFlags = new ArrayList<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        tower = newTower;
        resetView();
        isMouseWheelPressed = false;
        currentMode = GraphicPanelMode.DRAW;
        isSelecting = false;
    }

    public void addPoint(Point2D p) {
        userDefinedPoints.add(p);
        selectionFlags.add(false);
    }

    protected void savePoints() throws IOException {
        File f = new File(filename);
        FileWriter writer = new FileWriter(f);
        for (Point2D p : userDefinedPoints) {
            writer.write(p.getX() + " " + p.getY() + "\n");
        }
        writer.close();
    }

    protected void loadPoints() throws FileNotFoundException, IOException {
        userDefinedPoints.clear();
        selectionFlags.clear();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            double newX = new Double(words[0]);
            double newY = new Double(words[1]);
            userDefinedPoints.add(new Point2D.Double(newX, newY));
            selectionFlags.add(false);
        }
        repaint();
    }

    public void setCircleTower(CircleTower newTower) {
        this.tower = newTower;
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
        // Draw values on axes
        for (int i = -100; i < 100; i++) {
            g.drawString(i + "", (int) (x0 + i * zoom), y0); // x
            g.drawString(i + "", x0, (int) (y0 - i * zoom)); // y
        }

        // Draw grid
        for (int row = -30; row <= 30; row++) {
            for (int col = -30; col <= 30; col++) {
                g.drawRect((int) (x0 + col * zoom), (int) (y0 - row * zoom),
                        2, 2);
            }
        }

        // Draw user-defined points
        g.setColor(Color.red);
        int radius = 5;
        Point2D prev = null;
        int userPointIndex = 0;
        for (Point2D p : userDefinedPoints) {
            try {
                if (selectionFlags.get(userPointIndex)) {
                    int outerRadius = 2 * radius;
                    g.setColor(Color.blue.brighter());
                    g.fillOval((int) (x0 + p.getX() * zoom - outerRadius),
                            (int) (y0 - p.getY() * zoom - outerRadius),
                            2 * outerRadius,
                            2 * outerRadius);
                }

                g.setColor(Color.red);
                g.fillOval((int) (x0 + p.getX() * zoom - radius),
                        (int) (y0 - p.getY() * zoom - radius),
                        2 * radius,
                        2 * radius);
                if (prev != null) {
                    g.setColor(Color.black);
                    g.drawLine((int) (x0 + p.getX() * zoom), (int) (y0 - p.getY() * zoom),
                            (int) (x0 + prev.getX() * zoom), (int) (y0 - prev.getY() * zoom));
                }
                g.drawString("P" + userPointIndex,
                        (int) (x0 + p.getX() * zoom),
                        (int) (y0 - p.getY() * zoom));
                prev = p;
                userPointIndex++;
            } catch (IndexOutOfBoundsException e) {
                // No point in list
            }
        }
        tower.paint(g, x0, y0, zoom);

        if (isSelecting) {

            int xMin = (int) min(xClickApp, xMouse);
            int xMax = (int) max(xClickApp, xMouse);
            int yMin = (int) min(yClickApp, yMouse);
            int yMax = (int) max(yClickApp, yMouse);

            g.setColor(Color.blue);
            g.drawRect(xMin, yMin, xMax - xMin, yMax - yMin);
        }
    }

    public void receiveClick(double xClick, double yClick) {
        addPoint(new Point2D.Double(xClick, yClick));
        repaint();
    }

    public int getNbPoints() {
        return userDefinedPoints.size();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            // Mouse wheel click
            isMouseWheelPressed = true;
            xMouse = e.getX();
            yMouse = e.getY();
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            // Left button
            xMouse = e.getX();
            yMouse = e.getY();
            xClickApp = xMouse;
            yClickApp = yMouse;
            xClickReal = (e.getX() - x0) / zoom;
            yClickReal = (y0 - e.getY()) / zoom;
            if (currentMode.equals(GraphicPanelMode.DRAW)) {
                receiveClick(xClickReal, yClickReal);
            } else {
                isSelecting = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            // Mouse click
            isMouseWheelPressed = false;
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            double xReleaseReal = (e.getX() - x0) / zoom;
            double yReleaseReal = (y0 - e.getY()) / zoom;

            if (currentMode.equals(GraphicPanelMode.SELECT)) {
                selectPoints(xClickReal, yClickReal, xReleaseReal, yReleaseReal);
            }

            isSelecting = false;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        int dx = e.getX() - xMouse;
        int dy = e.getY() - yMouse;
        xMouse = e.getX();
        yMouse = e.getY();

        if (isMouseWheelPressed) {

            x0 += dx;
            y0 += dy;

            tower.drag(dx, dy);

            repaint();
        }
        if (isSelecting) {
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
        tower.wipe();
        repaint();
    }

    // Wipe the values computed by the tower
    public void restart() {
        tower.wipe();
        repaint();
    }

    public void receiveText(String text, int index) {
        System.out.println("panel receiving text <" + text + "> and index " + index);
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

    public void compute() {
        tower.computeCoefficients(userDefinedPoints);
        repaint();
    }

    public final void resetView() {
        x0 = width / 2;
        y0 = height / 2;
        zoom = 50;
        repaint();
    }

    protected void setMode(GraphicPanelMode newMode) {
        this.currentMode = newMode;
    }

    // Flag all points within the specified coordinates as selected, and the other points as non-selected.
    private void selectPoints(double xA, double yA, double xB, double yB) {
        int index = 0;
        try {
            for (Point2D p : userDefinedPoints) {
                selectionFlags.set(index, pointIsInSelection(p, xA, yA, xB, yB));
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            // No point in list
        }
    }

    private boolean pointIsInSelection(Point2D p, double xA, double yA, double xB, double yB) {
        double xMin = Math.min(xA, xB);
        double xMax = Math.max(xA, xB);
        double yMin = Math.min(yA, yB);
        double yMax = Math.max(yA, yB);

        return p.getX() >= xMin && p.getX() <= xMax && p.getY() >= yMin && p.getY() <= yMax;
    }

    protected void deleteSelected() {
        Iterator<Point2D> iter = userDefinedPoints.iterator();
        int index = 0;
        while (iter.hasNext()) {
            Point2D p = iter.next();
            if (selectionFlags.get(index)) {
                // Delete point
                iter.remove();
            }
            index++;
        }
        unselectEverything();
    }

    private void unselectEverything() {
        for (int index = 0; index < selectionFlags.size(); index++) {
            selectionFlags.set(index, false);
        }
    }

    // Subdivide every segment made of two selected points.
    protected void subdivideSelected() {

        ArrayList<Point2D> newPoints = new ArrayList<>();
        ArrayList<Integer> newIndices = new ArrayList<>();

        Point2D prev = null;
        int index = 0;

        for (Point2D p : userDefinedPoints) {

            if (index >= 1 && selectionFlags.get(index - 1) && selectionFlags.get(index)) {
                // Both points selected, must insert new point inbetween
                double newX = (p.getX() + prev.getX()) / 2;
                double newY = (p.getY() + prev.getY()) / 2;
                Point2D newPoint = new Point2D.Double(newX, newY);
                newPoints.add(newPoint);
                newIndices.add(index);
            }

            prev = p;
            index++;
        }

        // Reinsert all new points
        for (int addedIndex = newPoints.size() - 1; addedIndex >= 0; addedIndex--) {
            int rank = newIndices.get(addedIndex);
            Point2D p = newPoints.get(addedIndex);
            userDefinedPoints.add(rank, p);
            selectionFlags.add(rank, true);
        }

        repaint();
        System.out.println("Subdivision done;");
    }

}
