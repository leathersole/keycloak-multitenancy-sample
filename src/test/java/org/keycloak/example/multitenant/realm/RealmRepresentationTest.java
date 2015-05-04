package org.keycloak.example.multitenant.realm;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.junit.Ignore;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.util.JsonSerialization;

public class RealmRepresentationTest {
	/*
	 * src/test/resources/sample-realm.json:
	 * 
	 * { "id": "sample", "realm": "sample", "enabled": true, "sslRequired":
	 * "external", "passwordCredentialGrantAllowed": true,
	 * "requiredCredentials": [ "password" ], "users" : [ { "username" :
	 * "sample-administrator", "enabled": true, "credentials" : [ { "type" :
	 * "password", "value" : "password" } ], "realmRoles": [ "user","admin" ],
	 * "applicationRoles": { "realm-management": [ "realm-admin" ] } } ],
	 * "roles" : { "realm" : [ { "name": "user", "description":
	 * "User privileges" }, { "name": "admin", "description":
	 * "Administrator privileges" } ] } }
	 */
	@Test
	@Ignore("This depends on a Keycloak server environment")
	public void shouldRealmJsonCanBeParse() throws Exception {
		JsonFactory factory = JsonSerialization.mapper.getJsonFactory();
		JsonParser parser = factory.createJsonParser(new File(
				"src/test/resources/sample-realm.json"));
		parser.nextToken();
		assertThat(parser.getCurrentToken(),
				is(equalTo(JsonToken.START_OBJECT)));
		RealmRepresentation realmRep = parser
				.readValueAs(RealmRepresentation.class);
		assertThat(realmRep.getRealm(), is(equalTo("sample")));
		assertThat(realmRep.getUsers().get(0).getUsername(),
				is(equalTo("sample-administrator")));
		parser.close();

	}
}
