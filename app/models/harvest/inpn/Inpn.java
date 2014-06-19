package models.harvest.inpn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import manager.OccurrenceMG;
import models.Dataset;
import models.Occurrence;
import models.harvest.Harvester;
import oracle.spatial.geometry.JGeometry;
import play.Logger;
import play.Play;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class Inpn extends Harvester {

	public Dataset dataset;

	public static String mDatasetDirectory = Play.configuration
			.getProperty("dataset.path");

	public static String mTempDirectory = Play.configuration
			.getProperty("temp.path");

	final static int BUFFER = 2048;

	final static String SEPARATOR = ";";

	// private OccurrenceToDBHandler databaseSync = new OccurrenceToDBHandler();

	/**
	 * Constructeur
	 * 
	 * @param pDataset
	 * @param pTargetDirectory
	 */
	public Inpn(Dataset pDataset, String pTargetDirectory) {
		this.dataset = pDataset;
		Logger.info("INPN Harvester is started. Dataset = " + dataset.name);
		run();
	}

	/**
	 * 
	 * Realise l'import INPN en base de données
	 * 
	 */
	public void run() {
		int lNb;
		FileReader lReader = null;
		BufferedReader lBr = null;
		dataset.findById(dataset.id);
		try {
			// Commune
			Map<String, String[]> lMapsCommune = null;
			if (dataset.fileCommune != null && !dataset.fileCommune.isEmpty()) {
				// Open the file to read it
				lReader = new FileReader(dataset.fileCommune);
				lBr = new BufferedReader(lReader);

				lMapsCommune = new HashMap<String, String[]>();

				lNb = 0;
				// we loop on the file
				for (String line = lBr.readLine(); line != null; line = lBr
						.readLine()) {
					if (lNb == 0) {
						lNb++;
						continue;
					}
					String[] oneData = line.split(SEPARATOR);
					lMapsCommune.put(oneData[0], oneData);
					lNb++;

				}

				lReader.close();
				lBr.close();
			}

			// File Maille
			Map<String, String[]> lMapsMaille = null;
			if (dataset.fileMaille != null && !dataset.fileMaille.isEmpty()) {
				// Open the file to read it
				lReader = new FileReader(dataset.fileMaille);
				lBr = new BufferedReader(lReader);

				lMapsMaille = new HashMap<String, String[]>();

				lNb = 0;
				// we loop on the file
				for (String line = lBr.readLine(); line != null; line = lBr
						.readLine()) {
					if (lNb == 0) {
						lNb++;
						continue;
					}
					String[] oneData = line.split(SEPARATOR);
					lMapsMaille.put(oneData[0], oneData);
					lNb++;
				}

				lReader.close();
				lBr.close();
			}

			// File ENP
			Map<String, String[]> lMapsENP = null;
			if (dataset.fileENP != null && !dataset.fileENP.isEmpty()) {
				// Open the file to read it
				lReader = new FileReader(dataset.fileENP);
				lBr = new BufferedReader(lReader);

				lMapsENP = new HashMap<String, String[]>();

				lNb = 0;
				// we loop on the file
				for (String line = lBr.readLine(); line != null; line = lBr
						.readLine()) {
					if (lNb == 0) {
						lNb++;
						continue;
					}
					String[] oneData = line.split(SEPARATOR);
					lMapsENP.put(oneData[0], oneData);
					lNb++;
				}

				lReader.close();
				lBr.close();
			}

			// Shape File
			Map<String, Geometry> lMapsShape = null;
			String lProjection = null;
			if (dataset.fileShape != null && !dataset.fileShape.isEmpty()) {
				File lFile = new File(dataset.fileShape);
				File lDirectory = extractFile(lFile, mTempDirectory
						+ File.separator + "resource-" + dataset.id
						+ File.separator);

				dataset.tempDirectory = mTempDirectory + File.separator
						+ "resource-" + dataset.id + File.separator;
				dataset.save();

				for (String lFichier : lDirectory.list()) {
					if (lFichier.substring(lFichier.length() - 3,
							lFichier.length()).equals("shp")) {
						ShapeFile lShapeFile = new ShapeFile();
						lMapsShape = lShapeFile.reader(new File(lDirectory
								+ File.separator + lFichier));
						lProjection = lShapeFile.readPrj(new File(lDirectory
								+ File.separator + lFichier));
					}

				}
			}

			// File Principal
			// Open the file to read it
			lReader = new FileReader(dataset.fileDataset);
			// lBr = new BufferedReader(lReader);
			BufferedReader ltest = new BufferedReader(new InputStreamReader(
					new java.io.FileInputStream(dataset.fileDataset), "CP1252"));

			lNb = 0;

			// we loop on the file
			for (String line = ltest.readLine(); line != null; line = ltest
					.readLine()) {
				if (lNb == 0) {
					lNb++;
					continue;
				}

				String[] oneData = line.split(SEPARATOR);

				Occurrence lOccurrence = new Occurrence();
				lOccurrence.dataset = dataset;
				lOccurrence.catalogNumber = oneData[7];// identifiantOrigine
				lOccurrence.coordinateUncertaintyInMeters = oneData[15];// precision
				lOccurrence.county = oneData[16];// codeDepartement
				lOccurrence.decimalLatitude = oneData[13];// x
				lOccurrence.decimalLongitude = oneData[14];// y
				lOccurrence.identifiedBy = oneData[11];// determinateur
				lOccurrence.locality = oneData[17]; // toponyme
				lOccurrence.nameAccordingTo = "TAXREF"; // Constante
				lOccurrence.occurrenceStatus = oneData[2]; // statutObservation
				lOccurrence.recordNumber = oneData[7]; // identifiantOrigine
				lOccurrence.taxonID = oneData[9]; // codeNom
				lOccurrence.typeSource = oneData[0];
				lOccurrence.restrictionLocalisationP = oneData[3];
				lOccurrence.restrictionMaille = oneData[4];
				lOccurrence.restrictionCommune = oneData[5];
				lOccurrence.restrictionTotal = oneData[6];
				lOccurrence.lienOrigine = oneData[8];
				lOccurrence.nomScientifiqueCite = oneData[10];
				lOccurrence.identiteOBS = oneData[20];
				lOccurrence.organismeOBS = oneData[21];
				lOccurrence.validateur = oneData[12];
				lOccurrence.dateInf = oneData[18];
				lOccurrence.dateSup = oneData[19];

				// ENP
				if (lMapsENP != null && lMapsENP.size() > 0) {
					String[] lValueEnp = lMapsENP.get(oneData[7]);
					lOccurrence.typeEN = lValueEnp[1];
					lOccurrence.codeEN = lValueEnp[2];
					lOccurrence.pourcentageEN = lValueEnp[3];
				}
				// Commune
				if (lMapsCommune != null && lMapsCommune.size() > 0) {
					String[] lValueCommune = lMapsCommune.get(oneData[7]);
					lOccurrence.codeInsee = lValueCommune[1];
					lOccurrence.nomCommune = lValueCommune[2];
					lOccurrence.pourcentageCommune = lValueCommune[3];
				}

				// Maille
				if (lMapsMaille != null && lMapsMaille.size() > 0) {
					String[] lValueMaille = lMapsMaille.get(oneData[7]);
					lOccurrence.codeMaille = lValueMaille[1];
					lOccurrence.pourcentageMaille = lValueMaille[2];
				}

				// Shape
				if (lMapsShape != null && lMapsShape.size() > 0) {
					Geometry lValueShape = lMapsShape.get(oneData[8]);
					if (lValueShape != null) {
						lOccurrence.shape = lValueShape;
						lOccurrence.projection = lProjection;
					}
				}

				lNb++;
				OccurrenceMG lOccurrenceMG = new OccurrenceMG();
				lOccurrenceMG.insertINPN(lOccurrence);
			}

			ltest.close();
			lReader.close();

		} catch (Exception e) {
			this.withErrors = true;
			Logger.error("Inport INPN : " + e.toString());
		} finally {
			try {
				if (lBr != null) {
					lBr.close();
				}
				if (lReader != null) {
					lReader.close();
				}
			} catch (IOException e) {
				Logger.error("Inport INPN : " + e.toString());
			}
		}

	}

	/**
	 * Extracts the content of the archive in the specified directory
	 * 
	 * @param pFile
	 * @param pTargetDirectory
	 * @return File
	 */
	public static File extractFile(File pFile, String pTargetDirectory) {
		File lFileDirectory = null;
		ZipInputStream lZis = null;

		try {
			BufferedOutputStream lDest = null;
			FileInputStream lFis = new FileInputStream(pFile);
			lZis = new ZipInputStream(new BufferedInputStream(lFis));
			ZipEntry lEntry;
			while ((lEntry = lZis.getNextEntry()) != null) {
				Logger.info("Extracting directory: " + lEntry.getName());
				int lCount;
				byte lTabData[] = new byte[BUFFER];
				// write the files to the disk
				String lDirectory = pFile.getName().substring(0,
						pFile.getName().length() - 4);
				lFileDirectory = new File(pTargetDirectory + File.separator
						+ lDirectory);
				if (!lFileDirectory.exists()) {
					lFileDirectory.mkdirs();
				}

				FileOutputStream lFos = new FileOutputStream(pTargetDirectory
						+ File.separator + lDirectory + File.separator
						+ lEntry.getName());
				lDest = new BufferedOutputStream(lFos, BUFFER);
				while ((lCount = lZis.read(lTabData, 0, BUFFER)) != -1) {
					lDest.write(lTabData, 0, lCount);
				}
				lDest.flush();
				lDest.close();
			}
			lZis.close();
		} catch (Exception e) {
			Logger.error("Zip INPN : " + e.toString());
		} finally {
			if (lZis != null) {
				try {
					lZis.close();
				} catch (IOException e) {
					Logger.error("Zip INPN : " + e.toString());
				}
			}
		}
		return lFileDirectory;
	}
}
