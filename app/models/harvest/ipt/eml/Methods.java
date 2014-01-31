package models.harvest.ipt.eml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class Methods extends Model{

	public String methodSteps;
	
	@Column(length=2000)
	public String studyExtent;
	@Column(length=2000)
	public String samplingDescription;
	@Column(length=2000)
	public String qualityControl;
	
	@OneToOne
	public EmlData emlData;

	public Methods parse(Element child, EmlData data) {
		if (child!=null){
			ArrayList<String> words = new ArrayList<String>();
			List<Element> methodStepsElement = child.getChildren("methodStep");
			for (int i=0; i <methodStepsElement.size(); i++){
				if(methodStepsElement.get(i).getChild("description")!=null){
				words.add((methodStepsElement.get(i).getChild("description").getChildText("para")));
				}
			}
			this.setMethodSteps(words.toString());

			if (child.getChild("sampling")!=null){
				if (child.getChild("sampling").getChild("studyExtent")!=null){
					if (child.getChild("sampling").getChild("studyExtent").getChild("description")!=null){
						this.setStudyExtent(child.getChild("sampling").getChild("studyExtent").getChild("description").getChildText("para"));
					}
				}
				if (child.getChild("sampling").getChild("samplingDescription")!=null){
					this.setSamplingDescription(child.getChild("sampling").getChild("samplingDescription").getChildText("para"));
				}
			}

			if (child.getChild("qualityControl")!=null){
				if (child.getChild("qualityControl").getChild("description")!=null){
					this.setQualityControl(child.getChild("qualityControl").getChild("description").getChildText("para"));
				}
			}
			this.setEmlData(data);
			return this;
		}
		return null;
	}

	
	public String toString(){
		String str = "";

		str = str+ "\n\t studyExtent : " + this.getStudyExtent();
		str = str+ "\n\t samplingDescription : " + this.getSamplingDescription();
		str = str+ "\n\t qualityControl : " + this.getQualityControl();
		str = str+ "\n\t methodStep : " + this.getMethodSteps();
		return str;

	}


	public String getMethodSteps() {
		return methodSteps;
	}
	public void setMethodSteps(String methodSteps) {
		this.methodSteps = methodSteps;
	}
	public String getStudyExtent() {
		return studyExtent;
	}
	public void setStudyExtent(String studyExtent) {
		this.studyExtent = studyExtent;
	}
	public String getSamplingDescription() {
		return samplingDescription;
	}
	public void setSamplingDescription(String samplingDescription) {
		this.samplingDescription = samplingDescription;
	}
	public String getQualityControl() {
		return qualityControl;
	}
	public void setQualityControl(String qualityControl) {
		this.qualityControl = qualityControl;
	}

	public EmlData getEmlData() {
		return emlData;
	}

	public void setEmlData(EmlData emlData) {
		this.emlData = emlData;
	}

}
