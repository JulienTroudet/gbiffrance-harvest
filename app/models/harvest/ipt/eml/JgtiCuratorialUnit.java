package models.harvest.ipt.eml;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class JgtiCuratorialUnit extends Model{

	public String jgtiUnitType;

	private boolean jgtiUnitRange = false;
	public float beginRange;
	public float endRange;

	public float uncertaintyMeasure;
	public float jgtiUnits;
	
	@ManyToOne
	public GbifRacine gbifRacine;

	public JgtiCuratorialUnit parse(Element element, GbifRacine gbif) {
		if (element!=null){

			this.setJgtiUnitType(element.getChildText("jgtiUnitType"));

			if(element.getChild("jgtiUnitRange")!=null){
				this.setJgtiUnitRange(true);
				this.setBeginRange(Float.valueOf(element.getChild("jgtiUnitRange").getChildText("beginRange")));
				this.setEndRange(Float.valueOf(element.getChild("jgtiUnitRange").getChildText("endRange")));
			}
			else if(element.getChild("jgtiUnits")!=null){
				this.setJgtiUnitRange(false);
				this.setUncertaintyMeasure(Float.valueOf(element.getChild("jgtiUnits").getAttributeValue("uncertaintyMeasure")));
				this.setJgtiUnits(Float.valueOf(element.getChildText("jgtiUnits")));
			}
			gbifRacine = gbif;
			return this;
		}
		return null;
	}
	
public String toString(){
		String str = "";

		str = str+ "\n\t beginRange : " + this.getBeginRange();
		str = str+ "\n\t endRange : " + this.getEndRange();

		str = str+ "\n\t uncertaintyMeasure : " + this.getUncertaintyMeasure();
		str = str+ "\n\t jgtiUnits : " + this.getJgtiUnits();

		return str;
	}


	public String getJgtiUnitType() {
		return jgtiUnitType;
	}
	public void setJgtiUnitType(String jgtiUnitType) {
		this.jgtiUnitType = jgtiUnitType;
	}
	public boolean isJgtiUnitRange() {
		return jgtiUnitRange;
	}
	public void setJgtiUnitRange(boolean jgtiUnitRange) {
		this.jgtiUnitRange = jgtiUnitRange;
	}
	public float getBeginRange() {
		return beginRange;
	}
	public void setBeginRange(float beginRange) {
		this.beginRange = beginRange;
	}
	public float getEndRange() {
		return endRange;
	}
	public void setEndRange(float endRange) {
		this.endRange = endRange;
	}
	public float getUncertaintyMeasure() {
		return uncertaintyMeasure;
	}
	public void setUncertaintyMeasure(float uncertaintyMeasure) {
		this.uncertaintyMeasure = uncertaintyMeasure;
	}
	public float getJgtiUnits() {
		return jgtiUnits;
	}
	public void setJgtiUnits(float jgtiUnits) {
		this.jgtiUnits = jgtiUnits;
	}

	public GbifRacine getGbifRacine() {
		return gbifRacine;
	}

	public void setGbifRacine(GbifRacine gbifRacine) {
		this.gbifRacine = gbifRacine;
	}
	
}