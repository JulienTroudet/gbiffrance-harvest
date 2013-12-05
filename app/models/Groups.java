/**
 * 
 */
package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;

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
