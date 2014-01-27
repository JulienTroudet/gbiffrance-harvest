package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.jpa.GenericModel;

import com.google.gson.annotations.Expose;

@Entity
public class Field extends GenericModel {

	@Expose
	@Id
	@GeneratedValue
	public long id;

	@Expose
	public String name;
	
	@Column(name="CAMELCASE")
	public String camelCase;

	@ManyToMany(mappedBy = "fields")
	public List<DatasetType> datasetTypes;

	@ManyToMany(mappedBy = "fields")
	public List<Controls> controls;

	public Field() {

	}

}
