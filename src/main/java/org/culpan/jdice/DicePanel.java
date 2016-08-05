/*
 * Created on Mar 26, 2005
 *
 */
package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.culpan.jdice.HitLocationChart.Location;

/**
 * @author harry
 * 
 */
public class DicePanel extends BaseDicePanel implements ActionListener {
	public final static int KILLING_DAMAGE = 0;

	public final static int NORMAL_DAMAGE = 1;

	public final static int STANDARD_EFFECT = 2;

	public final static int NON_STANDARD_EFFECT = 3;

	class DiceParseException extends Exception {
		public DiceParseException(String msg) {
			super(msg);
		}

		public DiceParseException(String msg, Throwable t) {
			super(msg, t);
		}
	}

	protected DefaultListModel rollsListModel = new DefaultListModel();

	protected JList rollsList = new JList(rollsListModel);

	protected Properties props = new Properties();

	private static final Logger logger = Logger.getLogger(DicePanel.class);

	protected JLabel topResultLabel;

	protected JLabel bottomResultLabel;

	protected JLabel hitLocationResultLabel;

	protected JLabel knockbackResultLabel;

	protected JLabel notesLabel;

	protected JTextField kbDiceField;

	protected JButton knockbackResButton;
	
	protected JButton impairDescrButton;
	
	protected DefaultComboBoxModel hitLocationComboBoxModel = new DefaultComboBoxModel();

	protected JLabel kbDiceLabel;

	protected JMenu diceMenu;

	protected int[] lastDiceRoll;

	protected JScrollPane diceScrollPane;

	protected boolean standalone = true;

	protected boolean useHitLocationKA = false;

	protected boolean useHitLocationND = false;

	protected boolean rollKnockback = false;
	
	protected HitLocationChart chart;

	protected int lastBodyRolled = 0;

	protected int lastStunRolled = 0;

	protected float lastNumRolled = 0;

	protected int lastModRolled = 0;

	protected int lastStunMultRolled = 0;

	protected double lastStunMultTotal = 0;

	protected Location lastLocationRolled = null;

	protected int[] lastRollsRolled = null;

	protected int lastDamage = -1;

	protected int lastKnockbackResRolled = 0;

	protected SyntaxHelpDialog syntaxHelpDialog;
	
	protected HitLocationChartDialog hitLocationDialog;

	protected final Action optionsMenuAction = new AbstractAction() {
		{
			putValue(Action.NAME, "Options");
			putValue(Action.MNEMONIC_KEY, new Integer('O'));
		}

		public void actionPerformed(ActionEvent e) {
		}
	};

