package controllers;

import java.util.ArrayList;
import java.util.List;

import models.DataPublisher;
import models.Dataset;
import models.Occurrence;
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

}
