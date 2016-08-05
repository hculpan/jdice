/*
 * Created on Oct 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.culpan.jdice;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.culpan.jdice.action.RollAction;
import org.jdom.Element;

/**
 * @author CulpanH
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataStore {
    protected File file;

    protected Logger logger = Logger.getLogger(getClass().getName());

    protected DiceSession loadFile(File f) {
        DiceSession result = new DiceSession();

        file = f;

        Element root = Utils.loadXml(file);

        for (Iterator i = root.getChildren("dice").iterator(); i.hasNext();) {
            Element dice = (Element) i.next();
            int index = Integer.parseInt(dice.getAttributeValue("index"));
            RollAction action = new RollAction(index);
            action.setName(dice.getChildText("name"));
            action.setNumDice(Integer.parseInt(dice.getChildText("number-dice")));
            action.setNumSides(Integer.parseInt(dice.getChildText("die-type")));
            action.setMod(Integer.parseInt(dice.getChildText("modifier")));
            result.put(index, action);
        }

        Element hitPoints = root.getChild("hit-points");
        if (hitPoints != null) {
            result.setHitPoints(Integer.parseInt(hitPoints.getText()));
        }

        return result;
    }

    protected DiceSession loadFile(File f, DiceSession result) {
        file = f;

        Element root = Utils.loadXml(file);

        for (Iterator i = root.getChildren("dice").iterator(); i.hasNext();) {
            Element dice = (Element) i.next();
            int index = Integer.parseInt(dice.getAttributeValue("index"));
            if (logger.isDebugEnabled()) {
                logger.debug("Loading index " + Integer.toString(index) + ")");
            }
            RollAction action = result.get(index);
            if (action == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating new RollAction(" + Integer.toString(index) + ")");
                }
                action = new RollAction(index);
            }
            action.setName(dice.getChildText("name"));
            action.setNumDice(Integer.parseInt(dice.getChildText("number-dice")));
            action.setNumSides(Integer.parseInt(dice.getChildText("die-type")));
            action.setMod(Integer.parseInt(dice.getChildText("modifier")));
            result.put(index, action);
        }

        Element hitPoints = root.getChild("hit-points");
        if (hitPoints != null) {
            result.setHitPoints(Integer.parseInt(hitPoints.getText()));
        }

        return result;
    }

    protected void saveFile(File f, DiceSession session) {
        if (file.getName().indexOf('.') < 0) {
            file = new File(f.getAbsolutePath() + ".xml");
        } else {
            file = f;
        }

        Element root = new Element("jdice");
        if (logger.isDebugEnabled()) {
            logger.debug("Saving " + Integer.toString(session.size()) + " items");
        }
        int i = 0;
        while (true) {
            if (logger.isDebugEnabled()) {
                logger.debug("Saving item " + Integer.toString(i));
            }

            RollAction action = session.get(i);
            if (action != null) {
                Element d = new Element("dice");
                d.setAttribute("index", Integer.toString(i));
                d.addContent(new Element("name").setText(action.getName()));
                d.addContent(new Element("number-dice").setText(Integer.toString(action.getNumDice())));
                d.addContent(new Element("die-type").setText(Integer.toString(action.getNumSides())));
                d.addContent(new Element("modifier").setText(Integer.toString(action.getMod())));

                if (logger.isDebugEnabled()) {
                    logger.debug(action.toString());
                }

                root.addContent(d);
                i++;
            } else {
                break;
            }
        }

        root.addContent(new Element("hit-points").setText(Integer.toString(session.getHitPoints())));

        Utils.saveXml(root, file.getAbsolutePath());
    }

    /**
     * @return Returns the file.
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     *            The file to set.
     */
    public void setFile(File file) {
        this.file = file;
    }
}