package models.harvest.ipt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.FlushModeType;

import org.apache.log4j.Logger;
import org.gbif.dwc.record.StarRecord;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import play.Play;
import play.db.jpa.JPA;

import models.harvest.dataaccess.DBToDatasetHandler;
import models.harvest.dataaccess.OccurrenceToDBHandler;
import models.harvest.ipt.eml.EmlData;
import models.harvest.ipt.eml.EmlParser;
import models.*;

public class Harvester extends models.harvest.Harvester  
{
	private static final play.Logger LOG = new play.Logger();

	//private Connection conn;
	private Dataset dataset;
	private String targetDirectory;
	private OccurrenceToDBHandler databaseSync = new OccurrenceToDBHandler();
	private boolean fromOutside;
	private int maxRows = Integer.parseInt(Play.configuration.getProperty("harvest.ipt.maxRows"));
	final static int BUFFER = 2048;


	/**
  /**
	 * Creates the class 
	 */
	public Harvester(Dataset dataset, String targetDirectory) 
	{
		LOG.info("IPT Harvester is started. Dataset = " + dataset.name);
		//Load the dataset in the harvester
		this.dataset = dataset;
		String ressourceDirectory = targetDirectory + File.separator + "resource-" + dataset.id;
		this.targetDirectory = ressourceDirectory;
		File f = new File (targetDirectory + File.separator + "resource-" + dataset.id);
		if (!f.exists()) {
			f.mkdirs();
		}
		this.dataset.tempDirectory = ressourceDirectory;
		this.fromOutside = this.dataset.fromOutside;
		run(dataset);

	}

	/**
	 * Initiates a crawl
	 */
	private void run(Dataset dataset) 
	{
		try 
		{
			harvest(); 
			dataset = this.dataset;
		} 
		catch (Exception e) 
		{
			LOG.error("Harvesting failed terminally", e);
			e.printStackTrace();
		}
	}


	/**
	 * Does the harvesting
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws UnsupportedArchiveException 
	 * @throws SQLException 
	 */
	private void harvest() 
	{
		try {
			//retrieves the archive and downloads it
			File targetDirectoryFile = new File(targetDirectory);
			File fileName = downloadFile (this.dataset.url, targetDirectoryFile);  
			LOG.info("Archive successfully downloaded");
			//extracts the archive
			File fileDirectory = extractFile(fileName, targetDirectory);
			LOG.info("Archive successfully extracted");
			//parses the data
			createOccurences(fileDirectory);
			LOG.info("Occurrences successfully created");
			createMetadata(fileDirectory);
			LOG.info("Metadatas successfully created");
			//saves it in a database	
		}
		catch (Exception e) {
			this.withErrors = true;	
		}

	}

	/**
	 * Downloads the archive
	 * @throws IOException 
	 */
	public static void downloadFile(String address) 
	{
		downloadFile(address, null);
	}

	public static File downloadFile(String address, File dest) 
	{
		BufferedReader reader = null;
		FileOutputStream fos = null;
		InputStream in = null;
		String fileName = null;
		try 
		{
			//Connection initialization
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			LOG.info("Connection to the URL " + url);

			String FileType = conn.getContentType();
			LOG.info("Type File : " + FileType);

			// Response Reader
			in = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));

			File file = new File(url.getPath());
			fileName = file.getName();
			
			
			System.out.println(fileName);
			//fileName = dest + File.separator + fileName.substring(fileName.lastIndexOf('=') + 1) + ".zip";
			fileName = dest + File.separator + fileName;
			dest = new File(fileName);

