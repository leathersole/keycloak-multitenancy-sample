package org.keycloak.example.multitenant.realm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;

public class RealmAdminTest {

	static String authServer = "http://localhost:8080/auth";

	@BeforeClass
	public static void setup() throws Exception {
		Keycloak keycloak = getRealmAdminProxyInstance();
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

	@AfterClass
	public static void teardown() {
		Keycloak keycloak = Keycloak.getInstance(authServer, "master", "admin",
				"admin1234!", "admin-client",
				"68e62f67-3fb4-495c-9e32-e6d58eb7b874");
		keycloak.realm("sample").remove();
		keycloak.close();
	}

	@Test
	public void adminShuldGetUserName() throws Exception {
		Keycloak keycloak2 = Keycloak.getInstance(authServer, "sample",
				"sample-administrator", "password", "admin-client");
		List<UserRepresentation> users = keycloak2.realm("sample").users()
				.search("sample-administrator", null, null);
		assertThat(users.get(0).getUsername(),
				is(equalTo("sample-administrator")));
		keycloak2.close();
	}
	
	private static Keycloak getRealmAdminProxyInstance(){
		return Keycloak.getInstance(authServer, "master",
				"realm_manager_proxy", "password", "realm_manager_proxy",
				"846ce2e6-3dde-4ec0-ae88-645f326e320e");
	}

}
