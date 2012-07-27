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
public class TaxonomicClassification extends Model{
	
	private String taxonRankName;
	private String taxonRankValue;
	private String commonName;
	
	@ManyToOne
	public TaxonomicCoverage taxonomicCoverage;
	
	public TaxonomicClassification parse(Element element) {
		
		this.setTaxonRankName(element.getChildText("taxonRankName"));
		this.setTaxonRankValue(element.getChildText("taxonRankValue"));
		this.setCommonName(element.getChildText("commonName"));

		return this;
	}
	
	public String toSql(Connection connect, int key) {
		
		String str = "INSERT INTO taxonomic_classifications (" +
				"taxonomic_coverage_id, " +
				"taxon_rank_name, " +
				"taxon_rank_value, " +
				"common_name" +
				")" +
				" VALUES (" +
				"'"+key+"', " +
				"'"+this.getTaxonRankName()+"', " +
				"'"+this.getTaxonRankValue()+"', " +
				"'"+this.getCommonName()+"'" +
				")";
		System.out.println (str);

		try {
			Statement stm = connect.createStatement();

			stm.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			if ( rs.next() ) {
				key = rs.getInt(1);
				String st ="";
				return st+key;
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return "-1";
		
	}

	
	public String toString(){
		String str = "";

		str = str+ "\n\t\t taxonRankName : " + this.getTaxonRankName();
		str = str+ "\n\t\t taxonRankValue : " + this.getTaxonRankValue();
		str = str+ "\n\t\t commonName : " + this.getCommonName();
		
		return str;
	}
	
	public String getTaxonRankName() {
		return taxonRankName;
	}
	public void setTaxonRankName(String taxonRankName) {
		this.taxonRankName = taxonRankName;
	}
	public String getTaxonRankValue() {
		return taxonRankValue;
	}
	public void setTaxonRankValue(String taxonRankValue) {
		this.taxonRankValue = taxonRankValue;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public TaxonomicCoverage getTaxonomicCoverage() {
		return taxonomicCoverage;
	}

	public void setTaxonomicCoverage(TaxonomicCoverage taxCov) {
		this.taxonomicCoverage = taxCov;
	}


}
