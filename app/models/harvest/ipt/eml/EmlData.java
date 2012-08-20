package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.Dataset;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class EmlData extends Model{

	public ArrayList<String> alternateIdentifiers = new ArrayList<String>();
	public String title;

	@OneToMany(mappedBy="emlData", cascade=CascadeType.ALL)
	public List<Party> parties = new ArrayList<Party>();

	public Date pubDate;
	public String language;
	@Column(length=1000)
	public String dataAbstract;

	@OneToMany(mappedBy="emlData", cascade=CascadeType.ALL)
	public List<KeyWordSet> keywordSets = new ArrayList<KeyWordSet>();

	@Column(length=2000)
	public String additionalInfo;
	@Column(length=2000)
	public String intellectualRights;
	@Column(length=2000)
	public String distributionUrl;

	@OneToOne(mappedBy="emlData",  cascade = {CascadeType.ALL})
	public Coverage coverage;

	@Column(length=2000)
	public String purpose;

	

	@OneToOne(mappedBy="emlData",  cascade = {CascadeType.ALL})
	public Methods methods;

	@OneToOne(mappedBy="emlData",  cascade = {CascadeType.ALL})
	public Project project;

	@OneToOne(mappedBy="emlData",  cascade = {CascadeType.ALL})
	public GbifRacine gbifRacine;

	@OneToOne
	public Dataset dataset;


	public EmlData parse(Element dataElement) {
		parties = new ArrayList<Party>();
		//alternateIdentifiers
		alternateIdentifiers = new ArrayList<String>();
		List<Element> alternateIdentifiersElement = dataElement.getChildren("alternateIdentifier");
		for (int i=0; i <alternateIdentifiersElement.size(); i++){
			this.addAlternateIdentifier((alternateIdentifiersElement.get(i).getText()));
		}
		//title
		this.setTitle(dataElement.getChildText("title"));

		//creator
		Party creator = new Party();
		this.addParties(creator.parse(dataElement.getChild("creator"), "creator", this));

		//metadataProvider
		Party metadataProvider = new Party();
		this.addParties(metadataProvider.parse(dataElement.getChild("metadataProvider"), "metadataProvider", this));

		//associatedPartys
		
		List<Element> associatedPartysElement = dataElement.getChildren("associatedParty");
		for (int i=0; i <associatedPartysElement.size(); i++){
			Party party = new Party();
			this.addParties(party.parse(associatedPartysElement.get(i), "associatedParty", this));
		}

		//pubDate
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			this.setPubDate(formatter.parse(dataElement.getChildText("pubDate").replaceAll("\\p{Cntrl}", "")));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//language
		this.setLanguage(dataElement.getChildText("language"));

		//dataAbstract
		if (dataElement.getChild("abstract")!=null){
			this.setDataAbstract(dataElement.getChild("abstract").getChildText("para"));
		}
		//keywordSets
		keywordSets = new ArrayList<KeyWordSet>();
		List<Element> keywordSetsElement = dataElement.getChildren("keywordSet");
		for (int i=0; i <keywordSetsElement.size(); i++){
			KeyWordSet keyWordSet = new KeyWordSet();
			this.addKeywordSet(keyWordSet.parse(keywordSetsElement.get(i), this));
		}

		//additionalInfo
		if (dataElement.getChild("additionalInfo")!=null){
			this.setAdditionalInfo(dataElement.getChild("additionalInfo").getChildText("para"));
		}
		//intellectualRights
		if (dataElement.getChild("intellectualRights")!=null){
			this.setIntellectualRights(dataElement.getChild("intellectualRights").getChildText("para"));
		}
		//distributionUrl
		if (dataElement.getChild("distribution")!=null){
			if (dataElement.getChild("distribution").getChild("online")!=null){
				this.setDistributionUrl(dataElement.getChild("distribution").getChild("online").getChildText("url"));

			}
		}

		//coverage
		coverage = new Coverage();
		this.setCoverage(coverage.parse(dataElement.getChild("coverage"), this));

		//purpose
		if (dataElement.getChild("purpose")!=null){
			this.setPurpose(dataElement.getChild("purpose").getChildText("para"));
		}

		//contact
		Party contact = new Party();
		this.addParties(contact.parse(dataElement.getChild("contact"),"contact" , this));

		//methods
		methods = new Methods();
		this.setMethods(methods.parse(dataElement.getChild("methods"), this));

		//project
		project = new Project();
		this.setProject(project.parse(dataElement.getChild("project"), this));

		return this;
	}


	public String toString(){
		String str = "";

		str = str+ "alternateIdentifiers : " + this.getAlternateIdentifiers() + "\n";
		str = str+ "title : " + this.getTitle() + "\n";
		str = str+ "associatedPartys : " + this.getParties() + "\n";
		str = str+ "pubDate : " + this.getPubDate() + "\n";
		str = str+ "language : " + this.getLanguage() + "\n";
		str = str+ "dataAbstract : " + this.getDataAbstract() + "\n";
		str = str+ "keywordSets : " + this.getKeywordSet() + "\n";
		str = str+ "additionalInfo : " + this.getAdditionalInfo() + "\n";
		str = str+ "intellectualRights : " + this.getIntellectualRights() + "\n";
		str = str+ "distributionUrl : " + this.getDistributionUrl() + "\n";
		str = str+ "coverage : " + this.getCoverage() + "\n";
		str = str+ "purpose : " + this.getPurpose() + "\n";
		str = str+ "methods : " + this.getMethods() + "\n";
		str = str+ "project : " + this.getProject() + "\n";

		return str;
	}

	public ArrayList<String> getAlternateIdentifiers() {
		return alternateIdentifiers;
	}
	public void setAlternateIdentifiers(ArrayList<String> alternateIdentifiers) {
		this.alternateIdentifiers = alternateIdentifiers;
	}
	public void addAlternateIdentifier(String alt) {
		this.alternateIdentifiers.add(alt);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Party> getParties() {
		return parties;
	}
	public void setParties(ArrayList<Party> parties) {
		this.parties = parties;
	}
	public void addParties(Party part) {
		this.parties.add(part);
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDataAbstract() {
		return dataAbstract;
	}
	public void setDataAbstract(String dataAbstract) {
		this.dataAbstract = dataAbstract;
	}
	public List getKeywordSet() {
		return keywordSets;
	}
	public void setKeywordSet(ArrayList<KeyWordSet> keywordSet) {
		this.keywordSets = keywordSet;
	}
	public void addKeywordSet(KeyWordSet key) {
		this.keywordSets.add(key);
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getIntellectualRights() {
		return intellectualRights;
	}
	public void setIntellectualRights(String intellectualRights) {
		this.intellectualRights = intellectualRights;
	}
	public String getDistributionUrl() {
		return distributionUrl;
	}
	public void setDistributionUrl(String distributionUrl) {
		this.distributionUrl = distributionUrl;
	}
	public Coverage getCoverage() {
		return coverage;
	}
	public void setCoverage(Coverage coverage) {
		this.coverage = coverage;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public Methods getMethods() {
		return methods;
	}
	public void setMethods(Methods methods) {
		this.methods = methods;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public GbifRacine getGbifRacine() {
		return gbifRacine;
	}
	public void setGbifRacine(GbifRacine gbifRacine) {
		this.gbifRacine = gbifRacine;
	}



}

