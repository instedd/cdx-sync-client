

package org.instedd.cdx.app;

import java.util.logging.Logger;

import java.util.logging.Logger;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;

class Credentials {

	static Logger logger = Logger.getLogger(Credentials.name)

	File privateKey
	File publicKey

	Credentials(File privateKey, File publicKey) {
		assert publicKey
		assert publicKey.exists() && publicKey.isFile(), "Invalid public key file: ${publicKey}"

		// private key file is not readable, we cannot validate it more than this.
		assert privateKey

		this.privateKey = privateKey
		this.publicKey = publicKey
	}

	String getPrivateKeyPath() {
		privateKey.absolutePath
	}

	String getPublicKeyText() {
		publicKey.text
	}


	/** Initializes a new pair of SSH keys if necessary. */
	static Credentials initialize(String keysDirectoryPath) {
		def dir = new File(keysDirectoryPath)
		if(!dir.exists()) {
			logger.info("Creating ${dir.absolutePath} data directory...")
			dir.mkdirs()
		}

		assert dir.exists() && dir.isDirectory(), "${keysDirectoryPath} should be a directory"

		def privateKey = new File(dir, "act_key")
		if (!privateKey.exists()) {
			def stdoutBuffer = new StringBuffer()
			def stderrBuffer = new StringBuffer()
			try {
				logger.info("Generating a new pair of SSH keys [${privateKey.absolutePath}]")
				String emptyPassphrase = SystemUtils.IS_OS_WINDOWS ? "\"\"" : "" // windows ignores the argument if it's the empty string instead of passing an empty argument
				Process process = ["ssh-keygen", "-t", "rsa", "-N", emptyPassphrase, "-f", privateKey.path].execute()
				process.consumeProcessOutput(stdoutBuffer, stderrBuffer)
				process.waitForOrKill(5000)
			} catch (Exception e) {
				logger.severe("A problem occurred while generating ssh keys. Process stdout:\n${stdoutBuffer}\n Process stdin:\n${stderrBuffer}\n")
			}
		}

		new Credentials(privateKey, new File(dir, "act_key.pub"));
	}


}
