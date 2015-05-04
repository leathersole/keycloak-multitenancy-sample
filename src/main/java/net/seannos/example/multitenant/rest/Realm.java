package net.seannos.example.multitenant.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import net.seannos.example.multitenant.realm.RealmGenerator;

@Path("/realms")
public class Realm {

	@Inject
	RealmGenerator realmGenerator;

	@POST
	@Path("/")
	public Response createRealm(@FormParam("realm_name") String realmName,
			@FormParam("admin_name") String adminName,
			@FormParam("admin_pass") String adminPass)
			throws URISyntaxException {
		realmGenerator.generate(realmName, adminName, adminPass);
		return Response.temporaryRedirect(
				new URI("http://localhost:8080/multitenant/debug/" + realmName)).build();
	}
}
