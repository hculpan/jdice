/*
 * Created on Oct 18, 2004
 *
 */
package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.culpan.jdice.action.RollAction;

/**
 * @author CulpanH
 * 
 */
public class JDicePanel extends BaseDicePanel implements MouseListener {
    protected final static int MAX_DIE_ROW = 12;

    protected JSpinner currHitPoints;

    public static JFrame parentFrame;

    protected DataStore dataStore = new DataStore();
    protected DiceSession session;

    protected Logger logger = Logger.getLogger(getClass().getName());

    public JDicePanel() {
        initializeUi();

        session = new DiceSession(getActionMap());
    }

    protected void initializeUi() {
        setLayout(new BorderLayout());

        for (int i = 0; i < MAX_DIE_ROW; i++) {
            String key = "F" + Integer.toString(i + 1);
            getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(key), key);
            getActionMap().put(key, new RollAction(i));
        }

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "hp-increase");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK),
                "hp-increase");
        getActionMap().put("hp-increase", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                currHitPoints.getModel().setValue(currHitPoints.getModel().getNextValue());
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "hp-decrease");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK), "hp-decrease");
        getActionMap().put("hp-decrease", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                currHitPoints.getModel().setValue(currHitPoints.getModel().getPreviousValue());
            }
        });

        add(getCenterPanel(), BorderLayout.CENTER);
        add(getSouthPanel(), BorderLayout.SOUTH);
    }

    protected Container getCenterPanel() {
        JPanel result = new JPanel();

        result.setLayout(new GridLayout(4, 3, 5, 5));

        for (int i = 0; i < MAX_DIE_ROW; i++) {
            Action action = getActionMap().get("F" + Integer.toString(i + 1));
            JButton button = new JButton(action);
            result.add(button);
            button.addMouseListener(this);
        }

        return result;
    }

    protected JMenuBar getJMenuBar() {
        JMenuBar result = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(new JMenuItem(new AbstractAction() {
            {
                putValue(NAME, "Open");
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()));
            }

            public void actionPerformed(ActionEvent e) {
                JFileChooser dlg = new JFileChooser();
                dlg.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return (file.isDirectory() || (file.getName().toLowerCase().endsWith(".xml")));
                    }

                    public String getDescription() {
                        return "XML Files";
                    }
                });
                dlg.setCurrentDirectory(new File(System.getProperty("user.dir")));
                dlg.showOpenDialog(null);
                if (dlg.getSelectedFile() != null) {
                    dataStore.loadFile(dlg.getSelectedFile(), session);
                    currHitPoints.setValue(new Integer(session.getHitPoints()));
                    parentFrame.setTitle("JDice - " + dataStore.getFile().getName());
                }

                invalidate();
                repaint();
            }
        }));
        fileMenu.add(new JMenuItem(new AbstractAction() {
            {
                putValue(NAME, "Save");
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()));
            }

            public void actionPerformed(ActionEvent e) {
                File file = dataStore.getFile();

                if (file == null) {
                    JFileChooser dlg = new JFileChooser();
                    dlg.addChoosableFileFilter(new FileFilter() {
                        public boolean accept(File file) {
                            return (file.isDirectory() || (file.getName().toLowerCase().endsWith(".xml")));
                        }

                        public String getDescription() {
                            return "XML Files";
                        }
                    });
                    dlg.setCurrentDirectory(new File(System.getProperty("user.dir")));
                    dlg.showSaveDialog(null);
                    if (dlg.getSelectedFile() != null) {
                        file = dlg.getSelectedFile();
                    }
                }

                if (file != null) {
                    session.setHitPoints(((Integer) currHitPoints.getValue()).intValue());
                    dataStore.saveFile(file, session);
                    parentFrame.setTitle("JDice - " + dataStore.getFile().getName());
                }
            }
        }));

        fileMenu.add(new JMenuItem(new AbstractAction() {
            {
                putValue(NAME, "Save as");
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()));
            }

            public void actionPerformed(ActionEvent e) {
                JFileChooser dlg = new JFileChooser();
                dlg.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return (file.isDirectory() || (file.getName().toLowerCase().endsWith(".xml")));
                    }

                    public String getDescription() {
                        return "XML Files";
                    }
                });
                dlg.setCurrentDirectory(new File(System.getProperty("user.dir")));
                dlg.showSaveDialog(null);
                if (dlg.getSelectedFile() != null) {
                    session.setHitPoints(((Integer) currHitPoints.getValue()).intValue());
                    dataStore.saveFile(dlg.getSelectedFile(), session);
                    parentFrame.setTitle("JDice - " + dataStore.getFile().getName());
                }
            }
        }));

        fileMenu.addSeparator();

        fileMenu.add(new JMenuItem(new AbstractAction() {
            {
                putValue(NAME, "Quit");
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()));
            }

            public void actionPerformed(ActionEvent e) {
                exit();
            }
        }));

        result.add(fileMenu);

        JMenu diceMenu = new JMenu("Dice");
        diceMenu.setMnemonic('D');
        diceMenu.add(new JMenuItem(new AbstractAction() {
            {
                putValue(NAME, "Die roll");
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()));
                putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
            }

            public void actionPerformed(ActionEvent e) {
                rollDieDialog();
            }
        }));

        diceMenu.addSeparator();

        diceMenu.add(new JMenuItem(new RollAction("1d3", 1, 3, 0, KeyStroke.getKeyStroke(KeyEvent.VK_1, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_1))));
        diceMenu.add(new JMenuItem(new RollAction("1d4", 1, 4, 0, KeyStroke.getKeyStroke(KeyEvent.VK_2, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_2))));
        diceMenu.add(new JMenuItem(new RollAction("1d6", 1, 6, 0, KeyStroke.getKeyStroke(KeyEvent.VK_3, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_3))));
        diceMenu.add(new JMenuItem(new RollAction("1d8", 1, 8, 0, KeyStroke.getKeyStroke(KeyEvent.VK_4, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_4))));
        diceMenu.add(new JMenuItem(new RollAction("1d10", 1, 10, 0, KeyStroke.getKeyStroke(KeyEvent.VK_5, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_5))));
        diceMenu.add(new JMenuItem(new RollAction("1d12", 1, 12, 0, KeyStroke.getKeyStroke(KeyEvent.VK_6, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_6))));
        diceMenu.add(new JMenuItem(new RollAction("1d20", 1, 20, 0, KeyStroke.getKeyStroke(KeyEvent.VK_7, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_7))));
        diceMenu.add(new JMenuItem(new RollAction("1d100", 1, 100, 0, KeyStroke.getKeyStroke(KeyEvent.VK_8, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), new Integer(KeyEvent.VK_8))));

        result.add(diceMenu);

        return result;
    }

    protected void rollDieDialog() {
        RollDiceDialog dlg = new RollDiceDialog(parentFrame);
        dlg.setVisible(true);
    }

    protected Container getSouthPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());

        JPanel dice = new JPanel();
        dice.add(new JButton(new RollAction("1d3", 1, 3, 0)));
        dice.add(new JButton(new RollAction("1d4", 1, 4, 0)));
        dice.add(new JButton(new RollAction("1d6", 1, 6, 0)));
        dice.add(new JButton(new RollAction("1d8", 1, 8, 0)));
        dice.add(new JButton(new RollAction("1d10", 1, 10, 0)));
        dice.add(new JButton(new RollAction("1d12", 1, 12, 0)));
        dice.add(new JButton(new RollAction("1d20", 1, 20, 0)));
        dice.add(new JButton(new RollAction("1d100", 1, 100, 0)));
        result.add(dice, BorderLayout.NORTH);

        JPanel hp = new JPanel();
        hp.add(new JLabel("Hit Points : "));
        hp.add((currHitPoints = new JSpinner()));
        currHitPoints.setPreferredSize(new Dimension(50, 26));
        result.add(hp, BorderLayout.SOUTH);
        
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
        if (arg0.isPopupTrigger()) {
            mouseReleased(arg0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
        if (arg0.isPopupTrigger()) {
            JButton button = (JButton) arg0.getSource();
            EditDiceDialog d = new EditDiceDialog(parentFrame, (RollAction) button.getAction());
            d.setLocationRelativeTo(parentFrame);
            d.setVisible(true);
        }
    }
}