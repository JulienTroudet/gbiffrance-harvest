package controllers;


import java.util.List;

import models.DataPublisher;
import models.Dataset;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.mvc.Controller;
import play.mvc.With;
@With(Secure.class)
public class DataPublishers extends Controller {
	
  public static void add(){
	render();
  }
	
  public static void save(@Required(message="Name is required") String name, 
                          @Required(message="A description is required") String description,
                          @Required(message="Administrative contact is required") String administrativeContact, 
                          @Required(message="Technical contact is required") String technicalContact){
    if(validation.hasErrors()){
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
      add();
    }
    else{
      DataPublisher dataPublisher = new DataPublisher(name, description, administrativeContact, technicalContact);
      dataPublisher.save();
      Application.index();
    }
  }
}
