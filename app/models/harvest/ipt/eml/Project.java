package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class Project extends Model{

	public String title;
	public Party personnel;
	public String funding;
	public String studyAreaDescription;
	public String designDescription;
	
	@OneToOne
	public EmlData emlData;

	public Project parse(Element child, EmlData data) {
		if (child!=null){
			this.setTitle(child.getChildText("title"));
			personnel = new Party();
			this.setPersonnel(personnel.parse(child.getChild("personnel"), "personnel", this));

			if (child.getChild("funding")!=null){
				this.setFunding(child.getChild("funding").getChildText("para"));
			}

			if (child.getChild("studyAreaDescription")!=null){
				if (child.getChild("studyAreaDescription").getChild("descriptor")!=null){
					this.setStudyAreaDescription(child.getChild("studyAreaDescription").getChild("descriptor").getChildText("descriptorValue"));
				}
			}

			if (child.getChild("designDescription")!=null){
				if (child.getChild("designDescription").getChild("description")!=null){
					this.setDesignDescription(child.getChild("studyAreaDescription").getChild("descriptor").getChildText("para"));
				}
			}
			
			this.setEmlData(data);

			personnel.setProject(this);
			return this;
		}
		return null;
	}

	
	public String toString(){
		String str = "";

		str = str+ "\n\t title : " + this.getTitle();
		str = str+ "\n\t personnel : " + this.getPersonnel();
		str = str+ "\n\t funding : " + this.getFunding();
		str = str+ "\n\t studyAreaDescription : " + this.getStudyAreaDescription();
		str = str+ "\n\t designDescription : " + this.getDesignDescription();

		return str;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Party getPersonnel() {
		return personnel;
	}
	public void setPersonnel(Party personnel) {
		this.personnel = personnel;
	}
	public String getFunding() {
		return funding;
	}
	public void setFunding(String funding) {
		this.funding = funding;
	}
	public String getStudyAreaDescription() {
		return studyAreaDescription;
	}
	public void setStudyAreaDescription(String studyAreaDescription) {
		this.studyAreaDescription = studyAreaDescription;
	}
	public String getDesignDescription() {
		return designDescription;
	}
	public void setDesignDescription(String designDescription) {
		this.designDescription = designDescription;
	}

	public EmlData getEmlData() {
		return emlData;
	}

	public void setEmlData(EmlData emlData) {
		this.emlData = emlData;
	}	
}
