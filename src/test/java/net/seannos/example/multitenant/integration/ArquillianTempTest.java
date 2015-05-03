package net.seannos.example.multitenant.integration;

import java.io.File;

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
				.addAsLibraries(libs)
				.addAsResource("system.properties",
						"/WEB-INF/conf/system.properties")
				.addAsWebInfResource(
						new File("src/main/webapp",
								"WEB-INF/jboss-deployment-structure.xml"))
				.addAsResource(
						new File("src/main/resources/realm-template.json"))
				.addAsWebInfResource(
						new File("src/main/webapp",
								"WEB-INF/template/realm-template.json"))
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
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

}
