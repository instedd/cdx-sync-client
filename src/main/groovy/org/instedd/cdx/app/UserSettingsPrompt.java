package org.instedd.cdx.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.util.HashMap;
import java.util.Map;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserSettingsPrompt extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField inboxPathTextField;
  private JTextField outboxPathTextField;
  private JTextField remoteKeyTextField;
  private JTextField knownHostsPathTextField;

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
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        ColumnSpec.decode("119px"),
        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        ColumnSpec.decode("286px"),},
      new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("16px"),
        FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("28px"),
        FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("28px"),
        FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("28px"),
        FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("33px"),}));

    JLabel lblNewLabel = new JLabel("Enter your settings");
    contentPanel.add(lblNewLabel, "2, 2, left, top");

    JLabel remoteKeyLabel = new JLabel("Remote Key Path:");
    contentPanel.add(remoteKeyLabel, "2, 4, left, bottom");

    remoteKeyTextField = new JTextField();
    remoteKeyTextField.setColumns(10);
    contentPanel.add(remoteKeyTextField, "4, 4, fill, top");

    JLabel lblKnownHostsPath = new JLabel("Known Hosts Path:");
    contentPanel.add(lblKnownHostsPath, "2, 6, left, bottom");

    knownHostsPathTextField = new JTextField();
    knownHostsPathTextField.setColumns(10);
    contentPanel.add(knownHostsPathTextField, "4, 6, fill, fill");

    JLabel lblOutboxPath = new JLabel("Inbox Path:");
    contentPanel.add(lblOutboxPath, "2, 8, left, center");

    inboxPathTextField = new JTextField();
    inboxPathTextField.setColumns(10);
    contentPanel.add(inboxPathTextField, "4, 8, fill, fill");

    JLabel label = new JLabel("Outbox Path:");
    contentPanel.add(label, "2, 10, left, center");

    outboxPathTextField = new JTextField();
    outboxPathTextField.setColumns(10);
    contentPanel.add(outboxPathTextField, "4, 10, fill, fill");
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
        put("remoteKey", remoteKeyTextField.getText());
        put("knownHostsFilePath", knownHostsPathTextField.getText());
        put("localInboxDir",inboxPathTextField.getText());
        put("localOutboxDir", outboxPathTextField.getText());
      }
    };
  }
}
