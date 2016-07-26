package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.culpan.jdice.util.Classpath;

public class SelectHitLocationDialog extends JDialog {
    private final static Logger logger = Logger.getLogger(SelectHitLocationDialog.class);
    
    public HitLocationChart selectedChart = null;
    
    protected List<HitLocationChart> charts = new ArrayList<HitLocationChart>();
    
    protected DefaultListModel listModel = new DefaultListModel();
    protected JList jList;

    public SelectHitLocationDialog(JFrame owner) {
        super(owner);
        setSize(new Dimension(250, 300));
        setLocationRelativeTo(owner);
        setResizable(false);
        setModal(true);
        setTitle("Select Hit Location");
        
        loadCharts();
        initializeUi();
    }

    protected void loadCharts() {
        try {
            String[] fileNames = Classpath.getClasspathFileNamesWithExtension(".hlc");
            for (int i = 0; i < fileNames.length; i++) {
                logger.debug("Loading " + fileNames[i]);
                charts.add(HitLocationChart.createInstanceFromClasspath(fileNames[i]));
            }
            
            File currDir = new File(System.getProperty("user.dir"));
            File [] files = currDir.listFiles(new FileFilter() {
            	public boolean accept(File file) {
            		try {
            			return (!file.isDirectory() && file.getCanonicalPath().toLowerCase().endsWith(".hlc"));
            		} catch (IOException e) {
            			return false;
            		}
            	}
            });
            
            for (int i = 0; i < files.length; i++) {
            	logger.debug("Loading " + files[i].getCanonicalPath());
            	charts.add(HitLocationChart.createInstanceFromFile(files[i]));
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            JOptionPane.showMessageDialog(JDiceMain.frame, e.getLocalizedMessage(), "Error loading charts", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected final Action cancelAction = new AbstractAction() {
        {
            putValue(Action.MNEMONIC_KEY, new Integer('C'));
            putValue(Action.NAME, "Cancel");
        }

        public void actionPerformed(ActionEvent e) {
            selectedChart = null;
            setVisible(false);
        }

    };
    
    protected final Action viewAction = new AbstractAction() {
        {
            putValue(Action.MNEMONIC_KEY, new Integer('V'));
            putValue(Action.NAME, "View Chart");
        }

        public void actionPerformed(ActionEvent e) {
            if (jList.getSelectedIndex() >= 0) {
            	HitLocationChart chart = null;
                String name = (String)listModel.getElementAt(jList.getSelectedIndex());
                for (HitLocationChart c : charts) {
                    if (c.getName().equals(name)) {
                        chart = c;
                        break;
                    }
                }
                
                if (chart != null) {
                	JDialog parent = null;
                	Container current = (Container)e.getSource();
                	while (parent == null) {
                		if (current instanceof JDialog) {
                			parent = (JDialog)current;
                			break;
                		} else {
                			current = current.getParent();
                		}
                	}
                	
                	HitLocationChartDialog hitLocationDialog = new HitLocationChartDialog(parent);
            		hitLocationDialog.chart = chart;
            		hitLocationDialog.setVisible(true);
                }
            }
        }

    };
    
    protected final Action okAction = new AbstractAction() {
        {
            putValue(Action.MNEMONIC_KEY, new Integer('O'));
            putValue(Action.NAME, "Ok");
        }

        public void actionPerformed(ActionEvent e) {
            if (jList.getSelectedIndex() >= 0) {
                String name = (String)listModel.getElementAt(jList.getSelectedIndex());
                for (HitLocationChart c : charts) {
                    if (c.getName().equals(name)) {
                        selectedChart = c;
                        break;
                    }
                }
            }
            setVisible(false);
        }

    };
    
    protected void initializeUi() {
        setLayout(new BorderLayout());
        
        jList = new JList();
        jList.setModel(listModel);
        for (HitLocationChart c : charts) {
            listModel.addElement(c.name);
        }
        
        add(new JScrollPane(jList), BorderLayout.CENTER);
        
        JPanel lower = new JPanel();
        lower.add(new JButton(okAction));
        lower.add(new JButton(cancelAction));
        lower.add(new JButton(viewAction));
        add(lower, BorderLayout.SOUTH);
    }
}
