package org.keycloak.admin.client;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.keycloak.admin.client.resource.ApplicationsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ApplicationRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;

public class KeycloakWriteTest {

	String authServer = "http://localhost:8080/auth";

	@Test
	@Ignore("This depends on a Keycloak server environment")
	public void shouldCreateAndDeleteRealm() throws Exception {
		Keycloak keycloak = getClient();
		RealmRepresentation realm = new RealmRepresentation();
		realm.setRealm("sample");
		keycloak.realms().create(realm);
		keycloak.close();

		keycloak = getClient();
		List<RealmRepresentation> realms = keycloak.realms().findAll();
		Set<String> realmnames = new HashSet<>();
		for (RealmRepresentation r : realms) {
			realmnames.add(r.getRealm());
		}
		assertThat(realmnames, hasItem("sample"));

		keycloak.realm("sample").remove();
		keycloak.close();

		keycloak = getClient();
		realms = keycloak.realms().findAll();
		for (RealmRepresentation r : realms) {
			assertThat(r.getRealm(), is(not(equalTo("sample"))));
		}

	}

	private Keycloak getClient() {
		Keycloak keycloak = Keycloak.getInstance(authServer, "master", "admin",
				"admin1234!", "admin-client",
				"68e62f67-3fb4-495c-9e32-e6d58eb7b874");
		return keycloak;

	}

	@Test
	@Ignore("This depends on a Keycloak server environment")
	public void shouldMoveToRealm() throws Exception {
		Keycloak keycloak = Keycloak.getInstance(authServer, "master",
				"realm_manager_proxy", "password", "realm_manager_proxy",
				"846ce2e6-3dde-4ec0-ae88-645f326e320e");
		JsonFactory factory = JsonSerialization.mapper.getJsonFactory();
		JsonParser parser = factory.createJsonParser(new File(
				"src/test/resources/sample-realm.json"));
		parser.nextToken();
		RealmRepresentation realmRep = parser
				.readValueAs(RealmRepresentation.class);
		parser.close();
		keycloak.realms().create(realmRep);
		keycloak.close();
	}

	@Test
	@Ignore("This depends on a Keycloak server environment")
	public void testName() throws Exception {
		Keycloak keycloak2 = Keycloak.getInstance(authServer, "sample",
				"sample-administrator", "password", "admin-client");
		List<UserRepresentation> users = keycloak2.realm("sample").users().search("sample-administrator", null, null);
		assertThat(users.get(0).getUsername(), is(equalTo("sample-administrator")));
	}
}
