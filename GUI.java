package complexfourier;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author arthu
 */
public class GUI extends JFrame implements CustomListener {

    private GraphicPanel panel;
    private CircleTower tower;

    private JSlider lodSlider;

    public GUI() {
        super();

        tower = new CircleTower();
        panel = new GraphicPanel(tower);
        tower.addListener(this);

        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(); // Contains the buttons on the top row and the slider on the bottom row
        topPanel.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();

        JButton startButton = new JButton("Restart");
        startButton.addActionListener((e) -> {
            System.out.println("restart");
            panel.restart();
        });
        buttonsPanel.add(startButton);
        JButton computeButton = new JButton("Compute");
        computeButton.addActionListener((e) -> {
            System.out.println("compute");
            final int nbPoints = panel.getNbPoints();
            tower.setNbCircles(nbPoints);
            lodSlider.setMaximum(nbPoints);
            lodSlider.setValue(nbPoints);
            panel.compute();
            revalidate();
        });
        buttonsPanel.add(computeButton);

        JButton resetViewButton = new JButton("Reset view");
        resetViewButton.addActionListener((e) -> {
            panel.resetView();
        });
        buttonsPanel.add(resetViewButton);

        tower.setNbCircles(panel.getNbPoints());

        JButton savePointsButton = new JButton("Save Points");
        savePointsButton.addActionListener((e) -> {
            savePoints();
        });
        JButton loadPointsButton = new JButton("Load Points");
        loadPointsButton.addActionListener((e) -> {
            loadPoints();
        });
        buttonsPanel.add(savePointsButton);
        buttonsPanel.add(loadPointsButton);

        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener((e) -> {
            panel.setMode(GraphicPanel.GraphicPanelMode.DRAW);
        });
        buttonsPanel.add(drawButton);
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener((e) -> {
            panel.setMode(GraphicPanel.GraphicPanelMode.SELECT);
        });
        buttonsPanel.add(selectButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener((e) -> {
            panel.deleteSelected();
            panel.repaint();
        });
        buttonsPanel.add(deleteButton);

        topPanel.add(buttonsPanel, BorderLayout.NORTH);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BorderLayout());
        JLabel drawProgressionLabel = new JLabel("Draw progression");
        JSlider progressionSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
        progressionSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                panel.setTime(((double) progressionSlider.getValue()) / 1000);
            }
        });
        sliderPanel.add(drawProgressionLabel, BorderLayout.WEST);
        sliderPanel.add(progressionSlider, BorderLayout.CENTER);
        topPanel.add(sliderPanel, BorderLayout.CENTER);

        JPanel lodPanel = new JPanel();
        lodPanel.setLayout(new BorderLayout());
        JLabel lodLabel = new JLabel("Level of detail");
        lodSlider = new JSlider(0, 100);
        tower.setLOD(199);
        lodSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tower.setLOD(lodSlider.getValue());
                panel.repaint();
            }
        });
        lodPanel.add(lodLabel, BorderLayout.WEST);
        lodPanel.add(lodSlider, BorderLayout.CENTER);

        topPanel.add(lodPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);

        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }

    @Override
    public void update() {
    }

    private void savePoints() {
        try {
            panel.savePoints();
        } catch (IOException ex) {
            System.out.println("IO error while saving points");
        }
    }

    private void loadPoints() {
        try {
            panel.loadPoints();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");

        } catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
