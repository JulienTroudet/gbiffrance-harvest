/**
 * 
 */
package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

/**
 * Classe représentant les utilisateurs de l'INPN.
 * @author tstrauss
 *
 */
@Entity
@Table(schema="ISB")
public class Memberships extends GenericModel {

	
	@Id
	public Long id_member;
	@Id
	public Long id_group;
		 
    public String user_member; 
     
}
