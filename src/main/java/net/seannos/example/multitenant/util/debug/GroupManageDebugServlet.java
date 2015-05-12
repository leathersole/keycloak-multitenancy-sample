package net.seannos.example.multitenant.util.debug;

import static net.seannos.example.multitenant.util.Constants.PROP_REALM_OWNERS_NAME;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.seannos.example.multitenant.util.PropertyStore;

import org.keycloak.KeycloakSecurityContext;

@WebServlet(urlPatterns = "/groupmanage/*")
public class GroupManageDebugServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	@PersistenceContext(unitName = "default")
	EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(); // just for debugger.
		PrintWriter writer = resp.getWriter();
		resp.setContentType("text/html");

		KeycloakSecurityContext keycloakSession = (KeycloakSecurityContext) req
				.getAttribute(KeycloakSecurityContext.class.getName());
		if (keycloakSession != null) {
			String realm = keycloakSession.getRealm();
			writer.println("<h1>" + realm + " Group</h1>");
			writer.println("<h2>Management area</h2>");
			writer.println("<h3>Welcome, "
					+ keycloakSession.getIdToken().getPreferredUsername()
					+ "</h3>");

			String realmOwners = PropertyStore.get(PROP_REALM_OWNERS_NAME);
			if (realmOwners.equals(realm)) {
				writer.println("<h3>Create new group!</h3>");
				writer.println("<form id=\"generate_realm\" action=\"/multitenant/rest/realms/\" method=\"post\"> Group name: <input id=\"realm_name\" name=\"realm_name\" type=\"text\" /><br /> Administrator Id: <input id=\"admin_name\" name=\"admin_name\" type=\"text\" /><br /> Administrator Password: <input id=\"admin_pass\" name=\"admin_pass\" type=\"text\" /><br /> <input type=\"submit\" value=\"submit\" /> </form>");
			}

			writer.println("Realm:" + keycloakSession.getRealm() + "<br/>");
			writer.println("Issuer:" + keycloakSession.getIdToken().getIssuer()
					+ "<br/>");
			writer.println("Username:"
					+ keycloakSession.getIdToken().getPreferredUsername()
					+ "<br/>");
			writer.println("IDToken:" + keycloakSession.getIdTokenString()
					+ "<br/>");
			writer.println("Token:" + keycloakSession.getTokenString()
					+ "<br/>");
		}
		Principal principal = req.getUserPrincipal();
		if (principal != null) {
			writer.println("Principal - name:" + principal.getName() + "<br/>");
			writer.println("Implementation class of principal:"
					+ principal.getClass() + "<br/>");
		}
		Cookie[] cookies = req.getCookies();
		for (Cookie c : cookies) {
			if ("JSESSIONID".equals(c.getName())) {
				writer.println("JSESSIONID:" + c.getValue() + "<br/>");
			}
		}

		writer.flush();
		writer.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
