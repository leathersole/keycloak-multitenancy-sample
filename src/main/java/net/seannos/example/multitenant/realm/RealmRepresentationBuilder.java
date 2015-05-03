package net.seannos.example.multitenant.realm;

import org.keycloak.representations.idm.RealmRepresentation;

//This class will be removed.
@Deprecated
public class RealmRepresentationBuilder {
	private RealmRepresentation realmRepresentation;

	public RealmRepresentation build() {
		return realmRepresentation;
	}

	public RealmRepresentationBuilder prepareRealmRepresentation(String id) {
		realmRepresentation = new RealmRepresentation();
		realmRepresentation.setId(id);
		return this;
	}

	public RealmRepresentationBuilder setRealmName(String realmName) {
		realmRepresentation.setRealm(realmName);
		return this;
	}

	public RealmRepresentationBuilder setPublicKey(String publicKey) {
		realmRepresentation.setPublicKey(publicKey);
		return this;
	}

	public RealmRepresentationBuilder setEnabled(Boolean enabled) {
		realmRepresentation.setEnabled(enabled);
		return this;
	}

	public RealmRepresentationBuilder setAccessTokenLifespan(
			Integer accessTokenLifespan) {
		realmRepresentation.setAccessTokenLifespan(accessTokenLifespan);
		return this;
	}

	public RealmRepresentationBuilder setAccessCodeLifespan(
			Integer accessCodeLifespan) {
		realmRepresentation.setAccessCodeLifespan(accessCodeLifespan);
		return this;
	}

	public RealmRepresentationBuilder setAccessCodeLifespanUserAction(
			Integer accessCodeLifespanUserAction) {
		realmRepresentation
				.setAccessCodeLifespanUserAction(accessCodeLifespanUserAction);
		return this;
	}

	public RealmRepresentationBuilder setSslRequired(String sslRequired) {
		realmRepresentation.setSslRequired(sslRequired);
		return this;
	}

	public RealmRepresentationBuilder dummy(String sslRequired) {
		realmRepresentation.setSslRequired(sslRequired);
		return this;
	}

}
