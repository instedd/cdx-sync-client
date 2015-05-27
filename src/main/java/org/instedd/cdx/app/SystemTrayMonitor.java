package org.instedd.cdx.app;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;

import org.instedd.rsync_java_client.app.RSyncApplication;
import org.instedd.rsync_java_client.RsyncSynchronizerListener;

public class SystemTrayMonitor extends org.instedd.rsync_java_client.app.SystemTrayMonitor
  implements RsyncSynchronizerListener {

  private String dbPath;
  private String logPath;
  private Thread animationThread;
  private final Image[] animationIcons;
  private final Image defaultIcon;
  private final Image failIcon;
  private boolean failed;

  public SystemTrayMonitor(String tooltip, String dbPath, String logPath) {
    super(tooltip, SystemTrayMonitor.class.getResource("/icon-0.png"));
    this.dbPath = dbPath;
    this.logPath = logPath;


    animationIcons = new Image[3];
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    animationIcons[0] = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-60.png"));
    animationIcons[1] = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-120.png"));
    animationIcons[2] = defaultIcon = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-0.png"));
    failIcon = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-red.png"));
  }

  @Override
  protected void configureMenu(RSyncApplication application, PopupMenu menu) {
    super.configureMenu(application, menu);

    MenuItem menuItem = new MenuItem("Reconfigure");
    menuItem.addActionListener((e) -> {
      int result = JOptionPane.showConfirmDialog(null,
          "This will erase your settings. Do you want to proceed?", "Erase setting?", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        new File(dbPath).deleteOnExit();
        JOptionPane.showMessageDialog(null, "Changes will take effect after restart.");
      }
    });
    menu.add(menuItem);

    menuItem = new MenuItem("View Log...");
    menuItem.addActionListener((e ) -> {
      try {
        Desktop.getDesktop().open(new File(logPath));
      } catch (IOException ex) {

      }
    });
    menu.add(menuItem);
  }

  @Override
  public void transferStarted() {
    failed = false;

    if (animationThread != null) {
      animationThread.interrupt();
    }

    animationThread = new Thread(() -> {
      try {
        while (true) {
          for (int i = 0; i < animationIcons.length; i++) {
            getTrayIcon().setImage(animationIcons[i]);
            Thread.sleep(300);
          }
        }
      } catch (InterruptedException e) {
        // End of the animation
      }

      getTrayIcon().setImage(failed ? failIcon : defaultIcon);
      animationThread = null;
    });
    animationThread.start();
  }

  @Override
  public void transferFailed(String errorMessage) {
    getTrayIcon().displayMessage("Transfer Failed\t", null, MessageType.ERROR);
    failed = true;
    animationThread.interrupt();
  }

  @Override
  public void transferCompleted(List<String> uploadedFiles, List<String> downloadedFiles) {
    StringBuilder message = new StringBuilder();
    if (uploadedFiles != null) {
      message.append(uploadedFiles.size()).append(" file(s) uploaded:");
      message.append("\n");
      for (String file : uploadedFiles) {
        message.append("  - ").append(file);
      }
    }
    getTrayIcon().displayMessage("Transfer Completed\t", message.toString(), MessageType.INFO);
    animationThread.interrupt();
  }
}
