package org.instedd.cdx.app;

import java.awt.Container;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.instedd.rsync_java_client.credentials.Credentials;
import org.json.JSONObject;

public class ActivationDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  
  private static final Log log = LogFactory.getLog(ActivationDialog.class);

  public ActivationDialog(final CDXSettings settings) {
      setTitle("CDX Client - Device Activation");
      setModal(true);
      setBounds(100, 100, 450, 150);

      Container panel = getContentPane();
      GroupLayout layout = new GroupLayout(panel);
      layout.setAutoCreateGaps(true);
      layout.setAutoCreateContainerGaps(true);
      panel.setLayout(layout);

      JLabel tokenLabel = new JLabel("Activation token:");
      panel.add(tokenLabel);

      JTextField tokenTextField = new JTextField();
      panel.add(tokenTextField);

      JProgressBar activationProgress = new JProgressBar();
      activationProgress.setVisible(false);
      activationProgress.setIndeterminate(true);
      panel.add(activationProgress);

      JButton activateButton = new JButton("Activate");
      panel.add(activateButton);

      JButton cancelButton = new JButton("Cancel");
      panel.add(cancelButton);

      layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(tokenLabel)
          .addComponent(tokenTextField)
          .addComponent(activationProgress)
          .addGroup(layout.createSequentialGroup()
            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(activateButton)
            .addComponent(cancelButton))
      );

      layout.setVerticalGroup(
        layout.createSequentialGroup()
          .addComponent(tokenLabel)
          .addComponent(tokenTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(activationProgress)
          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(activateButton)
            .addComponent(cancelButton))
      );

      layout.linkSize(SwingConstants.HORIZONTAL, activateButton, cancelButton);

      activateButton.addActionListener((ev) -> {
        new Thread(() -> {
          Credentials credentials = new Credentials(new File(settings.getRemoteKeyPath()));
          String activationToken = tokenTextField.getText().trim();
          SyncAuthServer authServer = new SyncAuthServer(activationToken, settings.authServerUrl);

          try {
            activationProgress.setVisible(true);
            tokenTextField.setEnabled(false);
            activateButton.setEnabled(false);
            cancelButton.setEnabled(false);

            credentials.ensure();

            log.info("Activating to '" + settings.authServerUrl + "' with auth token '" + tokenTextField.getText() + "'");
            JSONObject serverSettings = authServer.authenticate(credentials.getPublicKey());
            log.info("Activation succeeded");

            settings.remoteHost = serverSettings.getString("host");
            settings.remotePort = serverSettings.getInt("port");
            settings.remoteUser = serverSettings.getString("user");
            settings.remoteInboxDir = serverSettings.getString("inbox_dir");
            settings.remoteOutboxDir = serverSettings.getString("outbox_dir");
            settings.deviceUUID = serverSettings.getString("device_uuid");
            settings.deviceKey = serverSettings.getString("device_key");

            setVisible(false);
          } catch(Exception e) {
            log.warn("Activation failed", e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Activation failed", JOptionPane.ERROR_MESSAGE);
          } finally {
            activationProgress.setVisible(false);
            tokenTextField.setEnabled(true);
            activateButton.setEnabled(true);
            cancelButton.setEnabled(true);
          }
        }).start();
      });

    cancelButton.addActionListener((ev) -> {
      setVisible(false);
    });
  }
}
