package org.instedd.cdx.app;

import java.util.Properties;

import org.instedd.rsync_java_client.Settings;

public class CDXSettings extends Settings {
  public String authServerUrl;
  public String deviceUUID;

  public CDXSettings() {
    super("cdx-sync-app", null);
    authServerUrl = "http://localhost:3000";
    strictHostChecking = false;
  }
  
  @Override
  public Properties toProperties() {
	Properties properties = super.toProperties();
	if (authServerUrl != null) {
		properties.setProperty("auth.server.url", authServerUrl);
	}
	if (deviceUUID != null) {
		properties.setProperty("device.uuid", deviceUUID);
	}
	return properties;
  }
  
  @Override
  public void fromProperties(Properties properties) {
	super.fromProperties(properties);
	this.authServerUrl = loadProperty(properties, "auth.server.url", authServerUrl);
	this.deviceUUID = loadProperty(properties, "device.uuid", deviceUUID);
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
