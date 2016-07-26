/*
 * Created on Mar 26, 2005
 *
 */
package org.culpan.jdice;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * @author harry
 *  
 */
public abstract class BaseDicePanel extends JPanel implements WindowListener {
    protected final static Random rnd = new Random();
    
    protected final Action diceMenuAction = new AbstractAction() {
        {
            putValue(Action.NAME, "Dice");
            putValue(Action.MNEMONIC_KEY, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
        }
    };

    protected final Action fileMenuAction = new AbstractAction() {
        {
            putValue(Action.NAME, "File");
            putValue(Action.MNEMONIC_KEY, new Integer('F'));
        }

        public void actionPerformed(ActionEvent e) {
        }
    };

    protected final Action quitAction = new AbstractAction() {
        {
            putValue(NAME, "Quit");
            putValue(Action.MNEMONIC_KEY, new Integer('Q'));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
                    .getMenuShortcutKeyMask()));
        }

        public void actionPerformed(ActionEvent e) {
            exit();
        }
    };

    protected void exit() {
        System.exit(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent arg0) {
        exit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent arg0) {
    }
    
    public static int rollDiceTotal(String diceText) throws Exception {
    	int numDice = 0, numSides = 0;
    	
    	String [] diceStats = diceText.split("d");
    	if (diceStats.length != 2) {
    		throw new Exception("Unable to parse '" + diceText + "'");
    	}
    	numDice = Integer.parseInt(diceStats[0]);
    	numSides = Integer.parseInt(diceStats[1]);
    	
    	return rollDiceTotal(numDice, numSides);
    }

    public static int[] rollDice(int numDice, int numSides) {
        int[] result = new int[numDice];

        for (int i = 0; i < numDice; i++) {
            result[i] = rnd.nextInt(numSides) + 1;
        }

        return result;
    }
    
    public static int[] rollDice(float numDice, int numSides) {
        int[] result;// = new int[(int)numDice + 1];
        if (numDice % 1 != 0) {
        	result = new int[(int)numDice + 1];
        } else {
        	result = new int[(int)numDice];
        }

        for (int i = 0; i < (int)numDice; i++) {
            result[i] = rnd.nextInt(numSides) + 1;
        }
        
        if (numDice % 1 != 0) {
            result[(int)numDice] = (int)(((double)(rnd.nextInt(numSides) + 1) * (numDice % 1)) + 0.5);
        } 

        return result;
    }
    
    public static int rollDiceTotal(int numDice, int numSides) {
    	int result = 0;
    	
    	int[] dice = rollDice(numDice, numSides);
    	
    	for (int i = 0; i < dice.length; i++) {
    		result += dice[i];
    	}
    	
    	return result;
    }
}