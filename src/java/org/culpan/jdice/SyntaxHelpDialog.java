package org.culpan.jdice;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class SyntaxHelpDialog extends JDialog {
    protected final Action okAction = new AbstractAction() {
        {
            putValue(Action.MNEMONIC_KEY, new Integer('O'));
            putValue(Action.NAME, "Ok");
        }

        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }

    };

    public SyntaxHelpDialog() {
        super(JDiceMain.frame);
        setSize(new Dimension(500, 300));
        setTitle("Syntax Help");
        setModal(false);
        setResizable(false);
        setLocationRelativeTo(JDiceMain.frame);

        setLayout(new BorderLayout());

        JTextPane editPane = new JTextPane();
        editPane.setEditable(false);
        editPane.setText(getSyntaxHelpText());
        add(new JScrollPane(editPane), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.add(new JButton(okAction));

        add(btnPanel, BorderLayout.SOUTH);
    }

    protected String getSyntaxHelpText() {
        String result = "";

        try {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(SyntaxHelpDialog.class.getClassLoader()
                    .getResourceAsStream("syntax.help")));
            String line = null;
            while ((line = rdr.readLine()) != null) {
                result += line + '\n';
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to find 'syntax.help' file", e);
        }

        return result;
    }
}
