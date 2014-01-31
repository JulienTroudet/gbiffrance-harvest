package models.harvest.ipt.eml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class KeyWordSet extends Model{

	public String keywords;
	public String keywordThesaurus;

	@ManyToOne
	public EmlData emlData;

	public KeyWordSet parse(Element element, EmlData data) {
		if (element!=null){
			ArrayList<String> words = new ArrayList<String>();
			List<Element> keywordsElement = element.getChildren("keyword");
			for (int i=0; i<keywordsElement.size(); i++){
				words.add((keywordsElement.get(i).getText()));
			}
			this.setKeywords(words.toString());
			this.setKeywordThesaurus(element.getChildText("keywordThesaurus"));
			this.emlData = data;
			return this;
		}
		return null;
	}

	public String toString(){
		String str = "\n keywordSet";
		str = str+ "\n\t keyword : " + this.getKeywords();
		str = str+ "\n\t keywordThesaurus : " + this.getKeywordThesaurus();

		return str;
	}

	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keyw) {
		this.keywords = keyw;
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
