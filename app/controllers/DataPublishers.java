package controllers;

import groovy.sql.DataSet;

import java.util.List;

import models.DataPublisher;
import models.Dataset;
import models.harvest.Harvester;
import play.data.validation.Required;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class DataPublishers extends Controller {

	/**
	 * Retrieve the data publisher list
	 */
	public static void list() {
		List<DataPublishers> dataPublishers = DataPublisher.all().fetch();
		render(dataPublishers);
	}

	/**
	 * Delete the given dataset
	 * 
	 * @param id
	 *            id of the datapublisher to delete
	 */
	@Transactional
	public static void delete(Long id) {
		DataPublisher dataPublisher = DataPublisher.findById(id);
		if (dataPublisher.datasets != null) {
			for (Dataset dataset : dataPublisher.datasets) {
				Harvester.deleteTemporaryDirectory(dataset.tempDirectory, null);
			}
		}
		dataPublisher.delete();
		list();
	}

	/**
	 * Init the form for adding a data publisher
	 */
	public static void add() {
		render();
	}

	/**
	 * Save a new data publisher
	 * 
	 * @param name
	 *            name of the new data publisher
	 * @param description
	 *            desciption of the new data publisher
	 * @param administrativeContact
	 *            administrative contact of the new data publisher
	 * @param technicalContact
	 *            technical contact of the new data publisher
	 */
	@Transactional
	public static void save(
			@Required(message = "Name is required") String name,
			@Required(message = "A description is required") String description,
			@Required(message = "Administrative contact is required") String administrativeContact,
			@Required(message = "Technical contact is required") String technicalContact) {
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			add();
		} else {
			DataPublisher dataPublisher = new DataPublisher(name, description,
					administrativeContact, technicalContact);
			dataPublisher.save();
			Application.index();
		}
	}

	/**
	 * Edit the given dataset.
	 * 
	 * @param id
	 *            id of the datapublisher to edit
	 */
	public static void edit(long id) {
		DataPublisher dataPublisher = DataPublisher.findById(id);
		render(dataPublisher);
	}

	/**
	 * Sauvegarde un fournisseur de données.
	 * @param id identifiant du fournisseur de données
	 * @param name nom du fournisseur de données
	 * @param description description du fournisseur de données
	 * @param administrativeContact contact administratif du fournisseur de données
	 * @param technicalContact contact technique du fournisseur de données
	 */
	@Transactional
	public static void editSave(
			long id,
			@Required(message = "Name is required") String name,
			@Required(message = "A description is required") String description,
			@Required(message = "Administrative contact is required") String administrativeContact,
			@Required(message = "Technical contact is required") String technicalContact) {
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			edit(id);
		} else {
			DataPublisher dataPublisher = DataPublisher.findById(id);
			dataPublisher.name = name;
			dataPublisher.description = description;
			dataPublisher.administrativeContact = administrativeContact;
			dataPublisher.technicalContact = technicalContact;
			dataPublisher.save();
			list();
		}
	}

}
