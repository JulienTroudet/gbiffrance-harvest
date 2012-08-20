package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class Methods extends Model{

	public ArrayList<String> methodSteps;
	
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
			methodSteps = new ArrayList<String>();
			List<Element> methodStepsElement = child.getChildren("methodStep");
			for (int i=0; i <methodStepsElement.size(); i++){
				if(methodStepsElement.get(i).getChild("description")!=null){
				this.addMethodStep((methodStepsElement.get(i).getChild("description").getChildText("para")));
				}
			}

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

	public String toSql(Connection connect) {
		int key = -1;
		
		String str = "INSERT INTO methods (" +
				"studye_xtent, " +
				"sampling_description, " +
				"quality_control" +
				")" +
					" VALUES (" +
				"'"+this.getStudyExtent()+"', " +
				"'"+this.getSamplingDescription()+"', " +
				"'"+this.getQualityControl()+"'" +
				")";
		System.out.println (str);
		
		try {
			Statement stm = connect.createStatement();
			
			stm.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			
			if ( rs.next() ) {
				key = rs.getInt(1);
				
				for (int i=0; i<this.getMethodSteps().size(); i++){
					str = "INSERT INTO method_steps (" +
							"method_id," +
							"method_step" +
							")" +
								" VALUES (" +
							"'"+key+"', " +
							"'"+this.getMethodSteps().get(i)+"'" +
							")";
					System.out.println (str);

					try {
						stm = connect.createStatement();
						
						stm.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
						rs = stm.getGeneratedKeys();
						

					} catch (SQLException e) {

						e.printStackTrace();
					}
				}
				String st ="";
				return st+key;
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		String st ="";
		return st+key;
	}
	
	public String toString(){
		String str = "";

		if (this.getMethodSteps()!=null){
			for (int i=0; i<this.getMethodSteps().size(); i++){
				str = str+ "\n\t methodStep : " + this.getMethodSteps().get(i);
			}
		}
		str = str+ "\n\t studyExtent : " + this.getStudyExtent();
		str = str+ "\n\t samplingDescription : " + this.getSamplingDescription();
		str = str+ "\n\t qualityControl : " + this.getQualityControl();
		return str;

	}


	public ArrayList<String> getMethodSteps() {
		return methodSteps;
	}
	public void setMethodSteps(ArrayList<String> methodSteps) {
		this.methodSteps = methodSteps;
	}
	public void addMethodStep(String methodStep) {
		this.methodSteps.add(methodStep);
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
