import org.datanucleus.store.rdbms.sql.method.SpatialPointFromTextMethod;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.junit.*;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

import play.db.jpa.JPA;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
  
	@SuppressWarnings("deprecation")
	@Before
	public void setup()
	{
		//Fixtures.deleteAll();
		if(Dataset.count() == 0) Fixtures.load("initial-data.yml");
	}
	
    //@Test
    /*public void createAndRetrieveOccurrence()
    {
    	// Create a dataset and save it
    	Dataset dataset = new Dataset(
    	  "T_name",
    	  "T_url",
    	  "T_type",
    	  null);
    	dataset.save();
    	
    	
    	// Create an occurrence and save it
    	new Occurrence(
    	  "T_occurrenceID",
    	  "T_institutionCode",
    	  "T_collectionID",
    	  "T_collectionCode",
    	  "T_catalogueNumber",
    	  "T_sex",
    	  "T_kingdom",
    	  "T_phylum",
    	  "T_klass",
    	  "T_order",
    	  "T_family",
    	  "T_genus",
    	  "T_subgenus",
    	  "T_specificEpithet",
    	  "T_infraSpecificEpithet",
    	  "T_scientificName",
    	  "T_scientificNameAuthorship",
    	  "T_taxonRank",
    	  "T_dateIdentified",
    	  "T_identifiedBy",
    	  "T_typeStatus",
    	  "T_continent",
    	  "T_waterBody",
    	  "T_country",
    	  "T_stateProvince",
    	  "T_locality",
    	  "T_decimalLatitude",
    	  "T_decimalLongitude",
    	  "T_coordinatePrecision",
    	  "T_minimumElevationInMeters",
    	  "T_maximumElevationInMeters",
    	  "T_minimumDepthInMeters",
    	  "T_maximumDepthInMeters",
    	  "T_status",
    	  dataset).save();
    	
    	//Retrieve the occurrence with scientific name "T_scientificName"
    	Occurrence occurrence = Occurrence.find("byScientificName", "T_scientificName").first();
    	
    	//Test
    	assertNotNull(occurrence);
    	assertEquals("T_occurrenceID", occurrence.occurrenceID);
    	assertEquals("T_url", occurrence.dataset.url);
    }*/
    
    //@Test
    public void createAndRetrieveDataset()
    {
    	// Create a dataset and save it
    	new Dataset(
    	  "T_name",
    	  "T_url",
    	  "T_type", null).save();
    	
    	//Retrieve the dataset with name "T_name"
    	Dataset dataset = Dataset.find("byName", "T_name").first();
    	
    	//Test
    	assertNotNull(dataset);
    	assertEquals("T_name", dataset.name);    	
    }
    
    //@Test
    public void deleteTemporaryDirectoryTest()
    {
    	//deleteTemporaryDirectory("/tmp/resource-1/", null);
    }
     
    //@Test
    public void synchronizeDelete(){
		// detach a Hibernate session to handle large amount of data to insert
	  	Session session = (Session) JPA.em().getDelegate(); 
		StatelessSession stateless = session.getSessionFactory().openStatelessSession(); 
		stateless.beginTransaction(); 
		stateless.createQuery("delete from Occurrence where dataset_id = 6 and scientificName > 'C'").executeUpdate();
		//LOG.info("Occurrences deleted");
		stateless.getTransaction().commit(); 
		stateless.close();
  }
    
  @Test
  public void deleteTemporaryDirectory() {
	File path = new File("/tmp/test");
	if( path.exists() ) {
	  File[] files = path.listFiles();
	  for(int i=0; i<files.length; ++i) {
	    if(files[i].isDirectory()) {
	      //deleteTemporaryDirectory(files[i].getAbsolutePath(), "Daa");
	    }
	    else if ("Daa" != null){
	      System.out.println(files[i].toString().substring(files[i].toString().length()- 10, files[i].toString().length()));	
	      if ((files[i].toString().split("/tmp/test/")[1].split("-")[0]).compareTo("Daa")>= 0 && !(files[i].toString().substring(files[i].toString().length()- 10, files[i].toString().length()).equals("-0_.txt.gz"))) {	        	
	        files[i].delete();   
	      }
	    }
	    else files[i].delete();
	    }
	   }
	   if (path.delete()){
	  //LOG.info("Temp directory deleted");	
	}
  }
}
