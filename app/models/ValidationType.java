package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class ValidationType extends Model {

	public String name;

	@OneToMany(mappedBy = "validationType", cascade = CascadeType.ALL)
	public List<Controls> controls;
	
	public ValidationType() {
	}
}
