/**
 * 
 */
package models;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;

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
