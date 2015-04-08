package org.instedd.cdx.app

import java.util.logging.Logger;

import groovyx.net.http.HTTPBuilder
import jcifs.ntlmssp.NtlmFlags
import jcifs.ntlmssp.Type1Message
import jcifs.ntlmssp.Type2Message
import jcifs.ntlmssp.Type3Message
import jcifs.util.Base64

import org.apache.http.auth.AuthScheme
import org.apache.http.auth.AuthSchemeFactory
import org.apache.http.auth.AuthScope
import org.apache.http.auth.NTCredentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.auth.NTLMEngine
import org.apache.http.impl.auth.NTLMEngineException
import org.apache.http.impl.auth.NTLMScheme
import org.apache.http.params.HttpParams
import org.instedd.rsync_java_client.Settings;

class ProxyConfiguration {

	private final Logger logger = Logger.getLogger(ProxyConfiguration.class.name)
	
	private static String CONFIG_FILE = "cdxproxy.properties"
	private static ProxyConfiguration detectedConfiguration;  
	
	private Properties props;
	
	ProxyConfiguration(Properties props) {
		this.props = props;
	}
	
	void apply(HTTPBuilder http) {
		if (proxyEnabled()) {
			def host = props.host
			def port = props.port.toInteger()
			
			logger.info("Will use HTTP proxy ${host}:${port} for credentials exchange")
			
			def schema = host.indexOf("https") >= 0 ? "https" : "http";
			http.setProxy(host, port, schema)
			
			def httpClient = http.client
			httpClient.authSchemes.register("ntlm", new NTLMSchemeFactory());
			
			if (props.user != null) {
				def user = props.user
				def pass = props.pass
				
				def scope = new AuthScope(host, port)
				def credentials
				
				if (user.indexOf('\\') >= 0) {
					logger.info("Using NT authentication with username ${user}");
					credentials = new NTCredentials(user.replace('\\', '/') + ":" + pass)
				} else {
					logger.info("Using basic authentication with username ${user}");
					credentials = new UsernamePasswordCredentials(user, pass)
				}
				
				httpClient.credentialsProvider.setCredentials(scope, credentials)
			}
		}
	}
	
	void overrideSyncSettings(Settings settings) {
		if (proxyEnabled()) {
			logger.info("Overriding rsync host settings because of proxy presence. Make sure to setup a tunnel from ${props.rsyncTunnelHost}:${props.rsyncTunnelPort} to ${settings.remoteHost}:${settings.remotePort}.")
			
			settings.remoteHost = props.rsyncTunnelHost
			settings.remotePort = props.rsyncTunnelPort.toInteger()
		}
	}
	
	private boolean proxyEnabled() {
		props != null && props.enabled.toBoolean()
	}
	
	static synchronized ProxyConfiguration detect() {
		if (detectedConfiguration == null)  {
			def props = new Properties()
			
			def propsFile = new File(CONFIG_FILE)
			if (propsFile.exists()) {
				propsFile.withInputStream { props.load(it) }
			}
			
			detectedConfiguration = new ProxyConfiguration(props);
		}
		return detectedConfiguration
	}

	static class NTLMSchemeFactory implements AuthSchemeFactory {

		@Override
		AuthScheme newInstance(HttpParams params) {
			new NTLMScheme(new JCIFSEngine());
		}
	}

	static class JCIFSEngine implements NTLMEngine {

		private static final int TYPE_1_FLAGS = NtlmFlags.NTLMSSP_NEGOTIATE_56\
					| NtlmFlags.NTLMSSP_NEGOTIATE_128\
					| NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2\
					| NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN\
					| NtlmFlags.NTLMSSP_REQUEST_TARGET;

		@Override
		public String generateType1Msg(final String domain, final String workstation) throws NTLMEngineException {
			final Type1Message type1Message = new Type1Message(TYPE_1_FLAGS, domain, workstation);
			return Base64.encode(type1Message.toByteArray());
		}

		@Override
		public String generateType3Msg(final String username, final String password, final String domain, final String workstation, final String challenge) throws NTLMEngineException {
			Type2Message type2Message;
			try {
				type2Message = new Type2Message(Base64.decode(challenge));
			} catch (final IOException exception) {
				throw new NTLMEngineException("Invalid NTLM type 2 message", exception);
			}
			final int type2Flags = type2Message.getFlags();
			final int type3Flags = type2Flags & (0xffffffff ^ (NtlmFlags.NTLMSSP_TARGET_TYPE_DOMAIN | NtlmFlags.NTLMSSP_TARGET_TYPE_SERVER));
			final Type3Message type3Message = new Type3Message(type2Message, password, domain, username, workstation, type3Flags);
			return Base64.encode(type3Message.toByteArray());
		}
	}

		
}