			fos = new FileOutputStream(dest);
			byte[] buff = new byte[1024];
			int l = in.read(buff);
			while (l > 0) 
			{
				fos.write(buff, 0, l);
				l = in.read(buff);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				fos.flush();
				fos.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			try
			{
				reader.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		LOG.info("Destination: " + fileName.toString());
		return dest;
	}

	/**
	 * Extracts the content of the archive in the specified directory
	 * @throws IOException 
	 */
	public static File extractFile(File file, String targetDirectory)
	{
		File fileDirectory = null;

		try
		{
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(file);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null)
			{
				LOG.info("Extracting directory: " + entry.getName());  
				int count;
				byte data[] = new byte [BUFFER];
				// write the files to the disk
				String directory = file.getName().substring(0, file.getName().length() - 4);
				fileDirectory = new File (targetDirectory + File.separator + directory);
				if (!fileDirectory.exists()) 
				{
					fileDirectory.mkdirs();
				}

				FileOutputStream fos = new FileOutputStream(targetDirectory + File.separator + directory + File.separator + entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1)
				{
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return fileDirectory;
	}

	public boolean createOccurences(File fileDirectory) throws IOException, UnsupportedArchiveException, SQLException 
	{
		// opens csv files with headers or dwc-a directories with a meta.xml descriptor

		Archive arch = ArchiveFactory.openArchive(fileDirectory);

		// does scientific name exist?
		if (!arch.getCore().hasTerm(DwcTerm.scientificName))
		{
			LOG.error("This application requires dwc-a with scientific names");
			System.exit(1);
		}

		// loop over star records. i.e. core with all linked extension records
		List<Occurrence> occurrences = new ArrayList<Occurrence>();
		int i = 0;   
		for (StarRecord rec : arch)
		{
			++i;	
			Occurrence occurrence = new Occurrence();

			if (this.fromOutside) {
				if (occurrence.inCountriesList(rec.value(DwcTerm.country))) {
					occurrence.country = rec.value(DwcTerm.country);
				}	
				try
				{
					if (occurrence.inBoundingBoxList(new Float(rec.value(DwcTerm.decimalLatitude)), new Float(rec.value(DwcTerm.decimalLongitude)))) {
						occurrence.decimalLatitude = rec.value(DwcTerm.decimalLatitude);
						occurrence.decimalLongitude = rec.value(DwcTerm.decimalLongitude);
					}	 
					//else continue;
				}
				catch(Exception e)
				{
					if (e.getClass() == NullPointerException.class || e.getClass() == NumberFormatException.class) {
						LOG.debug("Null or malformed latitude/longitude, skipped");
					}
					else e.printStackTrace();		
				}  
				if (occurrence.country == null && (occurrence.decimalLatitude == null || occurrence.decimalLongitude == null)) continue;
			}
			else {
				if (rec.value(DwcTerm.country) != null && !rec.value(DwcTerm.country).isEmpty()) occurrence.country = rec.value(DwcTerm.country);
				if (rec.value(DwcTerm.decimalLatitude) != null && !rec.value(DwcTerm.decimalLatitude).isEmpty()) occurrence.decimalLatitude = rec.value(DwcTerm.decimalLatitude);
				if (rec.value(DwcTerm.decimalLongitude) != null && !rec.value(DwcTerm.decimalLongitude).isEmpty()) occurrence.decimalLongitude = rec.value(DwcTerm.decimalLongitude);
			}


			occurrence.dataset = this.dataset;

			//Record-level
			if (rec.value(DwcTerm.basisOfRecord) != null && !rec.value(DwcTerm.basisOfRecord).isEmpty()) occurrence.typee = rec.value(DwcTerm.basisOfRecord);
			//*modified
			//*language
			//*rights
			//*rightsHolder
			//*accessRights
			//*bibliographicCitation
			if (rec.value(DwcTerm.institutionID) != null && !rec.value(DwcTerm.institutionID).isEmpty()) occurrence.institutionID = rec.value(DwcTerm.institutionID);
			if (rec.value(DwcTerm.institutionID) != null && !rec.value(DwcTerm.collectionID).isEmpty()) occurrence.collectionID = rec.value(DwcTerm.collectionID);
			if (rec.value(DwcTerm.datasetID) != null && !rec.value(DwcTerm.datasetID).isEmpty()) occurrence.datasetID = rec.value(DwcTerm.datasetID);
			if (rec.value(DwcTerm.institutionCode) != null && !rec.value(DwcTerm.institutionCode).isEmpty()) occurrence.institutionCode = rec.value(DwcTerm.institutionCode);
			if (rec.value(DwcTerm.collectionCode) != null && !rec.value(DwcTerm.collectionCode).isEmpty()) occurrence.collectionCode = rec.value(DwcTerm.collectionCode);
			if (rec.value(DwcTerm.datasetName) != null && !rec.value(DwcTerm.datasetName).isEmpty()) occurrence.datasetName = rec.value(DwcTerm.datasetName);
			if (rec.value(DwcTerm.ownerInstitutionCode) != null && !rec.value(DwcTerm.ownerInstitutionCode).isEmpty()) occurrence.ownerInstitutionCode = rec.value(DwcTerm.ownerInstitutionCode);
			if (rec.value(DwcTerm.basisOfRecord) != null && !rec.value(DwcTerm.basisOfRecord).isEmpty()) occurrence.basisOfRecord = rec.value(DwcTerm.basisOfRecord);
			if (rec.value(DwcTerm.informationWithheld) != null && !rec.value(DwcTerm.informationWithheld).isEmpty()) occurrence.informationWithheld = rec.value(DwcTerm.informationWithheld);
			if (rec.value(DwcTerm.dataGeneralizations) != null && !rec.value(DwcTerm.dataGeneralizations).isEmpty()) occurrence.dataGeneralizations = rec.value(DwcTerm.dataGeneralizations);
			if (rec.value(DwcTerm.dynamicProperties) != null && !rec.value(DwcTerm.dynamicProperties).isEmpty()) occurrence.dynamicProperties = rec.value(DwcTerm.dynamicProperties);

			//Occurrence
			if (rec.value(DwcTerm.occurrenceID) != null && !rec.value(DwcTerm.occurrenceID).isEmpty()) occurrence.occurrenceID = rec.value(DwcTerm.occurrenceID);
			if (rec.value(DwcTerm.catalogNumber) != null && !rec.value(DwcTerm.catalogNumber).isEmpty()) occurrence.catalogNumber = rec.value(DwcTerm.catalogNumber);
			if (rec.value(DwcTerm.occurrenceRemarks) != null && !rec.value(DwcTerm.occurrenceRemarks).isEmpty()) occurrence.occurrenceRemarks = rec.value(DwcTerm.occurrenceRemarks);
			if (rec.value(DwcTerm.recordNumber) != null && !rec.value(DwcTerm.recordNumber).isEmpty()) occurrence.recordNumber = rec.value(DwcTerm.recordNumber);
			if (rec.value(DwcTerm.recordedBy) != null && !rec.value(DwcTerm.recordedBy).isEmpty()) occurrence.recordedBy = rec.value(DwcTerm.recordedBy);
			if (rec.value(DwcTerm.individualID) != null && !rec.value(DwcTerm.individualID).isEmpty()) occurrence.individualID = rec.value(DwcTerm.individualID);
			if (rec.value(DwcTerm.individualCount) != null && !rec.value(DwcTerm.individualCount).isEmpty()) occurrence.individualCount = rec.value(DwcTerm.individualCount);
			if (rec.value(DwcTerm.sex) != null && !rec.value(DwcTerm.sex).isEmpty()) occurrence.sex = rec.value(DwcTerm.sex);
			if (rec.value(DwcTerm.lifeStage) != null && !rec.value(DwcTerm.lifeStage).isEmpty()) occurrence.lifeStage = rec.value(DwcTerm.lifeStage);
			if (rec.value(DwcTerm.reproductiveCondition) != null && !rec.value(DwcTerm.reproductiveCondition).isEmpty()) occurrence.reproductiveCondition = rec.value(DwcTerm.reproductiveCondition);
			if (rec.value(DwcTerm.behavior) != null && !rec.value(DwcTerm.behavior).isEmpty()) occurrence.behavior = rec.value(DwcTerm.behavior);
			if (rec.value(DwcTerm.establishmentMeans) != null && !rec.value(DwcTerm.establishmentMeans).isEmpty()) occurrence.establishmentMeans = rec.value(DwcTerm.establishmentMeans);
			if (rec.value(DwcTerm.occurrenceStatus) != null && !rec.value(DwcTerm.occurrenceStatus).isEmpty()) occurrence.occurrenceStatus = rec.value(DwcTerm.occurrenceStatus);
			if (rec.value(DwcTerm.preparations) != null && !rec.value(DwcTerm.preparations).isEmpty()) occurrence.preparations = rec.value(DwcTerm.preparations);
			if (rec.value(DwcTerm.disposition) != null && !rec.value(DwcTerm.disposition).isEmpty()) occurrence.disposition = rec.value(DwcTerm.disposition);
			if (rec.value(DwcTerm.otherCatalogNumbers) != null && !rec.value(DwcTerm.otherCatalogNumbers).isEmpty()) occurrence.otherCatalogNumbers = rec.value(DwcTerm.otherCatalogNumbers);
			if (rec.value(DwcTerm.previousIdentifications) != null && !rec.value(DwcTerm.previousIdentifications).isEmpty()) occurrence.previousIdentifications = rec.value(DwcTerm.previousIdentifications);
			if (rec.value(DwcTerm.associatedMedia) != null && !rec.value(DwcTerm.associatedMedia).isEmpty()) occurrence.associatedMedia = rec.value(DwcTerm.associatedMedia);
			if (rec.value(DwcTerm.associatedReferences) != null && !rec.value(DwcTerm.associatedReferences).isEmpty()) occurrence.associatedReferences = rec.value(DwcTerm.associatedReferences);
			if (rec.value(DwcTerm.associatedOccurrences) != null && !rec.value(DwcTerm.associatedOccurrences).isEmpty()) occurrence.associatedOccurrences = rec.value(DwcTerm.associatedOccurrences);
			if (rec.value(DwcTerm.associatedSequences) != null && !rec.value(DwcTerm.associatedSequences).isEmpty()) occurrence.associatedSequences = rec.value(DwcTerm.associatedSequences);
			if (rec.value(DwcTerm.associatedTaxa) != null && !rec.value(DwcTerm.associatedTaxa).isEmpty()) occurrence.associatedTaxa = rec.value(DwcTerm.associatedTaxa);

			//Location
			if (rec.value(DwcTerm.locationID) != null && !rec.value(DwcTerm.locationID).isEmpty()) occurrence.locationID = rec.value(DwcTerm.locationID);
			if (rec.value(DwcTerm.higherGeographyID) != null && !rec.value(DwcTerm.higherGeographyID).isEmpty()) occurrence.higherGeographyID = rec.value(DwcTerm.higherGeographyID);
			if (rec.value(DwcTerm.higherGeography) != null && !rec.value(DwcTerm.higherGeography).isEmpty()) occurrence.higherGeography = rec.value(DwcTerm.higherGeography);
			if (rec.value(DwcTerm.continent) != null && !rec.value(DwcTerm.continent).isEmpty()) occurrence.continent = rec.value(DwcTerm.continent);
			if (rec.value(DwcTerm.waterBody) != null && !rec.value(DwcTerm.waterBody).isEmpty()) occurrence.waterBody = rec.value(DwcTerm.waterBody);
			if (rec.value(DwcTerm.islandGroup) != null && !rec.value(DwcTerm.islandGroup).isEmpty()) occurrence.islandGroup= rec.value(DwcTerm.island);

			if (rec.value(DwcTerm.countryCode) != null && !rec.value(DwcTerm.countryCode).isEmpty()) occurrence.countryCode = rec.value(DwcTerm.countryCode);
			if (rec.value(DwcTerm.stateProvince) != null && !rec.value(DwcTerm.stateProvince).isEmpty()) occurrence.stateProvince = rec.value(DwcTerm.stateProvince);
			if (rec.value(DwcTerm.county) != null && !rec.value(DwcTerm.county).isEmpty()) occurrence.county = rec.value(DwcTerm.county);
			if (rec.value(DwcTerm.municipality) != null && !rec.value(DwcTerm.municipality).isEmpty()) occurrence.municipality = rec.value(DwcTerm.municipality);
			if (rec.value(DwcTerm.locality) != null && !rec.value(DwcTerm.locality).isEmpty()) occurrence.locality = rec.value(DwcTerm.locality);
			if (rec.value(DwcTerm.verbatimLocality) != null && !rec.value(DwcTerm.verbatimLocality).isEmpty()) occurrence.verbatimLocality = rec.value(DwcTerm.verbatimLocality);
			if (rec.value(DwcTerm.verbatimElevation) != null && !rec.value(DwcTerm.verbatimElevation).isEmpty()) occurrence.verbatimElevation = rec.value(DwcTerm.verbatimElevation);
			if (rec.value(DwcTerm.minimumElevationInMeters) != null && !rec.value(DwcTerm.minimumElevationInMeters).isEmpty()) occurrence.minimumElevationInMeters = rec.value(DwcTerm.minimumElevationInMeters);
			if (rec.value(DwcTerm.maximumElevationInMeters) != null && !rec.value(DwcTerm.maximumElevationInMeters).isEmpty()) occurrence.maximumElevationInMeters = rec.value(DwcTerm.maximumElevationInMeters);
			if (rec.value(DwcTerm.verbatimDepth) != null && !rec.value(DwcTerm.verbatimDepth).isEmpty()) occurrence.verbatimDepth = rec.value(DwcTerm.verbatimDepth);
			if (rec.value(DwcTerm.minimumDepthInMeters) != null && !rec.value(DwcTerm.minimumDepthInMeters).isEmpty()) occurrence.minimumDepthInMeters = rec.value(DwcTerm.minimumDepthInMeters);
			if (rec.value(DwcTerm.maximumDepthInMeters) != null && !rec.value(DwcTerm.maximumDepthInMeters).isEmpty()) occurrence.maximumDepthInMeters = rec.value(DwcTerm.maximumDepthInMeters);
			if (rec.value(DwcTerm.locationAccordingTo) != null && !rec.value(DwcTerm.locationAccordingTo).isEmpty()) occurrence.locationAccordingTo = rec.value(DwcTerm.locationAccordingTo);
			if (rec.value(DwcTerm.locationRemarks) != null && !rec.value(DwcTerm.locationRemarks).isEmpty()) occurrence.locationRemarks = rec.value(DwcTerm.locationRemarks);
			if (rec.value(DwcTerm.verbatimCoordinates) != null && !rec.value(DwcTerm.verbatimCoordinates).isEmpty()) occurrence.verbatimCoordinates = rec.value(DwcTerm.verbatimCoordinates);
			if (rec.value(DwcTerm.verbatimLatitude) != null && !rec.value(DwcTerm.verbatimLatitude).isEmpty()) occurrence.verbatimLatitude = rec.value(DwcTerm.verbatimLatitude);
			if (rec.value(DwcTerm.verbatimLongitude) != null && !rec.value(DwcTerm.verbatimLongitude).isEmpty()) occurrence.verbatimLongitude = rec.value(DwcTerm.verbatimLongitude);
			if (rec.value(DwcTerm.verbatimCoordinateSystem) != null && !rec.value(DwcTerm.verbatimCoordinateSystem).isEmpty()) occurrence.verbatimCoordinateSystem = rec.value(DwcTerm.verbatimCoordinateSystem);
			if (rec.value(DwcTerm.verbatimSRS) != null && !rec.value(DwcTerm.verbatimSRS).isEmpty()) occurrence.verbatimSRS = rec.value(DwcTerm.verbatimSRS);
			if (rec.value(DwcTerm.geodeticDatum) != null && !rec.value(DwcTerm.geodeticDatum).isEmpty()) occurrence.geodeticDatum = rec.value(DwcTerm.geodeticDatum);
			if (rec.value(DwcTerm.coordinateUncertaintyInMeters) != null && !rec.value(DwcTerm.coordinateUncertaintyInMeters).isEmpty()) occurrence.coordinateUncertaintyInMeters = rec.value(DwcTerm.coordinateUncertaintyInMeters);
			if (rec.value(DwcTerm.coordinatePrecision) != null && !rec.value(DwcTerm.coordinatePrecision).isEmpty()) occurrence.coordinatePrecision = rec.value(DwcTerm.coordinatePrecision);
			if (rec.value(DwcTerm.pointRadiusSpatialFit) != null && !rec.value(DwcTerm.pointRadiusSpatialFit).isEmpty()) occurrence.pointRadiusSpatialFit = rec.value(DwcTerm.pointRadiusSpatialFit);
			if (rec.value(DwcTerm.footprintWKT) != null && !rec.value(DwcTerm.footprintWKT).isEmpty()) occurrence.footprintWKT = rec.value(DwcTerm.footprintWKT);
			if (rec.value(DwcTerm.footprintSRS) != null && !rec.value(DwcTerm.footprintSRS).isEmpty()) occurrence.footprintSRS = rec.value(DwcTerm.footprintSRS);
			if (rec.value(DwcTerm.footprintSpatialFit) != null && !rec.value(DwcTerm.footprintSpatialFit).isEmpty()) occurrence.footprintSpatialFit = rec.value(DwcTerm.footprintSpatialFit);
			if (rec.value(DwcTerm.georeferencedBy) != null && !rec.value(DwcTerm.georeferencedBy).isEmpty()) occurrence.georeferencedBy = rec.value(DwcTerm.georeferencedBy);
			//*georeferencedDate
			if (rec.value(DwcTerm.georeferenceProtocol) != null && !rec.value(DwcTerm.georeferenceProtocol).isEmpty()) occurrence.georeferenceProtocol = rec.value(DwcTerm.georeferenceProtocol);
			if (rec.value(DwcTerm.georeferenceSources) != null && !rec.value(DwcTerm.georeferenceSources).isEmpty()) occurrence.georeferenceSources = rec.value(DwcTerm.georeferenceSources);
			if (rec.value(DwcTerm.georeferenceVerificationStatus) != null && !rec.value(DwcTerm.georeferenceVerificationStatus).isEmpty()) occurrence.georeferenceVerificationStatus = rec.value(DwcTerm.georeferenceVerificationStatus);
			if (rec.value(DwcTerm.georeferenceRemarks) != null && !rec.value(DwcTerm.georeferenceRemarks).isEmpty()) occurrence.georeferenceRemarks = rec.value(DwcTerm.georeferenceRemarks);

			//GeologicalContext
			if (rec.value(DwcTerm.geologicalContextID) != null && !rec.value(DwcTerm.geologicalContextID).isEmpty()) occurrence.geologicalContextID = rec.value(DwcTerm.geologicalContextID);
			if (rec.value(DwcTerm.earliestEonOrLowestEonothem) != null && !rec.value(DwcTerm.earliestEonOrLowestEonothem).isEmpty()) occurrence.earliestEonOrLowestEonothem = rec.value(DwcTerm.earliestEonOrLowestEonothem);
			if (rec.value(DwcTerm.latestEonOrHighestEonothem) != null && !rec.value(DwcTerm.latestEonOrHighestEonothem).isEmpty()) occurrence.latestEonOrHighestEonothem = rec.value(DwcTerm.latestEonOrHighestEonothem);
			if (rec.value(DwcTerm.earliestEraOrLowestErathem) != null && !rec.value(DwcTerm.earliestEraOrLowestErathem).isEmpty()) occurrence.earliestEraOrLowestErathem = rec.value(DwcTerm.earliestEraOrLowestErathem);
			if (rec.value(DwcTerm.latestEraOrHighestErathem) != null && !rec.value(DwcTerm.latestEraOrHighestErathem).isEmpty()) occurrence.latestEraOrHighestErathem = rec.value(DwcTerm.latestEraOrHighestErathem);
			if (rec.value(DwcTerm.earliestPeriodOrLowestSystem) != null && !rec.value(DwcTerm.earliestPeriodOrLowestSystem).isEmpty()) occurrence.earliestPeriodOrLowestSystem = rec.value(DwcTerm.earliestPeriodOrLowestSystem);
			if (rec.value(DwcTerm.latestPeriodOrHighestSystem) != null && !rec.value(DwcTerm.latestPeriodOrHighestSystem).isEmpty()) occurrence.latestPeriodOrHighestSystem = rec.value(DwcTerm.latestPeriodOrHighestSystem);
			if (rec.value(DwcTerm.earliestEpochOrLowestSeries) != null && !rec.value(DwcTerm.earliestEpochOrLowestSeries).isEmpty()) occurrence.earliestEpochOrLowestSeries = rec.value(DwcTerm.earliestEpochOrLowestSeries);
			if (rec.value(DwcTerm.latestEpochOrHighestSeries) != null && !rec.value(DwcTerm.latestEpochOrHighestSeries).isEmpty()) occurrence.latestEpochOrHighestSeries = rec.value(DwcTerm.latestEpochOrHighestSeries);
			if (rec.value(DwcTerm.earliestAgeOrLowestStage) != null && !rec.value(DwcTerm.earliestAgeOrLowestStage).isEmpty()) occurrence.earliestAgeOrLowestStage = rec.value(DwcTerm.earliestAgeOrLowestStage);
			if (rec.value(DwcTerm.lowestBiostratigraphicZone) != null && !rec.value(DwcTerm.lowestBiostratigraphicZone).isEmpty()) occurrence.lowestBiostratigraphicZone = rec.value(DwcTerm.lowestBiostratigraphicZone);
			if (rec.value(DwcTerm.highestBiostratigraphicZone) != null && !rec.value(DwcTerm.highestBiostratigraphicZone).isEmpty()) occurrence.highestBiostratigraphicZone = rec.value(DwcTerm.highestBiostratigraphicZone);
			if (rec.value(DwcTerm.lithostratigraphicTerms) != null && !rec.value(DwcTerm.lithostratigraphicTerms).isEmpty()) occurrence.lithostratigraphicTerms = rec.value(DwcTerm.lithostratigraphicTerms);
			if (rec.value(DwcTerm.group) != null && !rec.value(DwcTerm.group).isEmpty()) occurrence.groupp = rec.value(DwcTerm.group);
			if (rec.value(DwcTerm.formation) != null && !rec.value(DwcTerm.formation).isEmpty()) occurrence.formation = rec.value(DwcTerm.formation);
			if (rec.value(DwcTerm.member) != null && !rec.value(DwcTerm.member).isEmpty()) occurrence.member = rec.value(DwcTerm.member);
			if (rec.value(DwcTerm.bed) != null && !rec.value(DwcTerm.bed).isEmpty()) occurrence.bed = rec.value(DwcTerm.bed);

			//Identification
			if (rec.value(DwcTerm.identificationID) != null && !rec.value(DwcTerm.identificationID).isEmpty()) occurrence.identificationID = rec.value(DwcTerm.identificationID);
			if (rec.value(DwcTerm.identifiedBy) != null && !rec.value(DwcTerm.identifiedBy).isEmpty()) occurrence.identifiedBy = rec.value(DwcTerm.identifiedBy);
			if (rec.value(DwcTerm.dateIdentified) != null && !rec.value(DwcTerm.dateIdentified).isEmpty()) occurrence.dateIdentified = rec.value(DwcTerm.dateIdentified);
			//*identificationVerificationStatus
			if (rec.value(DwcTerm.identificationRemarks) != null && !rec.value(DwcTerm.identificationRemarks).isEmpty()) occurrence.identificationRemarks = rec.value(DwcTerm.identificationRemarks);
			if (rec.value(DwcTerm.identificationQualifier) != null && !rec.value(DwcTerm.identificationQualifier).isEmpty()) occurrence.identificationQualifier = rec.value(DwcTerm.identificationQualifier);
			if (rec.value(DwcTerm.typeStatus) != null && !rec.value(DwcTerm.typeStatus).isEmpty()) occurrence.typeStatus = rec.value(DwcTerm.typeStatus);

			//Taxon
			if (rec.value(DwcTerm.taxonID) != null && !rec.value(DwcTerm.taxonID).isEmpty()) occurrence.taxonID = rec.value(DwcTerm.taxonID);
			if (rec.value(DwcTerm.scientificNameID) != null && !rec.value(DwcTerm.scientificNameID).isEmpty()) occurrence.scientificNameID = rec.value(DwcTerm.scientificNameID);
			if (rec.value(DwcTerm.acceptedNameUsageID) != null && !rec.value(DwcTerm.acceptedNameUsageID).isEmpty()) occurrence.acceptedNameUsageID = rec.value(DwcTerm.acceptedNameUsageID);
			if (rec.value(DwcTerm.parentNameUsageID) != null && !rec.value(DwcTerm.parentNameUsageID).isEmpty()) occurrence.parentNameUsageID = rec.value(DwcTerm.parentNameUsageID);
			if (rec.value(DwcTerm.originalNameUsageID) != null && !rec.value(DwcTerm.originalNameUsageID).isEmpty()) occurrence.originalNameUsageID = rec.value(DwcTerm.originalNameUsageID);
			if (rec.value(DwcTerm.nameAccordingToID) != null && !rec.value(DwcTerm.nameAccordingToID).isEmpty()) occurrence.nameAccordingToID = rec.value(DwcTerm.nameAccordingToID);
			if (rec.value(DwcTerm.namePublishedInID) != null && !rec.value(DwcTerm.namePublishedInID).isEmpty()) occurrence.namePublishedInID = rec.value(DwcTerm.namePublishedInID);
			if (rec.value(DwcTerm.taxonConceptID) != null && !rec.value(DwcTerm.taxonConceptID).isEmpty()) occurrence.taxonConceptID = rec.value(DwcTerm.taxonConceptID);
			if (rec.value(DwcTerm.scientificName) != null && !rec.value(DwcTerm.scientificName).isEmpty()) occurrence.scientificName = rec.value(DwcTerm.scientificName);
			if (rec.value(DwcTerm.acceptedNameUsage) != null && !rec.value(DwcTerm.acceptedNameUsage).isEmpty()) occurrence.acceptedNameUsage = rec.value(DwcTerm.acceptedNameUsage);
			if (rec.value(DwcTerm.parentNameUsage) != null && !rec.value(DwcTerm.parentNameUsage).isEmpty()) occurrence.parentNameUsage = rec.value(DwcTerm.parentNameUsage);
			if (rec.value(DwcTerm.originalNameUsage) != null && !rec.value(DwcTerm.originalNameUsage).isEmpty()) occurrence.originalNameUsage = rec.value(DwcTerm.originalNameUsage);
			if (rec.value(DwcTerm.nameAccordingTo) != null && !rec.value(DwcTerm.nameAccordingTo).isEmpty()) occurrence.nameAccordingTo = rec.value(DwcTerm.nameAccordingTo);
			if (rec.value(DwcTerm.namePublishedIn) != null && !rec.value(DwcTerm.namePublishedIn).isEmpty()) occurrence.namePublishedIn = rec.value(DwcTerm.namePublishedIn);
			//*namePublishedInYear);
			if (rec.value(DwcTerm.higherClassification) != null && !rec.value(DwcTerm.higherClassification).isEmpty()) occurrence.higherClassification = rec.value(DwcTerm.higherClassification);
			if (rec.value(DwcTerm.kingdom) != null && !rec.value(DwcTerm.kingdom).isEmpty()) occurrence.kingdom = rec.value(DwcTerm.kingdom);
			if (rec.value(DwcTerm.phylum) != null && !rec.value(DwcTerm.phylum).isEmpty()) occurrence.phylum = rec.value(DwcTerm.phylum);
			if (rec.value(DwcTerm.classs) != null && !rec.value(DwcTerm.classs).isEmpty()) occurrence.classs = rec.value(DwcTerm.classs);
			if (rec.value(DwcTerm.order) != null && !rec.value(DwcTerm.order).isEmpty()) occurrence.orderr = rec.value(DwcTerm.order);
			if (rec.value(DwcTerm.family) != null && !rec.value(DwcTerm.family).isEmpty()) occurrence.family = rec.value(DwcTerm.family);
			if (rec.value(DwcTerm.genus) != null && !rec.value(DwcTerm.genus).isEmpty()) occurrence.genus = rec.value(DwcTerm.genus);
			if (rec.value(DwcTerm.subgenus) != null && !rec.value(DwcTerm.subgenus).isEmpty()) occurrence.subgenus = rec.value(DwcTerm.subgenus);
			if (rec.value(DwcTerm.specificEpithet) != null && !rec.value(DwcTerm.specificEpithet).isEmpty()) occurrence.specificEpithet = rec.value(DwcTerm.specificEpithet);
			if (rec.value(DwcTerm.infraspecificEpithet) != null && !rec.value(DwcTerm.infraspecificEpithet).isEmpty()) occurrence.infraSpecificEpithet = rec.value(DwcTerm.infraspecificEpithet);
			if (rec.value(DwcTerm.taxonRank) != null && !rec.value(DwcTerm.taxonRank).isEmpty()) occurrence.taxonRank = rec.value(DwcTerm.taxonRank);
			if (rec.value(DwcTerm.verbatimTaxonRank) != null && !rec.value(DwcTerm.verbatimTaxonRank).isEmpty()) occurrence.verbatimTaxonRank = rec.value(DwcTerm.verbatimTaxonRank);
			if (rec.value(DwcTerm.scientificNameAuthorship) != null && !rec.value(DwcTerm.scientificNameAuthorship).isEmpty()) occurrence.scientificNameAuthorship = rec.value(DwcTerm.scientificNameAuthorship);
			if (rec.value(DwcTerm.vernacularName) != null && !rec.value(DwcTerm.vernacularName).isEmpty()) occurrence.vernacularName = rec.value(DwcTerm.vernacularName);
			if (rec.value(DwcTerm.nomenclaturalCode) != null && !rec.value(DwcTerm.nomenclaturalCode).isEmpty()) occurrence.nomenclaturalCode = rec.value(DwcTerm.nomenclaturalCode);
			if (rec.value(DwcTerm.taxonomicStatus) != null && !rec.value(DwcTerm.taxonomicStatus).isEmpty()) occurrence.taxonomicStatus = rec.value(DwcTerm.taxonomicStatus);
			if (rec.value(DwcTerm.nomenclaturalStatus) != null && !rec.value(DwcTerm.nomenclaturalStatus).isEmpty()) occurrence.nomenclaturalStatus = rec.value(DwcTerm.nomenclaturalStatus);
			if (rec.value(DwcTerm.taxonRemarks) != null && !rec.value(DwcTerm.taxonRemarks).isEmpty()) occurrence.taxonRemarks = rec.value(DwcTerm.taxonRemarks);
			//GeoSpatial
			//occurrence.the_geom. = new SpatialPointFromTextMethod();
			occurrences.add(occurrence);
			//for JAVA heap space
			if (i%maxRows == 0) 
			{
				try {
					databaseSync.synchronize(occurrences);
					occurrences = new ArrayList<Occurrence>(); 
					LOG.info(i + " occurrences saved in database");
				}
				catch (RuntimeException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//this.dataset.occurrences = occurrences;
		// now synchronise the results to the database
		//LOG.info("Number of results: " + occurrences.size());
		try 
		{
			databaseSync.synchronize(occurrences);
			return true;
		}
		catch (RuntimeException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false; 
	}
	public boolean createMetadata(File fileDirectory) throws IOException, UnsupportedArchiveException, SQLException 
	{
		EmlParser parser = null;
		File emlFile = new File(fileDirectory+"/eml.xml");
		try {
			parser = new EmlParser(emlFile);
			EmlData eml = parser.getData();
			eml.dataset = this.dataset;
			this.dataset.emlData = eml;
			return true;
		} catch (org.jdom.JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false; 
	}
}
