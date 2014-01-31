package controllers;

import java.util.ArrayList;
import java.util.List;

import manager.ControlMG;
import manager.FieldMG;
import manager.job.BoudaryJob;
import manager.job.DoublonJob;
import manager.job.RegexJob;
import manager.job.RequireJob;
import manager.job.TaxonJob;
import manager.job.ValueJob;
import models.Controls;
import models.Dataset;
import models.DatasetType;
import models.Field;
import models.ValidationType;
import play.data.validation.Required;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.With;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@With(Secure.class)
public class Control extends Controller {

	private static ControlMG mControlMG = new ControlMG();

	private static FieldMG mFieldMG = new FieldMG();

	public static Long datasetTypeId = null;

	@Check("admin")
	public static void list() {
		List<Controls> controls = Controls.all().fetch();
		render(controls);
	}

	/**
	 * Function that returns a Json object (Field) in table
	 * 
	 * @param id
	 *            id of the datasetType
	 */
	public static void listField(long id) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		List<Field> fields = mFieldMG.listFieldByDatasetType(id);
		renderJSON(gson.toJson(fields));
	}

	/**
	 * Function that returns a Json object (Controls) in table
	 * 
	 * @param idDataset
	 *            id of the datasetType
	 * @param idValidationType
	 *            id of the type of validation
	 */
	public static void listControls(long idDataset, Long idValidationType) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		Dataset dataset = Dataset.findById(idDataset);
		List<Controls> controls = mControlMG
				.listControlsByDatasetTypeValidationType(dataset.type,
						idValidationType);
		renderJSON(gson.toJson(controls));
	}

	/**
	 * Init the form for adding a control
	 */
	@Check("admin")
	public static void add() {
		List<ValidationType> typeValidations = ValidationType.all().fetch();
		List<DatasetType> datasetTypes = DatasetType.all().fetch();
		if (datasetTypeId != null) {
			List<Field> fields = mFieldMG.listFieldByDatasetType(datasetTypeId);
			render(typeValidations, datasetTypes, fields);
		} else {
			render(typeValidations, datasetTypes);
		}
	}

	/**
	 * Init the form for editing a control
	 * 
	 * @param id
	 *            id of the control
	 */
	@Check("admin")
	public static void edit(long id) {
		List<ValidationType> typeValidations = ValidationType.all().fetch();
		Controls control = Controls.findById(id);
		List<DatasetType> datasetTypes = DatasetType.all().fetch();
		render(typeValidations, control, datasetTypes);
	}

	/**
	 * Init the form for launch a control
	 * 
	 * @param idDataset
	 *            id of the dataset
	 */
	@Check("publisher")
	public static void control(long idDataset) {
		Dataset dataset = Dataset.findById(idDataset);
		List<ValidationType> typeValidations = ValidationType.all().fetch();
		render(typeValidations, dataset);
	}

	/**
	 * Permet le lancement des controls sur un dataset ces controls sont lancés
	 * dans un thread séparé pour eviter de bloqué l'application
	 * 
	 * @param idControl
	 *            id of the control
	 * @param idDataset
	 *            id of the dataset
	 */
	@Check("publisher")
	public static void launchControl(
			@Required(message = "Control is required") Long idControl,
			long idDataset) {
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			control(idDataset);
		} else {

			Controls lControl = Controls.findById(idControl);
			switch (lControl.validationType.id.intValue()) {
			case 1:
				new DoublonJob(lControl, idDataset).now();
				break;
			case 2:
				new TaxonJob(lControl, idDataset).now();
				break;
			case 4:
				new RequireJob(lControl, idDataset).now();
				break;
			case 5:
				new ValueJob(lControl, idDataset).now();
				break;
			case 6:
				new RegexJob(lControl, idDataset).now();
				break;
			case 8:
				new BoudaryJob(lControl, idDataset).now();
				break;
			}

			flash.success("Success start of the control");
			Datasets.list();
		}
	}

	/**
	 * Save a new control
	 * 
	 * @param name
	 *            name of the new control
	 * @param description
	 *            description of the new control
	 * @param typeId
	 *            type of the new control
	 * @param datasetTypeId
	 *            datasetType of the new control
	 * @param boudaryLower
	 *            boudaryLower of the new control
	 * @param boudaryHigher
	 *            boudaryHigher of the new control
	 * @param value
	 *            value of the new control
	 * @param regex
	 *            regex of the new control
	 * @param fieldIdTab
	 *            fieldIdTab is a tab of field of the new control
	 */
	@Transactional
	@Check("admin")
	public static void save(
			@Required(message = "Name is required") String name,
			@Required(message = "Description is required") String description,
			@Required(message = "Validation type is required") Long typeId,
			@Required(message = "Type of dataset is required") Long datasetTypeId,
			String boudaryLower, String boudaryHigher, String value,
			String regex, long[] fieldIdTab) {

		if (validation.hasErrors()) {
			Control.datasetTypeId = datasetTypeId;
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			add();
		} else {
			ValidationType lTypeValidation = ValidationType.findById(typeId);
			DatasetType lDatasetType = DatasetType.findById(datasetTypeId);
			Controls lControl = new Controls(name, lTypeValidation,
					description, boudaryLower, boudaryHigher, value, regex,
					lDatasetType);
			List<Field> fields = new ArrayList<Field>();
			for (long lFieldId : fieldIdTab) {
				Field field = Field.findById(lFieldId);
				fields.add(field);
			}
			lControl.fields = fields;
			lControl.save();
			Control.datasetTypeId = null;
			list();
		}

	}

	/**
	 * Sauvegarde un controle
	 * 
	 * @param id
	 *            id of the control
	 * @param name
	 *            name of the control
	 * @param description
	 *            description of the control
	 * @param typeId
	 *            type of the control
	 * @param datasetTypeId
	 *            datasetType of the control
	 * @param boudaryLower
	 *            boudaryLower of the control
	 * @param boudaryHigher
	 *            boudaryHigher of the control
	 * @param value
	 *            value of the control
	 * @param regex
	 *            regex of the control
	 * @param fieldIdTab
	 *            fieldIdTab is a tab of field of the new control
	 */
	@Check("admin")
	public static void editSave(
			long id,
			@Required(message = "Name is required") String name,
			@Required(message = "Description is required") String description,
			@Required(message = "Validation type is required") Long typeId,
			@Required(message = "Type of dataset is required") Long datasetTypeId,
			String boudaryLower, String boudaryHigher, String value,
			String regex, long[] fieldIdTab) {
		if (validation.hasErrors()) {
			Control.datasetTypeId = datasetTypeId;
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			edit(id);
		} else {
			ValidationType lTypeValidation = ValidationType.findById(typeId);
			Controls lControl = Controls.findById(id);
			lControl.name = name;
			lControl.validationType = lTypeValidation;
			lControl.regex = regex;
			lControl.boudaryHigher = boudaryHigher;
			lControl.boudaryLower = boudaryLower;
			lControl.description = description;
			lControl.save();
			Control.datasetTypeId = null;
			list();
		}
	}

	/**
	 * Deletes the given controls
	 * 
	 * @param id
	 *            id of the control
	 */
	@Transactional
	@Check("admin")
	public static void delete(Long id) {
		Controls lControl = Controls.findById(id);
		lControl.delete();
		list();
	}

}
