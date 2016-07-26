package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.culpan.jdice.HitLocationChart.Location;

public class HitLocationChartDialog extends JDialog {
//	private final static Logger logger = Logger.getLogger(HitLocationChartDialog.class);
	
	class HLTableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
	
	public HitLocationChart chart;

	protected DefaultTableModel model = new HLTableModel();

	protected final Action okAction = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('O'));
			putValue(Action.NAME, "Ok");
		}

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}

	};
	
	public HitLocationChartDialog(Frame parent) {
		super(parent);
		initializeDialog();
	}

	public HitLocationChartDialog(Dialog parent) {
		super(parent);
		initializeDialog();
	}
	
	protected void initializeDialog() {
		setSize(new Dimension(400, 400));
		setTitle("Hit Location Chart");
		setModal(false);
		setResizable(false);
		setLocationRelativeTo(JDiceMain.frame);

		setLayout(new BorderLayout());

		JTable table = new JTable(model);
		DefaultTableModel m = (DefaultTableModel) table.getModel();
		m.setColumnCount(6);
		m.setColumnIdentifiers(new String[] { "3d6 Roll", "Location", "STUNx", "N STUN", "BODYx", "To Hit" });
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 0; i < m.getColumnCount(); i++) {
			TableColumn c = table.getColumnModel().getColumn(i);
			c.setCellRenderer(r);
		}
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		btnPanel.add(new JButton(okAction));

		add(btnPanel, BorderLayout.SOUTH);
	}
	
	protected String formatNum(float num) {
		String result = "";
		
		int mod = (int)((num % 1) * 10);
		if (mod == 5) {
			if (num < 1) {
				result = "1/2";
			} else {
				result = Integer.toString((int)num) + " 1/2";
			}
		} else if (mod > 0) {
			result = Float.toString(num);
		} else {
			result = Integer.toString((int)num);
		}
		
		return result;
	}
	
	protected void addToTable(Location l, int firstIndex) {
		if (firstIndex != l.number) {
			model.addRow(new Object[] {
					Integer.toString(firstIndex) + "-" + Integer.toString(l.number), l.name,
					"x" + formatNum(l.stunx), "x" + formatNum(l.nstun),
					"x" + formatNum(l.bodyx), formatNum(l.toHit) + " OCV" });
		} else {
			model.addRow(new Object[] { Integer.toString(l.number), l.name,
					"x" + formatNum(l.stunx), "x" + formatNum(l.nstun),
					"x" + formatNum(l.bodyx), formatNum(l.toHit) + " OCV" });
		}
	}

	public void setVisible(boolean value) {
		if (value && chart != null) {
			setTitle(getTitle() + " - " + chart.name);

			model.setRowCount(0);
			Location prevLoc = null;
			int firstIndex = 3;
			for (int i = 3; i < chart.locations.length; i++) {
				Location l = chart.locations[i];
				if (prevLoc != null && !l.equals(prevLoc)) {
					addToTable(prevLoc, firstIndex);
					firstIndex = i;
				}
				prevLoc = l;
			}

			// Need to get the very last one
			addToTable(prevLoc, firstIndex);
		}

		super.setVisible(value);
	}
}
