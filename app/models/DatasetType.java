package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class DatasetType extends Model {

	public String name;

	@OneToMany(mappedBy = "datasetType", cascade = CascadeType.ALL)
	public List<Controls> controls;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "FIELDDATASETTYPE", joinColumns = { @JoinColumn(name = "DATASETTYPE_ID") }, inverseJoinColumns = { @JoinColumn(name = "FIELD_ID") })
	public List<Field> fields;

	public DatasetType() {

	}

}
