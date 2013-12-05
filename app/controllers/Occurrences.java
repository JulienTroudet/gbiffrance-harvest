package controllers;

import java.util.ArrayList;
import java.util.List;

import models.DataPublisher;
import models.Dataset;
import models.Occurrence;
import models.harvest.Harvester;
import models.harvest.ipt.eml.EmlData;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

public class Occurrences extends Controller 
{
	public static void list(Long id, Integer page){

		Dataset data = Dataset.findById(id);
			
		render(data, page);
	}
	
	public static void map(Long id){

		Dataset data = Dataset.findById(id);
			
		render(data);
	}

	
	/*
	 * Deletes the occurrences of the given dataset. Empties the table
	 */
	public static void delete(Long id) {
	  Occurrence.delete("dataset_id=?", id);
	  //EmlData.delete("dataset_id=?", id);
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
