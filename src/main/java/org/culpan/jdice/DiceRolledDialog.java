/*
 * Created on Mar 29, 2005
 *
 */
package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * @author culpanh
 *  
 */
public class DiceRolledDialog extends JDialog {
    protected final static String X_LOC = "DiceRolledDialog.x";
    protected final static String Y_LOC = "DiceRolledDialog.y";
    protected final static String WIDTH = "DiceRolledDialog.width";
    protected final static String HEIGHT = "DiceRolledDialog.height";
    
    protected int[] diceRolled;
    protected JList diceRolls;

    protected Action closeAction = new AbstractAction() {
        {
            putValue(Action.NAME, "Close");
            putValue(Action.MNEMONIC_KEY, new Integer('C'));
        }

        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    };

    public DiceRolledDialog(Frame parent) {
        super(parent);
        setSize(new Dimension(150, 300));
        setTitle("Dice Rolled");
        setLocationRelativeTo(parent);
        initializeUi();
    }
    
    public DiceRolledDialog(Frame parent, Properties props) {
        this(parent);
        
        if (props.containsKey(X_LOC) && props.containsKey(Y_LOC)) {
            setLocation(Integer.parseInt(props.getProperty(X_LOC)), Integer.parseInt(props.getProperty(Y_LOC)));
        }
        if (props.containsKey(WIDTH) && props.containsKey(HEIGHT)) {
            setSize(Integer.parseInt(props.getProperty(WIDTH)), Integer.parseInt(props.getProperty(HEIGHT)));
        }
    }
    
    public Properties getProperties() {
        Properties props = new Properties();
        
        props.put(X_LOC, Integer.toString(getX()));
        props.put(Y_LOC, Integer.toString(getY()));
        props.put(WIDTH, Integer.toString(getWidth()));
        props.put(HEIGHT, Integer.toString(getHeight()));
        
        return props;
    }

    protected Container getButtonBar() {
        Container result = new JPanel();

        result.add(new JButton(closeAction));

        return result;
    }

    protected void initializeUi() {
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(new JScrollPane(diceRolls = new JList()));

        getContentPane().add(getButtonBar(), BorderLayout.SOUTH);
    }
    
    protected void updateDiceRolls() {
        if (diceRolled != null) {
            DefaultListModel model = new DefaultListModel();
            for (int i = 0; i < diceRolled.length; i++) {
                model.addElement(Integer.toString(diceRolled[i]));
            }
            diceRolls.setModel(model);
        }
    }

    /**
     * @return Returns the diceRolled.
     */
    public int[] getDiceRolled() {
        return diceRolled;
    }

    /**
     * @param diceRolled
     *            The diceRolled to set.
     */
    public void setDiceRolled(int[] diceRolled) {
        this.diceRolled = diceRolled;
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               updateDiceRolls();
           }
        });
    }
}