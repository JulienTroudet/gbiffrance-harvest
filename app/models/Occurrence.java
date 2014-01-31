package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import oracle.spatial.geometry.JGeometry;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Occurrence extends Model {
	// Record-level
	@Column(length = 2000)
	public String typee;
	@Column(length = 2000)
	public String modified;
	@Column(length = 2000)
	public String language;
	@Column(length = 2000)
	public String rights;
	@Column(length = 2000)
	public String rightsHolder;
	@Column(length = 2000)
	public String accessRights;
	@Column(length = 2000)
	public String bibliographicCitation;
	@Column(length = 2000)
	public String referencess;
	@Column(length = 2000)
	public String institutionID;
	@Column(length = 2000)
	public String collectionID;
	@Column(length = 2000)
	public String datasetID;
	@Column(length = 2000)
	public String institutionCode;
	@Column(length = 200)
	public String collectionCode;
	@Column(length = 2000)
	public String datasetName;
	@Column(length = 2000)
	public String ownerInstitutionCode;
	@Column(length = 2000)
	public String basisOfRecord;
	@Column(length = 2000)
	public String informationWithheld;
	@Column(length = 2000)
	public String dataGeneralizations;
	@Column(length = 2000)
	public String dynamicProperties;

	// Occurrence
	@Column(length = 2000)
	public String occurrenceID;
	@Column(length = 2000)
	public String catalogNumber;
	@Column(length = 300)
	public String occurrenceRemarks;
	@Column(length = 2000)
	public String recordNumber;
	@Column(length = 200)
	public String recordedBy;
	@Column(length = 2000)
	public String individualID;
	@Column(length = 2000)
	public String individualCount;
	@Column(length = 2000)
	public String sex;
	@Column(length = 2000)
	public String lifeStage;
	@Column(length = 2000)
	public String reproductiveCondition;
	@Column(length = 2000)
	public String behavior;
	@Column(length = 2000)
	public String establishmentMeans;
	@Column(length = 2000)
	public String occurrenceStatus;
	@Column(length = 2000)
	public String preparations;
	@Column(length = 2000)
	public String disposition;
	@Column(length = 2000)
	public String otherCatalogNumbers;
	@Column(length = 2000)
	public String previousIdentifications;
	@Column(length = 2000)
	public String associatedMedia;
	@Column(length = 2000)
	public String associatedReferences;
	@Column(length = 20000)
	public String associatedOccurrences;
	@Column(length = 2000)
	public String associatedSequences;
	@Column(length = 2000)
	public String associatedTaxa;

	// Event
	@Column(length = 2000)
	public String eventID;
	@Column(length = 2000)
	public String samplingProtocol;
	@Column(length = 2000)
	public String samplingEffort;
	@Column(length = 2000)
	public String eventDate;
	@Column(length = 2000)
	public String eventTime;
	@Column(length = 2000)
	public String startDayOfYear;
	@Column(length = 2000)
	public String endDayofYear;
	@Column(length = 2000)
	public String year;
	@Column(length = 2000)
	public String month;
	@Column(length = 2000)
	public String day;
	@Column(length = 2000)
	public String verbatimEventDate;
	@Column(length = 2000)
	public String habitat;
	@Column(length = 2000)
	public String fieldNumber;
	@Column(length = 2000)
	public String fieldNotes;
	@Column(length = 2000)
	public String eventRemarks;

	// Location
	@Column(length = 2000)
	public String locationID;
	@Column(length = 2000)
	public String higherGeographyID;
	@Column(length = 2000)
	public String higherGeography;
	@Column(length = 2000)
	public String continent;
	@Column(length = 2000)
	public String waterBody;
	@Column(length = 2000)
	public String islandGroup;
	@Column(length = 2000)
	public String island;
	@Column(length = 2000)
	public String country;
	@Column(length = 2000)
	public String countryCode;
	@Column(length = 2000)
	public String stateProvince;
	@Column(length = 2000)
	public String county;
	@Column(length = 2000)
	public String municipality;
	@Column(length = 2000)
	public String locality;
	@Column(length = 2000)
	public String verbatimLocality;
	@Column(length = 2000)
	public String verbatimElevation;
	@Column(length = 2000)
	public String minimumElevationInMeters;
	@Column(length = 2000)
	public String maximumElevationInMeters;
	@Column(length = 2000)
	public String verbatimDepth;
	@Column(length = 2000)
	public String minimumDepthInMeters;
	@Column(length = 2000)
	public String maximumDepthInMeters;
	@Column(length = 2000)
	public String minDistAboveSurfaceInMeters;
	@Column(length = 2000)
	public String maxDistAboveSurfaceInMeters;
	@Column(length = 2000)
	public String locationAccordingTo;
	@Column(length = 2000)
	public String locationRemarks;
	@Column(length = 2000)
	public String verbatimCoordinates;
	@Column(length = 2000)
	public String verbatimLatitude;
	@Column(length = 2000)
	public String verbatimLongitude;
	@Column(length = 2000)
	public String verbatimCoordinateSystem;
	@Column(length = 2000)
	public String verbatimSRS;
	@Column(length = 2000)
	public String decimalLatitude;
	@Column(length = 2000)
	public String decimalLongitude;
	@Column(length = 2000)
	public String geodeticDatum;
	@Column(length = 2000)
	public String coordinateUncertaintyInMeters;
	@Column(length = 2000)
	public String coordinatePrecision;
	@Column(length = 2000)
	public String pointRadiusSpatialFit;
	@Column(length = 2000)
	public String footprintWKT;
	@Column(length = 2000)
	public String footprintSRS;
	@Column(length = 2000)
	public String footprintSpatialFit;
	@Column(length = 2000)
	public String georeferencedBy;
	@Column(length = 2000)
	public String georeferencedDate;
	@Column(length = 2000)
	public String georeferenceProtocol;
	@Column(length = 2000)
	public String georeferenceSources;
	@Column(length = 2000)
	public String georeferenceVerifStatus;
	@Column(length = 2000)
	public String georeferenceRemarks;

	// GeologicalContext
	@Column(length = 2000)
	public String geologicalContextID;
	@Column(length = 2000)
	public String earliestEonOrLowestEonothem;
	@Column(length = 2000)
	public String latestEonOrHighestEonothem;
	@Column(length = 2000)
	public String earliestEraOrLowestErathem;
	@Column(length = 2000)
	public String latestEraOrHighestErathem;
	@Column(length = 2000)
	public String earliestPeriodOrLowestSystem;
	@Column(length = 2000)
	public String latestPeriodOrHighestSystem;
	@Column(length = 2000)
	public String earliestEpochOrLowestSeries;
	@Column(length = 2000)
	public String latestEpochOrHighestSeries;
	@Column(length = 2000)
	public String earliestAgeOrLowestStage;
	@Column(length = 2000)
	public String latestAgeOrHighestStage;
	@Column(length = 2000)
	public String lowestBiostratigraphicZone;
	@Column(length = 2000)
	public String highestBiostratigraphicZone;
	@Column(length = 2000)
	public String lithostratigraphicTerms;
	@Column(length = 2000)
	public String groupp;
	@Column(length = 2000)
	public String formation;
	@Column(length = 2000)
	public String member;
	@Column(length = 2000)
	public String bed;

	// Identification
	@Column(length = 2000)
	public String identificationID;
	@Column(length = 200)
	public String identifiedBy;
	@Column(length = 2000)
	public String dateIdentified;
	@Column(length = 2000)
	public String identifVerificationStatus;
	@Column(length = 2000)
	public String identificationRemarks;
	@Column(length = 2000)
	public String identificationQualifier;
	@Column(length = 2000)
	public String typeStatus;

	// Taxon
	@Column(length = 2000)
	public String taxonID;
	@Column(length = 2000)
	public String scientificNameID;
	@Column(length = 2000)
	public String acceptedNameUsageID;
	@Column(length = 2000)
	public String parentNameUsageID;
	@Column(length = 2000)
	public String originalNameUsageID;
	@Column(length = 2000)
	public String nameAccordingToID;
	@Column(length = 200)
	public String namePublishedInID;
	@Column(length = 2000)
	public String taxonConceptID;
	@Column(length = 2000)
	@Index(name = "idx_scientificName")
	@Required
	public String scientificName;
	@Column(length = 2000)
	public String acceptedNameUsage;
	@Column(length = 2000)
	public String parentNameUsage;
	@Column(length = 2000)
	public String originalNameUsage;
	@Column(length = 2000)
	public String nameAccordingTo;
	@Column(length = 2000)
	public String namePublishedIn;
	@Column(length = 2000)
	public String namePublishedInYear;
	@Column(length = 2000)
	public String higherClassification;
	@Column(length = 2000)
	public String kingdom;
	@Column(length = 2000)
	public String phylum;
	@Column(length = 2000)
	public String classs; // class is a Java keyword
	@Column(length = 2000)
	public String orderr; // order is a SQL keyword
	@Column(length = 2000)
	public String family;
	@Column(length = 2000)
	public String genus;
	@Column(length = 2000)
	public String subgenus;
	@Column(length = 2000)
	public String specificEpithet;
	@Column(length = 2000)
	public String infraSpecificEpithet;
	@Column(length = 2000)
	public String kingdom_interpreted;
	@Column(length = 2000)
	public String phylum_interpreted;
	@Column(length = 2000)
	public String classs_interpreted;
	@Column(length = 2000)
	public String orderr_interpreted;
	@Column(length = 2000)
	public String family_interpreted;
	@Column(length = 2000)
	public String genus_interpreted;
	@Column(length = 2000)
	public String subgenus_interpreted;
	@Column(length = 2000)
	public String specificEpithet_interpreted;
	@Column(length = 2000)
	public String infraSpecificEpithet_inter;
	@Column(length = 2000)
	public String taxonRank;
	@Column(length = 2000)
	public String verbatimTaxonRank;
	@Column(length = 2000)
	public String scientificNameAuthorship;
	@Column(length = 2000)
	public String vernacularName;
	@Column(length = 2000)
	public String nomenclaturalCode;
	@Column(length = 2000)
	public String taxonomicStatus;
	@Column(length = 2000)
	public String nomenclaturalStatus;
	@Column(length = 2000)
	public String taxonRemarks;

	@Column(name = "CD_NOM", length = 2000)
	public String cdNom;

	// Harvester
	@Column(length = 2000)
	public String taxonStatus;

	// ecat
	@Column(nullable = true)
	public int ecatTaxonId;
	@Column(nullable = true)
	public int ecatParentTaxonId;

	@ManyToOne
	public Dataset dataset;

	@OneToMany(mappedBy = "occurrence", cascade = CascadeType.ALL)
	public List<Result> results;

	public boolean qualified;

	@Type(type = "org.hibernatespatial.GeometryUserType")
	@Column(nullable = true)
	public JGeometry geometry;

	@Column(name = "TYPE_SOURCE", length = 2000)
	public String typeSource;

	@Column(name = "RESTRICTION_LOCALISATION_P", length = 2000)
	public String restrictionLocalisationP;

	@Column(name = "RESTRICTION_MAILLE", length = 2000)
	public String restrictionMaille;

	@Column(name = "RESTRICTION_COMMUNE", length = 2000)
	public String restrictionCommune;

	@Column(name = "RESTRICTION_TOTAL", length = 2000)
	public String restrictionTotal;

	@Column(name = "LIEN_ORIGINE", length = 2000)
	public String lienOrigine;

	@Column(name = "NOM_SCIENTIFIQUE_CITE", length = 2000)
	public String nomScientifiqueCite;

	@Column(name = "IDENTITE_OBS", length = 2000)
	public String identiteOBS;

	@Column(name = "ORGANISME_OBS", length = 2000)
	public String organismeOBS;

	@Column(length = 2000)
	public String validateur;

	@Column(name = "DATE_INF", length = 2000)
	public String dateInf;

	@Column(name = "DATE_SUP", length = 2000)
	public String dateSup;

	@Column(name = "CODE_INSEE", length = 2000)
	public String codeInsee;

	@Column(name = "NOM_COMMUNE", length = 2000)
	public String nomCommune;

	@Column(name = "POURCENTAGE_COMMUNE", length = 2000)
	public String pourcentageCommune;

	@Column(name = "CODE_EN", length = 2000)
	public String codeEN;

	@Column(name = "TYPE_EN", length = 2000)
	public String typeEN;

	@Column(name = "POURCENTAGE_EN", length = 2000)
	public String pourcentageEN;

	@Column(name = "CODE_MAILLE", length = 2000)
	public String codeMaille;

	@Column(name = "POURCENTAGE_MAILLE", length = 2000)
	public String pourcentageMaille;

	public Occurrence() {
	}

	public Occurrence(
			// Record-level
			String typee,
			String modified,
			String language,
			String rights,
			String rightsHolder,
			String accessRights,
			String bibliographicCitation,
			String referencess,
			String institutionID,
			String collectionID,
			String datasetID,
			String institutionCode,
			String collectionCode,
			String datasetName,
			String ownerInstitutionCode,
			String basisOfRecord,
			String informationWithheld,
			String dataGeneralizations,
			String dynamicProperties,
			// Occurrence
			String occurrenceID,
			String catalogNumber,
			String occurrenceRemarks,
			String recordNumber,
			String recordedBy,
			String individualID,
			String individualCount,
			String sex,
			String lifeStage,
			String reproductiveCondition,
			String behavior,
			String establishmentMeans,
			String occurrenceStatus,
			String preparations,
			String disposition,
			String otherCatalogNumbers,
			String previousIdentifications,
			String associatedMedia,
			String associatedReferences,
			String associatedOccurrences,
			String associatedSequences,
			String associatedTaxa,
			// Event
			String eventID,
			String samplingProtocol,
			String samplingEffort,
			String eventDate,
			String eventTime,
			String startDayOfYear,
			String endDayofYear,
			String year,
			String month,
			String day,
			String verbatimEventDate,
			String habitat,
			String fieldNumber,
			String fieldNotes,
			String eventRemarks,
			// Location
			String locationID,
			String higherGeographyID,
			String higherGeography,
			String continent,
			String waterBody,
			String islandGroup,
			String island,
			String country,
			String countryCode,
			String stateProvince,
			String county,
			String municipality,
			String locality,
			String verbatimLocality,
			String verbatimElevation,
			String minimumElevationInMeters,
			String maximumElevationInMeters,
			String verbatimDepth,
			String minimumDepthInMeters,
			String maximumDepthInMeters,
			String minimumDistanceAboveSurfaceInMeters,
			String maximumDistanceAboveSurfaceInMeters,
			String locationAccordingTo,
			String locationRemarks,
			String verbatimCoordinates,
			String verbatimLatitude,
			String verbatimLongitude,
			String verbatimCoordinateSystem,
			String verbatimSRS,
			String decimalLatitude,
			String decimalLongitude,
			String geodeticDatum,
			String coordinateUncertaintyInMeters,
			String coordinatePrecision,
			String pointRadiusSpatialFit,
			String footprintWKT,
			String footprintSRS,
			String footprintSpatialFit,
			String georeferencedBy,
			String georeferencedDate,
			String georeferenceProtocol,
			String georeferenceSources,
			String georeferenceVerificationStatus,
			String georeferenceRemarks,
			// GeologicalContext
			String geologicalContextID,
			String earliestEonOrLowestEonothem,
			String latestEonOrHighestEonothem,
			String earliestEraOrLowestErathem,
			String latestEraOrHighestErathem,
			String earliestPeriodOrLowestSystem,
			String latestPeriodOrHigherSystem,
			String earliestEpochOrLowestSeries,
			String latestEpochOrHigherSeries,
			String earliestAgeOrLowestStage,
			String latestAgeOrHigherStage,
			String lowestBiostratigraphicZone,
			String highestBiostratigraphicZone,
			String lithostratigraphicTerms,
			String groupp,
			String formation,
			String member,
			String bed,
			// Identification
			String identificationID,
			String identifiedBy,
			String dateIdentified,
			String identificationVerificationStatus,
			String identificationRemarks,
			String identificationQualifier,
			String typeStatus,
			// Taxon
			String taxonID, String scientificNameID,
			String acceptedNameUsageID,
			String parentNameUsageID,
			String originalNameUsageID,
			String nameAccordingToID,
			String namePublishedInID,
			String taxonConceptID,
			String scientificName,
			String acceptedNameUsage,
			String parentNameUsage,
			String originalNameUsage,
			String nameAccordingTo,
			String namePublishedIn,
			String namePublishedInYear,
			String higherClassification,
			String kingdom,
			String phylum,
			String classs, // class is a Java keyword
			String orderr, // order is a SQL keyword
			String family, String genus, String subgenus,
			String specificEpithet, String infraSpecificEpithet,
			String taxonRank, String verbatimTaxonRank,
			String scientificNameAuthorship, String vernacularName,
			String nomenclaturalCode, String taxonomicStatus,
			String nomenclaturalStatus, String taxonRemarks,
			// Harvester
			String taxonStatus,
			// ecat
			int ecatTaxonId, int ecatParentTaxonId, Dataset dataset) {
		// Record-level
		this.typee = typee;
		this.modified = modified;
		this.language = language;
		this.rights = rights;
		this.rightsHolder = rightsHolder;
		this.accessRights = accessRights;
		this.bibliographicCitation = bibliographicCitation;
		this.referencess = referencess;
		this.institutionID = institutionID;
		this.collectionID = collectionID;
		this.datasetID = datasetID;
		this.institutionCode = institutionCode;
		this.collectionCode = collectionCode;
		this.datasetName = datasetName;
		this.ownerInstitutionCode = ownerInstitutionCode;
		this.basisOfRecord = basisOfRecord;
		this.informationWithheld = informationWithheld;
		this.dataGeneralizations = dataGeneralizations;
		this.dynamicProperties = dynamicProperties;
		// Occurrence
		this.occurrenceID = occurrenceID;
		this.catalogNumber = catalogNumber;
		this.occurrenceRemarks = occurrenceRemarks;
		this.recordNumber = recordNumber;
		this.recordedBy = recordedBy;
		this.individualID = individualID;
		this.individualCount = individualCount;
		this.sex = sex;
		this.lifeStage = lifeStage;
		this.reproductiveCondition = reproductiveCondition;
		this.behavior = behavior;
		this.establishmentMeans = establishmentMeans;
		this.occurrenceStatus = occurrenceStatus;
		this.preparations = preparations;
		this.disposition = disposition;
		this.otherCatalogNumbers = otherCatalogNumbers;
		this.previousIdentifications = previousIdentifications;
		this.associatedMedia = associatedMedia;
		this.associatedReferences = associatedReferences;
		this.associatedOccurrences = associatedOccurrences;
		this.associatedSequences = associatedSequences;
		this.associatedTaxa = associatedTaxa;
		// Event
		this.eventID = eventID;
		this.samplingProtocol = samplingProtocol;
		this.samplingEffort = samplingEffort;
		this.eventDate = eventDate;
		this.eventTime = eventTime;
		this.startDayOfYear = startDayOfYear;
		this.endDayofYear = endDayofYear;
		this.year = year;
		this.month = month;
		this.day = day;
		this.verbatimEventDate = verbatimEventDate;
		this.habitat = habitat;
		this.fieldNumber = fieldNumber;
		this.fieldNotes = fieldNotes;
		this.eventRemarks = eventRemarks;
		// Location
		this.locationID = locationID;
		this.higherGeographyID = higherGeographyID;
		this.higherGeography = higherGeography;
		this.continent = continent;
		this.waterBody = waterBody;
		this.islandGroup = islandGroup;
		this.island = island;
		this.country = country;
		this.countryCode = countryCode;
		this.stateProvince = stateProvince;
		this.county = county;
		this.municipality = municipality;
		this.locality = locality;
		this.verbatimLocality = verbatimLocality;
		this.verbatimElevation = verbatimElevation;
		this.minimumElevationInMeters = minimumElevationInMeters;
		this.maximumElevationInMeters = maximumElevationInMeters;
		this.verbatimDepth = verbatimDepth;
		this.minimumDepthInMeters = minimumDepthInMeters;
		this.maximumDepthInMeters = maximumDepthInMeters;
		this.minDistAboveSurfaceInMeters = minimumDistanceAboveSurfaceInMeters;
		this.maxDistAboveSurfaceInMeters = maximumDistanceAboveSurfaceInMeters;
		this.locationAccordingTo = locationAccordingTo;
		this.locationRemarks = locationRemarks;
		this.verbatimCoordinates = verbatimCoordinates;
		this.verbatimLatitude = verbatimLatitude;
		this.verbatimLongitude = verbatimLongitude;
		this.verbatimCoordinateSystem = verbatimCoordinateSystem;
		this.verbatimSRS = verbatimSRS;
		this.decimalLatitude = decimalLatitude;
		this.decimalLongitude = decimalLongitude;
		this.geodeticDatum = geodeticDatum;
		this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
		this.coordinatePrecision = coordinatePrecision;
		this.pointRadiusSpatialFit = pointRadiusSpatialFit;
		this.footprintWKT = footprintWKT;
		this.footprintSRS = footprintSRS;
		this.footprintSpatialFit = footprintSpatialFit;
		this.georeferencedBy = georeferencedBy;
		this.georeferencedDate = georeferencedDate;
		this.georeferenceProtocol = georeferenceProtocol;
		this.georeferenceSources = georeferenceSources;
		this.georeferenceVerifStatus = georeferenceVerificationStatus;
		this.georeferenceRemarks = georeferenceRemarks;
		// GeologicalContext
		this.geologicalContextID = geologicalContextID;
		this.earliestEonOrLowestEonothem = earliestEonOrLowestEonothem;
		this.latestEonOrHighestEonothem = latestEonOrHighestEonothem;
		this.earliestEraOrLowestErathem = earliestEraOrLowestErathem;
		this.latestEraOrHighestErathem = latestEraOrHighestErathem;
		this.earliestPeriodOrLowestSystem = earliestPeriodOrLowestSystem;
		this.latestPeriodOrHighestSystem = latestPeriodOrHigherSystem;
		this.earliestEpochOrLowestSeries = earliestEpochOrLowestSeries;
		this.latestEpochOrHighestSeries = latestEpochOrHigherSeries;
		this.earliestAgeOrLowestStage = earliestAgeOrLowestStage;
		this.latestAgeOrHighestStage = latestAgeOrHigherStage;
		this.lowestBiostratigraphicZone = lowestBiostratigraphicZone;
		this.highestBiostratigraphicZone = highestBiostratigraphicZone;
		this.lithostratigraphicTerms = lithostratigraphicTerms;
		this.groupp = groupp;
		this.formation = formation;
		this.member = member;
		this.bed = bed;
		// Identification
		this.identificationID = identificationID;
		this.identifiedBy = identifiedBy;
		this.dateIdentified = dateIdentified;
		this.identifVerificationStatus = identificationVerificationStatus;
		this.identificationRemarks = identificationRemarks;
		this.identificationQualifier = identificationQualifier;
		this.typeStatus = typeStatus;
		// Taxon
		this.taxonID = taxonID;
		this.scientificNameID = scientificNameID;
		this.acceptedNameUsageID = acceptedNameUsageID;
		this.parentNameUsageID = parentNameUsageID;
		this.originalNameUsageID = originalNameUsageID;
		this.nameAccordingToID = nameAccordingToID;
		this.namePublishedInID = namePublishedInID;
		this.taxonConceptID = taxonConceptID;
		this.scientificName = scientificName;
		this.acceptedNameUsage = acceptedNameUsage;
		this.parentNameUsage = parentNameUsage;
		this.originalNameUsage = originalNameUsage;
		this.nameAccordingTo = nameAccordingTo;
		this.namePublishedIn = namePublishedIn;
		this.namePublishedInYear = namePublishedInYear;
		this.higherClassification = higherClassification;
		this.kingdom = kingdom;
		this.phylum = phylum;
		this.classs = classs; // class is a Java keyword
		this.orderr = orderr; // order is a SQL keyword
		this.family = family;
		this.genus = genus;
		this.subgenus = subgenus;
		this.specificEpithet = specificEpithet;
		this.infraSpecificEpithet = infraSpecificEpithet;
		this.taxonRank = taxonRank;
		this.verbatimTaxonRank = verbatimTaxonRank;
		this.scientificNameAuthorship = scientificNameAuthorship;
		this.vernacularName = vernacularName;
		this.nomenclaturalCode = nomenclaturalCode;
		this.taxonomicStatus = taxonomicStatus;
		this.nomenclaturalStatus = nomenclaturalStatus;
		this.taxonRemarks = taxonRemarks;
		// Harvester
		this.taxonStatus = taxonStatus;
		// ecat
		this.ecatTaxonId = ecatTaxonId;
		this.ecatParentTaxonId = ecatParentTaxonId;

		this.dataset = dataset;
	}

	public boolean inCountriesList(String value) {
		String[] countriesList = { "France", "Guadeloupe" };
		for (String country : countriesList)
			if (country.equals(value))
				return true;
		return false;
	}

	public boolean inBoundingBoxList(Float decimalLatitude,
			Float decimalLongitude) {
		ArrayList<Float[]> BoundingBoxesList = new ArrayList<Float[]>();
		BoundingBoxesList.add(new Float[] { (float) 41.333740,
				(float) 51.089062, (float) -5.140600, (float) 9.559320 }); // France
		BoundingBoxesList.add(new Float[] { (float) 14.96712, (float) 16.53148,
				(float) -61.82977, (float) -60.98185 }); // Guadeloupe
		BoundingBoxesList.add(new Float[] { (float) 13.93564,
				(float) 14.382440, (float) -61.23976, (float) -60.799 }); // Martinique
		BoundingBoxesList.add(new Float[] { (float) 1.10326, (float) 5.84561,
				(float) -54.67087, (float) -51.54721 }); // Guyane
		BoundingBoxesList.add(new Float[] { (float) -13.5459, (float) -12.5479,
				(float) 44.9371, (float) 45.3745 }); // Mayotte
		BoundingBoxesList.add(new Float[] { (float) -22.05384,
				(float) -20.85875, (float) 55.20124, (float) 55.85227 }); // Reunion
		BoundingBoxesList.add(new Float[] { (float) -16.55408,
				(float) -13.15017, (float) -178.2342, (float) -176.072 }); // Wallis&Futuna
		BoundingBoxesList.add(new Float[] { (float) -27.63533,
				(float) -19.44487, (float) 163.45591, (float) 168.2484 }); // Nouvelle-Calédonie
		BoundingBoxesList.add(new Float[] { (float) 49.48878, (float) 78.287,
				(float) -79.12063, (float) -37.50438 }); // TAAF
		BoundingBoxesList.add(new Float[] { (float) 46.43198, (float) 47.15415,
				(float) -56.41318, (float) -56.10561 }); // Saint-Pierre et
															// Miquelon
		BoundingBoxesList.add(new Float[] { (float) -27.653570,
				(float) 10.359830, (float) -154.700485, (float) -108.872910 }); // Polynesie

		for (int i = 0; i < BoundingBoxesList.size(); ++i) {
			if (decimalLatitude > BoundingBoxesList.get(i)[0]
					&& decimalLatitude < BoundingBoxesList.get(i)[1]
					&& decimalLongitude > BoundingBoxesList.get(i)[2]
					&& decimalLongitude < BoundingBoxesList.get(i)[3])
				return true;
		}
		return false;
	}

}
