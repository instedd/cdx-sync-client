package org.instedd.cdx.app;

import org.instedd.rsync_java_client.Settings;

public class CDXSettings extends Settings {
  public String authServerUrl;
  public String deviceUUID;

  public CDXSettings() {
    super("cdx-sync-app", null);
    authServerUrl = "http://localhost:3000";
    strictHostChecking = false;
  }

  public String logPath() {
    return rootPath.resolve(appName + ".log").toString();
  }

  public void copyTo(CDXSettings other) {
    super.copyTo(other);
    other.authServerUrl = this.authServerUrl;
    other.deviceUUID = this.deviceUUID;
  }

  public CDXSettings clone() {
    CDXSettings clone = new CDXSettings();
    this.copyTo(clone);
    return clone;
  }

}
