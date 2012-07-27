package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class TemporalCoverage extends Model{

	private boolean rangeOfDates;
	public Date singleDateTime;
	public Date beginDate;
	public Date endDate;
	@ManyToOne
	public Coverage coverage;

	public TemporalCoverage parse(Element element) {

		if(element.getChild("singleDateTime")!=null){
			this.setRangeOfDates(false);

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				this.setSingleDateTime(formatter.parse(element.getChild("singleDateTime").getChildText("calendarDate").replaceAll("\\p{Cntrl}", "")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		else if (element.getChild("rangeOfDates")!=null){
			this.setRangeOfDates(true);

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				this.setBeginDate(formatter.parse(element.getChild("rangeOfDates").getChild("beginDate").getChildText("calendarDate").replaceAll("\\p{Cntrl}", "")));
				this.setEndDate(formatter.parse(element.getChild("rangeOfDates").getChild("endDate").getChildText("calendarDate").replaceAll("\\p{Cntrl}", "")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return this;
	}


	public String toSql(Connection connect, int key) {
		String singleDateTimeSql = "";
		String beginDateSql = "";
		String endDateSql = "";

		if(this.getSingleDateTime()!=null){
			singleDateTimeSql = new java.sql.Date(this.getSingleDateTime().getTime()).toString();
		}
		if(this.getBeginDate()!=null){
			beginDateSql = new java.sql.Date(this.getBeginDate().getTime()).toString();
		}
		if(this.getEndDate()!=null){
			endDateSql = new java.sql.Date(this.getEndDate().getTime()).toString();
		}

		if (rangeOfDates){
			String str = "INSERT INTO temporal_coverages (" +
					"coverage_id, " +
					"begin_date, " +
					"end_date" +
					")" +
					" VALUES (" +
					""+key+", " +
					"'"+beginDateSql+"', " +
					"'"+endDateSql+"'" +
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
		}
		else{
			String str = "INSERT INTO temporal_coverages (" +
					"coverage_id, " +
					"single_dateTime" +
					")" +
					" VALUES (" +
					""+key+", " +
					"'"+singleDateTimeSql+"'" +
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
		}



		return "-1";

	}


	public String toString(){
		String str = "";

		if (rangeOfDates){
			str = str+ "\n\t\t rangeOfDates : " + this.getBeginDate() + " ==> " + this.getEndDate();
		}
		else{
			str = str+ "\n\t\t singleDateTime : " + this.getSingleDateTime();
		}

		return str;
	}

	public boolean isRangeOfDates() {
		return rangeOfDates;
	}
	public void setRangeOfDates(boolean rangeOfDates) {
		this.rangeOfDates = rangeOfDates;
	}

	public Date getSingleDateTime() {
		return singleDateTime;
	}
	public void setSingleDateTime(Date singleDateTime) {
		this.singleDateTime = singleDateTime;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Coverage getCoverage() {
		return coverage;
	}


	public void setCoverage(Coverage coverage) {
		this.coverage = coverage;
	}




}
