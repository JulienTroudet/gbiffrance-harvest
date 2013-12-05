package controllers;

import java.util.List;

import javax.persistence.TypedQuery;

import models.Groups;
import models.Utilisateurs;
import play.db.jpa.JPA;
 
public class Security extends Secure.Security {
    
    static boolean authenticate(String username, String password) {
    	Utilisateurs user = Utilisateurs.find("byLogin", username).first();
        return user != null && user.password.equals(password);
    }
    
    static boolean check(String profile) {
    	/*
    	 * On test que le profil demandé soit le bon
    	 */
    	String role = null;
    	if("admin".equals(profile))	{
    		role = "PDQI_ADMIN";
    	}
    	else if("publisher".equals(profile))	{
    		role = "PDQI_GESTION";
    	}
    	else if("consult".equals(profile))	{
    		role = "PDQI_CONSULTANT";
    	}
    	else	{
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
    	TypedQuery<Groups> q = JPA.em().createQuery("from Groups g where g.id_group IN (select m.id_group from Memberships m where m.id_member = :idUser and m.user_member = '1')", Groups.class);
		q.setParameter("idUser", user.id_utilisateur);
		List<Groups> groups = q.getResultList();
    	
    	if(groups != null)	{
    		for (Groups grp : groups) {
				/*
				 * On compare le nom du groupe associé au rôle
				 */
   				if(role.equals(grp.name_group))	{
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
