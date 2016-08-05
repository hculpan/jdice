/*
 * Created on Oct 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.culpan.jdice.action.RollAction;

/**
 * @author CulpanH
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RollDiceDialog extends JDialog {
    protected Logger logger = Logger.getLogger(getClass().getName());
    
    protected JSpinner numDice;
    protected JSpinner numSides;
    protected JSpinner mod;
    
    protected JLabel rollLabel;
    protected JLabel dieRollsLabel;
    
    protected JButton roll;
    
    protected RollAction action;
    
    public RollDiceDialog(JFrame parent, boolean modal) {
        this(parent);
        setModal(modal);
    }
    
    public RollDiceDialog(JFrame parent) {
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setTitle("Roll Die");
        
        initializeUi();
        
        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                numSides.requestFocus();
            }
        });
    }
    
    public RollDiceDialog(JFrame parent, RollAction action, boolean modal) {
        this(parent, action);
        setModal(modal);
    }
    
    public RollDiceDialog(JFrame parent, RollAction action) {
        this.action = action;
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setTitle(action.getName());
        
        initializeUi();
        
        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                numSides.requestFocus();
            }
        });
    }
    
    protected void initializeUi() {
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(getCenterContainer(), BorderLayout.CENTER);
        getContentPane().add(getSouthContainer(), BorderLayout.SOUTH);
        
        if (action != null) {
            roll.doClick();
        }
    }
    
    protected Container getCenterContainer() {
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        
        JPanel northPanel = new JPanel();
        
        northPanel.add((numDice = new JSpinner()));
        numDice.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        numDice.setPreferredSize(new Dimension(50, 22));
        
        if (action != null) {
            numDice.setValue(new Integer(action.getNumDice()));
        } 
        
        northPanel.add(new JLabel("d"));
        
        northPanel.add((numSides = new JSpinner()));
        numSides.setModel(new SpinnerListModel(new Integer [] {
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
        
        if (action != null) {
            numSides.setValue(new Integer(action.getNumSides()));
        } else {
            numSides.setValue(new Integer(20));
        }
        
        numSides.setPreferredSize(new Dimension(50, 22));
        
        northPanel.add(new JLabel("+"));
        
        northPanel.add((mod = new JSpinner()));
        mod.setPreferredSize(new Dimension(50, 22));
        
        if (action != null) {
            mod.setValue(new Integer(action.getMod()));
        }
        
        result.add(northPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        
        centerPanel.add((rollLabel = new JLabel()), BorderLayout.CENTER);
        rollLabel.setHorizontalAlignment(JLabel.CENTER);
        Font font = new Font("Arial", Font.BOLD, 32);
        rollLabel.setFont(font);
        
        centerPanel.add((dieRollsLabel = new JLabel()), BorderLayout.NORTH);
        dieRollsLabel.setHorizontalAlignment(JLabel.CENTER);
        
        result.add(centerPanel, BorderLayout.CENTER);
        
        return result;
    }
    
    protected Container getSouthContainer() {
        JPanel result = new JPanel();
        
        roll = new JButton(new AbstractAction() {
            {
                putValue(NAME, "Roll");
            }
            
            public void actionPerformed(ActionEvent e) {
                int roll = 0;
                
                int numRolls = ((Integer)numDice.getValue()).intValue();
                String rollsMsg = "Roll(s) : ";
                for (int i = 0; i < numRolls; i++) {
                    int num = Utils.rnd(((Integer)numSides.getValue()).intValue()) + 1;
                    
                    if (i > 0){
                        rollsMsg += ", " + Integer.toString(num);
                    } else {
                        rollsMsg += Integer.toString(num);
                    }
                    
                    roll += num;
                }
                roll += ((Integer)mod.getValue()).intValue();
                
                dieRollsLabel.setText(rollsMsg);
                rollLabel.setText(Integer.toString(roll));
                
                if (logger.isInfoEnabled()) {
                    String msg = "";
                    if (action != null) {
                        msg += action.getName();
                    } else {
                        msg += numDice.getValue().toString();
                        msg += "d";
                        msg += numSides.getValue().toString();
                        int modifier = ((Integer)mod.getValue()).intValue();
                        if (modifier > 0) {
                            msg += "+" + Integer.toString(modifier);
                        } else if (modifier < 0) {
                            msg += Integer.toString(modifier);
                        }
                    }
                    
                    msg += " : " + Integer.toString(roll);
                    logger.info(msg);
                }
            }
        });
        
        result.add(roll);
        
        final JButton close = new JButton(new AbstractAction() {
            {
                putValue(NAME, "Close");
            }
            
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        result.add(close);
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
        getRootPane().getActionMap().put("esc", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                close.doClick();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK), "roll");
        getRootPane().getActionMap().put("roll", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                roll.doClick();
            }
        });
        getRootPane().setDefaultButton(roll);
        
        return result;
    }
}
