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
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Datasets extends Controller {

	//
	public static String targetDirectory = Play.configuration
			.getProperty("temp.path");
	// Informs if a harvest job is already running. if yes, the user will not be
	// able to start a new one until the end of this
	public static boolean workInProgress = false;

	public final static String SEPARATOR = ";";

	/*
	 * Renders the available datasets
	 */
	public static void list() {
		List<DataPublishers> dataPublishers = DataPublisher.all().fetch();
		render(dataPublishers, workInProgress);
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
			@Required(message = "Access point is required") String url,
			@Required(message = "Type is required") String type,
			@Required(message = "You need to select a data publisher") Long dataPublisherId,
			boolean fromOutside) {
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			add();
		} else {
			Dataset dataset = new Dataset(name, url, type,
					(DataPublisher) DataPublisher.findById(dataPublisherId));
			dataset.fromOutside = fromOutside;
			dataset.save();
			list();
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
			@Required(message = "Access point is required") String url,
			@Required(message = "Type is required") String type,
			@Required(message = "You need to select a data publisher") Long dataPublisherId,
			boolean fromOutside) {
		if (validation.hasErrors()) {
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
			dataset.save();
			list();
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
		list();
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
			list();
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
				lWriter.append(lOccurrence.scientificName);
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
						TypedQuery<Occurrence> query = JPA
								.em()
								.createQuery(
										"select o from Occurrence join o.dataset d where o.taxonID=? and d.id=?",
										Occurrence.class);
						query.setParameter(1, oneData[2]);
						query.setParameter(2, id);
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
			list();
		}

	}
}
