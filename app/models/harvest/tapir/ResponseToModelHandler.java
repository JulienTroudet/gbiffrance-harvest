package models.harvest.tapir;

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
	  digester.addObjectCreate("*/dwrec:DarwinRecord", "models.Occurrence");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:InstitutionCode", "institutionCode");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:CollectionCode", "collectionCode");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:CatalogNumber", "catalogNumber");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:ScientificName", "scientificName");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:BasisOfRecord", "basisOfRecord");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Kingdom", "kingdom");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Phylum", "phylum");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Class", "classs");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Order", "orderr");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Family", "family");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Species", "specificEpithet");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:SubspeciesPhylum", "infraSpecificEpithet");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:ScientificNameAuthor", "scientificNameAuthorship");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:IdentifiedBy", "identifiedBy");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:YearIdentified", "dateIdentified");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:MonthIdentified", "dateIdentified");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:DayIdentified", "dateIdentified");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:TypeStatus", "typeStatus");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:FieldNumber", "fieldNumber");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:CollectorNumber", "recordNumber");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Collector", "recordedBy");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:YearCollected", "eventDate");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:MonthCollected", "eventDate");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:DayCollected", "eventDate");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:JulianDay", "startDayOfYear");
	  //digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:TimeOfDay", "startTimeOfDay");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:ContinentOcean", "waterBody");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Country", "country");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:StateProvince", "stateProvince");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:County", "county");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Locality", "locality");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwgeo:DecimalLongitude", "decimalLongitude");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwgeo:DecimalLatitude", "decimalLatitude");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:CoordinatePrecision", "coordinatePrecision");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:MinimumElevation", "minimumElevationInMeters");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:MaximumElevation", "maximumElevationInMeters");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:MinimumDepth", "minimumDepthInMeters");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:MaximumDepth", "maximumDepthInMeters");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Sex", "sex");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:PreparationType", "preparations");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:IndividualCount", "individualCount");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:PreviousCatalogNumber", "otherCatalogNumbers");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:RelationshipType", "relationshipOfResource");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:RelatedCatalogItem", "associatedOccurrences");
	  digester.addBeanPropertySetter("*/dwrec:DarwinRecord/dwcore:Notes", "occurrenceRemarks");
	  digester.addSetNext("*/dwrec:DarwinRecord", "add");
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