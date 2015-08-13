package org.instedd.cdx.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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

	public AppUpdatesMonitor(CDXSettings settings) {
		this.settings = settings;
		timer = new Timer();
	}

	@Override
	public void start(RSyncApplication application) {
		timer.schedule(new TimerTask() {
			public void run() {
				updateAppIfNecessary();
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
			URL versionSource = new URL("http://" + settings.remoteHost + "/client/version.json");
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
			File updater = File.createTempFile("cdxclient", "updater.exe");
			FileUtils.copyURLToFile(new URL(installerUrl), updater);
			
			// TODO: stop rsync process before running the updater
			
			ProcessBuilder command = new ProcessBuilder(updater.getPath(), "/S", "/launchApp");
			log.info("App update downloaded - closing app to run " + command.command());
			command.start();
			System.exit(0);
		} catch (IOException e) {
			log.warn("Error trying to update from " + installerUrl, e);
		}
	}

}
