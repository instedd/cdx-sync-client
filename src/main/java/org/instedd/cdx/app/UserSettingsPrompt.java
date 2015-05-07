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
  private JTextField inboxPathTextField;
  private JTextField outboxPathTextField;
  private JTextField authTokenTextBox;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      UserSettingsPrompt dialog = new UserSettingsPrompt();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
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

                        JLabel lblInboxPath = new JLabel("Inbox Path:");
                        GridBagConstraints gbc_lblInboxPath = new GridBagConstraints();
                        gbc_lblInboxPath.anchor = GridBagConstraints.WEST;
                        gbc_lblInboxPath.insets = new Insets(0, 0, 5, 5);
                        gbc_lblInboxPath.gridx = 0;
                        gbc_lblInboxPath.gridy = 2;
                        contentPanel.add(lblInboxPath, gbc_lblInboxPath);

                                    inboxPathTextField = new JTextField();
                                    inboxPathTextField.setColumns(10);
                                    GridBagConstraints gbc_inboxPathTextField = new GridBagConstraints();
                                    gbc_inboxPathTextField.fill = GridBagConstraints.BOTH;
                                    gbc_inboxPathTextField.insets = new Insets(0, 0, 5, 5);
                                    gbc_inboxPathTextField.gridx = 1;
                                    gbc_inboxPathTextField.gridy = 2;
                                    contentPanel.add(inboxPathTextField, gbc_inboxPathTextField);

                                    JButton inboxPathChooseButton = new JButton("Choose");
                                    inboxPathChooseButton.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                        fillTextFromFileChooser(inboxPathTextField);
                                      }
                                    });
                                    GridBagConstraints gbc_inboxPathChooseButton = new GridBagConstraints();
                                    gbc_inboxPathChooseButton.insets = new Insets(0, 0, 5, 0);
                                    gbc_inboxPathChooseButton.gridx = 2;
                                    gbc_inboxPathChooseButton.gridy = 2;
                                    contentPanel.add(inboxPathChooseButton, gbc_inboxPathChooseButton);

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
    {
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
        put("localInboxDir",inboxPathTextField.getText());
        put("localOutboxDir", outboxPathTextField.getText());
      }
    };
  }
}
