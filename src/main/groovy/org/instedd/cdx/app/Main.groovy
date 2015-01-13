package org.instedd.cdx.app

import static org.instedd.sync4j.util.Exceptions.interruptable

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import java.util.Scanner

import org.instedd.sync4j.Settings
import org.instedd.sync4j.app.ConsoleMonitor;
import org.instedd.sync4j.app.RSyncApplication
import org.instedd.sync4j.app.SystemTrayMonitor;
import org.instedd.sync4j.watcher.RsyncWatchListener.SyncMode

public class Main {
  public static void main(String[] args) {
    if (args.length != 1) {
      println("Usage: cdxsync <properties filename>")
      System.exit(1)
    }
    def propertiesFilename = args[0]
    def properties = properties(propertiesFilename)

    def settings = Settings.fromProperties(properties)
    printf("\n\n** Settings are %s **\n\n", settings)

    def appName = properties.getProperty("app.name")
    def appIcon = Main.class.getResource(properties.getProperty("app.icon"))

    def appMode = SyncMode.valueOf(properties.getProperty("app.mode").toUpperCase())

    def app = new RSyncApplication(settings, appMode)
    app.start(new SystemTrayMonitor(appName, appIcon), new ConsoleMonitor())

    printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir)
  }

  protected static properties(String propertiesFilename) {
    def properties = new Properties()
    new File(propertiesFilename).withInputStream { properties.load(it) }
    properties
  }

}
