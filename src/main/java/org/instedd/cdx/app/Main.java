package org.instedd.cdx.app;

import static org.instedd.rsync_java_client.util.Exceptions.interruptable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.EnumSet;
import java.nio.file.Files;

import org.apache.log4j.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.instedd.rsync_java_client.Settings;
import org.instedd.rsync_java_client.SyncMode;
import org.instedd.rsync_java_client.app.ConsoleMonitor;
import org.instedd.rsync_java_client.app.RSyncApplication;
import org.instedd.rsync_java_client.credentials.Credentials;
import org.instedd.rsync_java_client.settings.MapDBSettingsStore;

import org.json.JSONObject;

public class Main {
  private static final Log log = LogFactory.getLog(Main.class);

  public static void main(String[] args) {
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
    app.start(new SystemTrayMonitor(settings), new ConsoleMonitor());
  }

}
