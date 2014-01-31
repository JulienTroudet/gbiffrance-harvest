/**
 * 
 */
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

/**
 * Classe représentant les groupes applicatifs des applications de l'INPN.
 * @author tstrauss
 *
 */
@Entity
@Table(schema="ISB")
public class Groups extends GenericModel {

	@Id
    @GeneratedValue
    public Long id_group;
	
	@Required
	public String name_group;	

    @Override
    public Object _key() {
        return id_group;
    }
}
