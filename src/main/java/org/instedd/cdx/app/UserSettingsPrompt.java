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
  private JTextField remoteKeyTextField;
  private JTextField knownHostsPathTextField;
  private JTextField authServerUrlTextBox;
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
            gbc_authTokenTextBox.insets = new Insets(0, 0, 5, 5);
            gbc_authTokenTextBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_authTokenTextBox.gridx = 1;
            gbc_authTokenTextBox.gridy = 1;
            contentPanel.add(authTokenTextBox, gbc_authTokenTextBox);
            authTokenTextBox.setColumns(10);

            JLabel lblNewLabel_1 = new JLabel("Auth Server URL");
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_1.gridx = 0;
            gbc_lblNewLabel_1.gridy = 2;
            contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);

            authServerUrlTextBox = new JTextField();
            GridBagConstraints gbc_authServerUrlTextBox = new GridBagConstraints();
            gbc_authServerUrlTextBox.gridwidth = 2;
            gbc_authServerUrlTextBox.insets = new Insets(0, 0, 5, 5);
            gbc_authServerUrlTextBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_authServerUrlTextBox.gridx = 1;
            gbc_authServerUrlTextBox.gridy = 2;
            contentPanel.add(authServerUrlTextBox, gbc_authServerUrlTextBox);
            authServerUrlTextBox.setColumns(10);

            JLabel remoteKeyLabel = new JLabel("Remote Key Path:");
            GridBagConstraints gbc_remoteKeyLabel = new GridBagConstraints();
            gbc_remoteKeyLabel.anchor = GridBagConstraints.SOUTHWEST;
            gbc_remoteKeyLabel.insets = new Insets(0, 0, 5, 5);
            gbc_remoteKeyLabel.gridx = 0;
            gbc_remoteKeyLabel.gridy = 3;
            contentPanel.add(remoteKeyLabel, gbc_remoteKeyLabel);

        remoteKeyTextField = new JTextField();
        remoteKeyTextField.setColumns(10);
        GridBagConstraints gbc_remoteKeyTextField = new GridBagConstraints();
        gbc_remoteKeyTextField.anchor = GridBagConstraints.NORTH;
        gbc_remoteKeyTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_remoteKeyTextField.insets = new Insets(0, 0, 5, 5);
        gbc_remoteKeyTextField.gridx = 1;
        gbc_remoteKeyTextField.gridy = 3;
        contentPanel.add(remoteKeyTextField, gbc_remoteKeyTextField);

                JButton remoteKeyChooseButton = new JButton("Choose");
                remoteKeyChooseButton.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                    fillTextFromFileChooser(remoteKeyTextField);
                  }
                });
                GridBagConstraints gbc_remoteKeyChooseButton = new GridBagConstraints();
                gbc_remoteKeyChooseButton.insets = new Insets(0, 0, 5, 0);
                gbc_remoteKeyChooseButton.gridx = 2;
                gbc_remoteKeyChooseButton.gridy = 3;
                contentPanel.add(remoteKeyChooseButton, gbc_remoteKeyChooseButton);

                JLabel lblKnownHostsPath = new JLabel("Known Hosts Path:");
                GridBagConstraints gbc_lblKnownHostsPath = new GridBagConstraints();
                gbc_lblKnownHostsPath.anchor = GridBagConstraints.SOUTHWEST;
                gbc_lblKnownHostsPath.insets = new Insets(0, 0, 5, 5);
                gbc_lblKnownHostsPath.gridx = 0;
                gbc_lblKnownHostsPath.gridy = 4;
                contentPanel.add(lblKnownHostsPath, gbc_lblKnownHostsPath);

            knownHostsPathTextField = new JTextField();
            knownHostsPathTextField.setColumns(10);
            GridBagConstraints gbc_knownHostsPathTextField = new GridBagConstraints();
            gbc_knownHostsPathTextField.fill = GridBagConstraints.BOTH;
            gbc_knownHostsPathTextField.insets = new Insets(0, 0, 5, 5);
            gbc_knownHostsPathTextField.gridx = 1;
            gbc_knownHostsPathTextField.gridy = 4;
            contentPanel.add(knownHostsPathTextField, gbc_knownHostsPathTextField);

            JButton knownHostsPathChooseButton = new JButton("Choose");
            knownHostsPathChooseButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                fillTextFromFileChooser(knownHostsPathTextField);
              }
            });
            GridBagConstraints gbc_knownHostsPathChooseButton = new GridBagConstraints();
            gbc_knownHostsPathChooseButton.insets = new Insets(0, 0, 5, 0);
            gbc_knownHostsPathChooseButton.gridx = 2;
            gbc_knownHostsPathChooseButton.gridy = 4;
            contentPanel.add(knownHostsPathChooseButton, gbc_knownHostsPathChooseButton);

            JLabel lblOutboxPath = new JLabel("Inbox Path:");
            GridBagConstraints gbc_lblOutboxPath = new GridBagConstraints();
            gbc_lblOutboxPath.anchor = GridBagConstraints.WEST;
            gbc_lblOutboxPath.insets = new Insets(0, 0, 5, 5);
            gbc_lblOutboxPath.gridx = 0;
            gbc_lblOutboxPath.gridy = 5;
            contentPanel.add(lblOutboxPath, gbc_lblOutboxPath);

            inboxPathTextField = new JTextField();
            inboxPathTextField.setColumns(10);
            GridBagConstraints gbc_inboxPathTextField = new GridBagConstraints();
            gbc_inboxPathTextField.fill = GridBagConstraints.BOTH;
            gbc_inboxPathTextField.insets = new Insets(0, 0, 5, 5);
            gbc_inboxPathTextField.gridx = 1;
            gbc_inboxPathTextField.gridy = 5;
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
            gbc_inboxPathChooseButton.gridy = 5;
            contentPanel.add(inboxPathChooseButton, gbc_inboxPathChooseButton);

            JLabel label = new JLabel("Outbox Path:");
            GridBagConstraints gbc_label = new GridBagConstraints();
            gbc_label.anchor = GridBagConstraints.WEST;
            gbc_label.insets = new Insets(0, 0, 0, 5);
            gbc_label.gridx = 0;
            gbc_label.gridy = 6;
            contentPanel.add(label, gbc_label);

        outboxPathTextField = new JTextField();
        outboxPathTextField.setColumns(10);
        GridBagConstraints gbc_outboxPathTextField = new GridBagConstraints();
        gbc_outboxPathTextField.insets = new Insets(0, 0, 0, 5);
        gbc_outboxPathTextField.fill = GridBagConstraints.BOTH;
        gbc_outboxPathTextField.gridx = 1;
        gbc_outboxPathTextField.gridy = 6;
        contentPanel.add(outboxPathTextField, gbc_outboxPathTextField);

        JButton outboxPathChooseButton = new JButton("Choose");
        outboxPathChooseButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            fillTextFromFileChooser(outboxPathTextField);
          }
        });
        GridBagConstraints gbc_outboxPathChooseButton = new GridBagConstraints();
        gbc_outboxPathChooseButton.gridx = 2;
        gbc_outboxPathChooseButton.gridy = 6;
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
    int returnVal = fc.showOpenDialog(UserSettingsPrompt.this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        textField.setText(file.getAbsolutePath());
    }
  }

  public static Map<String, String> promptForUserSettings(Map<String, String> defaultSettings) {
    UserSettingsPrompt prompt = new UserSettingsPrompt();
//    prompt.setSettings(defaultSettings);
    prompt.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    prompt.setVisible(true);
    prompt.dispose();
    return prompt.getSettings();
  }

  public void setSettings(Map<String, String> settings) {
      remoteKeyTextField.setText(settings.get("remoteKey"));
      knownHostsPathTextField.setText(settings.get("knownHostsFilePath"));
      inboxPathTextField.setText(settings.get("localInboxDir"));
      outboxPathTextField.setText(settings.get("localOutboxDir"));
  }

  public Map<String, String> getSettings() {
    return new HashMap<String, String>(){
      {
        put("authToken", authTokenTextBox.getText());
        put("authServerUrl", authServerUrlTextBox.getText());
        put("remoteKey", remoteKeyTextField.getText());
        put("knownHostsFilePath", knownHostsPathTextField.getText());
        put("localInboxDir",inboxPathTextField.getText());
        put("localOutboxDir", outboxPathTextField.getText());
      }
    };
  }
}