	protected final Action closeAction = new AbstractAction() {
		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask()));
			putValue(Action.MNEMONIC_KEY, new Integer('C'));
			putValue(Action.NAME, "Close");
		}

		public void actionPerformed(ActionEvent e) {
			exit();
		}

	};

	protected final Action helpSyntaxAction = new AbstractAction() {
		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask()));
			putValue(Action.MNEMONIC_KEY, new Integer('S'));
			putValue(Action.NAME, "Syntax");
		}

		public void actionPerformed(ActionEvent e) {
			displaySyntaxHelp();
		}

	};

	protected final Action hitLocationDialogAction = new AbstractAction() {
		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask()));
			putValue(Action.MNEMONIC_KEY, new Integer('H'));
			putValue(Action.NAME, "Hit Location Chart");
		}

		public void actionPerformed(ActionEvent e) {
			displayHitLocationChart();
		}

	};

	protected final Action loadHlChart = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('L'));
			putValue(Action.NAME, "Load Hit Location Chart");
		}

		public void actionPerformed(ActionEvent e) {
			loadHitLocationChart();
		}
	};

	protected final Action loadStandardHlChart = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('S'));
			putValue(Action.NAME, "Load Standart Hit Location Chart");
		}

		public void actionPerformed(ActionEvent e) {
			loadStandardHitLocationChart();
		}
	};

	protected Action hitLocationKAAction = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('K'));
			putValue(Action.NAME, "Hit Locations for Killing Attacks");
		}

		public void actionPerformed(ActionEvent e) {
			useHitLocationKA = !useHitLocationKA;
			props.put("use-ka-hit-location", (useHitLocationKA ? "yes" : "no"));
		}

	};

	protected Action hitLocationNDAction = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('N'));
			putValue(Action.NAME, "Hit Locations for Normal Attacks");
		}

		public void actionPerformed(ActionEvent e) {
			useHitLocationND = !useHitLocationND;
			props.put("use-nd-hit-location", (useHitLocationND ? "yes" : "no"));
		}

	};

	protected Action rollKnockbackAction = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('K'));
			putValue(Action.NAME, "Roll Knockback Resistance");
		}

		public void actionPerformed(ActionEvent e) {
			rollKnockback = !rollKnockback;
			props.put("roll-knockback", (rollKnockback ? "yes" : "no"));
			if (!rollKnockback) {
				kbDiceField.setText("");
				kbDiceField.setEnabled(false);
				kbDiceLabel.setEnabled(false);
				knockbackResButton.setEnabled(false);
			}
		}

	};

	class RollAction extends AbstractAction {
		JTextField textField;

		public RollAction(JTextField textField) {
			this.textField = textField;
			putValue(Action.NAME, "Roll");
		}

		public void actionPerformed(ActionEvent e) {
			if (textField != null) {
				try {
					roll(textField.getText(), DiceButtonAction.STANDARD_ROLL);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public DicePanel() {
		this(true);
	}

	public DicePanel(boolean standalone) {
		if (isStandalone()) {
			logger.debug("Is standalone");
			File propsFile = new File(System.getProperty("user.home"), ".jdice");
			if (propsFile.exists()) {
				try {
					props.load(new FileInputStream(propsFile));
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}

		useHitLocationKA = Utils.equals(props.getProperty("use-ka-hit-location"), "yes");
		useHitLocationND = Utils.equals(props.getProperty("use-nd-hit-location"), "yes");
		rollKnockback = Utils.equals(props.getProperty("roll-knockback"), "yes");
		loadDefaultHitLocationChart();
		initializeUi();
	}

	protected JButton buildDiceButton(int num, int sides, int mod, int type) {
		JButton result = new JButton(new DiceButtonAction(this, num, sides, mod, type));

		result.setPreferredSize(new Dimension(26, 26));
		result.setMinimumSize(new Dimension(26, 26));
		result.setMaximumSize(new Dimension(26, 26));

		return result;
	}

	protected int addTextRow(int y, Container p) {
		JPanel panel = new JPanel();

		final JTextField textField = new JTextField(15);
		panel.add(textField);
		panel.add(new JButton(new RollAction(textField)));

		textField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						roll(textField.getText(), DiceButtonAction.STANDARD_ROLL);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			public void keyReleased(KeyEvent e) {

			}

			public void keyTyped(KeyEvent e) {
			}
		});

		p.add(panel);

		return y;
	}

	public void setLocation(Window w) {
		if (props.containsKey("frameLocX") && props.containsKey("frameLocY")) {
			w.setLocation(Integer.parseInt(props.getProperty("frameLocX")), Integer.parseInt(props
					.getProperty("frameLocY")));
		} else {
			w.setLocationRelativeTo(null);
		}
		
		if (props.containsKey("frameWidth")  && props.containsKey("frameHeight")) {
			w.setSize(Integer.parseInt(props.getProperty("frameWidth")), Integer.parseInt(props
					.getProperty("frameHeight")));
		} else {
            w.setSize(new Dimension(550, 550));
		}
	}

	protected void initializeUi() {
		setLayout(new BorderLayout());

		// Add panel of text fields
		JPanel entryPanel = new JPanel();
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.PAGE_AXIS));

		int y = -1;
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);
		y = addTextRow(y, entryPanel);

		add(entryPanel, BorderLayout.WEST);

		// Now add results panel
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));

		Dimension minSize = new Dimension(5, 100);
		Dimension prefSize = new Dimension(5, 100);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		resultPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		resultPanel.add(hitLocationResultLabel = new JLabel());
		hitLocationResultLabel.setHorizontalAlignment(JLabel.CENTER);
		hitLocationResultLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		hitLocationResultLabel.setText("");
		hitLocationResultLabel.setFont(hitLocationResultLabel.getFont().deriveFont(24.0f));
		resultPanel.add(topResultLabel = new JLabel());
		topResultLabel.setHorizontalAlignment(JLabel.CENTER);
		topResultLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		topResultLabel.setText("");
		topResultLabel.setFont(topResultLabel.getFont().deriveFont(24.0f));
		resultPanel.add(bottomResultLabel = new JLabel());
		bottomResultLabel.setHorizontalAlignment(JLabel.CENTER);
		bottomResultLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		bottomResultLabel.setText("");
		bottomResultLabel.setFont(topResultLabel.getFont().deriveFont(24.0f));
		resultPanel.add(notesLabel = new JLabel());
		notesLabel.setHorizontalAlignment(JLabel.CENTER);
		notesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		notesLabel.setText("");
		notesLabel.setFont(topResultLabel.getFont().deriveFont(14.0f));
		resultPanel.add(knockbackResultLabel = new JLabel());
		knockbackResultLabel.setHorizontalAlignment(JLabel.CENTER);
		knockbackResultLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		knockbackResultLabel.setText("");
		knockbackResultLabel.setFont(topResultLabel.getFont().deriveFont(14.0f));

		add(resultPanel, BorderLayout.CENTER);

		rollsList.addMouseListener(new MouseAdapter() {
			protected JPopupMenu popup = buildPopupMenu();

			protected JPopupMenu buildPopupMenu() {
				JPopupMenu result = new JPopupMenu();

				result.add(new JMenuItem(new AbstractAction() {
					{
						putValue(Action.MNEMONIC_KEY, new Integer('C'));
						putValue(Action.NAME, "Clear");
					}

					public void actionPerformed(ActionEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								rollsListModel.clear();
								rollsList.revalidate();
							}
						});
					}
				}));

				return result;
			}

			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(rollsList, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(rollsList, e.getX(), e.getY());
				}
			}
		});

		if (isStandalone()) {
			add(getButtonBar(), BorderLayout.SOUTH);
		}
	}

	Action rollHitLocation = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('L'));
			putValue(Action.NAME, "Roll Hit Location");
		}

		public void actionPerformed(ActionEvent e) {
			rerollHitLocation();
		}

	};

	Action rollKnockbackRes = new AbstractAction() {
		{
			putValue(Action.MNEMONIC_KEY, new Integer('K'));
			putValue(Action.NAME, "Roll KB Resistance");
		}

		public void actionPerformed(ActionEvent e) {
			rerollKnockback();
		}

	};

	protected Container getButtonBar() {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		JPanel hitLocationModPanel = new JPanel();
		hitLocationModPanel.add(new JButton(rollHitLocation));
		JComboBox hitLocationComboBox = new JComboBox(hitLocationComboBoxModel);
		hitLocationComboBox.setPreferredSize(new Dimension(100, 22));
		hitLocationComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object o = hitLocationComboBoxModel.getSelectedItem();
				if (o instanceof Location) {
					setHitLocation((Location)o);
				}
			}
		});
		hitLocationModPanel.add(hitLocationComboBox);

		result.add(hitLocationModPanel, BorderLayout.NORTH);

		JPanel knockbackResPanel = new JPanel();
		knockbackResPanel.add(kbDiceLabel = new JLabel("KB Dice:"));
		kbDiceLabel.setEnabled(false);
		knockbackResPanel.add(kbDiceField = new JTextField(4));
		kbDiceField.setEnabled(false);
		knockbackResPanel.add(knockbackResButton = new JButton(rollKnockbackRes));
		knockbackResButton.setEnabled(false);
		result.add(knockbackResPanel, BorderLayout.SOUTH);

		return result;
	}

	public JMenu getDiceMenu() {
		JMenu dice = new JMenu(diceMenuAction);

		return dice;
	}

	protected final Action helpMenuAction = new AbstractAction() {
		{
			putValue(Action.NAME, "Help");
			putValue(Action.MNEMONIC_KEY, new Integer('H'));
		}

		public void actionPerformed(ActionEvent e) {
		}
	};

	protected JMenuBar getJMenuBar() {
		JMenuBar result = new JMenuBar();

		JMenu file = new JMenu(fileMenuAction);
		file.add(new JMenuItem(loadHlChart));
		file.add(new JMenuItem(loadStandardHlChart));
		file.add(new JSeparator());
		file.add(new JMenuItem(quitAction));
		result.add(file);

		JMenu options = new JMenu(optionsMenuAction);
		JCheckBoxMenuItem hlKa = new JCheckBoxMenuItem(hitLocationKAAction);
		hlKa.setSelected(useHitLocationKA);
		options.add(hlKa);
		JCheckBoxMenuItem hlNd = new JCheckBoxMenuItem(hitLocationNDAction);
		hlNd.setSelected(useHitLocationND);
		options.add(hlNd);

		options.add(new JSeparator());

		JCheckBoxMenuItem rk = new JCheckBoxMenuItem(rollKnockbackAction);
		rk.setSelected(rollKnockback);
		options.add(rk);
		
		result.add(options);
		
		JMenu window = new JMenu(new AbstractAction() {
			{
				putValue(Action.NAME, "Window");
			}
			
			public void actionPerformed(ActionEvent e) {}
		});
		
		window.add(new JMenuItem(new AbstractAction() {
			{
				putValue(Action.NAME, "Default Main Window Size");
			}
			
			public void actionPerformed(ActionEvent e) {
				JDiceMain.frame.setSize(new Dimension(550, 550));
			}
		}));
		window.add(new JMenuItem(new AbstractAction() {
			{
				putValue(Action.NAME, "Default Main Window Location");
			}
			
			public void actionPerformed(ActionEvent e) {
				JDiceMain.frame.setLocationRelativeTo(null);
			}
		}));
		result.add(window);

		JMenu help = new JMenu(helpMenuAction);
		help.add(new JMenuItem(helpSyntaxAction));
		help.add(new JMenuItem(hitLocationDialogAction));
		result.add(help);

		// result.add(getDiceMenu());

		return result;
	}

	protected void viewLastDice() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (diceScrollPane.isVisible()) {
					logger.debug("Is visible; hiding it.");
				} else {
					logger.debug("Is not visible; showing it.");
				}
				diceScrollPane.setVisible(!diceScrollPane.isVisible());
			}
		});
	}

	protected String[] splitText(String text) {
		ArrayList<String> fields = new ArrayList<String>();

		String currField = "";
		boolean isDigitField = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isWhitespace(c) && currField.length() > 0) {
				fields.add(currField);
				currField = "";
			} else if (Character.isDigit(c) || c == '.') {
				if (!isDigitField && currField.length() > 0) {
					fields.add(currField);
					currField = "";
				}
				isDigitField = true;
				currField += c;
			} else if (Character.isLetter(c)) {
				if (isDigitField && currField.length() > 0) {
					fields.add(currField);
					currField = "";
				}
				isDigitField = false;
				currField += c;
			} else if (currField.length() > 0) {
				fields.add(currField);
				currField = "";
				fields.add(Character.toString(c));
			} else if (c == '+' || c == '-' || c == 'd' || c == 'D') {
				fields.add(Character.toString(c));
			}
		}

		if (currField.length() > 0) {
			fields.add(currField);
		}

		String[] result = new String[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			result[i] = fields.get(i).toString();
		}

		return result;
	}

	protected int getStunMultiplier(String[] fields, int currField) throws DiceParseException {
		int result = 0;

		if (currField < fields.length - 1) { // if we have more fields after
			// 'k', e.g., 2+1k+1
			if (fields.length != currField + 3) {
				throw new DiceParseException("Too many terms in expression");
			} else if (fields[currField + 1].equals("+")) {
				result = Integer.parseInt(fields[currField + 2]);
			} else if (fields[currField + 1].equals("-")) {
				result = Integer.parseInt(fields[currField + 1] + fields[currField + 2]);
			} else {
				throw new DiceParseException("Expected EOL; found '" + fields[currField + 1] + "'");
			}
		}

		return result;
	}

	protected void roll(String text, int type) throws DiceParseException {
		String[] fields = splitText(text);

		if (text.trim().length() == 0) {
			return; // Empty text, nothing to do
		} else if (fields.length < 1) {
			throw new DiceParseException("Invalid syntax; found only " + Integer.toString(fields.length) + " tokens");
		} else if (fields[fields.length - 1].equals("+") || fields[fields.length - 1].equals("-")) {
			throw new DiceParseException("Cannot terminate an expression with a '+' or '-'");
		}

		float num = 0;
		int mod = 0, sides = 6, stunMult = 0;
		try {
			num = Float.parseFloat(fields[0]);

			for (int i = 1; i < fields.length; i += 2) {
				if (fields[i].equalsIgnoreCase("d")) {
					sides = Integer.parseInt(fields[i + 1]);
				} else if (fields[i].equals("+")) {
					mod += Integer.parseInt(fields[i + 1]);
				} else if (fields[i].equals("-")) {
					mod -= Integer.parseInt(fields[i + 1]);
				} else if (fields[i].equalsIgnoreCase("k") || fields[i].equalsIgnoreCase("ka")) {
					type = DiceButtonAction.KILLING_DAMAGE;
					stunMult = getStunMultiplier(fields, i);
					break;
				} else if (fields[i].equalsIgnoreCase("n") || fields[i].equalsIgnoreCase("na")
						|| fields[i].equalsIgnoreCase("nd")) {
					type = DiceButtonAction.NORMAL_DAMAGE;
					if (i < fields.length - 1) {
						throw new DiceParseException("Expected EOL; found '" + fields[i + 1] + "'");
					}
				} else {
					throw new DiceParseException("Unrecognized token '" + fields[i] + "'");
				}
			}
		} catch (Exception e) {
			throw new DiceParseException("Error : " + e.getMessage(), e);
		}

		if (sides != 6) {
			rollNonStandard(num, sides, mod);
		} else if (type == DiceButtonAction.NORMAL_DAMAGE) {
			rollNormalDamage(num, mod);
		} else if (type == DiceButtonAction.KILLING_DAMAGE) {
			rollKillingDamage(num, mod, stunMult);
		} else if (type == DiceButtonAction.STANDARD_ROLL) {
			rollStandard(num, mod);
		}
	}

	protected void exit() {
		try {
			props.put("frameLocX", Integer.toString(JDiceMain.frame.getX()));
			props.put("frameLocY", Integer.toString(JDiceMain.frame.getY()));
			props.put("frameWidth", Integer.toString(JDiceMain.frame.getWidth()));
			props.put("frameHeight", Integer.toString(JDiceMain.frame.getHeight()));
			props.store(new FileOutputStream(new File(System.getProperty("user.home"), ".jdice")), "JDice");
		} catch (Exception e) {
			logger.error(e);
		}
		super.exit();
	}

	protected void rollNonStandard(float num, int sides, int mod) {
		int[] rolls = rollDice(num, sides);
		int stun = mod;
		for (int i = 0; i < rolls.length; i++) {
			stun += rolls[i];
		}

		addRollsSummary(formatDice(num, mod) + " Non-Std : ", rolls, "Total = " + Integer.toString(stun), null);
		updateDisplays(determineHitLocation(), stun, 0, 0, NON_STANDARD_EFFECT);
	}

	protected HitLocationChart.Location determineHitLocation() {
		HitLocationChart.Location result = null;

		int loc = rollDiceTotal(3, 6);
		result = chart.getLocation(loc);

		return result;
	}

	protected void updateDisplays(HitLocationChart.Location hitLocation, int stun, int body, double multiplier,
			int rollType) {

		// First set color
		final Color finalColor;
		switch (rollType) {
		case KILLING_DAMAGE:
			finalColor = Color.RED;
			break;
		case NORMAL_DAMAGE:
			finalColor = Color.BLUE;
			break;
		default:
			finalColor = Color.BLACK;
		}

		// Set the location text, if any
		final String finalLocationText;
		if ((useHitLocationKA && rollType == KILLING_DAMAGE) || (useHitLocationND && rollType == NORMAL_DAMAGE)) {
			finalLocationText = hitLocation.name;
			for (int i = 0; i < hitLocationComboBoxModel.getSize(); i++) {
				Location l = (Location)hitLocationComboBoxModel.getElementAt(i);
				if (l.equals(hitLocation)) {
					hitLocationComboBoxModel.setSelectedItem(l);
					break;
				}
			}
		} else {
			finalLocationText = "";
		}

		// Next set stun text
		final String finalStun;
		switch (rollType) {
		case KILLING_DAMAGE:
			finalStun = "Stun : " + Integer.toString(stun) + " (" + Double.toString(multiplier) + ")";
			break;
		case NORMAL_DAMAGE:
			finalStun = "Stun : " + Integer.toString(stun);
			break;
		default:
			finalStun = "Roll : " + Integer.toString(stun);
		}

		// Now do body text
		final String finalBody;
		switch (rollType) {
		case KILLING_DAMAGE:
		case NORMAL_DAMAGE:
			finalBody = "Body : " + Integer.toString(body);
			break;
		default:
			finalBody = "";
		}

		// Put in any notes
		final String notesText;
		switch (rollType) {
		case KILLING_DAMAGE:
			if (useHitLocationKA) {
				notesText = "x" + Double.toString(hitLocation.bodyx) + " applied to BODY";
			} else {
				notesText = "";
			}
			break;
		case NORMAL_DAMAGE:
			if (useHitLocationND) {
				notesText = "x" + Double.toString(hitLocation.bodyx) + "/" + "x" + Double.toString(hitLocation.nstun)
						+ " applied to BODY/STUN";
			} else {
				notesText = "";
			}
			break;
		default:
			notesText = "";
		}

		// Format knockback text
		final String finalKnockbackText;
		if (rollKnockback && (rollType == KILLING_DAMAGE || rollType == NORMAL_DAMAGE)) {
			int totalKb = (lastBodyRolled - lastKnockbackResRolled > 0 ? lastBodyRolled - lastKnockbackResRolled : 0);
			finalKnockbackText = "KB : " + Integer.toString(totalKb) + "\" (rolled "
					+ Integer.toString(lastKnockbackResRolled) + " KB resistance)";
		} else {
			finalKnockbackText = "";
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (finalLocationText != null) {
					hitLocationResultLabel.setText(finalLocationText);
				} else {
					hitLocationResultLabel.setText("");
				}
				hitLocationResultLabel.setForeground(finalColor);

				if (finalStun != null) {
					topResultLabel.setText(finalStun);
				} else {
					topResultLabel.setText("");
				}
				topResultLabel.setForeground(finalColor);

				if (finalBody != null) {
					bottomResultLabel.setText(finalBody);
				} else {
					bottomResultLabel.setText("");
				}
				bottomResultLabel.setForeground(finalColor);

				if (notesText != null) {
					notesLabel.setText(notesText);
				} else {
					notesLabel.setText("");
				}
				notesLabel.setForeground(finalColor);

				knockbackResultLabel.setText(finalKnockbackText);
				knockbackResultLabel.setForeground(finalColor);
			}
		});
	}

	protected static String formatDice(float num, int mod, int stunMod) {
		String result = formatDice(num, mod);
		if (stunMod > 0) {
			result += " (+" + Integer.toString(stunMod) + ")";
		} else if (stunMod < 0) {
			result += " (" + Integer.toString(stunMod) + ")";
		}

		return result;
	}

	protected static String formatDice(float num, int mod) {
		String result;
		if (num % 1 == 0) {
			result = Integer.toString((int) num);
		} else {
			result = Float.toString(num);
		}

		if (mod > 0) {
			result += "+" + Integer.toString(mod);
		} else if (mod < 0) {
			result += Integer.toString(mod);
		}

		return result;
	}

	protected void updateDisplays(HitLocationChart.Location hitLocation, String stun, String body, Color color) {
		final HitLocationChart.Location finalHitLocation = hitLocation;
		final String finalStun = stun;
		final String finalBody = body;
		final Color finalColor = color;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (finalHitLocation != null) {
					hitLocationResultLabel.setText(finalHitLocation.name);
				} else {
					hitLocationResultLabel.setText("");
				}
				hitLocationResultLabel.setForeground(finalColor);

				if (finalStun != null) {
					topResultLabel.setText(finalStun);
				} else {
					topResultLabel.setText("");
				}
				topResultLabel.setForeground(finalColor);

				if (finalBody != null) {
					bottomResultLabel.setText(finalBody);
				} else {
					bottomResultLabel.setText("");
				}
				bottomResultLabel.setForeground(finalColor);
			}
		});
	}

	protected void rollNormalDamage(float num, int mod) {
		HitLocationChart.Location hitLocation = null;
		int[] rolls = rollDice(num, 6);
		int stun = mod, body = 0, knockbackRes = rollKnockback(NORMAL_DAMAGE);
		for (int i = 0; i < rolls.length; i++) {
			stun += rolls[i];
			if (rolls[i] > 5) {
				body += 2;
			} else if (rolls[i] > 1) {
				body += 1;
			}
		}

		if (useHitLocationND) {
			hitLocation = determineHitLocation();
		}

		lastBodyRolled = body;
		lastStunRolled = stun;
		lastNumRolled = num;
		lastModRolled = mod;
		lastRollsRolled = rolls;
		lastDamage = NORMAL_DAMAGE;
		lastKnockbackResRolled = knockbackRes;
		lastLocationRolled = hitLocation;

		if (rollKnockback) {
			kbDiceField.setText("2");
			kbDiceField.setEnabled(true);
			kbDiceLabel.setEnabled(true);
			knockbackResButton.setEnabled(true);
		} else {
			kbDiceField.setText("");
			kbDiceField.setEnabled(false);
			kbDiceLabel.setEnabled(false);
			knockbackResButton.setEnabled(false);
		}

		displayNormalAttack(body, stun, num, mod, rolls, hitLocation);
	}

	protected void displayNormalAttack(int body, int stun, float num, int mod, int[] rolls,
			HitLocationChart.Location hitLocation) {
		String summary = "STUN = " + Integer.toString(stun) + ", BODY = " + body;
		if (hitLocation != null) {
			summary += " [" + hitLocation.name + ":" + hitLocation.number + "]";
		}

		if (hitLocation != null) {
			summary += " [" + hitLocation.name + "]";
		}

		addRollsSummary(formatDice(num, mod) + " ND : ", rolls, summary);
		updateDisplays(hitLocation, stun, body, 0, NORMAL_DAMAGE);
	}

	protected int rollKnockback(int damageType) {
		int result = 0;

		if (rollKnockback && damageType == KILLING_DAMAGE) {
			result = rollDiceTotal(3, 6);
		} else if (rollKnockback && damageType == NORMAL_DAMAGE) {
			result = rollDiceTotal(2, 6);
		}

		return result;
	}

	protected void rollKillingDamage(float num, int mod, int stunMult) {
		HitLocationChart.Location hitLocation = null;
		double multiplier = 0;
		int[] rolls = rollDice(num, 6);
		int body = mod, knockbackRes = rollKnockback(KILLING_DAMAGE);
		for (int i = 0; i < rolls.length; i++) {
			body += rolls[i];
		}
		multiplier = rnd.nextInt(3) + 1;

		if (useHitLocationKA) {
			hitLocation = determineHitLocation();
			multiplier = hitLocation.stunx;
		}

		multiplier += stunMult;

		lastBodyRolled = body;
		lastNumRolled = num;
		lastModRolled = mod;
		lastStunMultRolled = stunMult;
		lastStunMultTotal = multiplier;
		lastRollsRolled = rolls;
		lastDamage = KILLING_DAMAGE;
		lastKnockbackResRolled = knockbackRes;
		lastLocationRolled = hitLocation;
		lastStunRolled = (int) (multiplier * body);

		if (rollKnockback) {
			kbDiceField.setText("3");
			kbDiceField.setEnabled(true);
			kbDiceLabel.setEnabled(true);
			knockbackResButton.setEnabled(true);
		} else {
			kbDiceField.setText("");
			kbDiceField.setEnabled(false);
			kbDiceLabel.setEnabled(false);
			knockbackResButton.setEnabled(false);
		}

		displayKillingAttack(body, multiplier, num, mod, stunMult, rolls, hitLocation);
	}

	protected void displayKillingAttack(int body, double multiplier, float num, int mod, int stunMod, int[] rolls,
			HitLocationChart.Location hitLocation) {
		String summary = "STUN = " + Integer.toString((int) (body * multiplier)) + ", BODY = " + body;
		if (hitLocation != null) {
			summary += " [" + hitLocation.name + ":" + hitLocation.number + "]";
		}

		if (stunMod != 0) {
			addRollsSummary(formatDice(num, mod, stunMod) + " KA : ", rolls, summary);
		} else {
			addRollsSummary(formatDice(num, mod) + " KA : ", rolls, summary);
		}

		updateDisplays(hitLocation, (int) (body * multiplier), body, multiplier, KILLING_DAMAGE);
	}

	protected void rollStandard(float num, int mod) {
		int[] rolls = rollDice(num, 6);
		int effect = mod;
		for (int i = 0; i < rolls.length; i++) {
			effect += rolls[i];
		}

		addRollsSummary(formatDice(num, mod) + " Std : ", rolls, "Total = " + Integer.toString(effect));
		updateDisplays(determineHitLocation(), effect, 0, 0, STANDARD_EFFECT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof DiceButtonAction) {
			DiceButtonAction dba = (DiceButtonAction) arg0.getSource();
			if (dba.getType() == DiceButtonAction.NORMAL_DAMAGE) {
				rollNormalDamage(dba.getNum(), dba.getMod());
			} else if (dba.getType() == DiceButtonAction.KILLING_DAMAGE) {
				rollKillingDamage(dba.getNum(), dba.getMod(), 0);
			} else if (dba.getType() == DiceButtonAction.STANDARD_ROLL) {
				rollStandard(dba.getNum(), dba.getMod());
			} else {
				throw new DiceSystemException("Unknown roll type : " + dba.getType());
			}
		} else {
			logger.warn("Unknown source for action event : " + arg0.getSource().getClass().getName());
		}
	}

	/**
	 * @return Returns the standalone.
	 */
	public boolean isStandalone() {
		return standalone;
	}

	/**
	 * @param standalone
	 *            The standalone to set.
	 */
	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

	public String rollsToString(int[] diceRolls) {
		String result = "";

		if (diceRolls != null && diceRolls.length > 0) {
			result = Integer.toString(diceRolls[0]);

			for (int i = 1; i < diceRolls.length; i++) {
				result += ", " + Integer.toString(diceRolls[i]);
			}
		}

		return result;
	}

	public void addRollsSummary(final String prefix, final int[] diceRolls, final String text, final String suffix) {
		addRollsSummary(prefix, diceRolls, text + " " + suffix);
	}

	public void addRollsSummary(final String prefix, final int[] diceRolls, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				rollsListModel.add(0, prefix + text + " (" + rollsToString(diceRolls) + ")");

				rollsList.invalidate();
				rollsList.repaint();
			}
		});
	}
	
	protected void loadChartIntoComboBox(HitLocationChart chart) {
		Location prevLoc = null;
		hitLocationComboBoxModel.removeAllElements();
		for (int i = 3; i < 19; i++) {
			Location l = chart.getLocation(i);
			if (prevLoc == null || !prevLoc.equals(l)) {
				hitLocationComboBoxModel.addElement(l);
			}
			prevLoc = l;
		}
	}

	protected void loadHitLocationChart() {
		JFileChooser dlg = new JFileChooser();
		dlg.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return (f.isDirectory() || f.getName().toLowerCase().endsWith(".hlc"));
			}
			
			public String getDescription() {
				return "HLC Files (*.hlc)";
			}
		});
		if (dlg.showOpenDialog(JDiceMain.frame) == JFileChooser.APPROVE_OPTION) {
			chart = HitLocationChart.createInstanceFromFile(dlg.getSelectedFile());
			props.put("hit-location-chart", chart.filename);
			JDiceMain.chartLabel.setText("Hit Location Chart : " + chart.name);
			loadChartIntoComboBox(chart);
		}
