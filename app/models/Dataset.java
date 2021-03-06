package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.harvest.ipt.eml.EmlData;
import play.db.jpa.Model;

@Entity
public class Dataset extends Model {
	public String name;
	public String url;
	public String type;
	public String status;
	public String tempDirectory;
	public String currentLower;
	public boolean fromOutside;

	@Column(name = "FILE_DATASET")
	public String fileDataset;

	@Column(name = "FILE_COMMUNE")
	public String fileCommune;

	@Column(name = "FILE_MAILLE")
	public String fileMaille;

	@Column(name = "FILE_ENP")
	public String fileENP;

	@Column(name = "FILE_SHAPE")
	public String fileShape;

	@OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL)
	public List<Occurrence> occurrences;

	@ManyToOne
	public DataPublisher dataPublisher;

	@OneToOne(mappedBy = "dataset", cascade = CascadeType.ALL)
	public EmlData emlData;

	public Dataset() {
	}

	public Dataset(String name, String url, String type,
			DataPublisher dataPublisher) {
		this.name = name;
		this.url = url;
		this.type = type;
		this.status = "EMPTY";
		this.occurrences = new ArrayList<Occurrence>();
		this.dataPublisher = dataPublisher;
	}

	public void markDataset(String status) {
		this.status = status;
	}
}
