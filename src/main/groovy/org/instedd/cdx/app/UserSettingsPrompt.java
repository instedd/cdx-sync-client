package org.instedd.cdx.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
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

public class UserSettingsPrompt extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField inboxPathTextField;
  private JTextField outboxPathTextField;
  private JTextField remoteKeyTextField;
  private JTextField knownHostsPathTextField;
  private JTextField authServerUrlTextBox;

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
    gbl_contentPanel.columnWidths = new int[]{119, 286, 0};
    gbl_contentPanel.rowHeights = new int[]{16, 0, 28, 28, 28, 33, 0};
    gbl_contentPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    contentPanel.setLayout(gbl_contentPanel);

                JLabel lblNewLabel = new JLabel("Enter your settings");
                GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
                gbc_lblNewLabel.anchor = GridBagConstraints.NORTHWEST;
                gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
                gbc_lblNewLabel.gridx = 0;
                gbc_lblNewLabel.gridy = 0;
                contentPanel.add(lblNewLabel, gbc_lblNewLabel);

            JLabel lblNewLabel_1 = new JLabel("Auth Server URL");
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_1.gridx = 0;
            gbc_lblNewLabel_1.gridy = 1;
            contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);

            authServerUrlTextBox = new JTextField();
            authServerUrlTextBox.setText("http://localhost:3000");
            GridBagConstraints gbc_authServerUrlTextBox = new GridBagConstraints();
            gbc_authServerUrlTextBox.insets = new Insets(0, 0, 5, 0);
            gbc_authServerUrlTextBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_authServerUrlTextBox.gridx = 1;
            gbc_authServerUrlTextBox.gridy = 1;
            contentPanel.add(authServerUrlTextBox, gbc_authServerUrlTextBox);
            authServerUrlTextBox.setColumns(10);

            JLabel remoteKeyLabel = new JLabel("Remote Key Path:");
            GridBagConstraints gbc_remoteKeyLabel = new GridBagConstraints();
            gbc_remoteKeyLabel.anchor = GridBagConstraints.SOUTHWEST;
            gbc_remoteKeyLabel.insets = new Insets(0, 0, 5, 5);
            gbc_remoteKeyLabel.gridx = 0;
            gbc_remoteKeyLabel.gridy = 2;
            contentPanel.add(remoteKeyLabel, gbc_remoteKeyLabel);

        remoteKeyTextField = new JTextField();
        remoteKeyTextField.setColumns(10);
        GridBagConstraints gbc_remoteKeyTextField = new GridBagConstraints();
        gbc_remoteKeyTextField.anchor = GridBagConstraints.NORTH;
        gbc_remoteKeyTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_remoteKeyTextField.insets = new Insets(0, 0, 5, 0);
        gbc_remoteKeyTextField.gridx = 1;
        gbc_remoteKeyTextField.gridy = 2;
        contentPanel.add(remoteKeyTextField, gbc_remoteKeyTextField);

                JLabel lblKnownHostsPath = new JLabel("Known Hosts Path:");
                GridBagConstraints gbc_lblKnownHostsPath = new GridBagConstraints();
                gbc_lblKnownHostsPath.anchor = GridBagConstraints.SOUTHWEST;
                gbc_lblKnownHostsPath.insets = new Insets(0, 0, 5, 5);
                gbc_lblKnownHostsPath.gridx = 0;
                gbc_lblKnownHostsPath.gridy = 3;
                contentPanel.add(lblKnownHostsPath, gbc_lblKnownHostsPath);

            knownHostsPathTextField = new JTextField();
            knownHostsPathTextField.setColumns(10);
            GridBagConstraints gbc_knownHostsPathTextField = new GridBagConstraints();
            gbc_knownHostsPathTextField.fill = GridBagConstraints.BOTH;
            gbc_knownHostsPathTextField.insets = new Insets(0, 0, 5, 0);
            gbc_knownHostsPathTextField.gridx = 1;
            gbc_knownHostsPathTextField.gridy = 3;
            contentPanel.add(knownHostsPathTextField, gbc_knownHostsPathTextField);

            JLabel lblOutboxPath = new JLabel("Inbox Path:");
            GridBagConstraints gbc_lblOutboxPath = new GridBagConstraints();
            gbc_lblOutboxPath.anchor = GridBagConstraints.WEST;
            gbc_lblOutboxPath.insets = new Insets(0, 0, 5, 5);
            gbc_lblOutboxPath.gridx = 0;
            gbc_lblOutboxPath.gridy = 4;
            contentPanel.add(lblOutboxPath, gbc_lblOutboxPath);

            inboxPathTextField = new JTextField();
            inboxPathTextField.setColumns(10);
            GridBagConstraints gbc_inboxPathTextField = new GridBagConstraints();
            gbc_inboxPathTextField.fill = GridBagConstraints.BOTH;
            gbc_inboxPathTextField.insets = new Insets(0, 0, 5, 0);
            gbc_inboxPathTextField.gridx = 1;
            gbc_inboxPathTextField.gridy = 4;
            contentPanel.add(inboxPathTextField, gbc_inboxPathTextField);

            JLabel label = new JLabel("Outbox Path:");
            GridBagConstraints gbc_label = new GridBagConstraints();
            gbc_label.anchor = GridBagConstraints.WEST;
            gbc_label.insets = new Insets(0, 0, 0, 5);
            gbc_label.gridx = 0;
            gbc_label.gridy = 5;
            contentPanel.add(label, gbc_label);

        outboxPathTextField = new JTextField();
        outboxPathTextField.setColumns(10);
        GridBagConstraints gbc_outboxPathTextField = new GridBagConstraints();
        gbc_outboxPathTextField.fill = GridBagConstraints.BOTH;
        gbc_outboxPathTextField.gridx = 1;
        gbc_outboxPathTextField.gridy = 5;
        contentPanel.add(outboxPathTextField, gbc_outboxPathTextField);
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
        put("authServerUrl", authServerUrlTextBox.getText());
        put("remoteKey", remoteKeyTextField.getText());
        put("knownHostsFilePath", knownHostsPathTextField.getText());
        put("localInboxDir",inboxPathTextField.getText());
        put("localOutboxDir", outboxPathTextField.getText());
      }
    };
  }
}
