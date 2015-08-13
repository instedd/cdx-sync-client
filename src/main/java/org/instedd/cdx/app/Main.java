package org.instedd.cdx.app;

import java.io.IOException;
import java.util.EnumSet;
import javax.swing.UIManager;

import org.apache.log4j.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.instedd.rsync_java_client.SyncMode;
import org.instedd.rsync_java_client.app.ConsoleMonitor;
import org.instedd.rsync_java_client.app.RSyncApplication;

public class Main {
  private static final Log log = LogFactory.getLog(Main.class);

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {

    }

    CDXSettings settings = new CDXSettings();

    // Setup logging
    RollingFileAppender appender = new RollingFileAppender();
    String PATTERN = "%d [%p] %m%n";
    appender.setLayout(new PatternLayout(PATTERN));
    appender.setThreshold(Level.INFO);
    appender.setFile(settings.logPath());
    appender.setMaxFileSize("1MB");
    appender.setMaxBackupIndex(5);
    appender.activateOptions();
    Logger.getRootLogger().addAppender(appender);

    try {
      log.info("Loading settings");
      settings.load();
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(1);
    }

    if (!settings.isValid()) {
      log.info("Current settings are not valid. Displaying settings dialog.");

      try {
        if (!SettingsDialog.editSettings(settings)) {
          log.warn("Settings are still not valid. Exiting...");
          System.exit(1);
        }
      } catch (IOException ex) {
        System.err.println(ex.getMessage());
        System.exit(1);
      }
    }

    log.info("Starting application");
    log.info("Data directory path: " + settings.rootPath);
    log.info("Auth server URL: " + settings.authServerUrl);

    startApplication(settings);

    System.out.printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir);
  }

  static void startApplication(CDXSettings settings) {
    RSyncApplication app = new RSyncApplication(settings, EnumSet.of(SyncMode.UPLOAD));
    app.addMonitor(new SystemTrayMonitor(settings));
    app.addMonitor(new ConsoleMonitor());
    app.addMonitor(new AppUpdatesMonitor(settings));
    app.start();
  }

}
