package controllers;

import java.util.List;

import models.Field;
import models.Occurrence;
import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

	/*
	 * Renders the available datasets
	 */
	public static void index() {
		render();
	}

}