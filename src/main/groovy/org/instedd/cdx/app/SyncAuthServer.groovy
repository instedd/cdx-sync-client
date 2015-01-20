package org.instedd.cdx.app;

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

public class SyncAuthServer {

  private String authToken;
  private String authServerUrl;

  public SyncAuthServer(String authToken, String authServerUrl) {
    this.authToken = authToken;
    this.authServerUrl = authServerUrl;
  }

  public void authenticate(String publicKey) {
    def http = new HTTPBuilder(authServerUrl)
    def r
    http.request( POST, JSON ) {
      uri.path = '/api/activations'
      send URLENC, [public_key: publicKey, token: authToken]

      response.success = { resp, json ->
        println json
        if ( resp.status == 'success' ) {
          r =  json.settings
        } else {
          throw new Exception("Authentication failed: ${json.message}")
        }
      }
    }
    r
  }
}


