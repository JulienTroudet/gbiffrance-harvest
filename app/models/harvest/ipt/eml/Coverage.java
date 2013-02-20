package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class Coverage extends Model{

	//variables geographicCoverage

	public boolean geoCoverage = false;
	public boolean tempCoverage = false;
	public boolean taxCoverage = false;

	@Column(length=2000)
	public String geographicDescription;
	
	public float westBoundingCoordinate;
	public float eastBoundingCoordinate;
	public float northBoundingCoordinate;
	public float southBoundingCoordinate;

	@OneToOne
	public EmlData emlData;

	@OneToMany(mappedBy="coverage", cascade=CascadeType.ALL)
	private List<TemporalCoverage> temporalCoverages;

	@OneToMany(mappedBy="coverage", cascade=CascadeType.ALL)
	private List<TaxonomicCoverage> taxonomicCoverages;


	public Coverage parse(Element child, EmlData data) {
		if (child!=null){
			if (child.getChild("geographicCoverage")!=null){
				this.setGeoCoverage(true);
				this.setGeographicDescription(child.getChild("geographicCoverage").getChildText("geographicDescription"));
				if(child.getChild("geographicCoverage").getChild("boundingCoordinates")!=null){
					this.setEastBoundingCoordinate(Float.valueOf(child.getChild("geographicCoverage").getChild("boundingCoordinates").getChildText("westBoundingCoordinate")));
					this.setWestBoundingCoordinate(Float.valueOf(child.getChild("geographicCoverage").getChild("boundingCoordinates").getChildText("eastBoundingCoordinate")));
					this.setNorthBoundingCoordinate(Float.valueOf(child.getChild("geographicCoverage").getChild("boundingCoordinates").getChildText("northBoundingCoordinate")));
					this.setSouthBoundingCoordinate(Float.valueOf(child.getChild("geographicCoverage").getChild("boundingCoordinates").getChildText("southBoundingCoordinate")));
				}
			}

			if (child.getChildren("temporalCoverage")!=null){
				this.setTempCoverage(true);
				temporalCoverages = new ArrayList<TemporalCoverage>();
				List<Element> temporalCoveragesElement = child.getChildren("temporalCoverage");
				for (int i=0; i <temporalCoveragesElement.size(); i++){
					TemporalCoverage tc = new TemporalCoverage();
					this.addTemporalCoverage(tc.parse(temporalCoveragesElement.get(i)));
				}

			}

			if (child.getChildren("taxonomicCoverage")!=null){
				this.setTaxCoverage(true);
				taxonomicCoverages = new ArrayList<TaxonomicCoverage>();
				List<Element> taxonomicCoveragesElement = child.getChildren("taxonomicCoverage");

				for (int i=0; i <taxonomicCoveragesElement.size(); i++){
					TaxonomicCoverage tc = new TaxonomicCoverage();
					this.addTaxonomicCoverage(tc.parse(taxonomicCoveragesElement.get(i)));
				}

			}

			for(int j=0 ; j<temporalCoverages.size() ; j++){
				temporalCoverages.get(j).setCoverage(this);
			}
			for(int j=0 ; j<taxonomicCoverages.size() ; j++){
				taxonomicCoverages.get(j).setCoverage(this);
			}

			this.setEmlData(data);

			return this;
		}
		return null;

	}


	public String toString(){
		String str = "";
		if (this.isGeoCoverage()){
			str = str+ "\n\t geographicDescription : " + this.getGeographicDescription();
			str = str+ "\n\t westBoundingCoordinate : " + this.getWestBoundingCoordinate();
			str = str+ "\n\t eastBoundingCoordinate : " + this.getEastBoundingCoordinate();
			str = str+ "\n\t northBoundingCoordinate : " + this.getNorthBoundingCoordinate();
			str = str+ "\n\t southBoundingCoordinate : " + this.getSouthBoundingCoordinate();
		}
		if (this.isTempCoverage()){
			for (int i=0; i<this.getTemporalCoverages().size(); i++){
				str = str+ "\n\t temporalCoverages : " + this.getTemporalCoverages().get(i);
			}
		}
		if (this.isTaxCoverage()){
			for (int i=0; i<this.getTaxonomicCoverages().size(); i++){
				str = str+ "\n\t taxonomicCoverages : " + this.getTaxonomicCoverages().get(i);
			}
		}



		return str;
	}


	public boolean isGeoCoverage() {
		return geoCoverage;
	}
	public void setGeoCoverage(boolean geoCoverage) {
		this.geoCoverage = geoCoverage;
	}
	public boolean isTempCoverage() {
		return tempCoverage;
	}
	public void setTempCoverage(boolean tempCoverage) {
		this.tempCoverage = tempCoverage;
	}
	public boolean isTaxCoverage() {
		return taxCoverage;
	}
	public void setTaxCoverage(boolean taxCoverage) {
		this.taxCoverage = taxCoverage;
	}
	public String getGeographicDescription() {
		return geographicDescription;
	}
	public void setGeographicDescription(String geographicDescription) {
		this.geographicDescription = geographicDescription;
	}
	public float getWestBoundingCoordinate() {
		return westBoundingCoordinate;
	}
	public void setWestBoundingCoordinate(float westBoundingCoordinate) {
		this.westBoundingCoordinate = westBoundingCoordinate;
	}
	public float getEastBoundingCoordinate() {
		return eastBoundingCoordinate;
	}
	public void setEastBoundingCoordinate(float eastBoundingCoordinate) {
		this.eastBoundingCoordinate = eastBoundingCoordinate;
	}
	public float getNorthBoundingCoordinate() {
		return northBoundingCoordinate;
	}
	public void setNorthBoundingCoordinate(float northBoundingCoordinate) {
		this.northBoundingCoordinate = northBoundingCoordinate;
	}
	public float getSouthBoundingCoordinate() {
		return southBoundingCoordinate;
	}
	public void setSouthBoundingCoordinate(float southBoundingCoordinate) {
		this.southBoundingCoordinate = southBoundingCoordinate;
	}
	public List<TemporalCoverage> getTemporalCoverages() {
		return temporalCoverages;
	}
	public void setTemporalCoverages(ArrayList<TemporalCoverage> temporalCoverages) {
		this.temporalCoverages = temporalCoverages;
	}
	public void addTemporalCoverage(TemporalCoverage temporalCoverage) {
		this.temporalCoverages.add(temporalCoverage);
	}
	public List<TaxonomicCoverage> getTaxonomicCoverages() {
		return taxonomicCoverages;
	}
	public void setTaxonomicCoverages(ArrayList<TaxonomicCoverage> taxonomicCoverages) {
		this.taxonomicCoverages = taxonomicCoverages;
	}
	public void addTaxonomicCoverage(TaxonomicCoverage taxonomicCoverage) {
		this.taxonomicCoverages.add(taxonomicCoverage);
	}


	public EmlData getEmlData() {
		return emlData;
	}


	public void setEmlData(EmlData emlData) {
		this.emlData = emlData;
	}
	


}
