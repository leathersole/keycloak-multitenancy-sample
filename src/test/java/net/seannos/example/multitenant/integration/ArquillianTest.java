package net.seannos.example.multitenant.integration;

import java.io.File;

import net.seannos.example.multitenant.model.Tenant;
import net.seannos.example.multitenant.realm.RealmGenerator;
import net.seannos.example.multitenant.util.Constants;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ArquillianTest {

	RealmGenerator realmGenerator;

	@Deployment
	public static WebArchive createDeployment() {
		System.out.println(System.getProperty("user.dir"));
		File[] libs = Maven
				.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("org.keycloak:keycloak-admin-client",
						"org.keycloak:keycloak-adapter-core",
						"org.keycloak:keycloak-core",
						"org.bouncycastle:bcprov-jdk15on",
						"org.bouncycastle:bcpkix-jdk15on").withTransitivity()
				.asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(RealmGenerator.class)
				.addClass(Tenant.class)
				.addPackage(Constants.class.getPackage())
				.addAsLibraries(libs)
				.addAsResource("system.properties",
						"/WEB-INF/conf/system.properties")
				.addAsWebInfResource(
						new File("src/main/webapp",
								"WEB-INF/jboss-deployment-structure.xml"))
				.addAsResource(
						new File("src/main/resources/realm-template.json"))
				.addAsWebInfResource("multi-tenant-test-ds.xml")
				.addAsManifestResource("META-INF/test-persistence.xml",
						"persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void firstTest() throws Exception {
		realmGenerator = new RealmGenerator();
		realmGenerator.generate("foo", "bar", "baz");
	}
}
