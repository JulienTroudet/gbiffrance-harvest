/**
 * 
 */
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

/**
 * Classe représentant les utilisateurs de l'INPN.
 * 
 * @author tstrauss
 * 
 */
@Entity
@Table(schema = "ISB")
public class Utilisateurs extends GenericModel {

	@Id
	@GeneratedValue
	public Long id_utilisateur;

	@Required
	public String nom;
	@Required
	public String prenom;
	@Required
	public String civilite;
	@Required
	public String login;
	@Required
	public String password;

	@Override
	public Object _key() {
		return id_utilisateur;
	}
}
