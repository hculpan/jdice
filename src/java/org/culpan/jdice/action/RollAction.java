/*
 * Created on Oct 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.culpan.jdice.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.culpan.jdice.JDicePanel;
import org.culpan.jdice.RollDiceDialog;
import org.culpan.jdice.Utils;

/**
 * @author CulpanH
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RollAction extends AbstractAction {
    int index = 0;

    int numDice = 1;
    int numSides = 20;
    int mod = 0;
    int tempMod = 0;
    String name = null;
    
    public RollAction(int index) {
        this.index = index;
        this.name = "F" + Integer.toString(index + 1);
        this.putValue(NAME, name);
    }
    
    public RollAction(String name, int numDice, int numSides, int mod) {
        index = 0;
        setName(name);
        setNumDice(numDice);
        setNumSides(numSides);
        setMod(mod);
    }

    public RollAction(String name, int numDice, int numSides, int mod, KeyStroke keyStroke, Integer mnemonic) {
        index = 0;
        setName(name);
        setNumDice(numDice);
        setNumSides(numSides);
        setMod(mod);
        
        putValue(ACCELERATOR_KEY, keyStroke);
        putValue(MNEMONIC_KEY, mnemonic);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            RollDiceDialog dlg = new RollDiceDialog(JDicePanel.parentFrame, this);
            dlg.setVisible(true);
        } catch (NumberFormatException excp) {
            excp.printStackTrace(System.err);
            Utils.notifyError(excp);
        }
    }

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return Returns the mod.
     */
    public int getMod() {
        return mod;
    }

    /**
     * @param mod
     *            The mod to set.
     */
    public void setMod(int mod) {
        this.mod = mod;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        putValue(NAME, name);
        this.name = name;
    }

    /**
     * @return Returns the numDice.
     */
    public int getNumDice() {
        return numDice;
    }

    /**
     * @param numDice
     *            The numDice to set.
     */
    public void setNumDice(int numDice) {
        this.numDice = numDice;
    }

    /**
     * @return Returns the numSides.
     */
    public int getNumSides() {
        return numSides;
    }

    /**
     * @param numSides
     *            The numSides to set.
     */
    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    /**
     * @return Returns the tempMod.
     */
    public int getTempMod() {
        return tempMod;
    }

    /**
     * @param tempMod
     *            The tempMod to set.
     */
    public void setTempMod(int tempMod) {
        this.tempMod = tempMod;
    }
}

