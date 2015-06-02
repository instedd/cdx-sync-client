package org.instedd.cdx.app;

import java.io.IOException;

import java.awt.Container;
import java.awt.Font;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class SettingsDialog extends JDialog {
  private CDXSettings originalSettings;
  private CDXSettings settings;
  private JTextField outboxTextField;
  private JButton activationButton;
  private JButton deactivateButton;
  private JLabel activationStatus;
  private boolean cancelled;

  public SettingsDialog(CDXSettings originalSettings) {
    this.originalSettings = originalSettings;
    this.settings = originalSettings.clone();
    setTitle("CDX Client - Settings");
    setModal(true);
    setBounds(100, 100, 450, 300);

    Container panel = getContentPane();
    GroupLayout layout = new GroupLayout(panel);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    panel.setLayout(layout);

    JLabel outboxLabel = new JLabel("Outbox:");
    panel.add(outboxLabel);

    outboxTextField = new JTextField();
    panel.add(outboxTextField);

    JButton buttonOutbox = new JButton("...");
    panel.add(buttonOutbox);

    JLabel activationLabel = new JLabel("Device Activation:");
    panel.add(activationLabel);

    activationStatus = new JLabel();
    Font font = activationStatus.getFont();
    activationStatus.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
    panel.add(activationStatus);

    activationButton = new JButton("Activate Now...");
    panel.add(activationButton);

    deactivateButton = new JButton("Reactivate");
    panel.add(deactivateButton);

    JLabel advancedLabel = new JLabel("Advanced Settings:");
    panel.add(advancedLabel);

    JLabel serverUrlLabel = new JLabel("CDX Server URL:");
    panel.add(serverUrlLabel);

    JTextField serverUrlTextField = new JTextField();
    panel.add(serverUrlTextField);

    JButton okButton = new JButton("OK");
    panel.add(okButton);

    JButton cancelButton = new JButton("Cancel");
    panel.add(cancelButton);

    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addComponent(outboxLabel)
          .addComponent(outboxTextField)
          .addComponent(buttonOutbox))
        .addGroup(layout.createSequentialGroup()
          .addComponent(activationLabel)
          .addComponent(activationStatus)
          .addComponent(activationButton)
          .addComponent(deactivateButton))
        .addComponent(advancedLabel)
        .addGroup(layout.createSequentialGroup()
          .addGap(20)
          .addComponent(serverUrlLabel)
          .addComponent(serverUrlTextField))
        .addGroup(layout.createSequentialGroup()
          .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(okButton)
          .addComponent(cancelButton))
    );

    layout.setVerticalGroup(
      layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(outboxLabel)
          .addComponent(outboxTextField)
          .addComponent(buttonOutbox))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(activationLabel)
          .addComponent(activationStatus)
          .addComponent(activationButton)
          .addComponent(deactivateButton))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(advancedLabel)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(serverUrlLabel)
          .addComponent(serverUrlTextField))
        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(okButton)
          .addComponent(cancelButton))
    );

    layout.linkSize(SwingConstants.VERTICAL, outboxTextField, buttonOutbox);
    layout.linkSize(SwingConstants.HORIZONTAL, okButton, cancelButton);

    okButton.addActionListener((e) -> {
      if (validateSettings()) {
        settings.copyTo(originalSettings);
        cancelled = false;
        setVisible(false);
      }
    });

    cancelButton.addActionListener((e) -> {
      cancelled = true;
      setVisible(false);
    });

    buttonOutbox.addActionListener((e) -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        outboxTextField.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });

    deactivateButton.addActionListener((e) -> {
      settings.deactivate();
      refreshActivation();
    });

    activationButton.addActionListener((e) -> {
      ActivationDialog activationDialog = new ActivationDialog(settings);
      activationDialog.setVisible(true);
      refreshActivation();
    });

    serverUrlTextField.setText(settings.authServerUrl);
    serverUrlTextField.getDocument().addDocumentListener(new DocumentListener() {
      private void update() {
        settings.authServerUrl = serverUrlTextField.getText();
      }
      public void changedUpdate(DocumentEvent e) { update(); }
      public void removeUpdate(DocumentEvent e) { update(); }
      public void insertUpdate(DocumentEvent e) { update(); }
    });

    outboxTextField.setText(settings.localOutboxDir);

    refreshActivation();
  }

  private void refreshActivation() {
    activationStatus.setText(settings.isActivated() ? "OK" : "MISSING");
    activationButton.setVisible(!settings.isActivated());
    deactivateButton.setVisible(settings.isActivated());
  }

  private boolean validateSettings() {
    settings.localOutboxDir = outboxTextField.getText();

    try {
      settings.validate();
      return true;
    } catch (CDXSettings.ValidationError e) {
      if (e.getField().equals("remoteHost")) {
        JOptionPane.showMessageDialog(this, "Device is not activated", "Settings validation error", JOptionPane.ERROR_MESSAGE);
        activationButton.requestFocus();
        return false;
      }

      JOptionPane.showMessageDialog(this, e.getMessage(), "Settings validation error", JOptionPane.ERROR_MESSAGE);

      switch (e.getField()) {
        case "localOutboxDir":
          outboxTextField.requestFocus();
          break;
      }

      return false;
    }
  }

  public boolean wasCanceled() {
    return cancelled;
  }

  public static boolean editSettings(CDXSettings settings) throws IOException {
    SettingsDialog dialog = new SettingsDialog(settings);
    dialog.setVisible(true);
    if (dialog.wasCanceled()) {
      return false;
    } else {
      settings.save();
      return true;
    }
  }
}
