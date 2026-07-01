package complexfourier;

import java.util.Random;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author arthu
 */
public class CustomTextField extends JTextField {

    private static int NB_CUSTOM_TEXTFIELDS_CREATED = 0;
    private int id;

    public CustomTextField(GraphicPanel panel) {
        super("0");
        this.id = NB_CUSTOM_TEXTFIELDS_CREATED;

        NB_CUSTOM_TEXTFIELDS_CREATED++;

        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                updatePanel(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                updatePanel(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                updatePanel(e);
            }

            private void updatePanel(DocumentEvent e) {
                try {
                    int length = e.getDocument().getLength();
                    panel.receiveText(e.getDocument().getText(0, length), id);
                } catch (BadLocationException | NumberFormatException exc) {
                }
            }
        });
        if (id % 2 == 0) {
            setText("0");
        }
    }
}
