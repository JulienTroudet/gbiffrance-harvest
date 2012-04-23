package models.harvest.biocase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import models.Occurrence;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Inspects the response and builds lists of model objects
 * @author tim
 */
public class ResponseToModelHandler 
{
  public List<Occurrence> handleResponse(GZIPInputStream inputStream) throws IOException 
  {
    List<Occurrence> results = new ArrayList<Occurrence>();
    
    Digester digester = new Digester();
    // we need it to understand that /biocase:response is /response
    digester.setNamespaceAware(true);
    digester.setValidating(false);
    digester.push(results);
    // Digester uses the class loader of its own class to find classes needed for object create rules. As Digester is bundled, the wrong class loader was used leading to this Exception.
    digester.setUseContextClassLoader(true);
    // ever unit forces a new Occurrence record to be created
    digester.addObjectCreate("*/Units/Unit", "models.Occurrence");

    
    // extract the properties we are interested in
    //Record-level
    digester.addBeanPropertySetter("*/RecordBasis", "typee");
    digester.addBeanPropertySetter("/DataSets/DataSet/Units/Unit/IPRStatements", "rights");
    digester.addBeanPropertySetter("/DataSets/DataSet/Units/Unit/Owner/Person/FullName", "rightsHolder");
    digester.addBeanPropertySetter("/DataSets/DataSet/Units/Unit/IPRStatements", "accessRights");
    digester.addBeanPropertySetter("*/SpecimenUnit/NomenclaturalTypeDesignations/NomenclaturalTypeDesignation/NomenclaturalReference/TitleCitation", "bibliographicCitation");
    digester.addBeanPropertySetter("*/UnitReferences/UnitReferences", "referencess");
    digester.addBeanPropertySetter("*/SourceInstitutionID", "institutionID");
    digester.addBeanPropertySetter("*/SourceID", "collectionID");
    digester.addBeanPropertySetter("*/DataSetGUID", "datasetID");
    digester.addBeanPropertySetter("*/SourceInstitutionID", "institutionCode");
    digester.addBeanPropertySetter("*/SourceID", "collectionCode"); 
    digester.addBeanPropertySetter("*/SourceID", "datasetName"); 
    digester.addBeanPropertySetter("*/RecordBasis", "basisOfRecord");
    digester.addBeanPropertySetter("*/InformationWithheld", "informationWithheld");
    
    //Occurrence
    digester.addBeanPropertySetter("*/UnitGUID", "occurrenceID");
    digester.addBeanPropertySetter("*/UnitID", "catalogNumber");
    digester.addBeanPropertySetter("*/Notes", "occurrenceRemarks");
    digester.addBeanPropertySetter("*/CollectorsFieldNumber", "recordNumber");
    digester.addBeanPropertySetter("*/Gathering/GatheringAgents/GatheringAgentsText", "recordedBy");
    digester.addBeanPropertySetter("*/ObservationUnit/ObservationUnitIdentifiers/ObservationUnitIdentifier", "individualID");
    digester.addBeanPropertySetter("*/Gathering/SiteMeasurementsOrFacts/SiteMeasurementOrFact/MeasurementOrFactAtomised/LowerValue", "individualCount");
    digester.addBeanPropertySetter("*/Sex", "sex");
    digester.addBeanPropertySetter("*/ZoologicalUnit/PhasesOrStages/PhaseOrStage", "lifeStage");
    digester.addBeanPropertySetter("*/Gathering/EstablishmentMeans", "establishmentMeans");
    digester.addBeanPropertySetter("*/SpecimenUnit/Preparations/PreparationsText", "preparations");
    digester.addBeanPropertySetter("*/SpecimenUnit/Disposition", "disposition");
    digester.addBeanPropertySetter("*/SpecimenUnit/History/PreviousUnitsText", "otherCatalogNumbers");
    digester.addBeanPropertySetter("*/Identifications/Identification", "previousIdentifications");
    digester.addBeanPropertySetter("*/MultimediaObjects", "associatedMedia");
    digester.addBeanPropertySetter("*/UnitReferences", "associatedReferences");
    //digester.addBeanPropertySetter("*/", "associatedOccurrences");
    digester.addBeanPropertySetter("*/Sequences/Sequence/ID-in-Database", "associatedSequences");
    digester.addBeanPropertySetter("*/Gathering/Synecology/AssociatedTaxa", "associatedTaxa");
    
    //Event
    digester.addBeanPropertySetter("*/Gathering/Code", "eventID");
    digester.addBeanPropertySetter("*/Gathering/Method", "samplingProtocol");
    digester.addBeanPropertySetter("*/Gathering/ISODateTimeBegin", "eventDate");
    digester.addBeanPropertySetter("*/Gathering/ISODateTimeBegin", "eventTime");
    digester.addBeanPropertySetter("*/Gathering/DateTime/DayNumberBegin", "startDayOfYear");
    digester.addBeanPropertySetter("*/Gathering/DateTime/DayNumberEnd", "endDayOfYear");
    digester.addBeanPropertySetter("*/DataSets/DataSet/Units/Unit/Gathering/ISODateTimeBegin", "year");
    digester.addBeanPropertySetter("*/DataSets/DataSet/Units/Unit/Gathering/ISODateTimeBegin", "month");
    digester.addBeanPropertySetter("*/DataSets/DataSet/Units/Unit/Gathering/ISODateTimeBegin", "day");
    digester.addBeanPropertySetter("*/Gathering/DateTime/DateText", "verbatimEventDate");
    
    digester.addBeanPropertySetter("*/Gathering/Biotope/Text", "habitat");
    digester.addBeanPropertySetter("*/Gathering/Code", "fieldNumber");
    digester.addBeanPropertySetter("*/FieldNotes", "fieldNotes");
    
    //Location
    digester.addBeanPropertySetter("*/Gathering/NamedAreas/NamedArea/AreaName", "higherGeography");
    //digester.addBeanPropertySetter("*/", "continent");
    //digester.addBeanPropertySetter("*/", "waterBody");
    //digester.addBeanPropertySetter("*/", "islandGroup");
    //digester.addBeanPropertySetter("*/", "island");
    digester.addBeanPropertySetter("*/Gathering/GatheringSite/Country/CountryName", "country");
    digester.addBeanPropertySetter("*/Gathering/Country/ISO3166Code", "countryCode");
    //digester.addBeanPropertySetter("*/", "stateProvince");
    //digester.addBeanPropertySetter("*/", "county");
    //digester.addBeanPropertySetter("*/", "municipality");
    digester.addBeanPropertySetter("*/Gathering/AreaDetail", "locality");
    //digester.addBeanPropertySetter("*/", "verbatimLocality");
    digester.addBeanPropertySetter("*/Gathering/Altitude/MeasurementOrFactText", "verbatimElevation");
    digester.addBeanPropertySetter("*/Gathering/Altitude/MeasurementOrFactAtomised/LowerValue", "minimumElevationInMeters");
    digester.addBeanPropertySetter("*/Gathering/Altitude/MeasurementOrFactAtomised/UpperValue", "maximumElevationInMeters");
    digester.addBeanPropertySetter("*/Gathering/Depth/MeasurementOrFactText", "verbatimDepth");
    digester.addBeanPropertySetter("*/Gathering/Depth/MeasurementOrFactAtomised/LowerValue", "minimumDepthInMeters");
    digester.addBeanPropertySetter("*/Gathering/Depth/MeasurementOrFactAtomised/UpperValue", "maximumDepthInMeters");
    digester.addBeanPropertySetter("*/Gathering/Height/MeasurementOrFactAtomised/LowerValue", "minimumDistanceAboveSurfaceInMeters");
    digester.addBeanPropertySetter("*/Gathering/Height/MeasurementOrFactAtomised/UpperValue", "minimumDistanceAboveSurfaceInMeters");
    digester.addBeanPropertySetter("*/Gathering/AreaDetail", "locationRemarks");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/CoordinatesText", "verbatimCoordinates");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/VerbatimLatitude", "verbatimLatitude");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/VerbatimLongitude", "verbatimLongitude");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesGrid/GridCellSystem", "verbatimCoordinateSystem");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/LatitudeDecimal", "decimalLatitude");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/LongitudeDecimal", "decimalLongitude");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/SpatialDatum", "geodeticDatum");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/CoordinateErrorDistanceInMeters", "coordinateUncertaintyInMeters");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/AccuracyStatement", "coordinatePrecision");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/PointRadiusSpatialFit", "pointRadiusSpatialFit");
    digester.addBeanPropertySetter("*/Gathering/FootprintWKT", "footprintWKT");
    digester.addBeanPropertySetter("*/Gathering/FootprintSpatialFit", "footprintSpatialFit");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/GeoreferenceSources", "georeferenceSources");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/GeoreferenceVerificationStatus", "georeferenceVerificationStatus");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/GeoreferenceRemarks", "georeferenceRemarks");
    
    //Identification
    digester.addBeanPropertySetter("*/Identifications/Identification/Identifiers/IdentifiersText", "identifiedBy");
    digester.addBeanPropertySetter("*/Identifications/Identification/Date/DateText", "dateIdentified");
    digester.addBeanPropertySetter("*/Identifications/Identification/Notes", "identificationRemarks");
    digester.addBeanPropertySetter("*/Identifications/Identification/TaxonIdentified/IdentificationQualifier", "identificationQualifier");
    digester.addBeanPropertySetter("*/SpecimenUnit/NomenclaturalTypeDesignations/NomenclaturalTypeText", "typeStatus");
    
    //Taxon
    digester.addBeanPropertySetter("*/SpecimenUnit/NomenclaturalTypeDesignations/NomenclaturalTypeDesignation/NomenclaturalReference/TitleCitation", "namePublishedIn");
    //digester.addBeanPropertySetter("*/", "kingdom");
    //digester.addBeanPropertySetter("*/", "phylum");
    //digester.addBeanPropertySetter("*/", "classs");
    //digester.addBeanPropertySetter("*/", "orderr");
    //digester.addBeanPropertySetter("*/", "family");
    //digester.addBeanPropertySetter("*/", "genus");
    //digester.addBeanPropertySetter("*/", "subgenus");
    //digester.addBeanPropertySetter("*/", "specificEpithet");
    //digester.addBeanPropertySetter("*/", "infraSpecificEpithet");
    digester.addBeanPropertySetter("*/Identifications/Identification/TaxonIdentified/ScientificName/NameAtomised/Botanical/Rank", "taxonRank");
    digester.addBeanPropertySetter("*/ScientificName/FullScientificNameString", "scientificName");
    //digester.addBeanPropertySetter("*/", "scientificNameAuthorship");
    digester.addBeanPropertySetter("*/Identifications/Identification/TaxonIdentified/Code", "nomenclaturalCode");
    digester.addBeanPropertySetter("*/SpecimenUnit/NomenclaturalTypeDesignations/NomenclaturalTypeDesignation/NomenclaturalReference/TitleCitation", "nomenclaturalStatus");
    // add the created Occurrence record to the list
    digester.addSetNext("*/Units/Unit", "add");
    try {
      digester.parse(inputStream);
    } catch (SAXException e) {
      
    }    
    return results;
  }
}
