package complexfourier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class GraphicPanel extends JPanel implements MouseListener {

    private ArrayList<Point> points;
    int width, height;
    private CircleTower tower;

    public GraphicPanel() {
        super();

        width = 900;
        height = 400;

        setPreferredSize(new Dimension(width, height));
        points = new ArrayList<>();
        this.addMouseListener(this);
        tower = new CircleTower();
    }

    @Override
    public void paintComponent(Graphics g) {
        // Wipe previous image
        g.setColor(Color.white);
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);

        // Draw axes
        g.setColor(Color.black);
        g.drawLine(0, height / 2, width, height / 2);
        g.drawLine(width / 2, 0, width / 2, height);

        // Draw points
        g.setColor(Color.red);
        int radius = 5;
        Point prev = null;
        for (Point p : points) {
            g.setColor(Color.red);
            g.fillOval(p.x - radius + width / 2, height / 2 - p.y - radius, 2 * radius, 2 * radius);
            if (prev != null) {
                g.setColor(Color.black);
                g.drawLine(p.x + width / 2, height / 2 - p.y, prev.x + width / 2, height / 2 - prev.y);
            }
            prev = p;
        }
        tower.paint(g);
    }

    public void receiveClick(int clickCol, int clickRow) {
        int col = clickCol - width / 2;
        int row = height / 2 - clickRow;
        points.add(new Point(col, row));
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int clickCol = e.getX();
        int clickRow = e.getY();
        receiveClick(clickCol, clickRow);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
