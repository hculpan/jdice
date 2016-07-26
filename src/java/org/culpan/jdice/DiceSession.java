/*
 * Created on Oct 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.culpan.jdice;

import javax.swing.ActionMap;

import org.culpan.jdice.action.RollAction;

/**
 * @author CulpanH
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DiceSession {
    protected ActionMap actions = new ActionMap();
    protected int hitPoints = 0;
    
    public DiceSession() {
        
    }
    
    public DiceSession(ActionMap actions) {
        setActions(actions);
    }
    
    /**
     * @return Returns the actions.
     */
    public ActionMap getActions() {
        return actions;
    }

    /**
     * @param actions
     *            The actions to set.
     */
    public void setActions(ActionMap actions) {
        this.actions = actions;
    }

    public void put(Object key, RollAction action) {
        actions.put(key, action);
    }

    public RollAction get(Object key) {
        return (RollAction) actions.get(key);
    }

    public RollAction get(int index) {
        return get(indexToKey(index));
    }

    public void put(int index, RollAction action) {
        put(indexToKey(index), action);
    }

    protected static String indexToKey(int index) {
        return "F" + Integer.toString(index + 1);
    }

    public int size() {
        return actions.size();
    }

    /**
     * @return Returns the hitPoints.
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * @param hitPoints
     *            The hitPoints to set.
     */
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public static DiceSession getDefaultSession() {
        DiceSession result = new DiceSession();

        for (int i = 0; i < 12; i++) {
            RollAction action = new RollAction(i);
            action.setName(indexToKey(i));
            action.setMod(0);
            action.setNumDice(1);
            action.setNumSides(20);
            result.put(i, action);
        }

        return result;
    }
}