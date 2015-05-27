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
    if (args.length != 1) {
      System.out.println("Usage: cdxsync <properties filename>");
      System.exit(1);
    }

    String propertiesFilename = args[0];
    Properties properties = properties(propertiesFilename);

    String appName = properties.getProperty("app.name");
    String rootPath = properties.getProperty("app.root_path");

    Settings settings = new Settings(appName, rootPath);
    String dbPath = settings.rootPath.resolve("settingsdb").toString();

    // Setup logging
    RollingFileAppender appender = new RollingFileAppender();
    String PATTERN = "%d [%p] %m%n";
    appender.setLayout(new PatternLayout(PATTERN));
    appender.setThreshold(Level.INFO);
    String logPath = settings.rootPath.resolve(appName + ".log").toString();
    appender.setFile(logPath);
    appender.setMaxFileSize("1MB");
    appender.setMaxBackupIndex(5);
    appender.activateOptions();
    Logger.getRootLogger().addAppender(appender);

    log.info("Starting application");

    Map<String, String> appSettings = new Hashtable<>();
    appSettings.put("rootPath", settings.rootPath.toString());
    appSettings.put("authServerUrl", properties.getProperty("app.server.url"));
    appSettings.put("remoteKey", settings.getRemoteKeyPath());
    appSettings.put("knownHostsFilePath", properties.getProperty("app.know.hosts.file.path", settings.knownHostsFilePath));

    log.info("Data directory path: " + settings.rootPath);
    log.info("Auth server URL: " + appSettings.get("authServerUrl"));

    settings = readOrHandshakeSettings(dbPath, appSettings);

    startApplication(settings, appName, dbPath, logPath);

    System.out.printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir);
  }

  private static String combine(String path1, String path2)
  {
    File file1 = new File(path1);
    File file2 = new File(file1, path2);
    return file2.getPath();
  }

  static void startApplication(Settings settings, String appName, String dbPath, String logPath) {
    RSyncApplication app = new RSyncApplication(settings, EnumSet.of(SyncMode.UPLOAD));
    app.start(new SystemTrayMonitor(appName, dbPath, logPath), new ConsoleMonitor());
  }

  static Settings readOrHandshakeSettings(String dbPath, Map<String, String> appSettings) {
    MapDBSettingsStore db = MapDBSettingsStore.fromMapDB(dbPath);
    if (db.getSettings() == null) {
      db.setSettings(handshakeSettings(appSettings));
      JOptionPane.showMessageDialog(null, "Device is now activated");
    }
    return db.getSettings();
  }

  static Settings handshakeSettings(Map<String, String> appSettings) {
    JSONObject serverSettings;
    Map<String, String> userSettings;

    while (true) {
      userSettings = UserSettingsPrompt.promptForUserSettings();
      Credentials credentials = new Credentials(new File(appSettings.get("remoteKey")));
      SyncAuthServer authServer = new SyncAuthServer(userSettings.get("authToken"), appSettings.get("authServerUrl"));

      try {
        credentials.ensure();

        log.info("Activating to '" + appSettings.get("authServerUrl") + "' with auth token '" + userSettings.get("authToken") + "'");
        serverSettings = authServer.authenticate(credentials.getPublicKey());
        log.info("Activation succeeded");
        break;
      } catch(Exception e) {
        log.warn("Activation failed", e);
        confirmRetryOrExit(e);
      }
    }
    return merge(appSettings, userSettings, serverSettings);
  }

  static void confirmRetryOrExit(Exception e) {
    int result = JOptionPane.showConfirmDialog(null, e.getMessage() + ". Try again?", "Try again?", JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.NO_OPTION) {
      System.exit(1);
    }
  }

  static Settings merge(Map<String, String> appSettings, Map<String, String> userSettings, JSONObject serverSettings) {
    Settings settings = new Settings();
    settings.remoteHost = serverSettings.getString("host");
    settings.remotePort = serverSettings.getInt("port");
    settings.remoteUser = serverSettings.getString("user");
    settings.remoteInboxDir = serverSettings.getString("inbox_dir");
    settings.remoteOutboxDir = serverSettings.getString("outbox_dir");

    settings.remoteKey = appSettings.get("remoteKey");
    settings.knownHostsFilePath = appSettings.get("knownHostsFilePath");

    settings.localInboxDir = userSettings.get("localInboxDir");
    settings.localOutboxDir = userSettings.get("localOutboxDir");

    settings.strictHostChecking = false;

    ProxyConfiguration.detect().overrideSyncSettings(settings);
    return settings;
  }

  static Properties properties(String propertiesFilename) {
    Properties properties = new Properties();
    try (InputStream in = Files.newInputStream(new File(propertiesFilename).toPath())) {
      properties.load(in);
    } catch (IOException ex) {
      log.warn("Could not load proxy configuration file", ex);
    }

    return properties;
  }
}
