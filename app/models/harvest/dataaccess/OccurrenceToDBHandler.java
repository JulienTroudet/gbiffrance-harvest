package models.harvest.dataaccess;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import models.Dataset;
import models.Occurrence;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import play.db.jpa.JPA;

import com.mysql.jdbc.PreparedStatement;

/**
 * Utility to synchronise a single occurrence record with the database
 * @author tim
 */
public class OccurrenceToDBHandler {
  private static final play.Logger LOG = new play.Logger();
  public void synchronize(List<Occurrence> occurrences) throws SQLException {
	// detach a Hibernate session to handle large amount of data to insert
	Session session = (Session) JPA.em().getDelegate(); 
	StatelessSession stateless = 
	session.getSessionFactory().openStatelessSession(); 
	stateless.beginTransaction(); 
	for (Occurrence o : occurrences)
	{
	    try {
	    	stateless.insert(o);
	    }
	    catch (Exception e) {
	    	LOG.error ("Insertion error " + e.getMessage());
	    	continue;
	    }
	}
	stateless.getTransaction().commit(); 
	stateless.close();
  }
  
  public void synchronizeUpdate(List<Occurrence> occurrences) throws SQLException {
	// detach a Hibernate session to handle large amount of data to insert
	Session session = (Session) JPA.em().getDelegate(); 
	StatelessSession stateless = 
	session.getSessionFactory().openStatelessSession(); 
	stateless.beginTransaction(); 
	for (Occurrence o : occurrences)
	{
	    stateless.update(o);	
	}
	stateless.getTransaction().commit(); 
	stateless.close();
  }
  
  public void synchronizeDelete(Dataset dataset, String lower){
		// detach a Hibernate session to handle large amount of data to insert
	  	Session session = (Session) JPA.em().getDelegate(); 
		StatelessSession stateless = session.getSessionFactory().openStatelessSession(); 
		stateless.beginTransaction(); 
		stateless.createQuery("delete from Occurrence where dataset_id = " + dataset.id + " and scientificName > '" + lower + "'").executeUpdate();
		LOG.info("Occurrences deleted");
		stateless.getTransaction().commit(); 
		stateless.close();
  }
}