package org.instedd.cdx.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;

public class UserSettingsPrompt extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField outboxPathTextField;
  private JTextField authTokenTextBox;

  public UserSettingsPrompt() {
    setModal(true);
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[]{119, 286, 0, 0};
    gbl_contentPanel.rowHeights = new int[]{16, 0, 0, 28, 28, 28, 33, 0};
    gbl_contentPanel.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    contentPanel.setLayout(gbl_contentPanel);

    JLabel lblNewLabel = new JLabel("Enter your settings");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridwidth = GridBagConstraints.REMAINDER;
    gbc_lblNewLabel.gridy = 0;
    contentPanel.add(lblNewLabel, gbc_lblNewLabel);

    JLabel lblNewLabel_2 = new JLabel("Auth Token");
    GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
    gbc_lblNewLabel_2.fill = GridBagConstraints.HORIZONTAL;
    gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_2.gridx = 0;
    gbc_lblNewLabel_2.gridy = 1;
    contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

    authTokenTextBox = new JTextField();
    GridBagConstraints gbc_authTokenTextBox = new GridBagConstraints();
    gbc_authTokenTextBox.gridwidth = 2;
    gbc_authTokenTextBox.insets = new Insets(0, 0, 5, 0);
    gbc_authTokenTextBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_authTokenTextBox.gridx = 1;
    gbc_authTokenTextBox.gridy = 1;
    contentPanel.add(authTokenTextBox, gbc_authTokenTextBox);
    authTokenTextBox.setColumns(10);

    JLabel label = new JLabel("Outbox Path:");
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.anchor = GridBagConstraints.WEST;
    gbc_label.insets = new Insets(0, 0, 5, 5);
    gbc_label.gridx = 0;
    gbc_label.gridy = 3;
    contentPanel.add(label, gbc_label);

    outboxPathTextField = new JTextField();
    outboxPathTextField.setColumns(10);
    GridBagConstraints gbc_outboxPathTextField = new GridBagConstraints();
    gbc_outboxPathTextField.insets = new Insets(0, 0, 5, 5);
    gbc_outboxPathTextField.fill = GridBagConstraints.BOTH;
    gbc_outboxPathTextField.gridx = 1;
    gbc_outboxPathTextField.gridy = 3;
    contentPanel.add(outboxPathTextField, gbc_outboxPathTextField);

    JButton outboxPathChooseButton = new JButton("Choose");
    outboxPathChooseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillTextFromFileChooser(outboxPathTextField);
      }
    });
    GridBagConstraints gbc_outboxPathChooseButton = new GridBagConstraints();
    gbc_outboxPathChooseButton.insets = new Insets(0, 0, 5, 0);
    gbc_outboxPathChooseButton.gridx = 2;
    gbc_outboxPathChooseButton.gridy = 3;
    contentPanel.add(outboxPathChooseButton, gbc_outboxPathChooseButton);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    {
      JButton okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setVisible(false);
        }
      });
      okButton.setActionCommand("OK");
      buttonPane.add(okButton);
      getRootPane().setDefaultButton(okButton);
    }
  }

  protected void fillTextFromFileChooser(JTextField textField) {
    JFileChooser fc = new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = fc.showOpenDialog(UserSettingsPrompt.this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        textField.setText(file.getAbsolutePath());
    }
  }

  public static Map<String, String> promptForUserSettings() {
    UserSettingsPrompt prompt = new UserSettingsPrompt();
    prompt.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    prompt.setVisible(true);
    prompt.dispose();
    return prompt.getSettings();
  }

  public Map<String, String> getSettings() {
    return new HashMap<String, String>(){
      {
        put("authToken", authTokenTextBox.getText());
        put("localOutboxDir", outboxPathTextField.getText());
      }
    };
  }
}
