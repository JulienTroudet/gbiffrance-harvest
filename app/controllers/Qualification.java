package controllers;

import java.util.List;

import manager.ControlMG;
import manager.OccurrenceMG;
import models.Controls;
import models.Dataset;
import models.Occurrence;
import play.db.jpa.Transactional;
import play.modules.paginate.ModelPaginator;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Qualification extends Controller {

	private static ControlMG mControlMG = new ControlMG();

	/**
	 * List the occurrences of the given dataset for qualification.
	 * 
	 * @param id
	 *            id of the dataset
	 * @param page
	 * @param pOrder
	 * @param pNameField
	 */
	@Check("publisher")
	public static void list(long id, Integer page, String pOrder,
			String pNameField) {
		Dataset dataset = Dataset.findById(id);

		List<Controls> controls = mControlMG.listControlsDatasetType(id);

		ModelPaginator paginator = new ModelPaginator(Occurrence.class,
				"dataset.id=?", id);
		paginator.setPageSize(25);
		if (pNameField != null && !pNameField.isEmpty()) {
			if (pOrder != null && !pOrder.isEmpty()) {
				paginator.orderBy(pNameField + " " + pOrder);
			}
		}

		render(dataset, controls, page, paginator);
	}

	/**
	 * Save all the occurence of the table
	 * 
	 * @param id
	 *            id of the dataset
	 * @param page
	 * @param pOrder
	 * @param pNameField
	 * @param occurrences
	 *            table of occurences
	 */
	@Transactional
	@Check("publisher")
	public static void save(long id, Integer page, String pOrder,
			String pNameField, Occurrence[] occurrences) {
		OccurrenceMG lOccurrenceMG = new OccurrenceMG();

		for (Occurrence occurrence : occurrences) {
			lOccurrenceMG.updateOccurenceQualified(occurrence);
		}

		list(id, page, pOrder, pNameField);
	}

}
