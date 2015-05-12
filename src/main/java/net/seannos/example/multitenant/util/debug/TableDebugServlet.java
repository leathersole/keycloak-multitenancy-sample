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

@WebServlet(urlPatterns = "/debug/tenant")
public class TableDebugServlet extends HttpServlet {

	@PersistenceContext(unitName = "default")
	EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<Tenant> tenants = getTenants();
		HttpSession session = req.getSession(); // just for debugger.
		Enumeration<String> names = session.getAttributeNames();
		PrintWriter writer = resp.getWriter();

		for (Tenant t : tenants) {
			writer.println("ID:" + t.getId() + "<br/>");
			writer.println("Realm:" + t.getRealm() + "<br/>");
			writer.println("JSON:" + t.getJson() + "<br/>");
		}
		writer.flush();
		writer.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String value = req.getParameter("command");
	}

	private List<Tenant> getTenants() {
		Query query = em.createQuery("select t from Tenant t", Tenant.class);
		List<Tenant> tenants = (List<Tenant>) query.getResultList();
		return tenants;
	}
}
