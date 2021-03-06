package org.instedd.cdx.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.instedd.rsync_java_client.app.RSyncApplication;
import org.instedd.rsync_java_client.app.RSyncApplicationMonitor;
import org.json.JSONObject;

import com.github.zafarkhaja.semver.Version;

public class AppUpdatesMonitor implements RSyncApplicationMonitor {
	
	private static final Log log = LogFactory.getLog(AppUpdatesMonitor.class);

	private CDXSettings settings;
	private static final long updatesCheckDelay = 5 * 60 * 1000; // 5 minutes
	private static final long updatesCheckInterval = 5 * 60 * 60 * 1000; // 5 hours
	private Timer timer;

	private RSyncApplication app;

	public AppUpdatesMonitor(CDXSettings settings, RSyncApplication app) {
		this.settings = settings;
		this.app = app;
		timer = new Timer();
	}

	@Override
	public void start(RSyncApplication application) {
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					updateAppIfNecessary();
				} catch (RuntimeException e) {
					log.error("Unhandled error while checking application updates", e);
				}
			}
		}, updatesCheckDelay, updatesCheckInterval);
	}

	private void updateAppIfNecessary() {
		JSONObject versionInfo = latestVersion();
		if (updateAvailable(versionInfo.getString("version"))) {
			updateApp(versionInfo.getString("url"));
		}
	}
	
	private boolean updateAvailable(String version) {
		return Version.valueOf(version).greaterThan(AppVersion.APP_VERSION);
	}

	private JSONObject latestVersion() {
		try {
			URL versionSource = new URL(settings.authServerUrl + "/client/version.json");
			log.debug("Checking app updates from " + versionSource.toString());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionSource.openStream()));
			StringBuffer content = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line);
			}
			return new JSONObject(content.toString());
		} catch (IOException e) {
			log.warn("Couldn't check for app updates", e);
		}
		return null;
	}

	private void updateApp(String installerUrl) {
		log.info("Downloading app update from " + installerUrl);
		try {
			Path tempDir = Files.createTempDirectory("cdx-client", new FileAttribute[0]);
			File updater = new File(tempDir.toFile(), "CDX Client Updater.exe");;
			FileUtils.copyURLToFile(new URL(installerUrl), updater);
			
			app.stop();
			
			ProcessBuilder command = new ProcessBuilder(updater.getPath(), "/S", "/launchApp");
			log.info("App update downloaded - closing app to run " + command.command());
			command.start();
			System.exit(0);
		} catch (IOException e) {
			log.warn("Error trying to update from " + installerUrl, e);
			if(!app.isRunning()) {
				app.start();
			}
		}
	}

}
