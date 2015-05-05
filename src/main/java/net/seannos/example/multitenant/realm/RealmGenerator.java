package net.seannos.example.multitenant.realm;

import static net.seannos.example.multitenant.util.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

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
import org.keycloak.representations.idm.ApplicationRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.util.JsonSerialization;

@Named
public class RealmGenerator {

	@PersistenceContext(unitName = "default", type=PersistenceContextType.EXTENDED)
	EntityManager em;

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

	@Transactional
	public void generate(String realmName, String adminName,
			String adminPassword) {

		checkRealmExists(realmName);
		String json = createRealmOnServer(realmName, adminName, adminPassword);
		if (json != null) {
			persist(realmName, json);
		}
	}

	private String createRealmOnServer(String realmName, String adminName,
			String adminPassword) {
		String pathToTemplate = PropertyStore.get(PROP_REALM_TEMPLATE_JSON);
		Keycloak keycloak = getRealmAdminProxyInstance();
		JsonFactory factory = JsonSerialization.mapper.getJsonFactory();
		String json = null;
		try {
			JsonParser parser = factory.createJsonParser(getClass()
					.getResourceAsStream(pathToTemplate));
			parser.nextToken();
			RealmRepresentation realmRep = parser
					.readValueAs(RealmRepresentation.class);
			parser.close();
			realmRep.setId(realmName);
			realmRep.setRealm(realmName);
			realmRep.getUsers().get(0).setUsername(adminName);
			realmRep.getUsers().get(0).getCredentials().get(0)
					.setValue(adminPassword);

			KeyPair keypair = KeyPairUtils.generate();
			realmRep.setPublicKey(KeyPairUtils.printKey(keypair.getPublic()));
			realmRep.setPrivateKey(KeyPairUtils.printKey(keypair.getPrivate()));

			ApplicationRepresentation app = realmRep.getApplications().get(0);
			app.setBaseUrl("/multitenant/" + realmName);
			app.setAdminUrl("/multitenant/groupmanage/" + realmName);
			List<String> redirectUrls = new ArrayList<String>();
			redirectUrls.add("/multitenant/group/authn/" + realmName + "/*");
			redirectUrls.add("/multitenant/admin/" + realmName + "/*");
			redirectUrls.add("/multitenant/groupmanage/" + realmName + "/*");
			app.setRedirectUris(redirectUrls);

			keycloak.realms().create(realmRep);
			keycloak.close();
			keycloak = Keycloak.getInstance(authServer, realmName, adminName,
					adminPassword, "admin-client");
			json = getJson(realmName, "multi-tenant", keycloak.tokenManager()
					.getAccessTokenString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Failure e) {
			throw new RuntimeException(e);
		} finally {
			keycloak.close();
		}
		return json;
	}

	private void checkRealmExists(String realmName) {
		// EntityManagerFactory emf = Persistence
		// .createEntityManagerFactory("multitenant");
		// EntityManager em = emf.createEntityManager();
		Query query = em.createNamedQuery("findTenantByName", Tenant.class);
		List<Tenant> tenants = (List<Tenant>) query.setParameter("realm",
				realmName).getResultList();
		if (tenants.size() > 0) {
			throw new RuntimeException("The realm is already exisit.");
		}
		
	}

	private void persist(String realmName, String json) {
		// EntityManagerFactory emf = Persistence
		// .createEntityManagerFactory("multitenant");
		// EntityManager em = emf.createEntityManager();
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
