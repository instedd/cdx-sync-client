package org.instedd.cdx.app;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class SyncAuthServer {
  private String authToken;
  private String authServerUrl;

  public SyncAuthServer(String authToken, String authServerUrl) {
    this.authToken = authToken;
    this.authServerUrl = authServerUrl;
    ProxyConfiguration.detect().apply();
  }

  public JSONObject authenticate(String publicKey) throws Exception {
    HttpResponse<JsonNode> response = Unirest.post(authServerUrl + "/api/activations")
      .field("public_key", publicKey)
      .field("token", authToken)
      .asJson();

    if (response.getStatus() == 200) {
      JSONObject json = response.getBody().getObject();

      switch (json.getString("status")) {
        case "success":
          return json.getJSONObject("settings");
        default:
          throw new Exception("Activation failed: " + json.getString("message"));
      }
    }

    throw new Exception("Could not activate: " + response.getStatusText());
  }
}
