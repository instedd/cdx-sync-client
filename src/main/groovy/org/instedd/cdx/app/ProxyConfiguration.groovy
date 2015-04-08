package org.instedd.cdx.app

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

class ProxyConfiguration {

	private static String CONFIG_FILE = "cdxproxy.properties"
	
	private Properties props;
	
	ProxyConfiguration(Properties props) {
		this.props = props;
	}
	
	void apply(HTTPBuilder http) {
		if (props != null && props.enabled.toBoolean()) {
			def host = props.host
			def port = props.port.toInteger()
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
					credentials = new NTCredentials(user.replace('\\', '/') + ":" + pass)
				} else {
					credentials = new UsernamePasswordCredentials(user, pass)
				}
				
				httpClient.credentialsProvider.setCredentials(scope, credentials)
			}
		}
	}
	
	static ProxyConfiguration detect() {
		def props = new Properties()

		def propsFile = new File(CONFIG_FILE)
		if (propsFile.exists()) {
			propsFile.withInputStream { props.load(it) }
		}
		
		new ProxyConfiguration(props);
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
