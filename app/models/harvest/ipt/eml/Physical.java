package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class Physical extends Model{

	public String objectName;
	public String characterEncoding;
	public String formatName;
	public String formatVersion;
	public String distributionUrl;

	@ManyToOne
	public GbifRacine gbifRacine;

	public Physical parse(Element element, GbifRacine gbif) {

		if (element!=null){
			this.setObjectName(element.getChildText("objectName"));
			this.setCharacterEncoding(element.getChildText("characterEncoding"));

			if (element.getChild("dataFormat")!=null){
				if (element.getChild("dataFormat").getChild("externallyDefinedFormat")!=null){
					this.setFormatName(element.getChild("dataFormat").getChild("externallyDefinedFormat").getChildText("formatName"));
					this.setFormatVersion(element.getChild("dataFormat").getChild("externallyDefinedFormat").getChildText("formatVersion"));
				}
			}

			if (element.getChild("distribution")!=null){
				if (element.getChild("distribution").getChild("online")!=null){
					this.setDistributionUrl(element.getChild("distribution").getChild("online").getChildText("url"));

				}
			}
			gbifRacine = gbif;
			return this;
		}
		return null;
	}

public String toSql(Connection connect, int key) {
		
		String str = "INSERT INTO physicals (" +
				"gbif_metadata_id, " +
				"object_name, " +
				"character_encoding, " +
				"format_name, " +
				"format_version, " +
				"distribution_url" +
				")" +
				" VALUES (" +
				"'"+key+"', " +
				"'"+this.getObjectName()+"', " +
				"'"+this.getCharacterEncoding()+"', " +
				"'"+this.getFormatName()+"', " +
				"'"+this.getFormatVersion()+"', " +
				"'"+this.getDistributionUrl()+"'" +
				")";
		System.out.println (str);

		try {
			Statement stm = connect.createStatement();

			stm.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			if ( rs.next() ) {
				String st = "";
				return st+rs.getInt(1);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return "-1";
		
	}		
	
	public String toString(){
		String str = "";

		str = str+ "\n\t objectName : " + this.getObjectName();
		str = str+ "\n\t characterEncoding : " + this.getCharacterEncoding();
		str = str+ "\n\t formatName : " + this.getFormatName();
		str = str+ "\n\t formatVersion : " + this.getFormatVersion();
		str = str+ "\n\t distributionUrl : " + this.getDistributionUrl();


		return str;
	}


	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getCharacterEncoding() {
		return characterEncoding;
	}
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	public String getFormatName() {
		return formatName;
	}
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}
	public String getFormatVersion() {
		return formatVersion;
	}
	public void setFormatVersion(String formatVersion) {
		this.formatVersion = formatVersion;
	}
	public String getDistributionUrl() {
		return distributionUrl;
	}

	public void setDistributionUrl(String distributionUrl) {
		this.distributionUrl = distributionUrl;
	}

	public GbifRacine getGbifRacine() {
		return gbifRacine;
	}

	public void setGbifRacine(GbifRacine gbifRacine) {
		this.gbifRacine = gbifRacine;
	}
	
}