/*		SelectHitLocationDialog dlg = new SelectHitLocationDialog(JDiceMain.frame);
		dlg.setVisible(true);
		if (dlg.selectedChart != null) {
			chart = dlg.selectedChart;
			props.put("hit-location-chart", chart.filename);
			JDiceMain.chartLabel.setText("Hit Location Chart : " + chart.name);
			loadChartIntoComboBox(chart);
		}*/
	}

	protected void loadStandardHitLocationChart() {
		props.remove("hit-location-chart");
		loadDefaultHitLocationChart();
	}
	
	protected void loadDefaultHitLocationChart() {
		String hlFilename;
		if (props.containsKey("hit-location-chart")) {
			hlFilename = props.getProperty("hit-location-chart");
		} else {
			hlFilename = "standard.hlc";
		}

		chart = HitLocationChart.createInstanceFromClasspath(hlFilename);
		if (chart == null) {
			chart = HitLocationChart.createInstanceFromFile(new File(hlFilename));
			if (chart == null) {
				JOptionPane.showMessageDialog(JDiceMain.frame, "Unable to load chart " + hlFilename
						+ ".\nLoading default chart.", "Failed to load chart", JOptionPane.ERROR_MESSAGE);
				hlFilename = "standard.hlc";
				chart = HitLocationChart.createInstanceFromClasspath(hlFilename);
			}
		}
		
		if (chart != null) {
			loadChartIntoComboBox(chart);
		}
		
		JDiceMain.chartLabel.setText("Hit Location Chart : " + chart.name);
		props.put("hit-location-chart", chart.filename);
	}

	protected void rerollKnockback() {
		int kb = rollDiceTotal(Integer.parseInt(kbDiceField.getText()), 6);
		logger.debug("Rolling " + Integer.parseInt(kbDiceField.getText()) + "dice");
		logger.debug("Roll " + kb + " for KB resistance");
		lastKnockbackResRolled = kb;

		if (lastDamage == KILLING_DAMAGE) {
			updateDisplays(lastLocationRolled, lastStunRolled, lastBodyRolled, lastStunMultTotal, KILLING_DAMAGE);
		} else if (lastDamage == NORMAL_DAMAGE) {
			updateDisplays(lastLocationRolled, lastStunRolled, lastBodyRolled, 0, NORMAL_DAMAGE);
		}
	}
	
	protected void setHitLocation(Location hitLocation) {
		if (lastDamage == KILLING_DAMAGE && useHitLocationKA) {
			float multiplier = hitLocation.stunx;;

			multiplier += lastStunMultRolled;

			displayKillingAttack(lastBodyRolled, multiplier, lastNumRolled, lastModRolled, lastStunMultRolled,
					lastRollsRolled, hitLocation);
		} else if (lastDamage == NORMAL_DAMAGE && useHitLocationND) {
			displayNormalAttack(lastBodyRolled, lastStunRolled, lastNumRolled, lastModRolled, lastRollsRolled,
					hitLocation);
		}
	}

	protected void rerollHitLocation() {
		HitLocationChart.Location hitLocation = null;
		if (lastDamage == KILLING_DAMAGE) {
			float multiplier = 0;

			if (useHitLocationKA) {
				hitLocation = determineHitLocation();
				multiplier = hitLocation.stunx;
			} else {
				int tmp = rnd.nextInt(6);
				multiplier = (tmp < 1 ? 1 : tmp);
			}

			multiplier += lastStunMultRolled;

			displayKillingAttack(lastBodyRolled, multiplier, lastNumRolled, lastModRolled, lastStunMultRolled,
					lastRollsRolled, hitLocation);
		} else if (lastDamage == NORMAL_DAMAGE && useHitLocationND) {
			if (useHitLocationND) {
				hitLocation = determineHitLocation();
			}

			displayNormalAttack(lastBodyRolled, lastStunRolled, lastNumRolled, lastModRolled, lastRollsRolled,
					hitLocation);
		}
	}

	protected void displaySyntaxHelp() {
		if (syntaxHelpDialog == null) {
			syntaxHelpDialog = new SyntaxHelpDialog();
		}
		syntaxHelpDialog.setVisible(true);
	}

	protected void displayHitLocationChart() {
		if (hitLocationDialog == null) {
			hitLocationDialog = new HitLocationChartDialog(JDiceMain.frame);
		}
		hitLocationDialog.chart = chart;
		hitLocationDialog.setVisible(true);
	}

	public boolean isRollKnockback() {
		return rollKnockback;
	}

	public void setRollKnockback(boolean rollKnockback) {
		this.rollKnockback = rollKnockback;
	}
}