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
public class Citation extends Model {

	public String identifier;
	public String text;
	
	@ManyToOne
	public GbifRacine gbifRacine;

	public Citation parse(Element child, GbifRacine gbif) {
		if (child!=null){
			this.setIdentifier(child.getAttributeValue("identifier"));
			this.setText(child.getText());
			gbifRacine = gbif;
			return this;
		}
		return null;
	}
	
	public String toSql(Connection connect) {
		String str = "INSERT INTO citations (" +
				"identifier, " +
				"citation_text" +
				")" +
					" VALUES (" +
				"'"+this.getIdentifier()+"', " +
				"'"+this.getText()+"'" +
				")";
		System.out.println (str);
		try {
			Statement stm = connect.createStatement();
			
			stm.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			if ( rs.next() ) {
				String st ="";
				return st+rs.getInt(1);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return "-1";
	}

	public String toString(){
		String str = "";

		str = str+ "\n\t identifier : " + this.getIdentifier();
		str = str+ "\n\t text : " + this.getText();

		return str;
	}

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public GbifRacine getGbifRacine() {
		return gbifRacine;
	}

	public void setGbifRacine(GbifRacine gbifRacine) {
		this.gbifRacine = gbifRacine;
	}
	
}
