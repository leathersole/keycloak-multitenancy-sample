package org.keycloak.example.multitenant.realm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.junit.Test;
import org.keycloak.util.PemUtils;

public class KeyPairTest {
	@Test
	public void testName() throws Exception {
		if (Security.getProvider("BC") == null)
			Security.addProvider(new BouncyCastleProvider());
		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		System.out.println(printKey(keyPair.getPrivate()));
		System.out.println(printKey(keyPair.getPublic()));
	}

	private static String printKey(Object key) {
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
