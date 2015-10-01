package org.instedd.cdx.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.instedd.rsync_java_client.app.RSyncApplication;
import org.instedd.rsync_java_client.app.RSyncApplicationMonitor;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class CommandsCheckMonitor implements RSyncApplicationMonitor {
  private static final Log log = LogFactory.getLog(AppUpdatesMonitor.class);
  
  private CDXSettings settings;
  private static final long updatesCheckDelay = 5 * 60 * 1000; // 5 minutes
  private static final long updatesCheckInterval = 1 * 60 * 60 * 1000; // 1 hour
  private Timer timer;

  public CommandsCheckMonitor(CDXSettings settings) {
    this.settings = settings;
    timer = new Timer();
  }

  @Override
  public void start(RSyncApplication application) {
    timer.schedule(new TimerTask() {
      public void run() {
        try {
          checkCommands();
        } catch (Exception e) {
          log.error("Unhandled error while checking commands", e);
        }
      }
    }, updatesCheckDelay, updatesCheckInterval);
  }

  protected void checkCommands() throws Exception {
    String deviceUUID = settings.deviceUUID;
    String deviceKey = settings.deviceKey;
    
    if (deviceUUID == null || deviceKey == null)
      return;
    
    String commandsURL = settings.authServerUrl + "/devices/" + settings.deviceUUID + "/device_commands?key=" + settings.deviceKey;
    HttpResponse<JsonNode> response = Unirest.get(commandsURL).asJson();
    if (response.getStatus() == 200) {
       JSONArray commands = response.getBody().getArray();
       for(int i = 0; i < commands.length(); i += 1) {
         JSONObject command = commands.getJSONObject(i);
         long id = command.getLong("id");
         String commandName = command.getString("name");
         if ("send_logs".equals(commandName)) {
           sendLogs(id);
         }
       }
    }
  }

  private void sendLogs(long id) throws Exception {
    String logs = readLogs();
    String commandURL = settings.authServerUrl + "/devices/" + settings.deviceUUID + "/device_commands/" + id + "/reply?key=" + settings.deviceKey;
    Unirest.post(commandURL).body(logs).asString();
  }
  
  private String readLogs() throws IOException {
    try(BufferedReader reader = new BufferedReader(new FileReader(settings.logPath()))) {
      StringBuffer content = new StringBuffer();
      String line = null;
      while ((line = reader.readLine()) != null) {
        content.append(line);
      }
      return content.toString();
    }
  }
}
