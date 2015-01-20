package org.instedd.cdx.app

import static org.instedd.sync4j.util.Exceptions.interruptable

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import java.util.Scanner

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.instedd.sync4j.Settings
import org.instedd.sync4j.app.ConsoleMonitor;
import org.instedd.sync4j.app.RSyncApplication
import org.instedd.sync4j.app.SystemTrayMonitor;
import org.instedd.sync4j.credentials.Credentials;
import org.instedd.sync4j.watcher.RsyncWatchListener.SyncMode

public class Main {
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
    def dbPath = properties['app.dbPath']

    def settings
    if(new File(dbPath).exists()) {
      def db = MapDBDataStore.fromMapDB(dbPath);
      settings = db.settings
    } else {

      def serverSettings
      def userSettings
      while (true) {
        userSettings = UserSettingsPrompt.promptForUserSettings()
        def credentials = new Credentials(new File(userSettings.remoteKey))
        def authServer = new SyncAuthServer(userSettings.authToken, userSettings.authServerUrl)
        try {
          credentials.ensure()
          serverSettings = authServer.authenticate(credentials.publicKey)
          break
        } catch(Exception e) {
          e.printStackTrace()
          JOptionPane.showMessageDialog(null, e.message)
        }
      }
      settings = merge(userSettings, serverSettings)

      def db = MapDBDataStore.fromMapDB(dbPath)
      db.settings = settings
    }

    def app = new RSyncApplication(settings, appMode)
    app.start(new SystemTrayMonitor(appName, appIcon), new ConsoleMonitor())

    printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir)
  }


  protected static merge(userSettings, serverSettings) {
    new Settings(
      remoteHost: serverSettings.remoteHost,
      remotePort: serverSettings.remotePort,
      remoteUser: serverSettings.remoteUser,
      remoteInboxDir: serverSettings.remoteInboxDir,
      remoteOutboxDir: serverSettings.remoteOutboxDir,

      remoteKey: userSettings.remoteKey,
      knownHostsFilePath: userSettings.knownHostsFilePath,
      localInboxDir: userSettings.localInboxDir,
      localOutboxDir: userSettings.localOutboxDir)
  }

  protected static properties(String propertiesFilename) {
    def properties = new Properties()
    new File(propertiesFilename).withInputStream { properties.load(it) }
    properties
  }
}
