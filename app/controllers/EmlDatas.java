package controllers;

import java.util.List;

import models.DataPublisher;
import models.Dataset;
import models.harvest.ipt.eml.EmlData;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

public class EmlDatas extends Controller
{
	public static void see(Long id){

		Dataset data = Dataset.findById(id);
		if (data.type.equals("ipt")){
			EmlData emlData = EmlData.findById(data.emlData.id);
			render(emlData);
		}
	}

}
