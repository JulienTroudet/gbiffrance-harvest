package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class GbifCollection extends Model {
	
	private String parentCollectionIdentifier;
	private String collectionIdentifier;
	private String collectionName;

	public GbifCollection parse(Element child, GbifRacine gbif) {
		if (child!=null){
			this.setParentCollectionIdentifier(child.getChildText("parentCollectionIdentifier"));
			this.setCollectionIdentifier(child.getChildText("collectionIdentifier"));
			this.setCollectionName(child.getChildText("collectionName"));
			gbifRacine = gbif;
			return this;
		}
		return null;
	}
	
	@OneToOne
	public GbifRacine gbifRacine;
	
	public String toSql(Connection connect) {
		String str = "INSERT INTO collections (" +
				"parent_collection_identifier, " +
				"collection_identifier, " +
				"collection_name" +
				")" +
					" VALUES (" +
				"'"+this.getParentCollectionIdentifier()+"', " +
				"'"+this.getCollectionIdentifier()+"', " +
				"'"+this.getCollectionName()+"'" +
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

		str = str+ "\n\t parentCollectionIdentifier : " + this.getParentCollectionIdentifier();
		str = str+ "\n\t collectionIdentifier : " + this.getCollectionIdentifier();
		str = str+ "\n\t collectionName : " + this.getCollectionName();

		return str;
	}

	public String getParentCollectionIdentifier() {
		return parentCollectionIdentifier;
	}
	public void setParentCollectionIdentifier(String parentCollectionIdentifier) {
		this.parentCollectionIdentifier = parentCollectionIdentifier;
	}
	public String getCollectionIdentifier() {
		return collectionIdentifier;
	}
	public void setCollectionIdentifier(String collectionIdentifier) {
		this.collectionIdentifier = collectionIdentifier;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public GbifRacine getGbifRacine() {
		return gbifRacine;
	}

	public void setGbifRacine(GbifRacine gbifRacine) {
		this.gbifRacine = gbifRacine;
	}
	
}
