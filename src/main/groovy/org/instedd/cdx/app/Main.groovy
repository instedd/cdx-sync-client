package org.instedd.cdx.app

import static org.instedd.rsync_java_client.util.Exceptions.interruptable

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import java.util.Scanner
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.Level

import javax.swing.JDialog
import javax.swing.JOptionPane

import org.instedd.rsync_java_client.Settings
import org.instedd.rsync_java_client.app.ConsoleMonitor
import org.instedd.rsync_java_client.app.RSyncApplication
import org.instedd.rsync_java_client.credentials.Credentials
import org.instedd.rsync_java_client.settings.MapDBSettingsStore
import org.instedd.rsync_java_client.watcher.RsyncWatchListener.SyncMode

public class Main {
  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    if (args.length != 1) {
      println("Usage: cdxsync <properties filename>")
      System.exit(1)
    }
    def propertiesFilename = args[0]
    def properties = properties(propertiesFilename)

    def appName = properties['app.name']
    def appIcon = Main.getResource(properties['app.icon'])
    def appMode = SyncMode.valueOf(properties['app.mode'].toUpperCase())
    def rootPath = properties['app.root_path']

    def settings = new Settings(appName, rootPath)
    def dbPath = settings.rootPath.resolve("settingsdb").toString()

    // Setup logging
    def logPath = settings.rootPath.resolve(appName + ".log").toString()
    def fileHandler = new FileHandler(logPath, 1024 * 1024, 5, true)
    def rootLogger = Logger.getLogger("")
    rootLogger.addHandler(fileHandler)
    def logFormatter = new LogFormatter()
    for (handler in rootLogger.getHandlers()) {
      handler.formatter = logFormatter
    }

    logger.info("Starting application")

    def appSettings = [
  	  rootPath: settings.rootPath,
      authServerUrl: properties['app.server.url'],
      remoteKey: settings.getRemoteKeyPath(),
      knownHostsFilePath: properties['app.know.hosts.file.path']
    ]

    logger.info("Data directory path: " + settings.rootPath)
    logger.info("Auth server URL: " + appSettings['authServerUrl'])

    settings = readOrHandshakeSettings(dbPath, appSettings)

    startApplication(settings, appMode, appName, appIcon, dbPath)

    printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir)
  }

  private static String combine(String path1, String path2)
  {
    File file1 = new File(path1);
    File file2 = new File(file1, path2);
    return file2.getPath();
  }

  static startApplication(settings, SyncMode appMode, appName, URL appIcon, dbPath) {
    def app = new RSyncApplication(settings, appMode)
    app.start(new SystemTrayMonitor(appName, appIcon, dbPath), new ConsoleMonitor())
  }

  static readOrHandshakeSettings(dbPath, appSettings) {
    def db = MapDBSettingsStore.fromMapDB(dbPath)
    if(!db.settings) {
      db.settings = handshakeSettings(appSettings)
      JOptionPane.showMessageDialog(null, "Device is now activated");
    }
    db.settings
  }

  static handshakeSettings(appSettings) {
    def serverSettings
    def userSettings
    while (true) {
      userSettings = UserSettingsPrompt.promptForUserSettings()
      def credentials = new Credentials(new File(appSettings.remoteKey))
      def authServer = new SyncAuthServer(userSettings.authToken, appSettings.authServerUrl)
      try {
        credentials.ensure()

        logger.info("Activating to '" + appSettings.authServerUrl + "' with auth token '" + userSettings.authToken + "'")
        serverSettings = authServer.authenticate(credentials.publicKey)
        logger.info("Activation succeeded")
        break
      } catch(Exception e) {
        logger.log(Level.WARNING, "Activation failed", e)
        confirmRetryOrExit(e)
      }
    }
    merge(appSettings, userSettings, serverSettings)
  }

  static confirmRetryOrExit(Exception e) {
    def result = JOptionPane.showConfirmDialog(null, "${e.message}. Try again?", "Try again?", JOptionPane.YES_NO_OPTION)
    if (result == JOptionPane.NO_OPTION) {
      System.exit(1)
    }
  }

  static merge(appSettings, userSettings, serverSettings) {
    new Settings(
      remoteHost: serverSettings.host,
      remotePort: serverSettings.port,
      remoteUser: serverSettings.user,
      remoteInboxDir: serverSettings.inbox_dir,
      remoteOutboxDir: serverSettings.outbox_dir,

      remoteKey: appSettings.remoteKey,
      knownHostsFilePath: appSettings.knownHostsFilePath,

      localInboxDir: userSettings.localInboxDir,
      localOutboxDir: userSettings.localOutboxDir,

      strictHostChecking: false)
  }

  static properties(String propertiesFilename) {
    def properties = new Properties()
    new File(propertiesFilename).withInputStream { properties.load(it) }
    properties
  }
}
