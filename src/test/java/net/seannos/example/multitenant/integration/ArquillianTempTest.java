package net.seannos.example.multitenant.integration;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.seannos.example.multitenant.model.Tenant;
import net.seannos.example.multitenant.realm.RealmGenerator;
import net.seannos.example.multitenant.util.Constants;
import net.seannos.example.multitenant.util.PropertyStore;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.util.JsonSerialization;

@RunWith(Arquillian.class)
public class ArquillianTempTest {

	RealmGenerator realmGenerator;

	@Deployment
	public static WebArchive createDeployment() {
		System.out.println(System.getProperty("user.dir"));
		File[] libs = Maven
				.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("org.keycloak:keycloak-admin-client",
						"org.keycloak:keycloak-adapter-core",
						"org.keycloak:keycloak-core").withTransitivity()
				.asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(RealmGenerator.class)
				.addPackage(Constants.class.getPackage())
				.addClass(Tenant.class)
				.addAsLibraries(libs)
				.addAsResource("system.properties",
						"/WEB-INF/conf/system.properties")
				.addAsWebInfResource(
						new File("src/main/webapp",
								"WEB-INF/jboss-deployment-structure.xml"))
				.addAsResource(
						new File("src/main/resources/realm-template.json"))
				.addAsResource(
						new File("src/main/resources", "realm-template.json"))
				.addAsWebInfResource("multi-tenant-test-ds.xml")
				.addAsManifestResource("META-INF/test-persistence.xml",
						"persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	@Ignore("This is a tempral test")
	public void firstTest() throws Exception {

		JsonFactory factory = JsonSerialization.mapper.getJsonFactory();
		JsonParser parser;
		parser = factory.createJsonParser(getClass().getResourceAsStream(
				"/realm-template.json"));
		parser.nextToken();
		RealmRepresentation realmRep = parser
				.readValueAs(RealmRepresentation.class);
		parser.close();

	}

	@Test
	@Ignore("This is a tempral test")
	public void secondTest() throws Exception {
		String pathToTemplate = PropertyStore
				.get(Constants.PROP_REALM_TEMPLATE_JSON);

		JsonFactory factory = JsonSerialization.mapper.getJsonFactory();
		JsonParser parser;
		parser = factory
				.createJsonParser(new File(
						"/Users/taka/sources/github/leathersole/keycloak-multitenancy-sample/src/main/webapp/WEB-INF/template/realm-template.json"));
		parser.nextToken();
		RealmRepresentation realmRep = parser
				.readValueAs(RealmRepresentation.class);
		parser.close();

	}

	@Test
	@Ignore("This is a feasivility study")
	public void thirdTest() throws Exception {
		String realmName = "foo";
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("multitenant");
		EntityManager em = emf.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		Query query = em.createNamedQuery("findTenantByName", Tenant.class);
		List<Tenant> tenants = (List<Tenant>) query.setParameter("realm",
				realmName).getResultList();
		assertThat(tenants.size(), is(equalTo(0)));
		

		Tenant newTenant = new Tenant();
		newTenant.setRealm(realmName);
		// newTenant.setJson(json.replaceAll("[\n\r]", ""));
		em.getTransaction().begin();
		em.persist(newTenant);
		em.getTransaction().commit();
		emf = Persistence
				.createEntityManagerFactory("multitenant");
		em = emf.createEntityManager();
		query = em.createNamedQuery("findTenantByName", Tenant.class);
		tenants = (List<Tenant>) query.setParameter("realm", realmName)
				.getResultList();
		assertThat(tenants.size(), is(equalTo(1)));

	}

}
