package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;


import play.db.jpa.Model;

@Entity
public class DataPublisher extends Model 
{
	public String name;
	public String description;
	public String administrativeContact;
	public String technicalContact;

	@OneToMany(mappedBy="dataPublisher", cascade=CascadeType.ALL)
	public List<Dataset> datasets;	
	
	public DataPublisher(String name, String description, String administrativeContact, String technicalContact)
	{
		this.name = name;
		this.description = description;
		this.administrativeContact = administrativeContact;
		this.technicalContact = technicalContact;
	}
}
