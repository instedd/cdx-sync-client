package org.instedd.cdx.app;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.instedd.rsync_java_client.RsyncSynchronizerListener;
import org.instedd.rsync_java_client.app.RSyncApplication;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.org.apache.xerces.internal.util.URI;

public class SystemTrayMonitor extends org.instedd.rsync_java_client.app.SystemTrayMonitor
  implements RsyncSynchronizerListener {
	
	private static final Log log = LogFactory.getLog(SystemTrayMonitor.class);

  private CDXSettings settings;
  private Thread animationThread;
  private final Image[] animationIcons;
  private final Image defaultIcon;
  private final Image failIcon;
  private boolean failed;

  public SystemTrayMonitor(CDXSettings settings) {
    super(settings.appName, SystemTrayMonitor.class.getResource("/icon-0.png"));
    this.settings = settings;

    animationIcons = new Image[3];
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    animationIcons[0] = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-60.png"));
    animationIcons[1] = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-120.png"));
    animationIcons[2] = defaultIcon = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-0.png"));
    failIcon = toolkit.getImage(SystemTrayMonitor.class.getResource("/icon-red.png"));
  }

  @Override
  protected void configureMenu(RSyncApplication application, JPopupMenu menu) {
    super.configureMenu(application, menu);

    JMenuItem menuItem = new JMenuItem("Settings...");
    menuItem.addActionListener((e) -> {
      try {
        if (SettingsDialog.editSettings(settings)) {
          application.restart();
        }
      } catch (IOException ex) {

      }
    });
    menu.add(menuItem);

    menuItem = new JMenuItem("View Log...");
    menuItem.addActionListener((e) -> {
      try {
        Desktop.getDesktop().open(new File(settings.logPath()));
      } catch (IOException ex) {

      }
    });
    menu.add(menuItem);
  }

  private void startAnimation() {
    synchronized (this) {
      if (animationThread != null) {
        animationThread.interrupt();
      }

      Thread newAnimationThread = new Thread(() -> {
        try {
          while (true) {
            for (int i = 0; i < animationIcons.length; i++) {
              getTrayIcon().setImage(animationIcons[i]);
              Thread.sleep(300);
            }
          }
        } catch (InterruptedException e) {
          // End of the animation
        }

        getTrayIcon().setImage(failed ? failIcon : defaultIcon);
        animationThread = null;
      });
      newAnimationThread.start();
      animationThread = newAnimationThread;
    }
  }

  private void stopAnimation() {
    synchronized (this) {
      if (animationThread != null) {
        animationThread.interrupt();
      }
    }
  }

  @Override
  public void transferStarted() {
    super.transferStarted();
    failed = false;
    startAnimation();
  }

  @Override
  public void transferFailed(String errorMessage) {
    super.transferFailed(errorMessage);
    submitLog("Transfer failed: " + errorMessage);
    getTrayIcon().displayMessage("Transfer Failed\t", null, MessageType.ERROR);
    failed = true;
    stopAnimation();
  }

  @Override
  public void transferCompleted(List<String> uploadedFiles, List<String> downloadedFiles) {
    super.transferCompleted(uploadedFiles, downloadedFiles);
    stopAnimation();

    if ((uploadedFiles == null || uploadedFiles.size() == 0) &&
        (downloadedFiles == null || downloadedFiles.size() == 0)) { return; }

    StringBuilder message = new StringBuilder();
    if (uploadedFiles != null) {
      message.append(uploadedFiles.size()).append(" file(s) uploaded:");
      message.append("\n");
      for (String file : uploadedFiles) {
        message.append("  - ").append(file);
      }
    }
    getTrayIcon().displayMessage("Transfer Completed\t", message.toString(), MessageType.INFO);
  }

	private void submitLog(String string) {
		String deviceUUID = settings.deviceUUID;
		if (deviceUUID == null) return;
	
		String url = settings.authServerUrl + "/devices/" + deviceUUID + "/device_logs?key=" + settings.deviceKey;
		try {
			Unirest.post(url).body(string).asString();
		} catch (UnirestException e) {
			log.fatal("Can't send error log to CDX server", e);
			getTrayIcon().displayMessage("Can't send error log to CDX server\t", null, MessageType.ERROR);
		}
	}
}
