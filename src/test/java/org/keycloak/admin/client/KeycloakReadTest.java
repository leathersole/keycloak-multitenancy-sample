package org.keycloak.admin.client;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.keycloak.admin.client.resource.ApplicationsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ApplicationRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

public class KeycloakReadTest {

	private static Keycloak keycloak;

	@BeforeClass
	public static void setup(){
		String authServer = "http://localhost:8080/auth";
		
		keycloak = Keycloak.getInstance(authServer, "tenant1",
				"user-tenant1", "user-tenant1", "multi-tenant", "password");
	}
	
	@Test
	@Ignore("This depends on a Keycloak server environment")
	public void shuldGetRealmInfo() throws Exception {
		RealmRepresentation realm = keycloak.realm("tenant1")
				.toRepresentation();
		assertThat(realm.getRealm(), is(equalTo("tenant1")));
	}
	
}
