package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.GenericModel;

import com.google.gson.annotations.Expose;

/**
 * Classe représentant les contrôles sur les occurences
 * 
 * @author Rémy PLAISANCE
 * 
 */
@Entity
public class Controls extends GenericModel {

	@Expose
	@Id
	@GeneratedValue
	public long id;

	@Expose
	public String name;

	@ManyToOne
	public ValidationType validationType;

	@Expose
	public String description;

	public String regex;

	@Column(name = "boudary_higher")
	public String boudaryHigher;

	@Column(name = "boudary_lower")
	public String boudaryLower;

	public String value;

	@OneToMany(mappedBy = "controls", cascade = CascadeType.ALL)
	public List<Result> results;

	@ManyToOne
	public DatasetType datasetType;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "CONTROLSFIELD", joinColumns = { @JoinColumn(name = "CONTROLS_ID") }, inverseJoinColumns = { @JoinColumn(name = "FIELD_ID") })
	public List<Field> fields;

	public Controls() {

	}

	public Controls(String pName, ValidationType pValidationType,
			String pDescription, String pBoudaryLower, String pBoudaryHigher,
			String pValue, String pRegex, DatasetType pDatasetType) {
		this.name = pName;
		this.validationType = pValidationType;
		this.datasetType = pDatasetType;
		this.description = pDescription;
		this.boudaryHigher = pBoudaryHigher;
		this.boudaryLower = pBoudaryLower;
		this.value = pValue;
		this.regex = pRegex;
	}

}
