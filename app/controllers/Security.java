package controllers;

import java.util.List;

import manager.UserMG;
import models.Groups;
import models.Utilisateurs;

public class Security extends Secure.Security {

	private static UserMG mUserMG = new UserMG();

	static boolean authenticate(String username, String password) {
		Long nbGroup = 0L;
		Utilisateurs user = Utilisateurs.find("byLogin", username).first();
		if (user != null) {
			nbGroup = mUserMG.verifGroup(user.id_utilisateur);
			return user != null && user.password.equals(password);
		}
		return false;
	}

	static boolean check(String profile) {
		/*
		 * On test que le profil demandé soit le bon
		 */
		String role = null;
		if ("admin".equals(profile)) {
			role = "PDQI_ADMIN";
		} else if ("publisher".equals(profile)) {
			role = "PDQI_GESTION";
		} else if ("consult".equals(profile)) {
			role = "PDQI_CONSULTANT";
		} else {
			// Sinon on retourne FALSE
			return false;
		}
		/*
		 * Recherche de l'utilisateur
		 */
		Utilisateurs user = Utilisateurs.find("byLogin", connected()).first();
		/*
		 * Recherche des groups de l'utilisateur
		 */
		List<Groups> groups = mUserMG.listGroupByUser(user.id_utilisateur);

		if (groups != null) {
			for (Groups grp : groups) {
				/*
				 * On compare le nom du groupe associé au rôle
				 */
				if (role.equals(grp.name_group)) {
					return true;
				}
			}
		}
		return false;
	}

	static void onDisconnected() {
		Application.index();
	}

	static void onAuthenticated() {
		Application.index();
	}
}
