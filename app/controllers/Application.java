package controllers;

import play.*;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.*;
import play.test.Fixtures;

import groovy.sql.DataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import javax.persistence.TypedQuery;

import models.harvest.*;
import models.harvest.ipt.eml.EmlData;
import models.*;


public class Application extends Controller {

	/*
	 * Renders the available datasets
	 */
	public static void index() {
	  render();
    }
	
}