package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import play.db.jpa.Model;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@Entity
public class Dataset extends Model 
{
  public String name;
  public String url;
  public String type;
  public String status;
  public String tempDirectory;
  public String currentLower;
  public boolean fromOutside;
  
  @OneToMany(mappedBy="dataset", cascade=CascadeType.ALL)
  public List<Occurrence> occurrences;
  
  @ManyToOne
  public DataPublisher dataPublisher;
  
  public Dataset() {}
  
  public Dataset(String name, String url, String type, DataPublisher dataPublisher)
  {
	this.name = name;
	this.url = url;
	this.type = type; 
	this.status = "EMPTY";
	this.occurrences = new ArrayList<Occurrence>();
	this.dataPublisher = dataPublisher;
  }
  
  public void markDataset(String status)
  {
	this.status = status;
  }
}


