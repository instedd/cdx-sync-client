package org.instedd.cdx.app;

import static groovyx.net.http.ContentType.*
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

    def response
    http.post(
        path: '/api/activations',
        contentType: URLENC,
        body: [public_key: publicKey, token: authToken]) {  rs, json ->
          if ( json.status == 'success' ) {
            response =  json.settings
          } else {
            throw new Exception(json.status)
          }
        }
    response
  }
}


