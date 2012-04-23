package controllers;

import play.*;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
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

import models.harvest.*;
import models.*;

public class Application extends Controller {

	private static final play.Logger LOG = new play.Logger();
	public static String targetDirectory = Play.configuration.getProperty("temp.path");
	// Informs if a harvest job is already running. if yes, the user will not be able to start a new one until the end of this 
	public static boolean workInProgress = false;
	
	/**
	 * Call the appropriate Harvester for the given dataset. Temporary data will be stored in the targetDirectory
	 */
	public static class HarvestJob extends Job {
	  private Dataset dataset;
	  private String targetDirectory;
	  private String begin;
	  private String end;
	  
	  public HarvestJob(Dataset dataset, String begin, String end) {
		this.dataset = dataset;
		this.targetDirectory = Application.targetDirectory;
		this.begin = begin;
		this.end = end;
	  }

	  @Override
	  public void doJob() {
		Application.workInProgress = true;
		Harvester app = new Harvester();
		if (dataset.type.equals("ipt")) {
	  	  app = new models.harvest.ipt.Harvester(dataset, targetDirectory);
	    }
		else if (dataset.type.equals("biocase") || dataset.type.equals("tapir") || dataset.type.equals("digir")) {
	      app = new Harvester(dataset, targetDirectory, begin, end);
	    }
		else {
			LOG.error("Bad dataset type, cannot start the harvesting process");
		}
		//If a range error is thrown the dataset is marked as harvested with error
		if (app.withErrors)
		{
		  LOG.info("Harvest with error, process to a data cleaning from " + app.currentLower);
		  dataset.status = "HARVESTED_WITH_ERROR";
		  dataset.currentLower = app.currentLower;
		  //app.deleteTemporaryDirectory(app.dataset.tempDirectory, app.currentLower);
		  //app.deleteOccurrencesFrom(app.dataset, app.currentLower);
		}
		else
		  dataset.markDataset("SUCCESSFULLY_HARVESTED");
		dataset.save();
		Application.workInProgress = false;
		index();
	  }
	}
	
	/*
	 * Renders the available datasets
	 */
	public static void index() {
	  String targetDirectory = Application.targetDirectory;
	  List<DataPublishers> dataPublishers = DataPublisher.all().fetch();
	  boolean workInProgress = Application.workInProgress;
	  render(targetDirectory, dataPublishers, workInProgress);
    }
	
	/*
	 * Harvests the dataset related to the given id.  If begin and end are given, makes a partial harvest
	 */
	public static void harvest(Long id, String begin, String end) {
      Dataset dataset = Dataset.findById(id);
	  new HarvestJob(dataset, begin, end).doJob();
    }
	
	/*
	 * Deletes the given dataset
	 */
	public static void deleteDataset(Long id) {
	  Dataset dataset = Dataset.findById(id);
	  Harvester.deleteTemporaryDirectory(dataset.tempDirectory, null);
	  dataset.delete();
	  index();
	}
	
	/*
	 * Deletes the occurrences of the given dataset. Empties the table
	 */
	public static void deleteDatasetOccurrences(Long id) {
	  Occurrence.delete("dataset_id=?", id);
	  Dataset dataset = Dataset.findById(id);
	  Harvester.deleteTemporaryDirectory(dataset.tempDirectory, null);
	  dataset.status = "EMPTY";
	  dataset.currentLower = null;
	  dataset.tempDirectory = null;
	  dataset.save();
	  index();
	}
	
	/*
	 * Edits the target directory for temporary files
	 */
	public static void editTargetDirectory() {
	  render();
	}
	
	public static void saveTargetDirectory(@Required String targetDirectory) {
	  if(validation.hasErrors()){
	    params.flash(); // add http parameters to the flash scope
		validation.keep(); // keep the errors for the next request
		editTargetDirectory();
	  }
	  Application.targetDirectory = targetDirectory;
	  index();
	}
}