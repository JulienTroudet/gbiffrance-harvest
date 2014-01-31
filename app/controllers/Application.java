package controllers;

import java.lang.reflect.Field;
import java.util.List;

import models.Occurrence;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

	/*
	 * Renders the available datasets
	 */
	public static void index() {
		/*
		 * Permet de remplir le champ camelCase de la classe Field
		 * 
		 *  Occurrence
		 * lOccurrence = new Occurrence(); Field[] llistFields =
		 * lOccurrence.getClass().getDeclaredFields(); List<models.Field>
		 * lFields = models.Field.all().fetch(); for (models.Field field :
		 * lFields) { for (Field fieldClass : llistFields) { if (field.name
		 * .toLowerCase() .replace("_", "")
		 * .equals(fieldClass.getName().toLowerCase() .replace("_", ""))) {
		 * field.camelCase = fieldClass.getName(); field.save(); } }
		 * 
		 * }
		 */
		render();
	}

}