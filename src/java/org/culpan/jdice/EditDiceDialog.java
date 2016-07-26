/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import org.culpan.jdice.action.RollAction;

/**
 * @author CulpanH
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EditDiceDialog extends JDialog {
    public int buttonPressed;
    
    protected RollAction action;
    
    protected JTextField nameField;
    protected JSpinner numDiceField;
    protected JSpinner numSidesField;
    protected JSpinner modField;
    
    public EditDiceDialog(JFrame parent, RollAction action) {
        super(parent, true);
        setTitle("Edit Dice");
        setSize(new Dimension(400, 175));
        this.action = action;
        initializeUi();
    }
    
    protected void initializeUi() {
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(getRollInfo(), BorderLayout.CENTER);
        getContentPane().add(getButtons(), BorderLayout.SOUTH);
        
        if (action != null) {
            nameField.setText(action.getName());
            numDiceField.setValue(new Integer(action.getNumDice()));
            numSidesField.setValue(new Integer(action.getNumSides()));
            modField.setValue(new Integer(action.getMod()));
        }
    }
    
    protected Container getRollInfo() {
        JPanel result = new JPanel();
        
        JLabel l;
        
        result.setLayout(new GridLayout(4, 2));
        
        result.add((l = new JLabel("Name ")));
        l.setHorizontalAlignment(JLabel.RIGHT);
        result.add((nameField = new JTextField(15)));
        
        result.add((l = new JLabel("Number of dice")));
        l.setHorizontalAlignment(JLabel.RIGHT);
        result.add((numDiceField = new JSpinner()));
        numDiceField.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        
        result.add((l = new JLabel("Type of die")));
        l.setHorizontalAlignment(JLabel.RIGHT);
        result.add((numSidesField = new JSpinner()));
        numSidesField.setModel(new SpinnerListModel(new Integer[] {
                new Integer(2), 
                new Integer(3), 
                new Integer(4),
                new Integer(6),
                new Integer(8),
                new Integer(10),
                new Integer(12),
                new Integer(20),
                new Integer(100)
                }));
        numSidesField.setValue(new Integer(20));

        result.add((l = new JLabel("Modifer")));
        l.setHorizontalAlignment(JLabel.RIGHT);
        result.add((modField = new JSpinner()));
        
        return result;
    }
    
    protected Container getButtons() {
        JPanel result = new JPanel();
        
        result.add(new JButton(new AbstractAction() {
            {
                putValue(NAME, "Ok");
            }
            
            public void actionPerformed(ActionEvent e) {
                acceptDialog();
            }
        }));
        
        result.add(new JButton(new AbstractAction() {
            {
                putValue(NAME, "Cancel");
            }
            
            public void actionPerformed(ActionEvent e) {
                cancelDialog();
            }
        }));
        
        return result;
    }
    
    protected void cancelDialog() {
        buttonPressed = JOptionPane.CANCEL_OPTION;
        setVisible(false);
    }
    
    protected void acceptDialog() {
        buttonPressed = JOptionPane.OK_OPTION;
        action.setName(nameField.getText());
        action.setNumDice(((Integer)numDiceField.getValue()).intValue());
        action.setNumSides(((Integer)numSidesField.getValue()).intValue());
        action.setMod(((Integer)modField.getValue()).intValue());
        setVisible(false);
    }
}
