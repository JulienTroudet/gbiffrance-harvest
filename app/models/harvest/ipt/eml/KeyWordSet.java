package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class KeyWordSet extends Model{
	
	public ArrayList<String> keywords;
	public String keywordThesaurus;
	
	@ManyToOne
	public EmlData emlData;
	
	public KeyWordSet parse(Element element, EmlData data) {
		if (element!=null){
		keywords = new ArrayList<String>();
		List<Element> keywordsElement = element.getChildren("keyword");
		for (int i=0; i<keywordsElement.size(); i++){
			this.addKeywords((keywordsElement.get(i).getText()));
		}
		this.setKeywordThesaurus(element.getChildText("keywordThesaurus"));
		this.emlData = data;
		return this;
		}
		return null;
	}

public String toString(){
		String str = "\n keywordSet";

		for (int i=0; i<this.getKeywords().size(); i++){
			str = str+ "\n\t keyword : " + this.getKeywords().get(i);
		}
		
		str = str+ "\n\t keywordThesaurus : " + this.getKeywordThesaurus();

		return str;
	}
	
	public ArrayList<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	public void addKeywords(String keyword) {
		this.keywords.add(keyword);
	}
	public String getKeywordThesaurus() {
		return keywordThesaurus;
	}
	public void setKeywordThesaurus(String keywordThesaurus) {
		this.keywordThesaurus = keywordThesaurus;
	}

	public EmlData getEmlData() {
		return emlData;
	}

	public void setEmlData(EmlData emlData) {
		this.emlData = emlData;
	}
	

	
}
