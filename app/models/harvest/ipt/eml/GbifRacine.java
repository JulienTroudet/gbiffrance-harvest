package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class GbifRacine extends Model{

	private String dateStamp;
	private String hierarchyLevel;

	@OneToOne(mappedBy="gbifRacine",  cascade = {CascadeType.ALL})
	private Citation citation;

	@OneToMany(mappedBy="gbifRacine", cascade=CascadeType.ALL)
	private List<Biblio> bibliography = new ArrayList<Biblio>();

	@OneToMany(mappedBy="gbifRacine", cascade=CascadeType.ALL)
	private List<Physical> physicals = new ArrayList<Physical>();

	private String resourceLogoUrl;

	@OneToOne(mappedBy="gbifRacine",  cascade = {CascadeType.ALL})
	private GbifCollection collection;

	private String formationPeriod;
	private String specimenPreservationMethod;
	private String livingTimePeriod;

	@OneToMany(mappedBy="gbifRacine", cascade=CascadeType.ALL)
	private List<JgtiCuratorialUnit> jgtiCuratorialUnits = new ArrayList<JgtiCuratorialUnit>();

	@OneToOne
	public EmlData emlData;

	
	public GbifRacine parse(Element element, EmlData data) {
		this.setDateStamp(element.getChildText("dateStamp"));
		this.setHierarchyLevel(element.getChildText("hierarchyLevel"));

		citation = new Citation();
		this.setCitation(citation.parse(element.getChild("citation"), this));

		if(element.getChild("bibliography")!=null){
			bibliography = new ArrayList<Biblio>();
			List<Element> bibliographyElement = element.getChild("bibliography").getChildren("citation");
			for (int i=0; i <bibliographyElement.size(); i++){
				Biblio cit = new Biblio();
				this.addBibliography(cit.parse(bibliographyElement.get(i), this));
			}
		}

		physicals = new ArrayList<Physical>();
		List<Element> physicalsElement = element.getChildren("physical");
		for (int i=0; i <physicalsElement.size(); i++){
			Physical phys = new Physical();
			this.addPhysical(phys.parse(physicalsElement.get(i), this));
		}

		this.setResourceLogoUrl(element.getChildText("resourceLogoUrl"));

		collection = new GbifCollection();
		this.setCollection(collection.parse(element.getChild("collection"), this));

		this.setFormationPeriod(element.getChildText("formationPeriod"));
		this.setSpecimenPreservationMethod(element.getChildText("specimenPreservationMethod"));
		this.setLivingTimePeriod(element.getChildText("livingTimePeriod"));

		jgtiCuratorialUnits = new ArrayList<JgtiCuratorialUnit>();
		List<Element> jgtiElement = element.getChildren("jgtiCuratorialUnit");
		for (int i=0; i <jgtiElement.size(); i++){
			JgtiCuratorialUnit jgti = new JgtiCuratorialUnit();
			this.addJgtiCuratorialUnits(jgti.parse(jgtiElement.get(i), this));
		}
		this.setEmlData(data);
		return this;
	}

	public String toString(){
		String str = "";

		str = str+ "dateStamp : " + this.getDateStamp() + "\n";
		str = str+ "hierarchyLevel : " + this.getHierarchyLevel() + "\n";
		str = str+ "citation : " + this.getCitation() + "\n";
		str = str+ "bibliography : " + this.getBibliography() + "\n";
		str = str+ "physicals : " + this.getPhysicals() + "\n";
		str = str+ "resourceLogoUrl : " + this.getResourceLogoUrl() + "\n";
		str = str+ "collection : " + this.getCollection() + "\n";
		str = str+ "formationPeriod : " + this.getFormationPeriod() + "\n";
		str = str+ "specimenPreservationMethod : " + this.getSpecimenPreservationMethod() + "\n";
		str = str+ "livingTimePeriod : " + this.getLivingTimePeriod() + "\n";
		str = str+ "jgtiCuratorialUnits : " + this.getJgtiCuratorialUnits() + "\n";

		return str;
	}

	public String getDateStamp() {
		return dateStamp;
	}
	public void setDateStamp(String dateStamp) {
		this.dateStamp = dateStamp;
	}
	public String getHierarchyLevel() {
		return hierarchyLevel;
	}
	public void setHierarchyLevel(String hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}
	public Citation getCitation() {
		return citation;
	}
	public void setCitation(Citation citation) {
		this.citation = citation;
	}
	public List<Biblio> getBibliography() {
		return bibliography;
	}
	public void setBibliography(ArrayList<Biblio> bibliography) {
		this.bibliography = bibliography;
	}
	public void addBibliography(Biblio bibliography) {
		this.bibliography.add(bibliography);
	}
	public List<Physical> getPhysicals() {
		return physicals;
	}
	public void setPhysicals(ArrayList<Physical> physicals) {
		this.physicals = physicals;
	}
	public void addPhysical(Physical physical) {
		this.physicals.add(physical);
	}
	public String getResourceLogoUrl() {
		return resourceLogoUrl;
	}
	public void setResourceLogoUrl(String string) {
		this.resourceLogoUrl = string;
	}
	public GbifCollection getCollection() {
		return collection;
	}
	public void setCollection(GbifCollection collection) {
		this.collection = collection;
	}
	public String getFormationPeriod() {
		return formationPeriod;
	}
	public void setFormationPeriod(String formationPeriod) {
		this.formationPeriod = formationPeriod;
	}
	public String getSpecimenPreservationMethod() {
		return specimenPreservationMethod;
	}
	public void setSpecimenPreservationMethod(String specimenPreservationMethod) {
		this.specimenPreservationMethod = specimenPreservationMethod;
	}
	public String getLivingTimePeriod() {
		return livingTimePeriod;
	}
	public void setLivingTimePeriod(String livingTimePeriod) {
		this.livingTimePeriod = livingTimePeriod;
	}
	public List<JgtiCuratorialUnit> getJgtiCuratorialUnits() {
		return jgtiCuratorialUnits;
	}
	public void setJgtiCuratorialUnits(ArrayList<JgtiCuratorialUnit> jgtiCuratorialUnits) {
		this.jgtiCuratorialUnits = jgtiCuratorialUnits;
	}
	public void addJgtiCuratorialUnits(JgtiCuratorialUnit jgtiCuratorialUnit) {
		this.jgtiCuratorialUnits.add(jgtiCuratorialUnit);
	}
	public EmlData getEmlData() {
		return emlData;
	}
	public void setEmlData(EmlData emlData) {
		this.emlData = emlData;
	}

}
