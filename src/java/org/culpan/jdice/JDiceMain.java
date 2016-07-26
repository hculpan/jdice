/*
 * Created on Mar 26, 2005
 *
 */
package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

/**
 * @author harry
 *
 */
public class JDiceMain {
    public static JFrame frame;
    
    public static JLabel chartLabel = new JLabel();

    public static void main(String[] args) {
        try {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JDice");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            frame = new JDiceFrame();
            frame.getContentPane().setLayout(new BorderLayout());
            DicePanel v = new DicePanel();
            
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Dice", v);
            JScrollPane sPane = new JScrollPane(v.rollsList);
            sPane.setPreferredSize(new Dimension(300, 300));
            tabbedPane.addTab("Rolls", sPane);
            
            JDicePanel.parentFrame = frame;
            frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
            
            JPanel btmPanel = new JPanel();
            btmPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            btmPanel.add(chartLabel);
            chartLabel.setFont(chartLabel.getFont().deriveFont(11.0f));
            frame.add(btmPanel, BorderLayout.SOUTH);
            
            frame.addWindowListener(v);
            frame.setTitle("JDice");
            frame.setResizable(true);
            v.setLocation(frame);
            frame.setJMenuBar(v.getJMenuBar());
            frame.setVisible(true);
        } catch (Exception e) {
            Utils.notifyError(e);
        }
    }
}
