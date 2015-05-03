package net.seannos.example.multitenant.realm;

import static org.junit.Assert.*;

import org.junit.Test;

public class RealmGeneratorTest {
	@Test
	public void getJsonTest() throws Exception {
		RealmGenerator generator = new RealmGenerator();
		generator.getJson("foo", "bar", "baz");
	}
}
