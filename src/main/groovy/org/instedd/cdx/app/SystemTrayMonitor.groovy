package org.instedd.cdx.app

import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

import org.instedd.rsync_java_client.app.RSyncApplication;

class SystemTrayMonitor  extends org.instedd.rsync_java_client.app.SystemTrayMonitor {

  private String dbPath;
  private String logPath

  public SystemTrayMonitor(String tooltip, URL imageUrl, String dbPath, String logPath) {
    super(tooltip, imageUrl);
    this.dbPath = dbPath;
    this.logPath = logPath;
  }

  @Override
  protected void configureMenu(RSyncApplication application, PopupMenu menu) {
    super.configureMenu(application, menu);

    def menuItem = new MenuItem("Reconfigure");
    menuItem.addActionListener({
      def result = JOptionPane.showConfirmDialog(null,
          "This will erase your settings. Do you want to proceed?", "Erase setting?", JOptionPane.YES_NO_OPTION)
      if (result == JOptionPane.YES_OPTION) {
        new File(dbPath).deleteOnExit();
        JOptionPane.showMessageDialog(null, "Changes will take effect after restart.")
      }
    });
    menu.add(menuItem);

    menuItem = new MenuItem("View Log...");
    menuItem.addActionListener({
      Desktop.getDesktop().open(new File(logPath));
    })
    menu.add(menuItem);
  }
}
