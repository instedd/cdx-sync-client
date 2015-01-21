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
import org.instedd.sync4j.settings.MapDBSettingsStore;
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

    def appSettings = [
      authServerUrl: properties['app.server.url'],
      remoteKey: properties['app.remote.key'],
      knownHostsFilePath: properties['app.know.hosts.file.path']]

    def settings = readOrRequestSettings(dbPath, appSettings)

    def app = new RSyncApplication(settings, appMode)
    app.start(new SystemTrayMonitor(appName, appIcon), new ConsoleMonitor())

    printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir)
  }


  protected static readOrRequestSettings(dbPath, appSettings) {
    def db = MapDBSettingsStore.fromMapDB(dbPath)
    if(!db.settings) {
      def serverSettings
      def userSettings
      while (true) {
        userSettings = UserSettingsPrompt.promptForUserSettings()
        def credentials = new Credentials(new File(appSettings.remoteKey))
        def authServer = new SyncAuthServer(userSettings.authToken, appSettings.authServerUrl)
        try {
          credentials.ensure()
          serverSettings = authServer.authenticate(credentials.publicKey)
          break
        } catch(Exception e) {
          JOptionPane.showMessageDialog(null, e.message)
        }
      }
      JOptionPane.showMessageDialog(null, "Device is now activated");
      db.settings = merge(appSettings, userSettings, serverSettings)
    }
    db.settings
  }

  protected static merge(appSettings, userSettings, serverSettings) {
    new Settings(
      remoteHost: serverSettings.host,
      remotePort: serverSettings.port,
      remoteUser: serverSettings.user,
      remoteInboxDir: serverSettings.inbox_dir,
      remoteOutboxDir: serverSettings.outbox_dir,

      remoteKey: appSettings.remoteKey,
      knownHostsFilePath: appSettings.knownHostsFilePath,

      localInboxDir: userSettings.localInboxDir,
      localOutboxDir: userSettings.localOutboxDir)
  }

  protected static properties(String propertiesFilename) {
    def properties = new Properties()
    new File(propertiesFilename).withInputStream { properties.load(it) }
    properties
  }
}
