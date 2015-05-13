package org.instedd.cdx.app;

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.HTTPBuilder

public class SyncAuthServer {

  private String authToken;
  private String authServerUrl;
  private ProxyConfiguration proxyConfig;

  public SyncAuthServer(String authToken, String authServerUrl) {
    this.authToken = authToken;
    this.authServerUrl = authServerUrl;
    this.proxyConfig = ProxyConfiguration.detect();
  }

  def authenticate(String publicKey) {
    def http = new HTTPBuilder(authServerUrl)

    proxyConfig.apply(http)

    def settings
    http.request( POST, JSON ) {
      uri.path = '/api/activations'
      send URLENC, [public_key: publicKey, token: authToken]

      response.success = { resp, json ->
        if ( json.status == 'success' ) {
          settings =  json.settings
        } else {
          throw new Exception("Activation failed: ${json.message}")
        }
      }
    }
    settings
  }

}

