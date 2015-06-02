package org.instedd.cdx.app;

import org.instedd.rsync_java_client.Settings;

public class CDXSettings extends Settings {
  public String authServerUrl;

  public CDXSettings() {
    super("cdx-sync-app", null);
    authServerUrl = "http://cdp-stg.instedd.org";
    strictHostChecking = false;
  }

  public String logPath() {
    return rootPath.resolve(appName + ".log").toString();
  }

  public void copyTo(CDXSettings other) {
    super.copyTo(other);
    other.authServerUrl = this.authServerUrl;
  }

  public CDXSettings clone() {
    CDXSettings clone = new CDXSettings();
    this.copyTo(clone);
    return clone;
  }

}
