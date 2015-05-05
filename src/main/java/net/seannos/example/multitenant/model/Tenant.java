package net.seannos.example.multitenant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "tenant", indexes = { @Index(name = "IDX_MYIDX1", columnList = "id,realm") })
@NamedQuery(name = "findTenantByName", query = "from Tenant where realm = :realm")
public class Tenant {

	int id;
	String realm;
	String json;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true)
	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	@Column(length = 500)
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
