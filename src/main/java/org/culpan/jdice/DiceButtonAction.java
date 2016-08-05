/*
 * Created on Mar 26, 2005
 *
 */
package org.culpan.jdice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * @author harry
 *  
 */
public class DiceButtonAction extends AbstractAction {
    public final static int NORMAL_DAMAGE = 0;

    public final static int KILLING_DAMAGE = 1;

    public final static int STANDARD_ROLL = 2;

    protected ActionListener actionListener;

    protected int type = STANDARD_ROLL;

    protected String name;

    protected int num;

    protected int sides;

    protected int mod;

    protected String buildName(int num, int sides, int mod, int type) {
        String result = Integer.toString(num) + "d" + Integer.toString(sides);

        if (mod < 0) {
            result += " - " + Integer.toString(Math.abs(mod));
        } else if (mod > 0) {
            result += " + " + Integer.toString(mod);
        }
        
        switch (type) {
        	case NORMAL_DAMAGE:
        	    result += " NA";
        	    break;
        	case KILLING_DAMAGE:
        	    result += " KA";
        	    break;
        	case STANDARD_ROLL:
        	    break;
        	default:
        	    throw new DiceSystemException("Unknown type");
        }

        return result;
    }

    public DiceButtonAction(ActionListener al, int num, int sides, int mod, int type) {
        this.actionListener = al;
        this.num = num;
        this.sides = sides;
        this.mod = mod;
        this.type = type;
        setName(buildName(num, sides, mod, type));
    }

    public DiceButtonAction(ActionListener al, String name, int num, int sides, int mod, int type) {
        this(al, num, sides, mod, type);
        this.name = name;
    }

    public void actionPerformed(ActionEvent e) {
        if (actionListener != null) {
            e.setSource(this);
            actionListener.actionPerformed(e);
        }
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
        if (name != null) {
            putValue(Action.NAME, name);
        }
        this.name = name;
    }

    /**
     * @return Returns the num.
     */
    public int getNum() {
        return num;
    }

    /**
     * @param num
     *            The num to set.
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * @return Returns the sides.
     */
    public int getSides() {
        return sides;
    }

    /**
     * @param sides
     *            The sides to set.
     */
    public void setSides(int sides) {
        this.sides = sides;
    }

    /**
     * @return Returns the actionListener.
     */
    public ActionListener getActionListener() {
        return actionListener;
    }

    /**
     * @param actionListener
     *            The actionListener to set.
     */
    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }
}

