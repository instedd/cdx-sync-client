package org.instedd.cdx.app;

import static org.instedd.sync4j.util.Exceptions.interruptable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.instedd.sync4j.Settings;
import org.instedd.sync4j.app.RSyncApplication;
import org.instedd.sync4j.watcher.RsyncWatchListener.SyncMode;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length != 1) {
      System.out.println("Usage: cdxsync <properties filename>");
      System.exit(1);
    }
    String propertiesFilename = args[0];
    Properties properties = properties(propertiesFilename);

    Settings settings = Settings.fromProperties(properties);
    System.out.printf("\n\n** Settings are %s **\n\n", settings);

    String appName = properties.getProperty("app.name");
    String appIcon = properties.getProperty("app.icon");
    SyncMode appMode = SyncMode.valueOf(properties.getProperty("app.mode").toUpperCase());

    final RSyncApplication app = new RSyncApplication(settings, appName, appIcon, appMode);
    stopOnExit(app);
    app.start();

    System.out.printf("\n\n** Now go and create or edit some files on %s **\n\n", settings.localOutboxDir);
    loop(app);

  }

  protected static void stopOnExit(final RSyncApplication app) {
    Runtime.getRuntime().addShutdownHook(new Thread({ ->
      try {
        interruptable(app.&stop);
      } finally {
        System.out.println("bye!");
      }
    }));
  }

  protected static void loop(RSyncApplication app) {
    System.out.print("\n\n** Type bye to stop app, or stop it from the system tray **\n\n");
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine() && app.isRunning()) {
      if (scanner.nextLine().equals("bye"))
        break;
    }
    System.exit(0);
  }

  protected static properties(String propertiesFilename) {
    def properties = new Properties()
    new File(propertiesFilename).withInputStream { properties.load(it) }
    properties;
  }

}
