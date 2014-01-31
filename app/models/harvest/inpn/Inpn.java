package models.harvest.inpn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.TypedQuery;

import play.Logger;
import play.db.jpa.JPA;
import models.Dataset;
import models.Occurrence;
import models.harvest.Harvester;

public class Inpn extends Harvester {

	public Dataset mDataset;

	public String mRessourceDirectory;

	final static int BUFFER = 2048;

	final static String SEPARATOR = ";";

	/**
	 * Constructeur
	 * 
	 * @param pDataset
	 * @param pTargetDirectory
	 */
	public Inpn(Dataset pDataset, String pTargetDirectory) {
		Logger.info("INPN Harvester is started. Dataset = " + dataset.name);
		this.mDataset = pDataset;
		this.mRessourceDirectory = targetDirectory + File.separator
				+ "resource-" + dataset.id;
		File lF = new File(targetDirectory + File.separator + "resource-"
				+ dataset.id);
		if (!lF.exists()) {
			lF.mkdirs();
		}
		this.dataset.tempDirectory = mRessourceDirectory;

		run();
	}

	/**
	 * 
	 * Realise l'import INPN en base de données
	 * 
	 */
	public void run() {
		FileReader lReader = null;
		BufferedReader lBr = null;
		try {
			// retrieves the archive and downloads it
			File lTargetDirectoryFile = new File(mRessourceDirectory);
			File lFileName = downloadFile(this.dataset.url,
					lTargetDirectoryFile);
			Logger.info("Archive successfully downloaded");
			// extracts the archive
			File lFileDirectory = extractFile(lFileName, mRessourceDirectory);
			Logger.info("Archive successfully extracted");

			// Commune
			// Open the file to read it
			lReader = new FileReader(lFileDirectory + "StINPN_commune.csv");
			lBr = new BufferedReader(lReader);

			Map<String, String[]> lMapsCommune = new HashMap<String, String[]>();

			int lNb = 0;
			// we loop on the file
			for (String line = lBr.readLine(); line != null; line = lBr
					.readLine()) {
				if (lNb <= 1) {
					String[] oneData = line.split(SEPARATOR);
					lMapsCommune.put(oneData[0], oneData);
				}
			}

			lReader.close();
			lBr.close();

			// File Maille
			// Open the file to read it
			lReader = new FileReader(lFileDirectory + "StINPN_maille.csv");
			lBr = new BufferedReader(lReader);

			Map<String, String[]> lMapsMaille = new HashMap<String, String[]>();

			lNb = 0;
			// we loop on the file
			for (String line = lBr.readLine(); line != null; line = lBr
					.readLine()) {
				if (lNb <= 1) {
					String[] oneData = line.split(SEPARATOR);
					lMapsMaille.put(oneData[0], oneData);
				}
			}

			lReader.close();
			lBr.close();

			// File ENP
			// Open the file to read it
			lReader = new FileReader(lFileDirectory + "StINPN_ENP.csv");
			lBr = new BufferedReader(lReader);

			Map<String, String[]> lMapsENP = new HashMap<String, String[]>();

			lNb = 0;
			// we loop on the file
			for (String line = lBr.readLine(); line != null; line = lBr
					.readLine()) {
				if (lNb <= 1) {
					String[] oneData = line.split(SEPARATOR);
					lMapsENP.put(oneData[0], oneData);
				}
			}

			lReader.close();
			lBr.close();

			// File Principal
			// Open the file to read it
			lReader = new FileReader(lFileDirectory + "StINPN_principalv1.csv");
			lBr = new BufferedReader(lReader);

			lNb = 1;

			List<Occurrence> lListOccurrences = new ArrayList<Occurrence>();

			// we loop on the file
			for (String line = lBr.readLine(); line != null; line = lBr
					.readLine()) {
				if (lNb <= 1) {
					String[] oneData = line.split(SEPARATOR);

					Occurrence lOccurrence = new Occurrence();
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
					String[] lValueEnp = lMapsENP.get(oneData[7]);
					lOccurrence.typeEN = lValueEnp[1];
					lOccurrence.codeEN = lValueEnp[2];
					lOccurrence.pourcentageEN = lValueEnp[3];

					// Commune
					String[] lValueCommune = lMapsCommune.get(oneData[7]);
					lOccurrence.codeInsee = lValueCommune[1];
					lOccurrence.nomCommune = lValueCommune[2];
					lOccurrence.pourcentageCommune = lValueCommune[3];

					// Maille
					String[] lValueMaille = lMapsMaille.get(oneData[7]);
					lOccurrence.codeMaille = lValueMaille[1];
					lOccurrence.pourcentageMaille = lValueMaille[2];

					lListOccurrences.add(lOccurrence);
				}
				lNb++;
			}
			lBr.close();
			lReader.close();

			dataset.occurrences = lListOccurrences;

			dataset.save();

		} catch (Exception e) {
			this.withErrors = true;
			Logger.error(e.toString(), "Inport INPN");
		} finally {
			try {
				if (lBr != null) {
					lBr.close();
				}
				if (lReader != null) {
					lReader.close();
				}
			} catch (IOException e) {
				Logger.error(e.toString(), "Inport INPN");
			}
		}

	}

	/**
	 * 
	 * Downloads the archive
	 * 
	 * @param pAddress
	 * @param pDest
	 * @return File
	 */
	public static File downloadFile(String pAddress, File pDest) {
		BufferedReader lReader = null;
		FileOutputStream lFos = null;
		InputStream lIn = null;
		String lFileName = null;
		try {
			// Connection initialization
			URL lUrl = new URL(pAddress);
			URLConnection lConn = lUrl.openConnection();
			Logger.info("Connection to the URL " + lUrl);

			String FileType = lConn.getContentType();
			Logger.info("Type File : " + FileType);

			// Response Reader
			lIn = lConn.getInputStream();
			lReader = new BufferedReader(new InputStreamReader(lIn));

			File lFile = new File(lUrl.getPath());
			lFileName = lFile.getName();

			lFileName = pDest + File.separator + lFileName;
			pDest = new File(lFileName);

			lFos = new FileOutputStream(pDest);
			byte[] lBuff = new byte[1024];
			int l = lIn.read(lBuff);
			while (l > 0) {
				lFos.write(lBuff, 0, l);
				l = lIn.read(lBuff);
			}
		} catch (Exception e) {
			Logger.error(e.toString(), "Download INPN");
		} finally {
			try {
				lFos.flush();
				lFos.close();
			} catch (IOException e) {
				Logger.error(e.toString(), "Download INPN");
			}
			try {
				lReader.close();
			} catch (Exception e) {
				Logger.error(e.toString(), "Download INPN");
			}
		}
		Logger.info("Destination: " + lFileName.toString());
		return pDest;
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
			Logger.error(e.toString(), "Zip INPN");
		} finally {
			if (lZis != null) {
				try {
					lZis.close();
				} catch (IOException e) {
					Logger.error(e.toString(), "Zip INPN");
				}
			}
		}
		return lFileDirectory;
	}
}
