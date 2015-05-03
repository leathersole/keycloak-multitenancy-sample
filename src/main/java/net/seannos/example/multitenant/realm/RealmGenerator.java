package net.seannos.example.multitenant.realm;

import static net.seannos.example.multitenant.util.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.seannos.example.multitenant.model.Tenant;
import net.seannos.example.multitenant.util.KeyPairUtils;
import net.seannos.example.multitenant.util.PropertyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.keycloak.adapters.HttpClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.util.JsonSerialization;

public class RealmGenerator {

	static String authServer = PropertyStore.get(PROP_SERVER_AUTH_URL);

	public static class Failure extends Exception {
		private int status;

		public Failure(int status) {
			this.status = status;
		}

		public int getStatus() {
			return status;
		}
	}

	public void generate(String realmName, String adminName,
			String adminPassword) {

		String pathToTemplate = PropertyStore.get(PROP_REALM_TEMPLATE_JSON);

		Keycloak keycloak = getRealmAdminProxyInstance();
		JsonFactory factory = JsonSerialization.mapper.getJsonFactory();
		JsonParser parser;
		try {
			parser = factory.createJsonParser(getClass().getResourceAsStream(
					pathToTemplate));
			parser.nextToken();
			RealmRepresentation realmRep = parser
					.readValueAs(RealmRepresentation.class);
			parser.close();
			realmRep.setRealm(realmName);
			realmRep.getUsers().get(0).setUsername(adminName);
			realmRep.getUsers().get(0).getCredentials().get(0)
					.setValue(adminPassword);

			KeyPair keypair = KeyPairUtils.generate();
			realmRep.setPublicKey(KeyPairUtils.printKey(keypair.getPublic()));
			realmRep.setPrivateKey(KeyPairUtils.printKey(keypair.getPrivate()));

			keycloak.realms().create(realmRep);
			keycloak.close();
			keycloak = Keycloak.getInstance(authServer, realmName, adminName,
					adminPassword, "admin-client");
			String json = getJson(realmName, "multi-tenant", keycloak
					.tokenManager().getAccessTokenString());
			persist(realmName, json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Failure e) {
			throw new RuntimeException(e);
		} finally {
			keycloak.close();
		}
	}

	private void persist(String realmName, String json) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("keycloak");
		EntityManager em = emf.createEntityManager();
		Query query = em.createNamedQuery("findTenantByName", Tenant.class);
		List<Tenant> tenants = (List<Tenant>) query.setParameter("realm",
				realmName).getResultList();
		if (tenants.size() > 0) {
			throw new RuntimeException("The realm is already exisit.");
		}

		Tenant newTenant = new Tenant();
		newTenant.setRealm(realmName);
		newTenant.setJson(json);
		em.persist(newTenant);
	}

	public String getJson(String realmName, String appName, String token)
			throws Failure {
		HttpClient client = new HttpClientBuilder().build();
		try {
			HttpGet get = new HttpGet(authServer + "/admin/realms/" + realmName
					+ "/applications/" + appName + "/installation/json");
			get.addHeader("Authorization", "Bearer " + token);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Failure(response.getStatusLine().getStatusCode());
			}
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			return getStringFromInputStream(is);

		} catch (IOException e) {
			throw new RuntimeException(e);

		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	private static Keycloak getRealmAdminProxyInstance() {

		String oauthId = PropertyStore.get(PROP_REALM_MASTER_OAUTH_ID);
		String oauthSecret = PropertyStore.get(PROP_REALM_MASTER_OAUTH_SECRET);
		String user = PropertyStore.get(PROP_REALM_MATER_USER_PROXY);
		String pass = PropertyStore.get(PROP_REALM_MATER_USER_PASS);

		return Keycloak.getInstance(authServer, "master", user, pass, oauthId,
				oauthSecret);
	}

	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
