package org.instedd.cdx.app;

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*

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
        body: [public_key: publicKey, token: authToken]) {  rs -> response  = rs}
    response
  }
}


