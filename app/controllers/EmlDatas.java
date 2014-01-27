package controllers;

import models.Dataset;
import models.harvest.ipt.eml.EmlData;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class EmlDatas extends Controller {
	public static void see(Long id) {

		Dataset data = Dataset.findById(id);
		if (data != null) {
			if (data.type.equals("ipt")) {
				EmlData emlData = EmlData.findById(data.emlData.id);
				render(emlData);
			}
		}
	}

}
