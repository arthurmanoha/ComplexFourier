package complexfourier;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
public class GUI extends JFrame {// implements MouseListener {

    private GraphicPanel panel;

    public GUI() {
        super();

        panel = new GraphicPanel();

        this.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        JButton startButton = new JButton("Restart");
        startButton.addActionListener((e) -> {
            System.out.println("restart");
            panel.restart();
        });
        buttonsPanel.add(startButton);

        JPanel coefficientsPanel = new JPanel();
        coefficientsPanel.setLayout(new GridLayout(0, 2));
        coefficientsPanel.add(new JLabel("x"));
        coefficientsPanel.add(new JLabel("y"));

        int nbRows = 10;
        for (int rank = 0; rank < 2 * nbRows; rank++) {
            coefficientsPanel.add(new CustomTextField(panel));
        }

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                panel.setTime(((double) slider.getValue()) / 100);
            }
        });
        coefficientsPanel.add(slider);

        this.add(buttonsPanel, BorderLayout.NORTH);
        this.add(coefficientsPanel, BorderLayout.SOUTH);
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }
}
