package models.harvest.digir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.log4j.Logger;
import models.Occurrence;
import org.xml.sax.SAXException;

public class ResponseToModelHandler 
{
  protected int count;
  protected boolean endOfRecords;
	  
  public List<Occurrence> handleResponse(GZIPInputStream inputStream) throws IOException 
  {
    List<Occurrence> results = new ArrayList<Occurrence>();
	try
	{
	  // log the response
	  Digester digester = new Digester();
	  digester.setNamespaceAware(false);
	  digester.setValidating(false);
	  // Digester uses the class loader of its own class to find classes needed for object create rules. As Digester is bundled, the wrong class loader was used leading to this Exception.
	  digester.setUseContextClassLoader(true);
	  digester.push(results);
	  digester.addObjectCreate("*/record", "models.Occurrence");
	  digester.addBeanPropertySetter("*/record/darwin:InstitutionCode", "institutionCode");
	  digester.addBeanPropertySetter("*/record/darwin:CollectionCode", "collectionCode");
	  digester.addBeanPropertySetter("*/record/darwin:CatalogNumber", "catalogNumber");
	  digester.addBeanPropertySetter("*/record/darwin:ScientificName", "scientificName");
	  digester.addBeanPropertySetter("*/record/darwin:BasisOfRecord", "basisOfRecord");
	  digester.addBeanPropertySetter("*/record/darwin:Kingdom", "kingdom");
	  digester.addBeanPropertySetter("*/record/darwin:Phylum", "phylum");
	  digester.addBeanPropertySetter("*/record/darwin:Class", "classs");
	  digester.addBeanPropertySetter("*/record/darwin:Order", "orderr");
	  digester.addBeanPropertySetter("*/record/darwin:Family", "family");
	  digester.addBeanPropertySetter("*/record/darwin:Species", "specificEpithet");
	  digester.addBeanPropertySetter("*/record/darwin:SubspeciesPhylum", "infraSpecificEpithet");
	  digester.addBeanPropertySetter("*/record/darwin:ScientificNameAuthor", "scientificNameAuthorship");
	  digester.addBeanPropertySetter("*/record/darwin:IdentifiedBy", "identifiedBy");
	  digester.addBeanPropertySetter("*/record/darwin:YearIdentified", "dateIdentified");
	  digester.addBeanPropertySetter("*/record/darwin:MonthIdentified", "dateIdentified");
	  digester.addBeanPropertySetter("*/record/darwin:DayIdentified", "dateIdentified");
	  digester.addBeanPropertySetter("*/record/darwin:TypeStatus", "typeStatus");
	  digester.addBeanPropertySetter("*/record/darwin:FieldNumber", "fieldNumber");
	  digester.addBeanPropertySetter("*/record/darwin:CollectorNumber", "recordNumber");
	  digester.addBeanPropertySetter("*/record/darwin:Collector", "recordedBy");
	  digester.addBeanPropertySetter("*/record/darwin:YearCollected", "eventDate");
	  digester.addBeanPropertySetter("*/record/darwin:MonthCollected", "eventDate");
	  digester.addBeanPropertySetter("*/record/darwin:DayCollected", "eventDate");
	  digester.addBeanPropertySetter("*/record/darwin:JulianDay", "startDayOfYear");
	  //digester.addBeanPropertySetter("*/record/darwin:TimeOfDay", "startTimeOfDay");
	  digester.addBeanPropertySetter("*/record/darwin:ContinentOcean", "waterBody");
	  digester.addBeanPropertySetter("*/record/darwin:Country", "country");
	  digester.addBeanPropertySetter("*/record/darwin:StateProvince", "stateProvince");
	  digester.addBeanPropertySetter("*/record/darwin:County", "county");
	  digester.addBeanPropertySetter("*/record/darwin:Locality", "locality");
	  digester.addBeanPropertySetter("*/record/darwin:Longitude", "decimalLongitude");
	  digester.addBeanPropertySetter("*/record/darwin:Latitude", "decimalLatitude");
	  digester.addBeanPropertySetter("*/record/darwin:CoordinatePrecision", "coordinatePrecision");
	  digester.addBeanPropertySetter("*/record/darwin:MinimumElevation", "minimumElevationInMeters");
	  digester.addBeanPropertySetter("*/record/darwin:MaximumElevation", "maximumElevationInMeters");
	  digester.addBeanPropertySetter("*/record/darwin:MinimumDepth", "minimumDepthInMeters");
	  digester.addBeanPropertySetter("*/record/darwin:MaximumDepth", "maximumDepthInMeters");
	  digester.addBeanPropertySetter("*/record/darwin:Sex", "sex");
	  digester.addBeanPropertySetter("*/record/darwin:PreparationType", "preparations");
	  digester.addBeanPropertySetter("*/record/darwin:IndividualCount", "individualCount");
	  digester.addBeanPropertySetter("*/record/darwin:PreviousCatalogNumber", "otherCatalogNumbers");
	  //digester.addBeanPropertySetter("*/record/darwin:RelationshipType", "relationshipOfResource");
	  digester.addBeanPropertySetter("*/record/darwin:RelatedCatalogItem", "associatedOccurrences");
	  digester.addBeanPropertySetter("*/record/darwin:Notes", "occurrenceRemarks");
	  
	  
	  
	  
	  //digester.addCallMethod("response/diagnostics/diagnostic", "incrementCount", 2);
	  //digester.addCallParam("response/diagnostics/diagnostic", 0, "code");
	  //digester.addCallParam("response/diagnostics/diagnostic", 1);
	  //digester.addCallMethod("response/diagnostics/diagnostic", "endOfRecords", 2);
	  //digester.addCallParam("response/diagnostics/diagnostic", 0, "code");
	  //digester.addCallParam("response/diagnostics/diagnostic", 1);
	  digester.addSetNext("*/record", "add");
	  digester.parse(inputStream);
	}
	catch (SAXException e) 
	{
	  throw new IOException(e.getMessage());
	} 
	finally 
	{
	  inputStream.close();
	}
	//LOG.info(results.get(0).getInstitutionCode());		
	return results;
  }
	
  public void incrementCount(String code, String value) 
  {
	if ("RECORD_COUNT".equals(code)) 
	{
	  try 
	  {
		count+=Integer.parseInt(value);
	  }
  	  catch (NumberFormatException e) 
  	  {
  	  }
	}
  }
		
  public void endOfRecords(String code, String value) 
  {
    endOfRecords = false;
    if ("END_OF_RECORDS".equals(code)) 
    {
      if ("TRUE".equalsIgnoreCase(value)) 
      {
        endOfRecords=true;
      } 
    }
  }

  public int getCount() 
  {
	return count;
  }
  
  public void setCount(int count) 
  {
	this.count = count;
  }
  
  public boolean isEndOfRecords() 
  {
	return endOfRecords;
  }
  
  public void setEndOfRecords(boolean endOfRecords) 
  {
	this.endOfRecords = endOfRecords;
  }
}