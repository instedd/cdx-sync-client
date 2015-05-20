package org.instedd.cdx.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;

import com.mashape.unirest.http.Unirest;

import org.apache.http.HttpHost;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.instedd.rsync_java_client.Settings;

class ProxyConfiguration {

	private static final Log log = LogFactory.getLog(ProxyConfiguration.class);

	private static String CONFIG_FILE = "cdxproxy.properties";
	private static ProxyConfiguration detectedConfiguration;

	private Properties props;

	ProxyConfiguration(Properties props) {
		this.props = props;
	}

	void apply() {
		if (proxyEnabled()) {
			String host = props.getProperty("host");
			int port = Integer.parseInt(props.getProperty("port"));

			log.info("Will use HTTP proxy " + host + ":" + port + " for credentials exchange");

			String schema = host.indexOf("https") >= 0 ? "https" : "http";
			Unirest.setProxy(new HttpHost(host, port, schema));

			// client.authSchemes.register("ntlm", new NTLMSchemeFactory());

			String user;
			if ((user = props.getProperty("user")) != null) {
				String pass = props.getProperty("pass");

				AuthScope scope = new AuthScope(host, port);
				Credentials credentials;

				if (user.indexOf('\\') >= 0) {
					log.info("Using NT authentication with username ${user}");
					credentials = new NTCredentials(user.replace('\\', '/') + ":" + pass);
				} else {
					log.info("Using basic authentication with username ${user}");
					credentials = new UsernamePasswordCredentials(user, pass);
				}

				CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(scope, credentials);
        HttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

        Unirest.setHttpClient(httpclient);
			}
		}
	}

	void overrideSyncSettings(Settings settings) {
		if (proxyEnabled()) {
			log.info("Overriding rsync host settings because of proxy presence. Make sure to setup a tunnel from ${props.rsyncTunnelHost}:${props.rsyncTunnelPort} to ${settings.remoteHost}:${settings.remotePort}.");

			settings.remoteHost = props.getProperty("rsyncTunnelHost");
			settings.remotePort = Integer.parseInt(props.getProperty("rsyncTunnelPort"));
		}
	}

	private boolean proxyEnabled() {
		String enabled = props.getProperty("enabled");
		return enabled != null && Boolean.parseBoolean(enabled);
	}

	static synchronized ProxyConfiguration detect() {
		if (detectedConfiguration == null)  {
			Properties props = new Properties();

			File propsFile = new File(CONFIG_FILE);
			if (propsFile.exists()) {
				try (InputStream in = Files.newInputStream(propsFile.toPath())) {
					props.load(in);
				} catch (IOException ex) {
					log.warn("Could not load proxy configuration file", ex);
				}
			}

			detectedConfiguration = new ProxyConfiguration(props);
		}
		return detectedConfiguration;
	}
}
