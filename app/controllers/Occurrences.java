package controllers;

import java.util.ArrayList;

import models.Dataset;
import models.Occurrence;
import models.harvest.Harvester;
import play.db.jpa.Transactional;
import play.modules.paginate.ModelPaginator;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Occurrences extends Controller {

	/**
	 * List the occurrences of the given dataset with pagination.
	 * 
	 * @param id
	 *            id of the dataset
	 * @param page
	 * @param order
	 * @param nomChamp
	 */
	public static void list(Long id, int page, String order, String nomChamp) {
		ModelPaginator paginator = new ModelPaginator(Occurrence.class,
				"dataset.id=?", id);
		paginator.setPageSize(25);
		if (nomChamp != null && !nomChamp.isEmpty()) {
			if (order != null && !order.isEmpty()) {
				paginator.orderBy(nomChamp + " " + order);
			}
		}

		Dataset data = Dataset.findById(id);

		render(data, paginator, page);
	}

	/**
	 * @param id
	 *            id of the dataset
	 */
	public static void map(Long id) {

		Dataset data = Dataset.findById(id);

		render(data);
	}

	/**
	 * Deletes the occurrences of the given dataset. Empties the table
	 * 
	 * @param id
	 *            id of the dataset
	 */
	@Transactional
	@Check("publisher")
	public static void delete(Long id) {
		Occurrence.delete("dataset_id=?", id);
		// EmlData.delete("dataset_id=?", id);
		Dataset dataset = Dataset.findById(id);
		dataset.emlData.delete();
		Harvester.deleteTemporaryDirectory(dataset.tempDirectory, null);
		dataset.status = "EMPTY";
		dataset.occurrences = new ArrayList<Occurrence>();
		dataset.currentLower = null;
		dataset.tempDirectory = null;
		dataset.emlData = null;
		dataset.save();
		Datasets.list();
	}

}
