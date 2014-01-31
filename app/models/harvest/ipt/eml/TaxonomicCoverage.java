package models.harvest.ipt.eml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class TaxonomicCoverage extends Model{

	@Column(length=2000)
	public String generalTaxonomicCoverage;
	
	@OneToMany(mappedBy="taxonomicCoverage", cascade=CascadeType.ALL)
	public List<TaxonomicClassification> taxonomicClassifications;
	
	@ManyToOne
	public Coverage coverage;

	public TaxonomicCoverage parse(Element element) {

		this.setGeneralTaxonomicCoverage(element.getChildText("generalTaxonomicCoverage"));

		taxonomicClassifications = new ArrayList<TaxonomicClassification>();
		List<Element> taxonomicClassificationsElement = element.getChildren("taxonomicClassification");

		for (int i=0; i <taxonomicClassificationsElement.size(); i++){
			TaxonomicClassification tc = new TaxonomicClassification();
			this.addTaxonomicClassification(tc.parse(taxonomicClassificationsElement.get(i)));
		}

		for(int j=0 ; j<taxonomicClassifications.size() ; j++){
			taxonomicClassifications.get(j).setTaxonomicCoverage(this);
		}
		return this;
	}


	public String toString(){
		String str = "";

		str = str+ "\n\t\t generalTaxonomicCoverage : " + this.getGeneralTaxonomicCoverage();

		for (int i=0; i<this.getTaxonomicClassification().size(); i++){
			str = str+ "\n\t\t taxonomicClassification : " + this.getTaxonomicClassification().get(i);
		}

		return str;
	}

	public String getGeneralTaxonomicCoverage() {
		return generalTaxonomicCoverage;
	}
	public void setGeneralTaxonomicCoverage(String generalTaxonomicCoverage) {
		this.generalTaxonomicCoverage = generalTaxonomicCoverage;
	}
	public List<TaxonomicClassification> getTaxonomicClassification() {
		return taxonomicClassifications;
	}
	public void setTaxonomicClassification(ArrayList<TaxonomicClassification> taxonomicClassification) {
		this.taxonomicClassifications = taxonomicClassification;
	}
	public void addTaxonomicClassification(TaxonomicClassification taxonomicClassification) {
		this.taxonomicClassifications.add(taxonomicClassification);
	}


	public Coverage getCoverage() {
		return coverage;
	}


	public void setCoverage(Coverage coverage) {
		this.coverage = coverage;
	}

}
