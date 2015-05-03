package net.seannos.example.multitenant.util;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.keycloak.util.PemUtils;

public class KeyPairUtils {

	public static KeyPair generate() {
		if (Security.getProvider("BC") == null)
			Security.addProvider(new BouncyCastleProvider());
		KeyPair keyPair = null;
		try {
			keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return keyPair;
	}

	public static String printKey(Object key) {
		StringWriter writer = new StringWriter();
		PEMWriter pemWriter = new PEMWriter(writer);
		try {
			pemWriter.writeObject(key);
			pemWriter.flush();
			pemWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String s = writer.toString();
		return PemUtils.removeBeginEnd(s);

	}
}
