package complexfourier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author arthu
 */
public class GUI extends JFrame implements CustomListener {

    private GraphicPanel panel;
    private CircleTower tower;

    private ArrayList<JTextField> textFieldsList;
    private JPanel coefficientsPanel;

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
            tower.setNbCircles(panel.getNbPoints());
            initializeTextFields();
            panel.compute();
            revalidate();
        });
        buttonsPanel.add(computeButton);

        coefficientsPanel = new JPanel();
        coefficientsPanel.setLayout(new GridBagLayout());

        JButton resetViewButton = new JButton("Reset view");
        resetViewButton.addActionListener((e) -> {
            panel.resetView();
        });
        buttonsPanel.add(resetViewButton);

        tower.setNbCircles(panel.getNbPoints());
        initializeTextFields();

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

        topPanel.add(buttonsPanel, BorderLayout.NORTH);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                panel.setTime(((double) slider.getValue()) / 1000);
            }
        });
        topPanel.add(slider, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);

        JScrollPane coefficientsScrollPane = new JScrollPane(coefficientsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        coefficientsScrollPane.setPreferredSize(new Dimension(200, 200));
        this.add(coefficientsScrollPane, BorderLayout.SOUTH);
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }

    private void initializeTextFields() {

        textFieldsList = new ArrayList<>();
        int nbRows = tower.getSize();
        coefficientsPanel.removeAll();

        // Indices column:
        for (int rank = 0; rank < nbRows; rank++) {
            JLabel label = new JLabel("" + (rank - nbRows / 2));
            label.setPreferredSize(new Dimension(15, 20));
            GridBagConstraints c1 = new GridBagConstraints();
            c1.gridx = 0;
            c1.gridwidth = 1;
            c1.gridy = rank;
            coefficientsPanel.add(label, c1);

            // Values column
            JTextField field = new CustomTextField(panel);
            field.setPreferredSize(new Dimension(200, 20));
            GridBagConstraints c2 = new GridBagConstraints();
            c2.gridx = 1;
            c2.gridwidth = 1;
            c2.gridy = rank;
            coefficientsPanel.add(field, c2);
            textFieldsList.add(field);

            JTextField field2 = new CustomTextField(panel);
            field2.setPreferredSize(new Dimension(200, 20));
            GridBagConstraints c3 = new GridBagConstraints();
            c3.gridx = 2;
            c3.gridwidth = 1;
            c3.gridy = rank;
            coefficientsPanel.add(field2, c3);
            textFieldsList.add(field2);
        }
    }

    @Override
    public void update() {
        // Set the text fields from the values of the tower
        for (int rank = 0; rank < tower.getSize(); rank++) {
            double radius = tower.getRadius(rank);
            double argument = tower.getArgument(rank);
            textFieldsList.get(2 * rank).setText(radius + "");
            textFieldsList.get(2 * rank + 1).setText(argument + "");
        }
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
