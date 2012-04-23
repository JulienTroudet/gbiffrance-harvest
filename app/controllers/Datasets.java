package controllers;

import java.util.List;

import models.DataPublisher;
import models.Dataset;
import play.data.validation.Required;
import play.mvc.Controller;

public class Datasets extends Controller
{
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
	  Application.index();
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
	  Application.index();
	}		
  }
  
}
