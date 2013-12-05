package controllers;

import java.util.List;

import models.DataPublisher;
import models.Dataset;
import models.harvest.Harvester;
import play.Play;
import play.data.validation.Required;
import play.jobs.Job;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Datasets extends Controller
{
	
	private static final play.Logger LOG = new play.Logger();
	// 
	public static String targetDirectory = Play.configuration.getProperty("temp.path");
	// Informs if a harvest job is already running. if yes, the user will not be able to start a new one until the end of this
	public static boolean workInProgress = false;
	
	/*
	 * Renders the available datasets
	 */
	public static void list() {
	  List<DataPublishers> dataPublishers = DataPublisher.all().fetch();
	  render(dataPublishers, workInProgress);
    }
	
	
	
  public static void add(){
    List<DataPublisher> datapublishers = DataPublisher.all().fetch();	
	render(datapublishers);
  }
	
  public static void save(@Required(message="Name is required") String name, 
                          @Required(message="Access point is required") String url,
                          @Required(message="Type is required") String type, 
                          @Required(message="You need to select a data publisher") Long dataPublisherId,
  						  boolean fromOutside)
  {
	if(validation.hasErrors()){
	  params.flash(); // add http parameters to the flash scope
	  validation.keep(); // keep the errors for the next request
	  add();
	}
	else{
	  Dataset dataset = new Dataset(name, url, type, (DataPublisher) DataPublisher.findById(dataPublisherId));
	  dataset.fromOutside = fromOutside;
	  dataset.save();
	  list();
	}		
  }
  
  public static void edit(long id){
	    Dataset dataset = Dataset.findById(id);
	    List<DataPublisher> datapublishers = DataPublisher.all().fetch();	
	    render(dataset, datapublishers);
  }
  
  public static void editSave(long id,
		  					@Required(message="Name is required") String name, 
          					@Required(message="Access point is required") String url,
          					@Required(message="Type is required") String type, 
          					@Required(message="You need to select a data publisher") Long dataPublisherId,
          					boolean fromOutside){
	System.out.println(name);
	if(validation.hasErrors()){
	  params.flash(); // add http parameters to the flash scope
	  validation.keep(); // keep the errors for the next request
	  edit(id);
	}
	else{
	  Dataset dataset = Dataset.findById(id);
	  dataset.name = name;
	  dataset.url = url;
	  dataset.type = type;
	  dataset.dataPublisher = DataPublisher.findById(dataPublisherId);
	  dataset.fromOutside = fromOutside;
	  dataset.save();
	  list();
	}		
  }
  
  /*
	 * Deletes the given dataset
	 */
	public static void delete(Long id) {
	  Dataset dataset = Dataset.findById(id);
	  Harvester.deleteTemporaryDirectory(dataset.tempDirectory, null);
	  dataset.delete();
	  list();
	}
  
  /*
   * Harvests the dataset related to the given id.  If begin and end are given, makes a partial harvest
   */
  public static void harvest(Long id, String begin, String end) {
	  Dataset dataset = Dataset.findById(id);
	  new HarvestJob(dataset, begin, end).doJob();
  }
  
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
		this.targetDirectory = Datasets.targetDirectory;
		this.begin = begin;
		this.end = end;
	  }

	  @Override
	  public void doJob() {
		workInProgress = true;
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
		workInProgress = false;
		list();
	  }
	}
  
  
  
}
