package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.persistence.TypedQuery;

import models.DataPublisher;
import models.Dataset;
import models.Occurrence;
import models.harvest.Harvester;
import models.harvest.inpn.Inpn;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.jobs.Job;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Datasets extends Controller {

	//
	public static String targetDirectory = Play.configuration
			.getProperty("temp.path");

	public static String datasetDirectory = Play.configuration
			.getProperty("dataset.path");
	// Informs if a harvest job is already running. if yes, the user will not be
	// able to start a new one until the end of this
	public static boolean workInProgress = false;

	public final static String SEPARATOR = ";";

	/*
	 * Renders the available datasets
	 */
	public static void list(String name) {
		List<DataPublishers> dataPublishers = null;
		if (name != null && !name.isEmpty()) {
			dataPublishers = DataPublisher
					.find("name like ?", "%" + name + "%").fetch();
		} else {
			dataPublishers = DataPublisher.all().fetch();
		}

		render(dataPublishers, workInProgress, name);
	}

	/**
	 * Renders upload of TaxRef
	 * 
	 * @param id
	 *            id of the dataset
	 */
	public static void result(long id) {
		Dataset dataset = Dataset.findById(id);
		render(dataset);
	}

	/**
	 * allow to add a dataset
	 */
	@Check("publisher")
	public static void add() {
		List<DataPublisher> datapublishers = DataPublisher.all().fetch();
		render(datapublishers);
	}

	/**
	 * Save a new dataset
	 * 
	 * @param name
	 *            name of the new dataset
	 * @param url
	 *            url of the new dataset
	 * @param type
	 *            type of the new dataset
	 * @param dataPublisherId
	 *            id data publisher of the new dataset
	 * @param fromOutside
	 *            fromOutside of the new dataset
	 */
	@Transactional
	@Check("publisher")
	public static void save(
			@Required(message = "Name is required") String name,
			String url,
			File file,
			File fileCommune,
			File fileMaille,
			File fileENP,
			File fileShape,
			@Required(message = "Type is required") String type,
			@Required(message = "You need to select a data publisher") Long dataPublisherId,
			boolean fromOutside) {
		if (validation.hasErrors() && (url == null || file == null)) {
			// validation.addError(url, "Access point or file is required");
			if (url == null && file == null) {
				validation.required(url);
			}
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			add();
		} else {
			Dataset dataset = new Dataset(name, url, type,
					(DataPublisher) DataPublisher.findById(dataPublisherId));
			dataset.fromOutside = fromOutside;
			dataset.save();
			File lFileTo = null;
			if (file != null) {
				File lDirectory = new File(datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator);
				if (!lDirectory.exists()) {
					lDirectory.mkdirs();
				}
				lFileTo = new File(datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator
						+ file.getName());
				Files.copy(file, lFileTo);

				dataset.fileDataset = datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator
						+ file.getName();
				dataset.save();
			}
			if (type.equals("inpn")) {
				if (fileCommune != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileCommune.getName());
					Files.copy(fileCommune, lFileTo);
					dataset.fileCommune = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileCommune.getName();
				}
				if (fileMaille != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileMaille.getName());
					Files.copy(fileMaille, lFileTo);
					dataset.fileMaille = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileMaille.getName();
				}
				if (fileENP != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileENP.getName());
					dataset.fileENP = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileENP.getName();
					Files.copy(fileENP, lFileTo);
				}
				if (fileShape != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileShape.getName());
					dataset.fileShape = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileShape.getName();
					Files.copy(fileShape, lFileTo);
				}
			}
			list(null);
		}
	}

	/**
	 * Allow to edit a dataset
	 * 
	 * @param id
	 *            id of the dataset
	 */
	@Check("publisher")
	public static void edit(long id) {
		Dataset dataset = Dataset.findById(id);
		List<DataPublisher> datapublishers = DataPublisher.all().fetch();
		render(dataset, datapublishers);
	}

	/**
	 * Sauvegarde un dataset
	 * 
	 * @param id
	 *            id of the dataset
	 * @param name
	 *            name of the dataset
	 * @param url
	 *            url of the dataset
	 * @param type
	 *            type of the dataset
	 * @param dataPublisherId
	 *            id data publisher of the dataset
	 * @param fromOutside
	 *            fromOutside of the dataset
	 */
	@Transactional
	@Check("publisher")
	public static void editSave(
			long id,
			@Required(message = "Name is required") String name,
			String url,
			File file,
			File fileCommune,
			File fileMaille,
			File fileENP,
			File fileShape,
			@Required(message = "Type is required") String type,
			@Required(message = "You need to select a data publisher") Long dataPublisherId,
			boolean fromOutside) {
		if (validation.hasErrors()) {
			if (url == null && file == null) {
				validation.required(url);
			}
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			edit(id);
		} else {
			Dataset dataset = Dataset.findById(id);
			dataset.name = name;
			dataset.url = url;
			dataset.type = type;
			dataset.dataPublisher = DataPublisher.findById(dataPublisherId);
			dataset.fromOutside = fromOutside;
			File lFileTo = null;
			if (file != null) {
				// On supprime les anciens fichiers
				deleteAll(new File(datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator));

				File lDirectory = new File(datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator);
				if (!lDirectory.exists()) {
					lDirectory.mkdirs();
				}
				// On met le nouveau fichier
				lFileTo = new File(datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator
						+ file.getName());
				Files.copy(file, lFileTo);

				// On set le nouveau lien du fichier
				dataset.fileDataset = datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator
						+ file.getName();

			} else if (dataset.url != null && !dataset.url.isEmpty()) {
				// Si l'utilisateur souhaite utiliser une url
				deleteAll(new File(datasetDirectory + File.separator
						+ "resource-" + dataset.id + File.separator));
				dataset.fileDataset = null;
			}
			if (type.equals("inpn")) {
				if (fileCommune != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileCommune.getName());
					Files.copy(fileCommune, lFileTo);
					dataset.fileCommune = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileCommune.getName();
				}
				if (fileMaille != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileMaille.getName());
					Files.copy(fileMaille, lFileTo);
					dataset.fileMaille = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileMaille.getName();
				}
				if (fileENP != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileENP.getName());
					dataset.fileENP = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileENP.getName();
					Files.copy(fileENP, lFileTo);
				}
				if (fileShape != null) {
					lFileTo = new File(datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileShape.getName());
					dataset.fileShape = datasetDirectory + File.separator
							+ "resource-" + dataset.id + File.separator
							+ fileShape.getName();
					Files.copy(fileShape, lFileTo);
				}
			}
			dataset.save();
			list(null);
		}
	}

	/**
	 * Deletes the given dataset
	 * 
	 * @param id
	 *            id of the dataset
	 */
	@Check("publisher")
	public static void delete(Long id) {
		Dataset dataset = Dataset.findById(id);
		Harvester.deleteTemporaryDirectory(dataset.tempDirectory, null);
		dataset.delete();
		list(null);
	}

	/*
	 * Harvests the dataset related to the given id. If begin and end are given,
	 * makes a partial harvest
	 */
	@Check("publisher")
	public static void harvest(Long id, String begin, String end) {
		Dataset dataset = Dataset.findById(id);
		new HarvestJob(dataset, begin, end).doJob();
	}

	/**
	 * Call the appropriate Harvester for the given dataset. Temporary data will
	 * be stored in the targetDirectory
	 */
	public static class HarvestJob extends Job {
		private Dataset dataset;
		private String targetDirectory;
		private String begin;
		private String end;

		public HarvestJob(Dataset dataset, String begin, String end) {
			this.dataset = dataset;
			this.targetDirectory = Datasets.targetDirectory;
			this.begin = begin;
			this.end = end;
		}

		@Override
		public void doJob() {
			workInProgress = true;
			Harvester app = new Harvester();
			if (dataset.type.equals("ipt")) {
				app = new models.harvest.ipt.Harvester(dataset, targetDirectory);
			} else if (dataset.type.equals("biocase")
					|| dataset.type.equals("tapir")
					|| dataset.type.equals("digir")) {
				app = new Harvester(dataset, targetDirectory, begin, end);
			} else if (dataset.type.equals("inpn")) {
				app = new Inpn(dataset, targetDirectory);

			} else {
				Logger.error("Bad dataset type, cannot start the harvesting process");
			}
			// If a range error is thrown the dataset is marked as harvested
			// with error
			if (app.withErrors) {
				Logger.info("Harvest with error, process to a data cleaning from "
						+ app.currentLower);
				dataset.status = "HARVESTED_WITH_ERROR";
				dataset.currentLower = app.currentLower;
				// app.deleteTemporaryDirectory(app.dataset.tempDirectory,
				// app.currentLower);
				// app.deleteOccurrencesFrom(app.dataset, app.currentLower);
			} else
				dataset.markDataset("SUCCESSFULLY_HARVESTED");
			dataset.save();
			workInProgress = false;
			list(null);
		}
	}

	/**
	 * Export a file for TaxRef
	 * 
	 * @param id
	 *            id of the dataset
	 */
	@Check("publisher")
	public static void export(Long id) {

		Dataset data = Dataset.findById(id);

		File lFile;
		FileWriter lWriter = null;
		FileInputStream lIn = null;
		try {
			lFile = File.createTempFile("taxrefmatch", ".csv");

			lWriter = new FileWriter(lFile);
			lWriter.append("nom_complet");
			lWriter.append(';');
			lWriter.append("classification");
			lWriter.append(';');
			lWriter.append("fk");
			lWriter.append('\n');
			for (Occurrence lOccurrence : data.occurrences) {
				if (data.type.equals("inpn")) {
					lWriter.append(lOccurrence.nomScientifiqueCite);
				} else {
					lWriter.append(lOccurrence.scientificName);
				}

				lWriter.append(";;");
				lWriter.append(lOccurrence.taxonID == null ? ""
						: lOccurrence.taxonID);
				lWriter.append('\n');
			}
			lWriter.flush();
			lWriter.close();

			response.setContentTypeIfNotSet("application/x-download");
			lIn = new FileInputStream(lFile);
			renderBinary(lIn, lFile.getName());
		} catch (IOException e) {
			Logger.error(e.toString(), "Taxref export");
		} finally {
			try {
				if (lWriter != null) {
					lWriter.flush();
					lWriter.close();
				}
				if (lIn != null) {
					lIn.close();
				}
			} catch (IOException e) {
				Logger.error(e.toString(), "Taxref export");
			}
		}
	}

	/**
	 * Upload a file to reconcile
	 * 
	 * @param id
	 *            id of the dataset
	 * @param attachment
	 *            file
	 */
	@Transactional
	@Check("publisher")
	public static void uploadResult(
			@Required(message = "Dataset is required") Long id,
			@Required(message = "File is required") File attachment) {
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			result(id);
		} else {
			FileReader lReader = null;
			BufferedReader lBr = null;
			try {
				// Open the file to read it
				lReader = new FileReader(attachment);
				lBr = new BufferedReader(lReader);

				int lNb = 1;
				// we loop on the file
				for (String line = lBr.readLine(); line != null; line = lBr
						.readLine()) {
					if (lNb <= 1) {
						String[] oneData = line.split(SEPARATOR);
						// With the fk find the good occurence
						TypedQuery<Occurrence> query = null;
						if (oneData[2] != null && !oneData[2].isEmpty()) {
							query = JPA
									.em()
									.createQuery(
											"select o from Occurrence join o.dataset d where o.taxonID=? and d.id=?",
											Occurrence.class);
							query.setParameter(1, oneData[2]);
							query.setParameter(2, id);
						} else {
							query = JPA
									.em()
									.createQuery(
											"select o from Occurrence join o.dataset d where o.nomScientifiqueCite=? and d.id=?",
											Occurrence.class);
							query.setParameter(1, oneData[0]);
							query.setParameter(2, id);
						}
						Occurrence lOccurrence = query.getSingleResult();
						if (oneData[0] != null && !oneData[0].isEmpty()) {
							lOccurrence.cdNom = oneData[3];
						}
						lOccurrence.save();
					}
					lNb++;
				}
				lBr.close();
				lReader.close();

			} catch (FileNotFoundException e) {
				try {
					if (lReader != null) {
						lReader.close();
					}
					if (lBr != null) {
						lBr.close();
					}
					Logger.error(e.toString(), "taxref import");
				} catch (IOException e1) {
					Logger.error(e1.toString(), "taxref import");
				}
			} catch (IOException e) {
				try {
					if (lReader != null) {
						lReader.close();
					}
					if (lBr != null) {
						lBr.close();
					}
					Logger.error(e.toString(), "taxref import");
				} catch (IOException e1) {
					Logger.error(e1.toString(), "taxref import");
				}
			}
			list(null);
		}

	}

	private static boolean deleteAll(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteAll(children[i]);
				if (!success)
					return false;
			}
		}
		return dir.delete();
	}
}
