package controllers;

import java.util.ArrayList;
import java.util.List;

import manager.ControlMG;
import manager.FieldMG;
import manager.job.BoudaryJob;
import manager.job.DoublonJob;
import manager.job.GeographicalJob;
import manager.job.LocalisationTaxonJob;
import manager.job.RefGeoAdminJob;
import manager.job.RegexJob;
import manager.job.RequireJob;
import manager.job.TaxonJob;
import manager.job.ValueJob;
import models.Controls;
import models.Dataset;
import models.DatasetType;
import models.Field;
import models.Occurrence;
import models.ValidationType;
import play.data.validation.Error;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation.ValidationResult;
import play.db.jpa.Transactional;
import play.modules.paginate.ModelPaginator;
import play.mvc.Controller;
import play.mvc.With;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@With(Secure.class)
public class Control extends Controller {

	private static ControlMG mControlMG = new ControlMG();

	private static FieldMG mFieldMG = new FieldMG();

	public static Long mDatasetTypeId = null;

	/**
	 * Render a list of controls
	 * 
	 * @param nomChamp
	 *            String
	 * @param order
	 *            String
	 * @param name
	 *            String
	 */
	@Check("admin")
	public static void list(String nomChamp, String order, String name) {
		ModelPaginator<Controls> paginator = null;
		if (name != null && !name.isEmpty()) {
			paginator = new ModelPaginator<Controls>(Controls.class,
					"name like ?", "%" + name + "%");
		} else {
			paginator = new ModelPaginator(Controls.class);
		}
		paginator.setPageSize(25);
		if (nomChamp != null && !nomChamp.isEmpty()) {
			if (order != null && !order.isEmpty()) {
				paginator.orderBy(nomChamp + " " + order);
			}
		}

		render(paginator, nomChamp, order, name);
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
		if (mDatasetTypeId != null) {
			List<Field> fields = mFieldMG
					.listFieldByDatasetType(mDatasetTypeId);
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
			case 3:
				new RefGeoAdminJob(lControl, idDataset).now();
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
			case 7:
				new GeographicalJob(lControl, idDataset).now();
				break;
			case 8:
				new BoudaryJob(lControl, idDataset).now();
				break;
			case 9:
				new LocalisationTaxonJob(lControl, idDataset).now();
				break;
			}

			flash.success("Success start of the control");
			Datasets.list(null);
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
	 * @param codeTerritoire
	 *            territoire of the control if control is taxo
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
			String boudaryLower,
			String boudaryHigher,
			String value,
			String regex,
			long taxoId,
			long refGeoId,
			String codeTerritoire,
			@Required(message = "Field for control is required") long[] fieldIdTab) {
		if (typeId != null) {
			if (typeId == 9L) {
				Error error = validation.required(codeTerritoire).error;
				if (error != null) {
					error.message();
				}
			}
			if (typeId == 5L) {
				Error error = validation.required(value).error;
				if (error != null) {
					error.message();
				}
			}
			if (typeId == 3L) {
				Error error = validation.min(refGeoId, 1L).error;
				if (error != null) {
					validation.addError("refGeoId", "Type of georgaphical referentiel is required");
				}
			}
			if (typeId == 2L) {
				Error error = validation.min(taxoId, 1L).error;
				if (error != null) {
					validation.addError("taxoId", "Territory is required");
				}
			}
			if (typeId == 8L) {
				Error error = validation.required(boudaryLower).error;
				Error error2 = validation.required(boudaryHigher).error;
				if (error != null) {
					error.message();
				}
				if (error2 != null) {
					error2.message();
				}
			}
			if (typeId == 6L) {
				Error error = validation.required(regex).error;
				if (error != null) {
					error.message();
				}
			}
		}
		if (validation.hasErrors()) {
			mDatasetTypeId = datasetTypeId;
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			add();
		} else {
			ValidationType lTypeValidation = ValidationType.findById(typeId);
			DatasetType lDatasetType = DatasetType.findById(datasetTypeId);
			Controls lControl = new Controls(name, lTypeValidation,
					description, boudaryLower, boudaryHigher, value, regex,
					lDatasetType, taxoId, refGeoId, codeTerritoire);
			List<Field> fields = new ArrayList<Field>();
			for (long lFieldId : fieldIdTab) {
				Field field = Field.findById(lFieldId);
				fields.add(field);
			}
			lControl.fields = fields;
			lControl.save();
			mDatasetTypeId = null;
			list(null, null, null);
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
	 *            fieldIdTab is a tab of field of the control
	 */
	@Check("admin")
	@Transactional
	public static void editSave(
			long id,
			@Required(message = "Name is required") String name,
			@Required(message = "Description is required") String description,
			@Required(message = "Validation type is required") Long typeId,
			@Required(message = "Type of dataset is required") Long datasetTypeId,
			String boudaryLower,
			String boudaryHigher,
			String value,
			String regex,
			long taxoId,
			long refGeoId,
			String codeTerritoire,
			@Required(message = "Field for control is required") long[] fieldIdTab) {
		if (typeId != null) {
			if (typeId == 9L) {
				Error error = validation.required(codeTerritoire).error;
				if (error != null) {
					error.message();
				}
			}
			if (typeId == 5L) {
				Error error = validation.required(value).error;
				if (error != null) {
					error.message();
				}
			}
			if (typeId == 3L) {
				Error error = validation.min(refGeoId, 1L).error;
				if (error != null) {
					validation.addError("refGeoId", "Type of georgaphical referentiel is required");
				}
			}
			if (typeId == 2L) {
				Error error = validation.min(taxoId, 1L).error;
				if (error != null) {
					validation.addError("taxoId", "Territory is required");
				}
			}
			if (typeId == 8L) {
				Error error = validation.required(boudaryLower).error;
				Error error2 = validation.required(boudaryHigher).error;
				if (error != null) {
					error.message();
				}
				if (error2 != null) {
					error2.message();
				}
			}
			if (typeId == 6L) {
				Error error = validation.required(regex).error;
				if (error != null) {
					error.message();
				}
			}
		}
		if (validation.hasErrors()) {
			mDatasetTypeId = datasetTypeId;
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			edit(id);
		} else {
			ValidationType lTypeValidation = ValidationType.findById(typeId);
			DatasetType lDatasetType = DatasetType.findById(datasetTypeId);
			Controls lControl = Controls.findById(id);
			lControl.name = name;
			lControl.validationType = lTypeValidation;
			lControl.datasetType = lDatasetType;
			lControl.regex = regex;
			lControl.boudaryHigher = boudaryHigher;
			lControl.boudaryLower = boudaryLower;
			lControl.description = description;
			lControl.taxoId = taxoId;
			lControl.refGeoAdmID = refGeoId;
			lControl.codeTerritoire = codeTerritoire;
			List<Field> fields = new ArrayList<Field>();
			for (long lFieldId : fieldIdTab) {
				Field field = Field.findById(lFieldId);
				fields.add(field);
			}
			lControl.fields = fields;
			lControl.save();
			mDatasetTypeId = null;
			list(null, null, null);
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
		list(null, null, null);
	}

}
