package net.seannos.example.multitenant.util.debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;

import net.seannos.example.multitenant.model.Tenant;

@WebServlet(urlPatterns = "/group/authn/*")
public class GroupMemberDebugServlet extends HttpServlet {

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
			writer.println("<h1>" + keycloakSession.getRealm() + " Group</h1>");
			writer.println("<h2>Member area</h2>");
			writer.println("<h3>Welcome, "
					+ keycloakSession.getIdToken().getPreferredUsername()
					+ "</h3>");

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
		writer.flush();
		writer.close();
	}
}
